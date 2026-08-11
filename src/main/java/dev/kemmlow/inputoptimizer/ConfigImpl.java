package dev.kemmlow.inputoptimizer;

public class ConfigImpl {
    private boolean enabled = true;
    private boolean rawInputEnabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isRawInputEnabled() {
        return rawInputEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRawInputEnabled(boolean rawInputEnabled) {
        this.rawInputEnabled = rawInputEnabled;
    }
}
