package me.untouchedodin0.plugin.util.materials;

import org.bukkit.Material;

public class Materials {

    private Material cornerMaterial = Material.POWERED_RAIL;
    private Material spawnMaterial = Material.CHEST;
    private Material npcMaterial = Material.WHITE_WOOL;

    public Material getCornerMaterial() {
        return cornerMaterial;
    }

    public void setCornerMaterial(Material cornerMaterial) {
        this.cornerMaterial = cornerMaterial;
    }

    public Material getSpawnMaterial() {
        return spawnMaterial;
    }

    public void setSpawnMaterial(Material spawnMaterial) {
        this.spawnMaterial = spawnMaterial;
    }

    public Material getNpcMaterial() {
        return npcMaterial;
    }

    public void setNpcMaterial(Material npcMaterial) {
        this.npcMaterial = npcMaterial;
    }
}
