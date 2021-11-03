package me.untouchedodin0.plugin.util.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.untouchedodin0.plugin.PrivateMines;
import me.untouchedodin0.plugin.mines.Mine;
import me.untouchedodin0.plugin.storage.MineStorage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PrivateMinesExpansion extends PlaceholderExpansion {

    PrivateMines privateMines;
    MineStorage mineStorage;
    Mine mine;
    UUID uuid;

    @Override
    public @org.jetbrains.annotations.NotNull String getAuthor() {
        return "UntouchedOdin0";
    }

    @Override
    public @NotNull
    String getName() {
        return "PrivateMines";
    }

    @Override
    public @NotNull
    String getIdentifier() {
        return "PrivateMines";
    }


    @Override
    public @org.jetbrains.annotations.NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(final OfflinePlayer offlinePlayer, @NotNull String identifier) {
        Player player = offlinePlayer.getPlayer();

        if (player != null) {
            uuid = player.getUniqueId();
        }

        this.privateMines = PrivateMines.getPrivateMines();
        this.mineStorage = privateMines.getMineStorage();
        if (mineStorage.hasMine(uuid)) {
            mine = mineStorage.getMine(uuid);

            try {
                switch (identifier) {
                    case "type":
                        return mine.getMineType().getName();
                    case "hasMine":
                        return String.valueOf(mineStorage.hasMine(uuid));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "NOT_WORKING";
            }
        }
        return identifier;
    }
}
