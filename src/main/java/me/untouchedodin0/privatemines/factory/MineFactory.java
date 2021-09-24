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

    /**
     * @param player   - The target player to be given a mine
     * @param location - The spigot world location where to create the mine
     * @param mineData - The mine data such as the MultiBlockStructure and the Materials
     * @return The newly created mine
     */

    public Mine createMine(Player player, Location location, MineData mineData) {
        Mine mine = new Mine();
        mine.setMineOwner(player.getUniqueId());
        mine.setMineLocation(location);
        mine.setMineData(mineData);
        mine.build();
        mineStorage.addMine(player.getUniqueId(), mine);
        return mine;
    }
}
