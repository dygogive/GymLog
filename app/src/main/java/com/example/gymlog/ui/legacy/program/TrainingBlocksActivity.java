package com.example.gymlog.ui.legacy.program;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymlog.R;
import com.example.gymlog.data.local.legacy.PlanManagerDAO;
import com.example.gymlog.data.repository.training_block_repository.TrainingBlockRepository;
import com.example.gymlog.data.repository.training_block_repository.TrainingBlockRepositoryAdapter;
import com.example.gymlog.data.repository.training_block_repository.TrainingBlocksCallback;
import com.example.gymlog.domain.model.legacy.exercise.Exercise;
import com.example.gymlog.domain.model.legacy.exercise.ExerciseInBlock;
import com.example.gymlog.domain.model.legacy.plan.TrainingBlock;
import com.example.gymlog.ui.legacy.dialogs.ConfirmDeleteDialog;
import com.example.gymlog.ui.legacy.exercise.DialogForExerciseEdit;
import com.example.gymlog.ui.legacy.program.adapters.TrainingBlockAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Активність для редагування тренувальних блоків (створення, видалення, переміщення).
 */
@AndroidEntryPoint
public class TrainingBlocksActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView recyclerView;
    private TrainingBlockAdapter adapter;
    private ItemTouchHelper touchHelper;

    // Data
    private final List<TrainingBlock> blocks = new ArrayList<>();
    private PlanManagerDAO dao;
    private long gymDayId;

    @Inject
    TrainingBlockRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_training_block_edit);

        if (!retrieveIntentData()) return;
        dao = new PlanManagerDAO(this);

        bindViews();
        configureRecyclerView();
        configureDragAndDrop();
        loadBlocks();
    }

    // === Initialization ===
    private boolean retrieveIntentData() {
        gymDayId = getIntent().getLongExtra("gym_day_id", -1);
        if (gymDayId < 0) {
            showToast("Помилка: невідомий день тренування");
            finish();
            return false;
        }
        return true;
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recyclerViewTrainingBlocks);
        FloatingActionButton fab = findViewById(R.id.buttonAddTrainingBlock);
        fab.setOnClickListener(v -> openBlockDialog(null));

        setupHeader();
    }

    private void setupHeader() {
        TextView title = findViewById(R.id.textViewBlockTitle);
        TextView desc  = findViewById(R.id.textViewBlockDescription);

        String name = getIntent().getStringExtra("gym_day_name");
        String details = getIntent().getStringExtra("gym_day_description");

        title.setText(name);
        setTruncatedDescription(desc, details, 50);
    }

    private void setTruncatedDescription(TextView view, String text, int limit) {
        String display = text.length() <= limit ? text : text.substring(0, limit) + "...";
        view.setText(display);
        view.setOnClickListener(v -> showFullText(getIntent().getStringExtra("gym_day_name"), text));
    }

    private void showFullText(String title, String message) {
        new AlertDialog.Builder(this, R.style.RoundedDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // === RecyclerView Setup ===
    private void configureRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrainingBlockAdapter(
                this, blocks, dao,
                new BlockMenuListener(),
                holder -> touchHelper.startDrag(holder)
        );
        recyclerView.setAdapter(adapter);
    }

    private void configureDragAndDrop() {
        if (touchHelper != null) touchHelper.attachToRecyclerView(null);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                0
        ) {
            @Override
            public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder target) {
                int from = vh.getBindingAdapterPosition();
                int to   = target.getBindingAdapterPosition();
                adapter.moveItem(from, to);
                dao.updateTrainingBlockPositions(blocks);
                return true;
            }
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) { /* no-op */ }
            @Override public boolean isLongPressDragEnabled() { return false; }
        };

        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    // === Data Loading ===
    private void loadBlocks() {

        TrainingBlockRepositoryAdapter.loadTrainingBlocks(
                gymDayId, repository,
                new TrainingBlocksCallback() {
                    @Override public void onResult(@NotNull List<? extends TrainingBlock> list) {
                        // 1) Очищаємо старий список
                        blocks.clear();
                        // 2) Додаємо новий
                        blocks.addAll(list);
                        // 3) Повідомляємо адаптер, що дані змінилися
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onError(@NonNull Throwable ex) {
                        showToast("Помилка: " + ex.getMessage());
                        Log.e("TrainingBlocks", "Loading failed", ex);
                    }
                }
        );
    }

    // === Actions ===
    private void openBlockDialog(TrainingBlock block) {
        new DialogBlocCreator(
                this, gymDayId, block, this::loadBlocks
        ).show();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // === Inner Listener ===
    private class BlockMenuListener implements TrainingBlockAdapter.OnMenuTrainingBlockListener {
        @Override public void onEditClick(TrainingBlock block) { openBlockDialog(block); }

        @SuppressLint("NotifyDataSetChanged")
        @Override public void onDeleteClick(TrainingBlock block) {
            ConfirmDeleteDialog.show(
                    TrainingBlocksActivity.this,
                    block.getName(),
                    () -> {
                        dao.deleteTrainingBlock(block.getId());
                        blocks.remove(block);
                        adapter.notifyDataSetChanged();
                        showToast("Блок видалено");
                    }
            );
        }

        @Override public void onAddExercise(TrainingBlock block) {
            DialogForExerciseEdit dialog = new DialogForExerciseEdit(
                    TrainingBlocksActivity.this, TrainingBlocksActivity.this::loadBlocks
            );
            dialog.setOnExerciseCreatedListener(ex -> {
                dao.addExerciseToBlock(block.getId(), ex.getId());
                loadBlocks();
            });
            dialog.showWithPreselectedFilters(
                    null, block.getMotions(), block.getMuscleGroupList(), block.getEquipmentList()
            );
        }

        @Override public void onEditExercises(TrainingBlock block) {
            showExerciseSelection(block);
        }

        @Override public void onCloneTrainingBlock(TrainingBlock block) {
            TrainingBlock clone = dao.onStartCloneTrainingBlock(block);
            if (clone != null) {
                showToast("План клоновано!");
                loadBlocks();
            } else showToast("Помилка при клонуванні");
        }
    }

    // === Exercise Selection Dialog ===
    private void showExerciseSelection(TrainingBlock block) {
        List<Exercise> all = dao.recommendExercisesForTrainingBlock(block.getId());
        List<ExerciseInBlock> selected = dao.getBlockExercises(block.getId());
        Set<Long> ids = new HashSet<>();
        selected.forEach(e -> ids.add(e.getId()));

        String[] names = new String[all.size()];
        boolean[] checks = new boolean[all.size()];
        for (int i = 0; i < all.size(); i++) {
            Exercise e = all.get(i);
            names[i]  = e.getNameOnly(this);
            checks[i] = ids.contains(e.getId());
        }

        new AlertDialog.Builder(this, R.style.RoundedDialogTheme2)
                .setTitle("Редагувати вправи блоку: " + block.getName())
                .setMultiChoiceItems(names, checks, (dlg, idx, isChecked) -> checks[idx] = isChecked)
                .setPositiveButton("Зберегти", (dlg, w) -> applyExerciseChanges(block, all, checks))
                .setNegativeButton("Скасувати", null)
                .show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void applyExerciseChanges(TrainingBlock block, List<Exercise> all, boolean[] checks) {
        List<ExerciseInBlock> result = new ArrayList<>();
        Set<Long> newIds = new HashSet<>();
        for (int i = 0; i < all.size(); i++) if (checks[i]) newIds.add(all.get(i).getId());

        // Keep existing
        dao.getBlockExercises(block.getId()).forEach(old -> {
            if (newIds.remove(old.getId())) result.add(old);
        });
        // Add new
        all.stream()
                .filter(e -> newIds.contains(e.getId()))
                .forEach(e -> result.add(new ExerciseInBlock(-1, e.getId(), e.getName(), e.getDescription(), e.getMotion(), e.getMuscleGroupList(), e.getEquipment(), -1)));

        // Reassign positions
        for (int i = 0; i < result.size(); i++) result.get(i).setPosition(i);
        dao.updateTrainingBlockExercises(block.getId(), result);
        loadBlocks();
    }
}
