/*
MIT License

Copyright (c) 2021 - 2022 Kyle Hicks

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package me.untouchedodin0.privatemines.we_6.worldedit;

import org.bukkit.Material;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WorldEdit6MineType {

    private String name;
    private final File schematicFile;
    private int mineTier = 1;
    private int resetTime = 1;
    private Material material = Material.STONE;
    private Map<Material, Double> materials = new HashMap<>();

    public WorldEdit6MineType(File schematic) {
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