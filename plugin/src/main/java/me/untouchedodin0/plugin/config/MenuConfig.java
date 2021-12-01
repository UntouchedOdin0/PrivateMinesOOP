package me.untouchedodin0.plugin.config;

import org.bukkit.Material;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigPath;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.List;

@ConfigMappable
public class MenuConfig {

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
