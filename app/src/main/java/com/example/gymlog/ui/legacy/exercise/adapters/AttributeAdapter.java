package com.example.gymlog.ui.legacy.exercise.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.legacy.attribute.AttributeItem;
import com.example.gymlog.domain.model.legacy.attribute.FilterItem;
import com.example.gymlog.domain.model.legacy.attribute.HeaderItem;
import com.example.gymlog.domain.model.legacy.attribute.ListHeaderAndAttribute;
import com.example.gymlog.domain.model.legacy.attribute.DescriptionAttribute;

import java.util.List;

/**
 * Адаптер для відображення атрибутів вправ у RecyclerView
 * @param <E> тип перерахування, що реалізує DescriptionAttribute
 */
public class AttributeAdapter<E extends Enum<E> & DescriptionAttribute> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ListHeaderAndAttribute> items;
    private final OnItemClickListener listener;
    private Context context;

    /**
     * Інтерфейс для обробки натискань на елементи списку
     */
    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    /**
     * Конструктор адаптера
     * @param items список елементів для відображення
     * @param listener обробник натискань
     */
    public AttributeAdapter(List<ListHeaderAndAttribute> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case ListHeaderAndAttribute.TYPE_HEADER:
                return new HeaderViewHolder(
                        inflater.inflate(R.layout.item_attributes_header, parent, false));

            case ListHeaderAndAttribute.TYPE_ITEM:
                return new ItemViewHolder(
                        inflater.inflate(R.layout.item_attribute, parent, false));

            case ListHeaderAndAttribute.TYPE_FILTER:
                return new FilterAttributeHolder(
                        inflater.inflate(R.layout.item_filtre_attributes, parent, false));

            default:
                // Запасний варіант
                return new ItemViewHolder(
                        inflater.inflate(R.layout.item_attribute, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListHeaderAndAttribute item = items.get(position);

        try {
            if (holder instanceof HeaderViewHolder && item instanceof HeaderItem) {
                bindHeaderViewHolder((HeaderViewHolder) holder, (HeaderItem) item);
            }
            else if (holder instanceof ItemViewHolder && item instanceof AttributeItem<?>) {
                bindItemViewHolder((ItemViewHolder) holder, (AttributeItem<?>) item);
            }
            else if (holder instanceof FilterAttributeHolder && item instanceof FilterItem<?>) {
                bindFilterViewHolder((FilterAttributeHolder) holder, (FilterItem<?>) item);
            }
        } catch (ClassCastException e) {
            // Обробка помилок приведення типів
            e.printStackTrace();
        }
    }

    /**
     * Прив'язує дані до ViewHolder заголовка
     */
    private void bindHeaderViewHolder(HeaderViewHolder holder, HeaderItem item) {
        holder.titleHeaderAttribute.setText(item.getTitle());
        holder.itemView.setOnClickListener(null); // Заголовки неклікабельні
    }

    /**
     * Прив'язує дані до ViewHolder елемента
     */
    @SuppressWarnings("unchecked")
    private void bindItemViewHolder(ItemViewHolder holder, AttributeItem<?> item) {
        try {
            E attribute = (E) item.getAttribute();
            holder.titleTextView.setText(attribute.getDescription(context));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(attribute));
        } catch (ClassCastException e) {
            // Обробка помилки приведення типу
            e.printStackTrace();
        }
    }

    /**
     * Прив'язує дані до ViewHolder фільтра
     */
    @SuppressWarnings("unchecked")
    private void bindFilterViewHolder(FilterAttributeHolder holder, FilterItem<?> item) {
        try {
            E filter = (E) item.getAttribute();
            holder.titleFilterAttribute.setText(filter.getDescription(context));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(filter));
        } catch (ClassCastException e) {
            // Обробка помилки приведення типу
            e.printStackTrace();
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

    /**
     * ViewHolder для заголовків у списку
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView titleHeaderAttribute;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleHeaderAttribute = itemView.findViewById(R.id.titleHoldersAttribure);
        }
    }

    /**
     * ViewHolder для звичайних елементів списку
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }

    /**
     * ViewHolder для елементів фільтрів
     */
    public static class FilterAttributeHolder extends RecyclerView.ViewHolder {
        TextView titleFilterAttribute;

        public FilterAttributeHolder(@NonNull View itemView) {
            super(itemView);
            titleFilterAttribute = itemView.findViewById(R.id.titleFiltreAttribure);
        }
    }
}