package com.example.gymlog.ui.legacy.exercise.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.attribute.AttributeItem;
import com.example.gymlog.domain.model.attribute.FiltreAttributes;
import com.example.gymlog.domain.model.attribute.HeaderItem;
import com.example.gymlog.domain.model.attribute.ListHeaderAndAttribute;
import com.example.gymlog.domain.model.attribute.TypeAttributeExercises;


import java.util.List;

public class AttributeAdapter<E extends Enum<E> & TypeAttributeExercises> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ListHeaderAndAttribute> items;
    private final OnItemClickListener listener;

    Context context;

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    public AttributeAdapter(List<ListHeaderAndAttribute> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // 1) Створюємо View з вашого item_attribute.xml
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        if (viewType == ListHeaderAndAttribute.TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_attributes_header, parent, false);
            return new HeaderViewHolder(view);
        }

        else if (viewType == ListHeaderAndAttribute.TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_attribute, parent, false);
            return new ItemViewHolder(view);
        }

        else if (viewType == ListHeaderAndAttribute.TYPE_FILTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_filtre_attributes, parent, false);
            return new FiltreAttributeHolder(view);
        }



        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_filtre_attributes, parent, false));
    }



    // 2) Прив’язуємо дані (іконка + текст) до ViewHolder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListHeaderAndAttribute item = items.get(position);

        Log.d("errAttribute", "1 - " );


        if (holder instanceof HeaderViewHolder && item instanceof HeaderItem) {
            ((HeaderViewHolder) holder).titleHoldersAttribure.setText(((HeaderItem) item).getTitle());
            holder.itemView.setOnClickListener(null); // Заголовки неклікабельні
        }


        else if (holder instanceof ItemViewHolder && item instanceof AttributeItem<?>) {
             E attribute = (E) ((AttributeItem<?>) item).getEquipment();
             ((ItemViewHolder) holder).titleTextView.setText(attribute.getDescription(context));
             holder.itemView.setOnClickListener(v -> listener.onItemClick(attribute));
        }

        else if (holder instanceof FiltreAttributeHolder && item instanceof FiltreAttributes<?>) {
            E filtre = (E) ((FiltreAttributes<?>) item).getEquipment();
            ((FiltreAttributeHolder) holder).titleFiltreAttribure.setText(filtre.getDescription(context));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(filtre));
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }


    // Холдер для заголовків
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView titleHoldersAttribure;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            titleHoldersAttribure = itemView.findViewById(R.id.titleHoldersAttribure);
        }
    }

    //Холдер для ітемів
    public static class ItemViewHolder  extends RecyclerView.ViewHolder {

        TextView titleTextView;

        public ItemViewHolder (@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }


    //Окремий Холдер для назв атрибутів
    public static class FiltreAttributeHolder extends RecyclerView.ViewHolder {

        TextView titleFiltreAttribure;

        public FiltreAttributeHolder(@NonNull View itemView) {
            super(itemView);
            titleFiltreAttribure = itemView.findViewById(R.id.titleFiltreAttribure);
        }
    }


}
