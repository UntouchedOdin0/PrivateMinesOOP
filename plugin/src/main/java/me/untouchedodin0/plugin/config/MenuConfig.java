package me.untouchedodin0.plugin.config;

import me.untouchedodin0.plugin.config.menu.MenuItemType;
import redempt.redlib.config.annotations.ConfigMappable;

import java.util.HashMap;
import java.util.Map;

@ConfigMappable
public class MenuConfig {

    public static Map<String, MenuItemType> menuItemTypeMap = new HashMap<>();
}
