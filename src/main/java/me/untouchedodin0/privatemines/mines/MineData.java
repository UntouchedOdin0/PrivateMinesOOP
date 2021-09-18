package me.untouchedodin0.privatemines.mines;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class MineData {

    /*
        name: Name of the mine type
        mineTier: The tier of the mine (where it goes in the upgrade process)
        resetTime: How often the mine should reset in minutes.
        materials: A list of materials and their percentages of which goes in the mine
     */

    private String name;
    private int mineTier = 1;
    private int resetTime = 1;
    private Map<Material, Double> materials = new HashMap<>();

    public void setName(String name) { this.name = name; }

    public String getName() { return name; }

    public void setMineTier(int mineTier) { this.mineTier = mineTier; }

    public int getMineTier() { return mineTier; }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    public int getResetTime() {
        return resetTime;
    }

    public void setMaterials(Map<Material, Double> mineBlocks) {
        this.materials = mineBlocks;
    }

    public Map<Material, Double> getMaterials() { return materials; }
}
