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

package me.untouchedodin0.privatemines.storage;

import me.untouchedodin0.privatemines.mines.Mine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MineStorage {

    /**
     *  The map of all the mines and their owner's UUID.
     */

    private final Map<UUID, Mine> mines = new HashMap<>();

    /**
     *
     * @param uuid - The UUID Of the mine owner to be added to the storage
     * @param mine - The Mine to be added into the storage
     */

    public void addMine(UUID uuid, Mine mine) {
        mines.putIfAbsent(uuid, mine);
    }

    /**
     *
     * @param uuid - The UUID of the player who's mine we'll be deleting from storage
     */

    public void removeMine(UUID uuid) {
        mines.remove(uuid);
    }

    /**
     *
     * @return A map of all the mines with the owners UUID's.
     */

    public Map<UUID, Mine> getMines() {
        return mines;
    }

    // This is a way to to get a players mine

    public Mine getMine(UUID uuid) {
        return mines.get(uuid);
    }

    /**
     *  A simple method to see if a user has a mine or not
     *  @return boolean if the user has a mine or not.
     */

    public boolean hasMine(UUID uuid) {
        return mines.containsKey(uuid);
    }
}
