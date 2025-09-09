package dev.quentintyr.visiblearmorslots.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration options for the mod (JSON-backed)
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
    private boolean showOffhandSlot = true; // New: toggle offhand slot
    private Set<String> allowedContainers = new HashSet<>(); // registry ids of container screen handlers

    private static final String FILE_NAME = "visiblearmorslots.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Singleton
    private static ModConfig instance;

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = new ModConfig();
        }
        return instance;
    }

    private ModConfig() {
        // Provide some sensible defaults for allowed containers (player can edit)
        Collections.addAll(allowedContainers,
                "minecraft:crafting_table",
                "minecraft:anvil",
                "minecraft:enchanting_table",
                "minecraft:chest",
                "minecraft:barrel",
                "minecraft:trapped_chest",
                "minecraft:furnace",
                "minecraft:blast_furnace",
                "minecraft:smoker",
                "minecraft:grindstone",
                "minecraft:cartography_table",
                "minecraft:loom",
                "minecraft:smithing_table",
                "minecraft:stonecutter",
                "minecraft:hopper",
                "minecraft:brewing_stand",
                "minecraft:shulker_box");
    }

    /* ================= Getters ================= */
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

    public boolean isShowOffhandSlot() {
        return showOffhandSlot;
    }

    public Set<String> getAllowedContainers() {
        return Collections.unmodifiableSet(allowedContainers);
    }

    /* ================= Setters ================= */
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

    public void setShowOffhandSlot(boolean showOffhandSlot) {
        this.showOffhandSlot = showOffhandSlot;
    }

    public boolean isContainerAllowed(Identifier id) {
        if (allowedContainers.isEmpty())
            return true; // empty means allow all
        return allowedContainers.contains(id.toString());
    }

    /* ================= Persistence ================= */
    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        if (!Files.exists(path)) {
            // Save defaults
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null)
                return;
            ModConfig cfg = getInstance();
            if (root.has("enabled"))
                cfg.enabled = root.get("enabled").getAsBoolean();
            if (root.has("showOffhandSlot"))
                cfg.showOffhandSlot = root.get("showOffhandSlot").getAsBoolean();
            if (root.has("showTooltips"))
                cfg.showTooltips = root.get("showTooltips").getAsBoolean();
            if (root.has("autoPositioning"))
                cfg.autoPositioning = root.get("autoPositioning").getAsBoolean();
            if (root.has("positioning")) {
                try {
                    cfg.positioning = Side.valueOf(root.get("positioning").getAsString().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (root.has("marginX"))
                cfg.marginX = root.get("marginX").getAsInt();
            if (root.has("marginY"))
                cfg.marginY = root.get("marginY").getAsInt();
            if (root.has("allowedContainers")) {
                cfg.allowedContainers.clear();
                JsonArray arr = root.getAsJsonArray("allowedContainers");
                arr.forEach(e -> cfg.allowedContainers.add(e.getAsString()));
            }
        } catch (IOException e) {
            // ignore: keep defaults if read fails
        }
    }

    public static void save() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        JsonObject root = new JsonObject();
        ModConfig cfg = getInstance();
        root.addProperty("enabled", cfg.enabled);
        root.addProperty("showOffhandSlot", cfg.showOffhandSlot);
        root.addProperty("showTooltips", cfg.showTooltips);
        root.addProperty("autoPositioning", cfg.autoPositioning);
        root.addProperty("positioning", cfg.positioning.name());
        root.addProperty("marginX", cfg.marginX);
        root.addProperty("marginY", cfg.marginY);
        JsonArray arr = new JsonArray();
        cfg.allowedContainers.forEach(arr::add);
        root.add("allowedContainers", arr);
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(root, writer);
        } catch (IOException ignored) {
        }
    }
}
