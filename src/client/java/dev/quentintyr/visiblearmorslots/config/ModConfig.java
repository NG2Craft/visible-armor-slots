package dev.quentintyr.visiblearmorslots.config;

/**
 * Configuration options for the mod
 */
public class ModConfig {
    
    public enum Side {
        LEFT, RIGHT
    }
    
    private Side positioning = Side.LEFT;
    private int marginX = 4;
    private int marginY = 0;
    private boolean enabled = true;
    private boolean showTooltips = true;
    private boolean autoPositioning = true; // Auto-reposition with potion effects
    
    // Singleton pattern
    private static ModConfig instance;
    
    public static ModConfig getInstance() {
        if (instance == null) {
            instance = new ModConfig();
        }
        return instance;
    }
    
    private ModConfig() {}
    
    // Getters
    public Side getPositioning() {
        return positioning;
    }
    
    public int getMarginX() {
        return marginX;
    }
    
    public int getMarginY() {
        return marginY;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean shouldShowTooltips() {
        return showTooltips;
    }
    
    public boolean isAutoPositioning() {
        return autoPositioning;
    }
    
    // Setters
    public void setPositioning(Side positioning) {
        this.positioning = positioning;
    }
    
    public void setMarginX(int marginX) {
        this.marginX = Math.max(0, Math.min(128, marginX));
    }
    
    public void setMarginY(int marginY) {
        this.marginY = Math.max(-64, Math.min(64, marginY));
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setShowTooltips(boolean showTooltips) {
        this.showTooltips = showTooltips;
    }
    
    public void setAutoPositioning(boolean autoPositioning) {
        this.autoPositioning = autoPositioning;
    }
}
