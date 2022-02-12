package me.untouchedodin0.plugin.mines;

import me.untouchedodin0.plugin.PrivateMines;

import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

public class MineTypeManager {
    private final NavigableMap<String, MineType> mineTypes = new TreeMap<>();
    private final PrivateMines privateMines;

    public MineTypeManager(PrivateMines privateMines) {
        this.privateMines = privateMines;
    }

    public void registerMineType(MineType mineType) {
        mineTypes.put(mineType.getName(), mineType);
    }

    public MineType getMineType(String name) {
        return mineTypes.get(name);
    }

    public MineType getDefaultMineType() {
        if (mineTypes.isEmpty()) {
            privateMines.getLogger().info("No default world edit mine type was found!\n" +
                                          "Create a mine type in the mineTypes section of the config.yml" +
                                          "Please ask in the discord server if you need help");
            throw new RuntimeException();
        }
        return mineTypes.firstEntry().getValue();
    }

    public void clear() {
        mineTypes.clear();
    }

    public boolean isLastMineType(MineType mineType) {
        return mineTypes.lastEntry().getValue().equals(mineType);
    }

    public MineType getNextMineType(MineType mineType) {
        return Optional.ofNullable(mineTypes.higherEntry(mineType.getName()))
                .orElse(mineTypes.lastEntry())
                .getValue();
    }

    public NavigableMap<String, MineType> getMineTypes() {
        return mineTypes;
    }
}
