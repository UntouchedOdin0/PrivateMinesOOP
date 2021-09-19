package me.untouchedodin0.privatemines.storage;

import me.untouchedodin0.privatemines.mines.MineData;

import java.util.HashMap;
import java.util.Map;

public class MineStorage {

    private final Map<String, MineData> mineDataMap = new HashMap<>();

    public void addMineData(String string, MineData mineData) {
        mineDataMap.putIfAbsent(string, mineData);
    }

    public void removeMineData(MineData mineData) {
        if (!mineDataMap.containsValue(mineData)) return;
        mineDataMap.remove(mineData.getName());
    }

    public Map<String, MineData> getMineDataMap() {
        return mineDataMap;
    }
}
