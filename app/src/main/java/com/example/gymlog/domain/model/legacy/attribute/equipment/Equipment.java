package com.example.gymlog.domain.model.legacy.attribute.equipment;

import android.content.Context;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.legacy.attribute.AttributeItem;
import com.example.gymlog.domain.model.legacy.attribute.HeaderItem;
import com.example.gymlog.domain.model.legacy.attribute.ListHeaderAndAttribute;
import com.example.gymlog.domain.model.legacy.attribute.DescriptionAttribute;

import java.util.ArrayList;
import java.util.List;

public enum Equipment implements DescriptionAttribute {

    // Вільна вага
    BARBELL(R.string.equipment_barbell), // Штанга
    DUMBBELLS(R.string.equipment_dumbbells), // Гантелі
    KETTLEBELL(R.string.equipment_kettlebell), // Гиря
    WEIGHT_PLATE(R.string.equipment_weight_plate), // Млинець

    // Тренажери
    BODYWEIGHT(R.string.equipment_bodyweight), // Вага тіла
    MACHINE(R.string.equipment_machine), // Стаціонарний тренажер
    CABLE_MACHINE(R.string.equipment_cable_machine), // Блочний тренажер
    PLATE_LOADED_MACHINE(R.string.equipment_plate_loaded_machine), // Тренажер із млинцями

    // Інші види
    RESISTANCE_BAND(R.string.equipment_resistance_band), // Резина (еспандер)
    SANDBAG(R.string.equipment_sandbag), // Мішок з піском
    MEDICINE_BALL(R.string.equipment_medicine_ball), // Медичний м'яч
    SUSPENSION_TRAINER(R.string.equipment_suspension_trainer); // Петлі TRX


    public static Equipment[] getAllValues() {
        return values(); // <- тут вже викликається рідний метод
    }



    //посилання на строку що описує обладнання
    private final int descriptionResId;

    //конструктор
    Equipment(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }


    public int getIconResId() {
        return R.drawable.ic_exercise;
    }

    //видати ідентифікатор опису обладнання
    public int getDescriptionResId() {
        return descriptionResId;
    }

    //видати текст опису обладнання
    public String getDescription(Context context) {
        return context.getString(descriptionResId);
    }

    //Щоб сформувати масив String[] з описів для кожного елемента перерахування Equipment
    public static String[] getEquipmentDescriptions(Context context) {
        Equipment[] equipments = Equipment.values();  // Отримуємо всі елементи перерахування
        String[] descriptions = new String[equipments.length];  // Створюємо масив для зберігання описів

        // Заповнюємо масив описами для кожного руху
        for (int i = 0; i < equipments.length; i++) {
            descriptions[i] = equipments[i].getDescription(context);
        }

        return descriptions;  // Повертаємо масив з описами
    }

    public static Equipment getEquipmentByDescription(Context context, String description) {
        Equipment[] equipments = Equipment.values();  // Отримуємо всі елементи перерахування

        // Проходимо по всіх елементах перерахування
        for (Equipment equipment : equipments) {
            // Порівнюємо опис кожного елемента з переданим рядком
            if (equipment.getDescription(context).equals(description)) {
                return equipment;  // Повертаємо відповідний елемент enum
            }
        }

        return null;  // Якщо не знайдено відповідного опису, повертаємо null
    }


    public static List<ListHeaderAndAttribute> getGroupedEquipmentItems(Context context) {
        List<ListHeaderAndAttribute> items = new ArrayList<>();

        items.add(new HeaderItem(context.getString(R.string.header_free_weights)));
        items.add(new AttributeItem<>(BARBELL));
        items.add(new AttributeItem<>(DUMBBELLS));
        items.add(new AttributeItem<>(KETTLEBELL));
        items.add(new AttributeItem<>(WEIGHT_PLATE));

        items.add(new HeaderItem(context.getString(R.string.header_machines)));
        items.add(new AttributeItem<>(BODYWEIGHT));
        items.add(new AttributeItem<>(MACHINE));
        items.add(new AttributeItem<>(CABLE_MACHINE));
        items.add(new AttributeItem<>(PLATE_LOADED_MACHINE));

        items.add(new HeaderItem(context.getString(R.string.header_others)));
        items.add(new AttributeItem<>(RESISTANCE_BAND));
        items.add(new AttributeItem<>(SANDBAG));
        items.add(new AttributeItem<>(MEDICINE_BALL));
        items.add(new AttributeItem<>(SUSPENSION_TRAINER));

        return items;
    }
}
