package me.untouchedodin0.privatemines.storage;

import me.untouchedodin0.privatemines.mines.MineData;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class MineStorage {

    private final Map<String, MineData> mineDataMap = new HashMap<>();

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
}
