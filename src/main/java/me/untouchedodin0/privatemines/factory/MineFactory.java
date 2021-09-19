package me.untouchedodin0.privatemines.factory;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.storage.MineStorage;

public class MineFactory {

    PrivateMines privateMines;
    MineStorage mineStorage;

    public MineFactory(PrivateMines privateMines,
                       MineStorage mineStorage) {
        this.privateMines = privateMines;
        this.mineStorage = mineStorage;
    }
}
