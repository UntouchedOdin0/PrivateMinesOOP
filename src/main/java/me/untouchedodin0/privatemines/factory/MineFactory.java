package me.untouchedodin0.privatemines.factory;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mines.Mine;
import me.untouchedodin0.privatemines.mines.MineData;
import me.untouchedodin0.privatemines.storage.MineStorage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MineFactory {

    PrivateMines privateMines;
    MineStorage mineStorage;

    public MineFactory(PrivateMines privateMines,
                       MineStorage mineStorage) {
        this.privateMines = privateMines;
        this.mineStorage = mineStorage;
    }

    public Mine createMine(Player player, Location location, MineData mineData) {
        Mine mine = new Mine();
        mine.setMineOwner(player.getUniqueId());
        mine.setMineLocation(location);
        mine.setMineData(mineData);
        return mine;
    }
}
