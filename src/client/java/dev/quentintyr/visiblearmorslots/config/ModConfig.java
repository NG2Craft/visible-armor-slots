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
import java.util.*;

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
    // Raw values exactly as written in the JSON (block ids or handler ids)
    private Set<String> allowedContainers = new HashSet<>();
    // Expanded set of actual screen handler ids derived from user friendly entries
    private final Set<String> expandedAllowed = new HashSet<>();

    // Mapping of common block ids -> one or more screen handler registry ids
    private static final Map<String, List<String>> BLOCK_TO_HANDLERS = createBlockToHandlerMappings();

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
        // User-friendly defaults (block names) instead of opaque screen handler ids
        Collections.addAll(allowedContainers,
                "minecraft:crafting_table",
                "minecraft:anvil",
                "minecraft:enchanting_table",
                "minecraft:chest",
                "minecraft:trapped_chest",
                "minecraft:barrel",
                "minecraft:grindstone",
                "minecraft:smithing_table",
                "minecraft:shulker_box");
        rebuildExpanded();
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
        // Direct match (user already put handler id) or expanded match
        return allowedContainers.contains(id.toString()) || expandedAllowed.contains(id.toString());
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
            cfg.rebuildExpanded();
        } catch (IOException e) {
            dev.quentintyr.visiblearmorslots.Visiblearmorslots.LOGGER.warn("Failed to load config file, using defaults: {}", e.getMessage());
        } catch (Exception e) {
            dev.quentintyr.visiblearmorslots.Visiblearmorslots.LOGGER.error("Error parsing config file, using defaults", e);
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
        } catch (IOException e) {
            dev.quentintyr.visiblearmorslots.Visiblearmorslots.LOGGER.error("Failed to save config file: {}", e.getMessage());
        }
    }

    private static Map<String, List<String>> createBlockToHandlerMappings() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("minecraft:crafting_table", List.of("minecraft:crafting"));
        map.put("minecraft:enchanting_table", List.of("minecraft:enchantment"));
        map.put("minecraft:smithing_table", List.of("minecraft:smithing"));
        map.put("minecraft:cartography_table", List.of("minecraft:cartography"));
        map.put("minecraft:stonecutter", List.of("minecraft:stonecutter"));
        map.put("minecraft:grindstone", List.of("minecraft:grindstone"));
        map.put("minecraft:loom", List.of("minecraft:loom"));
        map.put("minecraft:brewing_stand", List.of("minecraft:brewing_stand"));
        map.put("minecraft:hopper", List.of("minecraft:hopper"));
        map.put("minecraft:furnace", List.of("minecraft:furnace"));
        map.put("minecraft:blast_furnace", List.of("minecraft:blast_furnace"));
        map.put("minecraft:smoker", List.of("minecraft:smoker"));
        map.put("minecraft:anvil", List.of("minecraft:anvil"));
        map.put("minecraft:shulker_box", List.of("minecraft:shulker_box"));
        // Chest-like legacy names -> multiple generic sizes
        map.put("minecraft:chest", List.of("minecraft:generic_9x3", "minecraft:generic_9x6"));
        map.put("minecraft:trapped_chest", List.of("minecraft:generic_9x3", "minecraft:generic_9x6"));
        map.put("minecraft:barrel", List.of("minecraft:generic_9x3"));
        return map;
    }

    private void rebuildExpanded() {
        expandedAllowed.clear();
        for (String raw : allowedContainers) {
            // Always allow the raw string itself (covers direct handler ids and modded
            // ones)
            expandedAllowed.add(raw);
            List<String> mapped = BLOCK_TO_HANDLERS.get(raw);
            if (mapped != null) {
                expandedAllowed.addAll(mapped);
            }
        }
    }
}
