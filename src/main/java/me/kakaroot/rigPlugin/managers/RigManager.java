package me.kakaroot.rigPlugin.managers;

import me.kakaroot.rigPlugin.RigPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RigManager {

    private final Map<UUID, List<Location>> chestSelections = new HashMap<>();
    private final File rigsFile;
    private final YamlConfiguration rigsConfig;

    public RigManager(RigPlugin plugin) {
        rigsFile = new File(plugin.getDataFolder(), "rigs.yml");
        if (!rigsFile.exists()) plugin.saveResource("rigs.yml", false);
        rigsConfig = YamlConfiguration.loadConfiguration(rigsFile);
    }

    // Temporary selection for admins
    public void addChest(UUID playerId, Location loc) {
        chestSelections.computeIfAbsent(playerId, k -> new ArrayList<>()).add(loc);
    }

    public int getChestCount(UUID playerId) {
        return chestSelections.getOrDefault(playerId, Collections.emptyList()).size();
    }

    // In saveRig(), add a default frequency if not set yet
    private void setDefaultFrequencyIfMissing(String rigName) {
        if (rigsConfig.get("rigs." + rigName + ".frequency") == null) {
            // Example default: 60 minutes
            rigsConfig.set("rigs." + rigName + ".frequency", 60);
        }
    }

    // Call it in saveRig
    public void saveRig(UUID playerId, String rigName) {
        List<Location> chests = chestSelections.getOrDefault(playerId, Collections.emptyList());
        List<String> chestStrings = new ArrayList<>();
        for (Location loc : chests) chestStrings.add(serialize(loc));

        rigsConfig.set("rigs." + rigName + ".chests", chestStrings);
        setDefaultGuardsIfMissing(rigName);
        setDefaultLootIfMissing(rigName);
        setDefaultFrequencyIfMissing(rigName);

        saveConfig();
        chestSelections.remove(playerId);
    }

    // Getter for frequency
    public int getFrequency(String rigName) {
        return rigsConfig.getInt("rigs." + rigName + ".frequency", 60); // default 60 if missing
    }

    // Setter to change frequency later
    public void setFrequency(String rigName, int minutes) {
        rigsConfig.set("rigs." + rigName + ".frequency", minutes);
        saveConfig();
    }

    public long getLastStart(String rigName) {
        return rigsConfig.getLong("rigs." + rigName + ".lastStart", 0L);
    }

    public void updateLastStart(String rigName) {
        rigsConfig.set("rigs." + rigName + ".lastStart", System.currentTimeMillis());
        saveConfig();
    }


    private void setDefaultGuardsIfMissing(String rigName) {
        if (rigsConfig.getConfigurationSection("rigs." + rigName + ".guards") == null) {
            rigsConfig.set("rigs." + rigName + ".guards.count", 3);
            rigsConfig.set("rigs." + rigName + ".guards.radius", 5);
        }
    }

    private void setDefaultLootIfMissing(String rigName) {
        if (rigsConfig.getConfigurationSection("rigs." + rigName + ".loot") == null) {
            rigsConfig.set("rigs." + rigName + ".loot.common", Arrays.asList("Iron_Ingot:5-10", "Bread:3-5"));
            rigsConfig.set("rigs." + rigName + ".loot.rare", Arrays.asList("Diamond:1-2"));
        }
    }

    private void saveConfig() {
        try {
            rigsConfig.save(rigsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get chest locations for a rig
    public List<Location> getChests(String rigName) {
        List<String> list = rigsConfig.getStringList("rigs." + rigName + ".chests");
        List<Location> locs = new ArrayList<>();
        for (String s : list) locs.add(deserialize(s));
        return locs;
    }

    public int getGuardCount(String rigName) {
        return rigsConfig.getInt("rigs." + rigName + ".guards.count", 3);
    }

    public int getGuardRadius(String rigName) {
        return rigsConfig.getInt("rigs." + rigName + ".guards.radius", 5);
    }

    public Map<String, List<String>> getLoot(String rigName) {
        Map<String, List<String>> loot = new HashMap<>();
        if (rigsConfig.getConfigurationSection("rigs." + rigName + ".loot") != null) {
            for (String key : rigsConfig.getConfigurationSection("rigs." + rigName + ".loot").getKeys(false)) {
                loot.put(key, rigsConfig.getStringList("rigs." + rigName + ".loot." + key));
            }
        }
        return loot;
    }

    private String serialize(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public Location deserialize(String s) {
        String[] parts = s.split(",");
        return new Location(
                Bukkit.getWorld(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3])
        );
    }

    public Set<String> getAllRigNames() {
        if (rigsConfig.getConfigurationSection("rigs") == null) {
            return Collections.emptySet();
        }
        return rigsConfig.getConfigurationSection("rigs").getKeys(false);
    }

    public void deleteRig(String rigName) {
        if (rigsConfig.getConfigurationSection("rigs." + rigName) != null) {
            rigsConfig.set("rigs." + rigName, null);
            saveConfig();
        }
    }

    public void updateGuardTemplates(String rigName, List<Map<?, ?>> newTemplates) {
        rigsConfig.set("rigs." + rigName + ".guards.templates", newTemplates);
        saveConfig();
    }

    public void addLoot(String rigName, String rarity, String entry) {
        List<String> list = rigsConfig.getStringList("rigs." + rigName + ".loot." + rarity);
        list.add(entry);
        rigsConfig.set("rigs." + rigName + ".loot." + rarity, list);
        saveConfig();
    }

    public void removeLoot(String rigName, String rarity, String entry) {
        List<String> list = rigsConfig.getStringList("rigs." + rigName + ".loot." + rarity);
        list.remove(entry);
        rigsConfig.set("rigs." + rigName + ".loot." + rarity, list);
        saveConfig();
    }

    public void updateLoot(String rigName, String rarity, List<String> newEntries) {
        rigsConfig.set("rigs." + rigName + ".loot." + rarity, newEntries);
        saveConfig();
    }

    public List<Map<?, ?>> getGuardTemplates(String rigName) {
        List<Map<?, ?>> list = new ArrayList<>();
        if (rigsConfig.contains("rigs." + rigName + ".guards.templates")) {
            list = rigsConfig.getMapList("rigs." + rigName + ".guards.templates");
        } else {
            // default single template
            Map<String, Object> defaultTemplate = new HashMap<>();
            defaultTemplate.put("type", "ZOMBIE");
            defaultTemplate.put("name", "Guard");
            defaultTemplate.put("armour", Arrays.asList("LEATHER_HELMET", "LEATHER_CHESTPLATE"));
            defaultTemplate.put("weapon", "IRON_SWORD");
            list.add(defaultTemplate);

            // save defaults to config
            rigsConfig.set("rigs." + rigName + ".guards.templates", list);
            saveConfig();
        }
        return list;
    }

    // Update a single guard template
    public void updateGuardTemplate(String rigName, int index, Map<String, Object> newTemplate) {
        List<Map<String, Object>> templates = new ArrayList<>();

        // Convert existing templates to proper type
        for (Map<?, ?> m : getGuardTemplates(rigName)) {
            templates.add(new HashMap<>((Map<String, Object>) m));
        }

        if (index >= 0 && index < templates.size()) {
            templates.set(index, newTemplate);
            rigsConfig.set("rigs." + rigName + ".guards.templates", templates);
            saveConfig();
        }
    }

    // Remove a single guard template
    public void removeGuardTemplate(String rigName, int index) {
        List<Map<String, Object>> templates = new ArrayList<>();
        for (Map<?, ?> m : getGuardTemplates(rigName)) {
            templates.add(new HashMap<>((Map<String, Object>) m));
        }

        if (index >= 0 && index < templates.size()) {
            templates.remove(index);
            rigsConfig.set("rigs." + rigName + ".guards.templates", templates);
            saveConfig();
        }
    }




}
