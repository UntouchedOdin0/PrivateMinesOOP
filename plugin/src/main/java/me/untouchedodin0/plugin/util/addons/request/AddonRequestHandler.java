package me.untouchedodin0.plugin.util.addons.request;

import java.util.Locale;

public class AddonRequestHandler {

    private final String label;

    protected AddonRequestHandler(String label) {
        this.label = label.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Get request handler label
     *
     * @return label
     */

    public String getLabel() {
        return label;
    }
}
