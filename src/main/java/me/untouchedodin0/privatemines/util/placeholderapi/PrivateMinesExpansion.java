package me.untouchedodin0.privatemines.util.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.untouchedodin0.privatemines.mines.Mine;
import me.untouchedodin0.privatemines.storage.MineStorage;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PrivateMinesExpansion extends PlaceholderExpansion {

    private final MineStorage mineStorage;
    private final Map<String, String> placeholders = new HashMap<>();

    public PrivateMinesExpansion(MineStorage mineStorage) {
        this.mineStorage = mineStorage;
    }

    /**
     * The placeholder identifier of this expansion. May not contain {@literal %},
     * {@literal {}} or _
     *
     * @return placeholder identifier that is associated with this expansion
     */

    @Override
    public @NotNull String getIdentifier() {
        return "privatemines";
    }

    /**
     * The author of this expansion
     *
     * @return name of the author for this expansion
     */

    @Override
    public @NotNull String getAuthor() {
        return "UntouchedOdin0";
    }

    /**
     * The version of this expansion
     *
     * @return current version of this expansion
     */
    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    /*
        This is required or else PlaceholderAPI will unregister the Expansion on reload,
        please don't reload..... it's not good for the server
    */

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String placeholder) {
        Mine mine = mineStorage.getMine(offlinePlayer.getUniqueId());
        boolean hasMine = mineStorage.hasMine(offlinePlayer.getUniqueId());
        placeholders.put("owner", String.valueOf(hasMine));
        placeholders.put("structure", mine.getStructure().toString());
        return placeholders.get(placeholder);
    }
}
