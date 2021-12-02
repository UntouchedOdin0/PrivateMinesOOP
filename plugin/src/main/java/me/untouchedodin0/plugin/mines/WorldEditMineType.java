package me.untouchedodin0.plugin.mines;

import lombok.Getter;
import lombok.Setter;
import me.untouchedodin0.plugin.PrivateMines;
import org.bukkit.Material;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class WorldEditMineType {

    PrivateMines privateMines;

    private String name;
    private final File schematicFile;
    private int mineTier = 1;
    private int resetTime = 1;
    private Material material = Material.STONE;
    private Map<Material, Double> materials = new HashMap<>();

    public WorldEditMineType(PrivateMines privateMines, File schematic) {
        this.privateMines = privateMines;
        this.schematicFile = schematic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public File getSchematicFile() {
        return schematicFile;
    }

    public void setMineTier(int tier) {
        this.mineTier = tier;
    }

    public int getMineTier() {
        return mineTier;
    }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    public int getResetTime() {
        return resetTime;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterials(Map<Material, Double> materials) {
        this.materials = materials;
    }

    public Map<Material, Double> getMaterials() {
        return materials;
    }
}
