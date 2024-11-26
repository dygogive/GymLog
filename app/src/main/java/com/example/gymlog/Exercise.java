package com.example.gymlog;

import java.util.List;

public class Exercise {

    private String name;
    private Motion motion;
    private List<MuscleGroup> muscleGroupList;
    private Equipment equipment;

    public Exercise(String name, Motion motion, List<MuscleGroup> muscleGroupList, Equipment equipment) {
        this.name = name;
        this.motion = motion;
        this.muscleGroupList = muscleGroupList;
        this.equipment = equipment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMotion(Motion motion) {
        this.motion = motion;
    }

    public void setMuscleGroupList(List<MuscleGroup> muscleGroupList) {
        this.muscleGroupList = muscleGroupList;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public String getName() {
        return name;
    }

    public Motion getMotion() {
        return motion;
    }

    public List<MuscleGroup> getMuscleGroupList() {
        return muscleGroupList;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public enum HalfBody {
        BODY("Pulls and presses with hands"),
        LEGS("Pulls and presses with legs");

        private final String description;

        HalfBody(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum Motion {
        PRESS("press with arms or legs"),
        PULL("pull with arms or legs");

        private final String description;

        Motion(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum MuscleGroup {
        CHEST_LOWER("Chest Lower"),
        CHEST_UPPER("Chest Upper"),
        CHEST_FULL("Chest full"),
        TRICEPS("Triceps"),
        BICEPS_ARMS("Biceps Arms"),
        TRAPS_UPPER("Trapezius Upper"),
        TRAPS_MIDDLE("Trapezius Middle"),
        TRAPS_LOWER("Trapezius Lower"),
        LATS("Latissimus Dorsi"),
        HAMSTRINGS("Hamstrings"),
        QUADRICEPS("Quadriceps"),
        GLUTES("Glutes"),
        DELTS_REAR("Deltoids Rear"),
        DELTS_SIDE("Deltoids Side"),
        DELTS_FRONT("Deltoids Front"),
        LONGISSIMUS("Longissimus muscle ");

        private final String description;

        MuscleGroup(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }


    public enum Equipment {
        BARBELL, DUMBBELLS, BODY_WEIGHT, TRAINER;
    }
}
