package me.untouchedodin0.plugin.mines;

import org.bukkit.Material;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public class MineType {
    private final Path schematicFile;
    private String name;
    private int mineTier = 1;
    private int resetTime = 1;
    private Map<Material, Double> materials = new EnumMap<>(Material.class);

    public MineType(Path schematicFile) {
        this.schematicFile = schematicFile;
    }

    public Path getSchematicFile() {
        return schematicFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMineTier() {
        return mineTier;
    }

    public void setMineTier(int mineTier) {
        this.mineTier = mineTier;
    }

    public int getResetTime() {
        return resetTime;
    }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    public Map<Material, Double> getMaterials() {
        return materials;
    }

    public void setMaterials(Map<Material, Double> materials) {
        this.materials = materials;
    }
}
