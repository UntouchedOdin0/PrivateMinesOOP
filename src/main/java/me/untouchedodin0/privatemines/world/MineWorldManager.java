package me.untouchedodin0.privatemines.world;

import org.bukkit.*;
import static me.untouchedodin0.privatemines.world.utils.Direction.NORTH;

public class MineWorldManager {

    private final World minesWorld;
    private final Location defaultLocation;
    private final int borderDistance;
    private int distance = 0;
    private me.untouchedodin0.privatemines.world.utils.Direction direction;

    public MineWorldManager() {
        this.minesWorld = Bukkit.createWorld(
                new WorldCreator("privatemines")
                .type(WorldType.FLAT)
                .generator(new EmptyWorldGenerator()));
        this.borderDistance = 150;
        this.direction = NORTH;
        defaultLocation = new Location(minesWorld, 0, 1, 0); // may need to raise the Y sometime?
    }

    public World getMinesWorld() {
        return minesWorld;
    }

    public synchronized Location getNextFreeLocation() {
        if (distance == 0) {
            distance++;
            return defaultLocation;
        }

        if (direction == null) direction = NORTH;
        Location location = direction.addTo(defaultLocation, distance * borderDistance);
        direction = direction.next();
        if (direction == NORTH) distance++;
        return location;
    }
}
