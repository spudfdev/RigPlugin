package me.kakaroot.rigPlugin.inventories.Guards;

import me.kakaroot.rigPlugin.inventories.ViewRigsInventory;
import me.kakaroot.rigPlugin.managers.GUIManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EditGuardArmourInventory {

    private static final List<String> SLOTS = Arrays.asList("HELMET", "CHESTPLATE", "LEGGINGS",
            "BOOTS");

    private static final Map<String, List<String>> VALID_MATERIALS = Map.of(
            "HELMET", Arrays.asList("AIR", "LEATHER_HELMET", "CHAINMAIL_HELMET", "GOLDEN_HELMET", "IRON_HELMET", "DIAMOND_HELMET", "NETHERITE_HELMET"),
            "CHESTPLATE", Arrays.asList("AIR", "LEATHER_CHESTPLATE", "CHAINMAIL_CHESTPLATE",
                    "GOLDEN_CHESTPLATE", "IRON_CHESTPLATE", "DIAMOND_CHESTPLATE",
                    "NETHERITE_CHESTPLATE"),
            "LEGGINGS", Arrays.asList("AIR", "LEATHER_LEGGINGS", "CHAINMAIL_LEGGINGS",
                    "GOLDEN_LEGGINGS", "IRON_LEGGINGS", "DIAMOND_LEGGINGS", "NETHERITE_LEGGINGS"),
            "BOOTS", Arrays.asList("AIR", "LEATHER_BOOTS", "CHAINMAIL_BOOTS", "GOLDEN_BOOTS",
                    "IRON_BOOTS", "DIAMOND_BOOTS", "NETHERITE_BOOTS")
    );

    public static void open(Player player, String rigName, int guardIndex, RigManager rigManager, ViewRigsInventory viewRigsInventory) {
        GUIManager gui = new GUIManager("Edit Armour", 6, player.getUniqueId());

        Map<String, Object> guard = rigManager.getGuardTemplates(rigName).get(guardIndex);
        List<String> armour = new ArrayList<>(
                (List<String>) guard.getOrDefault("armour", Arrays.asList
                        ("AIR", "AIR", "AIR", "AIR"))
        );

        for (int i = 0; i < SLOTS.size(); i++) {
            int index = i;
            String slot = SLOTS.get(i);
            String currentPiece = index < armour.size() ? armour.get(index) : "AIR";

            Material displayMaterial = materialFromString(currentPiece);
            ItemStack item = GUIManager.createItem(
                    displayMaterial,
                    "§a" + slot,
                    "§7Current: " + currentPiece,
                    "§7Click to toggle"
            );

            gui.addItem(i, item);

            gui.setClickAction(i, (p, e) -> {
                String current = index < armour.size() ? armour.get(index) : "AIR";
                String next = getNextPiece(current, slot);

                if (armour.size() > index) {
                    armour.set(index, next);
                } else {
                    armour.add(next);
                }

                guard.put("armour", armour);
                rigManager.updateGuardTemplate(rigName, guardIndex, guard);

                open(player, rigName, guardIndex, rigManager, viewRigsInventory);
            });
        }

        // Back button
        ItemStack back = GUIManager.createItem(Material.ARROW, "§eBack to Edit Guard");
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setLore(List.of("Click to go back to editing this guard."));
            back.setItemMeta(backMeta);
        }
        gui.addItem(45, back);
        gui.setClickAction(45, (p, e) -> {
            EditGuardInventory.open(p, rigName, guardIndex, rigManager, viewRigsInventory);
        });

        gui.open(player);
    }

    private static String getNextPiece(String current, String slot) {
        List<String> valid = VALID_MATERIALS.get(slot);
        int currentIndex = valid.indexOf(current);
        int nextIndex = (currentIndex + 1) % valid.size();
        return valid.get(nextIndex);
    }

    private static Material materialFromString(String str) {
        if ("AIR".equals(str)) return Material.BARRIER;
        try {
            return Material.valueOf(str);
        } catch (IllegalArgumentException e) {
            return Material.BARRIER;
        }
    }
}