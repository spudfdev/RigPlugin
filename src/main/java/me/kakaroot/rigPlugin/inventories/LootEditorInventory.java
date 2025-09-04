package me.kakaroot.rigPlugin.inventories;

import me.kakaroot.rigPlugin.managers.GUIManager;
import me.kakaroot.rigPlugin.managers.MsgManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LootEditorInventory {

    public static void open(Player player, String rigName, RigManager rigManager) {
        UUID uuid = player.getUniqueId();
        GUIManager gui = new GUIManager("Edit Loot for " + rigName, 6, uuid);

        Map<String, List<String>> loot = rigManager.getLoot(rigName);

        int slot = 0;
        for (Map.Entry<String, List<String>> entry : loot.entrySet()) {
            String rarity = entry.getKey();
            for (String lootString : entry.getValue()) {
                String[] parts = lootString.split(":");
                String matName = parts[0];
                String range = parts.length > 1 ? parts[1] : "1";

                Material mat = Material.getMaterial(matName.toUpperCase());
                if (mat == null) continue;

                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§e" + matName + " §7(" + rarity + ")");
                    meta.setLore(Arrays.asList(
                            "§7Range: " + range,
                            "§eLeft-click: Increase min",
                            "§eRight-click: Decrease min",
                            "§eShift-left: Increase max",
                            "§eShift-right: Decrease max",
                            "§cDrop: Remove item"
                    ));
                    item.setItemMeta(meta);
                }

                gui.addItem(slot, item);

                final String currentEntry = lootString;

                // Set click actions for editing/removing
                gui.setClickAction(slot, (p, e) -> {
                    int min, max;
                    try {
                        if (range.contains("-")) {
                            String[] r = range.split("-");
                            min = Integer.parseInt(r[0]);
                            max = Integer.parseInt(r[1]);
                        } else {
                            min = max = Integer.parseInt(range);
                        }
                    } catch (Exception ex) {
                        min = 1; max = 1;
                    }

                    boolean updated = false;

                    if (e.getClick() == ClickType.LEFT) {
                        if (min < max) { // only allow increasing if min < max
                            min++;
                            updated = true;
                        }
                    } else if (e.getClick() == ClickType.RIGHT && min > 1) {
                        min--;
                        updated = true;
                    } else if (e.getClick() == ClickType.SHIFT_LEFT) {
                        max++;
                        updated = true;
                    } else if (e.getClick() == ClickType.SHIFT_RIGHT && max > min) {
                        max--;
                        updated = true;
                    } else if (e.getClick() == ClickType.DROP) {
                        rigManager.removeLoot(rigName, rarity, currentEntry);
                        MsgManager.send(p,"&cRemoved " + matName + " from " + rarity + " loot.");
                        open(player, rigName, rigManager);
                        return;
                    }

                    if (updated) {
                        String newEntry = matName + ":" + min + "-" + max;
                        List<String> list = new ArrayList<>(rigManager.getLoot(rigName).get(rarity));
                        list.remove(currentEntry);
                        list.add(newEntry);
                        rigManager.updateLoot(rigName, rarity, list);

                        MsgManager.send(p,"&aUpdated " + matName + " to " + min + "-" + max);
                        open(player, rigName,rigManager); // refresh
                    }
                });

                slot++;
            }
        }

        // Add new loot slot
        ItemStack addButton = GUIManager.createItem(Material.EMERALD, "§aAdd Loot", "§7Drag an item here to add it");
        gui.addItem(53, addButton);

        gui.setClickAction(53, (p, e) -> {
            ItemStack cursor = e.getCursor();
            if (cursor.getType() != Material.AIR) {
                String entry = cursor.getType().name() + ":1-1";
                rigManager.addLoot(rigName, "common", entry);
                MsgManager.send(p,"&aAdded " + cursor.getType().name() + " to loot!");
                open(player, rigName, rigManager); // refresh
            }
        });

        gui.open(player);
    }
}
