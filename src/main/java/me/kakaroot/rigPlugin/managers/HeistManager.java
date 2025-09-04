package me.kakaroot.rigPlugin.managers;

import me.kakaroot.rigPlugin.RigPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class HeistManager {

    private final RigPlugin plugin;
    private final Random random = new Random();

    public HeistManager(RigPlugin plugin) {
        this.plugin = plugin;
    }

    public void startHeist(String rigName) {
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

            spawnGuards(chestLoc, guardCount, guardRadius,rigName);
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
                        System.out.println("Invalid material: " + matName);
                        continue;
                    }

                    ItemStack item = new ItemStack(mat, amount);
                    inv.addItem(item);
                } catch (Exception e) {
                    System.out.println("Error parsing loot string: " + s);
                    e.printStackTrace();
                }
            }
        });
    }


    private void spawnGuards(Location center, int count, int radius, String rigName) {
        World world = center.getWorld();
        List<Map<?, ?>> templates = plugin.getRigManager().getGuardTemplates(rigName);

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
            if (!type.isAlive()) type = EntityType.ZOMBIE; // fallback

            LivingEntity guard = (LivingEntity) world.spawnEntity(spawnLoc, type);
            guard.setCustomName(name);
            guard.setCustomNameVisible(true);

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

}

