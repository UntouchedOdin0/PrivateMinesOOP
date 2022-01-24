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

package me.untouchedodin0.plugin.storage;

import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.mines.WorldEditMine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MineStorage {

    private static final Map<UUID, Mine> staticMines = new HashMap<>();

    /**
     * The map of all the mines and their owner's UUID.
     */

    private final Map<UUID, Mine> mines = new HashMap<>();


    private final Map<UUID, WorldEditMine> worldEditMines = new HashMap<>();


    /**
     * A simple get method to get how many mines have been created
     *
     * @return the amount of mines created in a integer form.
     */

    public static int getLoadedMineSize() {
        return staticMines.size();
    }


    /**
     * A simple get method to get how many worldedit mines have been created
     *
     * @return the amount of mines created in a integer form.
     */

    public int getLoadedWorldEditMinesSize() {
        return worldEditMines.size();
    }


    /**
     * @param uuid - The UUID Of the mine owner to be added to the storage
     * @param mine - The Mine to be added into the storage
     */

    public void addMine(UUID uuid, Mine mine) {
        mines.putIfAbsent(uuid, mine);
        staticMines.put(uuid, mine);
    }


    /**
     * @param uuid          - The UUID Of the mine owner to be added to the storage
     * @param worldEditMine - The World Edit Mine to be added into the storage
     */

    public void addWorldEditMine(UUID uuid, WorldEditMine worldEditMine) {
        worldEditMines.putIfAbsent(uuid, worldEditMine);
    }

    /**
     * @param uuid - The UUID of the player who's mine we'll be deleting from storage
     */

    public void removeMine(UUID uuid) {
        mines.remove(uuid);
    }

    /**
     * @param uuid - The UUID of the player who's worldedit mine we'll be deleting from storage
     */

    public void removeWorldEditMine(UUID uuid) {
        worldEditMines.remove(uuid);
    }

    /**
     * @param uuid The UUID of the player who's mine we'll be deleting from storage
     * @param mine The new mine of which will replace the old mine
     */

    public void replaceMine(UUID uuid, Mine mine) {
        mines.replace(uuid, getMine(uuid), mine);
    }

    public void replaceMine(UUID uuid, WorldEditMine worldEditMine) {
        worldEditMines.replace(uuid, worldEditMine);
    }

    /**
     * @return A map of all the mines with the owners UUID's.
     */

    public Map<UUID, Mine> getMines() {
        return mines;
    }

    /**
     * @return A map of all the worldedit mines with the owners UUID's.
     */

    public Map<UUID, WorldEditMine> getWorldEditMines() {
        return worldEditMines;
    }

    // This is a way to get a players mine


    public Mine getMine(UUID uuid) {
        return mines.get(uuid);
    }

    // Get a players world edit mine

    public WorldEditMine getWorldEditMine(UUID uuid) {
        return worldEditMines.get(uuid);
    }

    /**
     * A simple method to see if a user has a mine or not
     *
     * @return boolean if the user has a mine or not.
     */

    public boolean hasMine(UUID uuid) {
        return mines.containsKey(uuid);
    }

    public boolean hasWorldEditMine(UUID uuid) {
        return worldEditMines.containsKey(uuid);
    }

    public int getMineCount() {
        return mines.size();
    }

    public int getWorldEditMinesCount() {
        return worldEditMines.size();
    }
}
