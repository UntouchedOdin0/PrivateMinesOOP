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

package me.untouchedodin0.plugin.config;

import org.bukkit.Material;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigPath;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.List;

@ConfigMappable
public class MenuConfig {

    @ConfigValue
    private String title = "Private Mines";

    @ConfigPath
    private final String item = "Default";

    @ConfigValue
    private String name = "Name";

    @ConfigValue
    private List<String> lore = ConfigManager.stringList();

    @ConfigValue
    private Material type = Material.STONE;

    @ConfigValue
    private int slot = 0;

    @ConfigValue
    private String action = "action";

    public String getTitle() {
        return title;
    }

    public String getItem() {
        return item;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getType() {
        return type;
    }

    public int getSlot() {
        return slot;
    }

    public String getAction() {
        return action;
    }
}
