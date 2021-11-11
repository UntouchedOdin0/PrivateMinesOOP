package me.untouchedodin0.plugin.util.worldedit;

import org.bukkit.Location;

import java.io.File;

public interface MineFactoryCompat {

    WorldEditRegion pasteSchematic(File schematic, Location location);
}
