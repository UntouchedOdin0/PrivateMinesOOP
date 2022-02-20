package me.untouchedodin0.api.example;

import me.untouchedodin0.api.API;
import me.untouchedodin0.plugin.mines.Mine;

import java.util.UUID;

@SuppressWarnings("unused")
public class TestExample {

    API api = new API();

    public Mine getMine(UUID uuid) {
        return api.getMine(uuid);
    }

    public boolean hasMine(UUID uuid) {
        return api.hasMine(uuid);
    }

    public int getLoadedMines() {
        return api.getTotalMines();
    }
}
