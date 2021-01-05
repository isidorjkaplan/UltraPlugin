package co.amscraft.rp;

import co.amscraft.ultralib.UltraObject;

public class RoleplayPower extends UltraObject {
    private String description = "<description>";
    private String name = "power";
    public enum PowerLevel {
        WEAK, MINOR, NORMAL, STRONG, VERY_STRONG, LEGENDARY;
        public static PowerLevel getPowerLevel(String level) {
            try {
                return PowerLevel.valueOf(level.toUpperCase());
            } catch (Exception e) {
                return null;
            }
        }
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }

    public static RoleplayPower getPower(String name) {
        return getObject(RoleplayPower.class, "name", name);
    }


}
