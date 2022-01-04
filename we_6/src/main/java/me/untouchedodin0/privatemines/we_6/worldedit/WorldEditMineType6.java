package me.untouchedodin0.privatemines.we_6.worldedit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Setter
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

    public File getSchematicFile() {
        return schematicFile;
    }
}
