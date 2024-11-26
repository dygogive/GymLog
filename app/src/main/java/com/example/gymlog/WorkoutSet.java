package com.example.gymlog;

//Клас для зберігання виконаного підходу
public class WorkoutSet {
    private String exercise;
    private int weight;
    private int reps;
    private RepType repType;

    public enum RepType {
        STRENGTH,           //сИЛА - ДО 4-6 ПОІВТОРЕНЬ
        HYPERTROPHY,        //Гіпертрофія, об'єм м'язів - 7-12 повторень
        ENDURANCE           //Витривалість, памп - 13-20 повторень
    }

    public WorkoutSet() {
    }

    public WorkoutSet(String exercise, int weight, int reps) {
        this.repType = RepType.STRENGTH;
        this.reps = reps;
        this.weight = weight;
        this.exercise = exercise;
    }

    public WorkoutSet(RepType repType, int reps, int weight, String exercise) {
        this.repType = repType;
        this.reps = reps;
        this.weight = weight;
        this.exercise = exercise;
    }


    public String getExercise() {
        return exercise;
    }

    public int getWeight() {
        return weight;
    }

    public int getReps() {
        return reps;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setRepType(RepType repType) {
        this.repType = repType;
    }

    public String getRepType() {
        return repType.toString();
    }

    @Override
    public String toString() {
        return "WorkoutSet{" +
                "exercise='" + exercise + '\'' +
                ", weight=" + weight +
                ", reps=" + reps +
                ", repType=" + repType +
                '}';
    }
}
