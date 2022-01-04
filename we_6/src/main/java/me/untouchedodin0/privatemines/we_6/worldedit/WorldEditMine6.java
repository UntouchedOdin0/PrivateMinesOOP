package me.untouchedodin0.privatemines.we_6.worldedit;

import com.sk89q.worldedit.WorldEdit;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class WorldEditMine6 {


    public void sayHi() {
        Bukkit.getLogger().info("hi i'm on bukkit ver " + Bukkit.getBukkitVersion());
        Bukkit.getLogger().info("I'm aso running wordedit version: " + WorldEdit.getVersion());
        Utils utils = new Utils();
        Bukkit.getLogger().info("block type #1 " + utils.bukkitToBlockType(Material.STONE));
        Bukkit.getLogger().info("block type #2 " + utils.bukkitToBlockType(Material.COBBLESTONE));
        Bukkit.getLogger().info("block type #3 " + utils.bukkitToBlockType(Material.DIAMOND_ORE));
        Bukkit.getLogger().info("block type #4 " + utils.bukkitToBlockType(Material.DIAMOND_BLOCK));
        Bukkit.getLogger().info("block type #5 " + utils.bukkitToBlockType(Material.EMERALD_ORE));
        Bukkit.getLogger().info("block type #6 " + utils.bukkitToBlockType(Material.EMERALD_BLOCK));
        Bukkit.getLogger().info("block type #7 " + utils.bukkitToBlockType(Material.REDSTONE_ORE));
        Bukkit.getLogger().info("block type #8 " + utils.bukkitToBlockType(Material.REDSTONE_BLOCK));
        Bukkit.getLogger().info("block type #9 " + utils.bukkitToBlockType(Material.COAL_ORE));
        Bukkit.getLogger().info("block type #10 " + utils.bukkitToBlockType(Material.COAL_BLOCK));

    }
}
