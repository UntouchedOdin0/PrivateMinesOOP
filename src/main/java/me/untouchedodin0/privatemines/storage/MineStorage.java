package me.untouchedodin0.privatemines.storage;

import me.untouchedodin0.privatemines.mines.Mine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MineStorage {

    private final Map<UUID, Mine> mines = new HashMap<>();

    public void addMine(UUID uuid, Mine mine) {
        mines.putIfAbsent(uuid, mine);
    }

    public void removeMine(UUID uuid) {
        mines.remove(uuid);
    }

    public Map<UUID, Mine> getMines() {
        return mines;
    }

    /*
    public void addMineData(String string, MineData mineData) {
        mineDataMap.putIfAbsent(string, mineData);
    }

    public void removeMineData(MineData mineData) {
        if (!mineDataMap.containsValue(mineData)) return;
        Bukkit.getLogger().info("Removing mineData " + mineData.getName() + " from mine storage");
        mineDataMap.remove(mineData.getName());
    }

    public Map<String, MineData> getMineDataMap() {
        return mineDataMap;
    }
    */
}
