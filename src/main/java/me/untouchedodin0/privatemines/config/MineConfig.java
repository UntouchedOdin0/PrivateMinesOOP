/*
MIT License

Copyright (c) 2021 Kyle Hicks

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

package me.untouchedodin0.privatemines.config;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mines.MineData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigPath;
import redempt.redlib.configmanager.annotations.ConfigPostInit;
import redempt.redlib.configmanager.annotations.ConfigValue;
import redempt.redlib.multiblock.MultiBlockStructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@ConfigMappable
public class MineConfig {

    private PrivateMines privateMines;
    private Path path;
    private String contents;

    @ConfigPath
    private String name = "Default";

    @ConfigValue
    private String file = "structure.dat"; // reads the file

    @ConfigValue
    private int priority = 1; // reads the priority from the section

    @ConfigValue
    private int resetTime = 5; // reads the reset time from the section

    @ConfigValue
    private Map<Material, Double> materials =
            ConfigManager.map(Material.class, Double.class); // reads the materials from the section

    private MultiBlockStructure multiBlockStructure;

    public MineConfig() {
        this.privateMines = PrivateMines.getPlugin(PrivateMines.class);
    }

    /*
        These tasks are fired after all the objects in the config file have been mapped.
        Path: The path of the specified file to be processed
        Contents: The contents of the the file e.g. 14x24x27;bedrock*9072;
     */

    @ConfigPostInit
    private void postInit() throws IOException {
        if (privateMines == null) {
            Bukkit.getLogger().info(
                    "Private Mines instance in the MineConfig was null " +
                            "please make a ticket on the discord reporting this");
        }

        this.path = privateMines.getDataFolder().toPath().resolve(file);
        this.contents = Files.lines(path).collect(Collectors.joining());

        this.multiBlockStructure = MultiBlockStructure.create(contents, name, false, true);

        MineData mineData = new MineData(privateMines);
        mineData.setName(getName());
        mineData.setMultiBlockStructure(multiBlockStructure);
        mineData.setMineTier(getPriority());
        mineData.setResetTime(getResetTime());
        mineData.setMaterials(getMaterials());
        mineData.setupRelativeLocations();

        privateMines.addMineData(getName(), mineData);
    }

    // a getter for the private mines instance
    public PrivateMines getPrivateMines() {
        return privateMines;
    }

    // A getter for the path
    public Path getPath() { return path; }

    // A getter for the contents

    public String getContents() { return contents; }

    // A getter for getting the section name

    public String getName() { return name; }

    // A getter for getting the file name from the section

    public String getFile() {
        return file;
    }

    // A getter for getting the priority from the section

    public int getPriority() {
        return priority;
    }

    // A getter for getting the reset time from the section

    public int getResetTime() { return resetTime; }

    // A getter for getting the materials from the secion

    public Map<Material, Double> getMaterials() {
        return materials;
    }

    // A getter for getting the multi block structure

    public MultiBlockStructure getMultiBlockStructure() {
        return multiBlockStructure;
    }
}
