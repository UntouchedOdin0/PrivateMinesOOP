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
