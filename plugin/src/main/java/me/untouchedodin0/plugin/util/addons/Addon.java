package me.untouchedodin0.plugin.util.addons;

import java.io.File;

public abstract class Addon {

    private static final String ADDON_CONFIG_FILENAME = "config.yml";
    private State state;
    private File file;

    protected Addon() {
        state = State.DISABLED;
    }

    /**
     * Executes the code when enabling the addon.
     */

    public abstract void onEnable();

    /**
     * Executes the code when disabling the addon
     */

    public abstract void onDisable();

    /**
     * Represents the current run-time state of a {@link Addon}.
     *
     * @author Poslovitch
     */
    public enum State {

        /**
         * The addon has been correctly loaded.
         */
        LOADED,

        /**
         * The addon has been correctly enabled and is now fully working.
         */
        ENABLED,

        /**
         * The addon is fully disabled.
         */
        DISABLED,

        /**
         * The addon loading or enabling process has been interrupted by an unhandled error.
         */
        ERROR
    }
}
