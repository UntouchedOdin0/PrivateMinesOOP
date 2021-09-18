package me.untouchedodin0.privatemines.storage;

import me.untouchedodin0.privatemines.mines.MineData;

import java.util.HashMap;
import java.util.Map;

public class MineStorage {

    private final Map<String, MineData> mines = new HashMap<>();

    public void addMine(String string, MineData mineData) {
        mines.putIfAbsent(string, mineData);
    }

    public void removeMine(MineData mineData) {
        if (!mines.containsValue(mineData)) return;
        mines.remove(mineData.getName());
    }

    public Map<String, MineData> getMines() {
        return mines;
    }
}
