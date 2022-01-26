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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MineFactory6 {

    public void sayHi() {
        Bukkit.getLogger().info("hi from 6");
    }

    public void createMine(Player player, Location location, WorldEdit6MineType worldEdit6MineType, boolean replaceOld) {
        Utils utils = new Utils();
        UUID uuid = player.getUniqueId();
        List<Location> corners = new ArrayList<>();

        player.sendMessage("player: " + player);
        player.sendMessage("location: " + location);
        player.sendMessage("worldEdit6MineType: " + worldEdit6MineType);
        player.sendMessage("replaceOld: " + replaceOld);
        player.sendMessage("utils: " + utils);
        player.sendMessage("uuid: " + uuid);
    }
}
