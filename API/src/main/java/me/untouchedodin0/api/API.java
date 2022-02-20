package me.untouchedodin0.api;

import lombok.Getter;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.PrivateMinesAPI;
import me.untouchedodin0.plugin.factory.MineFactory;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.storage.MineStorage;

import java.util.UUID;

@Getter
public class API {

    API api = new API();

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    PrivateMinesAPI privateMinesAPI = PrivateMines.getAPI();

    MineFactory mineFactory = privateMinesAPI.getMineFactory();
    MineStorage mineStorage = privateMinesAPI.getMineStorage();

    public Mine getMine(UUID uuid) {
        return mineStorage.getMine(uuid);
    }

    public boolean hasMine(UUID uuid) {
        return mineStorage.hasMine(uuid);
    }
}
