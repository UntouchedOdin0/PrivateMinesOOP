package me.untouchedodin0.privatemines.we_6.worldedit;

import lombok.Getter;
import org.bukkit.Material;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class WorldEditMineType6 {

    private String name;
    private final File schematicFile;
    private int mineTier;
    private int resetTime;
    private Material material = Material.STONE;
    private Map<Material, Double> materials = new HashMap<>();

    public WorldEditMineType6(File schematic) {
        this.schematicFile = schematic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getSchematicFile() {
        return schematicFile;
    }

    public void setMineTier(int mineTier) {
        this.mineTier = mineTier;
    }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setMaterials(Map<Material, Double> materials) {
        this.materials = materials;
    }
}
