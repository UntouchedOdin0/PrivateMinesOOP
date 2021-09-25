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
}
