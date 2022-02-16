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

package me.untouchedodin0.plugin.config;

import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.mines.MineType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import redempt.redlib.config.annotations.ConfigMappable;
import redempt.redlib.config.annotations.ConfigPath;
import redempt.redlib.config.annotations.ConfigPostInit;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigMappable
public class MineConfig {

    private final transient PrivateMines privateMines;
    private Path path;

    @ConfigPath
    public static String name = "Default";
    public static String file = "structure.dat"; // reads the file
    public static int priority = 1; // reads the priority from the section
    public static int resetTime = 5; // reads the reset time from the section
    public static double resetPercentage = 50.00; // reads the reset percentage from the section
    public static Material material = Material.STONE;
    public static Map<Material, Double> materials = new HashMap<>();
    public static List<String> allowFlags = new ArrayList<>();
    public static List<String> denyFlags = new ArrayList<>();

//    private Map<Material, Double> materials =
//            ConfigManager.map(Material.class, Double.class); // reads the materials from the section

    public MineConfig() {
        this.privateMines = PrivateMines.getPrivateMines();
    }

    /*
        Ths is called for when the config is loaded and reloaded
     */

    @ConfigPostInit
    private void postInit() {

        privateMines.getLogger().info("" + privateMines);
        if (privateMines == null) {
            String instanceNull = "Private Mines instance in the MineConfig was null " +
                    "please make a ticket on the discord reporting this";
            Bukkit.getLogger().info(
                    instanceNull);
            return;
        }

        if (file == null) {
            String missingFile = "Can't find the file specified, are you sure it exists?";
            privateMines.getLogger().warning(missingFile);
        }


        this.path = privateMines.getSchematicsDirectory().resolve(file);
        MineType mineType = new MineType(path);
        mineType.setName(getName());
        mineType.setMineTier(getPriority());
        mineType.setResetTime(getResetTime());
        mineType.setMaterials(getMaterials());

        privateMines.getLogger().info("mineType: " + mineType);
        privateMines.getLogger().info("mineType name: " + mineType.getName());
        privateMines.getLogger().info("mineType reset time: " + mineType.getResetTime());
        privateMines.getLogger().info("mineType materials: " + mineType.getMaterials());

        //privateMines.getMineTypeManager().registerMineType(mineType);

        privateMines.getLogger().info("file: " + file);
        privateMines.getLogger().info("path: " + path);
    }

    // a getter for the private mines instance
    public PrivateMines getPrivateMines() {
        return privateMines;
    }

    // A getter for the path

    public Path getPath() {
        return path;
    }

    // A getter for getting the section name

    public String getName() {
        return name;
    }

    // A getter for getting the file name from the section

    public String getFile() {
        return file;
    }

    // A getter for getting the priority from the section

    public int getPriority() {
        return priority;
    }

    // A getter for getting the reset time from the section

    public int getResetTime() {
        return resetTime;
    }

    public double getResetPercentage() {
        return resetPercentage;
    }

    // A getter for getting the materials from the section

    public Map<Material, Double> getMaterials() {
        return materials;
    }

    // A getter for getting the material from the section

//    public Material getMaterial() {
//        return material;
//    }


    public List<String> getAllowFlags() {
        return allowFlags;
    }

    public List<String> getDenyFlags() {
        return denyFlags;
    }
}
