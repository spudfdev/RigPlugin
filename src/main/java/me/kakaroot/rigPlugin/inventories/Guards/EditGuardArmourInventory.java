package me.kakaroot.rigPlugin.inventories.Guards;

import me.kakaroot.rigPlugin.managers.GUIManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EditGuardArmourInventory {

    private static final List<String> ARMOUR_MATERIALS = Arrays.asList(
            "AIR",
            "LEATHER",
            "CHAINMAIL",
            "GOLDEN",
            "IRON",
            "DIAMOND",
            "NETHERITE"
    );

    public static void open(Player player, String rigName, int guardIndex, RigManager rigManager) {
        GUIManager gui = new GUIManager("Edit Armour", 6, player.getUniqueId());

        Map<String, Object> guard = (Map<String, Object>) rigManager.getGuardTemplates(rigName).get(guardIndex);
        List<String> armour = (List<String>) guard.getOrDefault("armour", List.of());

        String[] slots = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"};

        for (int i = 0; i < slots.length; i++) {
            int index = i;
            String currentPiece = index < armour.size() ? armour.get(index) : "AIR";

            Material displayMaterial = materialFromString(currentPiece);
            ItemStack item = GUIManager.createItem(
                    displayMaterial,
                    "§a" + slots[i],
                    "§7Current: " + currentPiece,
                    "§7Click to toggle"
            );

            gui.addItem(i, item);

            gui.setClickAction(i, (p, e) -> {
                String current = index < armour.size() ? armour.get(index) : "AIR";
                String newPiece = getNextPiece(current, slots[index]);

                if (armour.size() > index) {
                    armour.set(index, newPiece);
                } else {
                    armour.add(newPiece);
                }

                guard.put("armour", armour);
                rigManager.updateGuardTemplate(rigName, guardIndex, guard);

                open(player, rigName, guardIndex, rigManager); // refresh
            });
        }
        gui.open(player);
    }

    private static String getNextPiece(String current, String slot) {
        String base = current.equals("AIR") ? "AIR" : current.split("_")[0];

        int currentIndex = ARMOUR_MATERIALS.indexOf(base);
        int nextIndex = (currentIndex + 1) % ARMOUR_MATERIALS.size();

        String nextBase = ARMOUR_MATERIALS.get(nextIndex);
        return nextBase.equals("AIR") ? "AIR" : nextBase + "_" + slot;
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