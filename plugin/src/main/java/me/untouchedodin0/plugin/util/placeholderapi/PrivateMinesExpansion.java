package me.untouchedodin0.plugin.util.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PrivateMinesExpansion extends PlaceholderExpansion {

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
        try {
            switch (identifier) {
                case "1":
                    return "yes";
                case "2":
                    return "no";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "NOT_WORKING";
        }
        return identifier;
    }
}
