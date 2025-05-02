package com.example.gymlog.domain.model.legacy.plan;

// Базовий інтерфейс для програм тренувань та днів тренувань
public interface BasePlanItem {
    long getId();
    String getName();
    String getDescription();
}
