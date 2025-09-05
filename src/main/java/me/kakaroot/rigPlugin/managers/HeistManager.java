package me.kakaroot.rigPlugin.managers;

import me.kakaroot.rigPlugin.RigPlugin;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HeistManager {

    private final RigPlugin plugin;
    private final Random random = new Random();
    private final NamespacedKey guardKey;

    public HeistManager(RigPlugin plugin) {
        this.plugin = plugin;
        this.guardKey = new NamespacedKey(plugin, "rig_guard");
    }

    public void startGuardWatcher() {
        int taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            boolean guardsStay = plugin.getConfig().getBoolean("heists.guards-stay-at-rig", true);
            if (!guardsStay) return;

            double guardRadius = plugin.getConfig().getDouble("heists.guard-radius", 10.0);
            double moveSpeed = 0.5;

            for (World world : Bukkit.getWorlds()) {
                for (LivingEntity entity : world.getLivingEntities()) {
                    String rigName = entity.getPersistentDataContainer().get(guardKey, PersistentDataType.STRING);
                    if (rigName == null) continue;

                    List<Location> chestLocations = plugin.getRigManager().getChests(rigName);
                    if (chestLocations.isEmpty()) continue;

                    Location guardLoc = entity.getLocation();
                    Location nearestChest = chestLocations.get(0);
                    double nearestDistanceSq = guardLoc.distanceSquared(nearestChest);

                    for (Location chestLoc : chestLocations) {
                        double distSq = guardLoc.distanceSquared(chestLoc);
                        if (distSq < nearestDistanceSq) {
                            nearestChest = chestLoc;
                            nearestDistanceSq = distSq;
                        }
                    }

                    if (guardLoc.distance(nearestChest) > guardRadius) {
                        @NotNull Vector direction = nearestChest.toVector().subtract(guardLoc.toVector()).normalize();
                        entity.setVelocity(direction.multiply(moveSpeed));
                    }
                }
            }
        }, 0L, 2L).getTaskId();
    }
    public void startHeist(String rigName) {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int minPlayers = plugin.getConfig().getInt("heists.min-players", 1);
        boolean respawnGuards = plugin.getConfig().getBoolean("respawn-guards", true);

        if (onlinePlayers < minPlayers) {
            plugin.getLogger().info("Not enough players to start heist for rig: " + rigName);
            return;
        }

        List<Location> chests = plugin.getRigManager().getChests(rigName);
        int guardCount = plugin.getRigManager().getGuardCount(rigName);
        int guardRadius = plugin.getRigManager().getGuardRadius(rigName);
        Map<String, List<String>> lootTable = plugin.getRigManager().getLoot(rigName);

        for (Location chestLoc : chests) {
            BlockFace facing = BlockFace.NORTH;
            if (chestLoc.getBlock().getBlockData() instanceof Directional directional) {
                facing = directional.getFacing();
            }

            chestLoc.getBlock().setType(Material.CHEST);
            if (chestLoc.getBlock().getBlockData() instanceof Directional newDirectional) {
                newDirectional.setFacing(facing);
                chestLoc.getBlock().setBlockData(newDirectional);
            }

            Chest chest = (Chest) chestLoc.getBlock().getState();
            Inventory inv = chest.getBlockInventory();
            inv.clear();
            populateLoot(inv, lootTable);

            if (respawnGuards) {
                removeGuards(rigName);
            }

            spawnGuards(chestLoc, guardCount, guardRadius, rigName);
        }

        Bukkit.broadcastMessage(MsgManager.colourise(
                MsgManager.prefix + "&6Rig: &a" + rigName + "&6 has started."
        ));
    }

    private void removeGuards(String rigName) {
        for (World world : Bukkit.getWorlds()) {
            List<LivingEntity> toRemove = new ArrayList<>();

            for (LivingEntity entity : world.getLivingEntities()) {
                String tag = entity.getPersistentDataContainer().get(guardKey, PersistentDataType.STRING);
                if (rigName.equals(tag)) {
                    toRemove.add(entity);
                }
            }

            for (LivingEntity guard : toRemove) {
                plugin.getLogger().info("Removing guard for rig: " + rigName + " at " + guard.getLocation());
                guard.remove();
            }
        }
    }

    private void spawnGuards(Location center, int count, int radius, String rigName) {
        World world = center.getWorld();
        if (world == null) return;

        List<Map<String, Object>> templates = plugin.getRigManager().getGuardTemplates(rigName);

        for (int i = 0; i < count; i++) {
            double dx = (random.nextDouble() * 2 - 1) * radius;
            double dz = (random.nextDouble() * 2 - 1) * radius;
            Location spawnLoc = center.clone().add(dx, 0, dz);

            Map<?, ?> rawTemplate = templates.get(random.nextInt(templates.size()));
            Map<String, Object> template = (Map<String, Object>) rawTemplate;

            String typeStr = (String) template.getOrDefault("type", "ZOMBIE");
            String name = (String) template.getOrDefault("name", "Guard");
            List<String> armourList = (List<String>) template.getOrDefault("armour", Collections.emptyList());
            String weaponStr = (String) template.getOrDefault("weapon", null);

            EntityType type = EntityType.valueOf(typeStr.toUpperCase());
            if (!type.isAlive()) type = EntityType.ZOMBIE;

            LivingEntity guard = (LivingEntity) world.spawnEntity(spawnLoc, type);
            guard.setCustomName(name);
            guard.setCustomNameVisible(true);

            // Tag the entity
            guard.getPersistentDataContainer().set(guardKey, PersistentDataType.STRING, rigName);

            plugin.getLogger().info("Spawned guard for rig: " + rigName + " at " + guard.getLocation() + " with tag " + guard.getPersistentDataContainer().get(guardKey, PersistentDataType.STRING));

            // Equip armour
            for (String armourMat : armourList) {
                Material mat = Material.getMaterial(armourMat.toUpperCase());
                if (mat == null) continue;

                switch (mat) {
                    case LEATHER_HELMET, IRON_HELMET, DIAMOND_HELMET, NETHERITE_HELMET -> guard.getEquipment().setHelmet(new ItemStack(mat));
                    case LEATHER_CHESTPLATE, IRON_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE -> guard.getEquipment().setChestplate(new ItemStack(mat));
                    case LEATHER_LEGGINGS, IRON_LEGGINGS, DIAMOND_LEGGINGS, NETHERITE_LEGGINGS -> guard.getEquipment().setLeggings(new ItemStack(mat));
                    case LEATHER_BOOTS, IRON_BOOTS, DIAMOND_BOOTS, NETHERITE_BOOTS -> guard.getEquipment().setBoots(new ItemStack(mat));
                }
            }

            if (weaponStr != null) {
                Material weapon = Material.getMaterial(weaponStr.toUpperCase());
                if (weapon != null) guard.getEquipment().setItemInMainHand(new ItemStack(weapon));
            }
        }
    }

    private void populateLoot(Inventory inv, Map<String, List<String>> lootTable) {
        lootTable.forEach((rarity, items) -> {
            for (String s : items) {
                try {
                    String[] parts = s.split(":");
                    String matName = parts[0];
                    int min = Integer.parseInt(parts[1].split("-")[0]);
                    int max = parts[1].contains("-") ? Integer.parseInt(parts[1].split("-")[1]) : min;
                    int amount = random.nextInt(max - min + 1) + min;

                    Material mat = Material.getMaterial(matName.toUpperCase());
                    if (mat == null) {
                        plugin.getLogger().warning("Invalid material: " + matName);
                        continue;
                    }

                    inv.addItem(new ItemStack(mat, amount));
                } catch (Exception e) {
                    plugin.getLogger().warning("Error parsing loot string: " + s);
                    e.printStackTrace();
                }
            }
        });
    }
}