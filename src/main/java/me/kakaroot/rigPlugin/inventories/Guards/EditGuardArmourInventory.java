package me.kakaroot.rigPlugin.inventories.Guards;

import me.kakaroot.rigPlugin.managers.GUIManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class EditGuardArmourInventory {

    public static void open(Player player, String rigName, int guardIndex, RigManager rigManager) {
        GUIManager gui = new GUIManager("Edit Armour", 6, player.getUniqueId());

        Map<String, Object> guard = (Map<String, Object>) rigManager.getGuardTemplates(rigName).get(guardIndex);
        List<String> armour = (List<String>) guard.getOrDefault("armour", List.of());

        String[] slots = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"};

        for (int i = 0; i < slots.length; i++) {
            int index = i; // effectively final for lambda
            String currentPiece = index < armour.size() ? armour.get(index) : "AIR";

            Material displayMaterial = materialFromString(currentPiece, slots[i]);
            ItemStack item = GUIManager.createItem(
                    displayMaterial,
                    "§a" + slots[i],
                    "§7Current: " + currentPiece,
                    "§7Click to toggle"
            );

            gui.addItem(i, item);

            gui.setClickAction(i, (p, e) -> {
                // Get latest current value
                String current = index < armour.size() ? armour.get(index) : "AIR";
                String newPiece = getNextPiece(current, slots[index]);

                if (armour.size() > index) armour.set(index, newPiece);
                else armour.add(newPiece);

                guard.put("armour", armour);
                rigManager.updateGuardTemplate(rigName, guardIndex, guard);

                // Refresh GUI to show updated state
                open(player, rigName, guardIndex, rigManager);
            });
        }

        gui.open(player);
    }

    private static String getNextPiece(String current, String slot) {
        if ("AIR".equals(current)) return "LEATHER_" + slot;
        if (("LEATHER_" + slot).equals(current)) return "IRON_" + slot;
        if (("IRON_" + slot).equals(current)) return "DIAMOND_" + slot;
        return "AIR";
    }


    private static Material materialFromString(String str, String slot) {
        if ("AIR".equals(str)) return Material.BARRIER; // show barrier instead of air
        try {
            return Material.valueOf(str); // directly use the string like "LEATHER_HELMET"
        } catch (IllegalArgumentException e) {
            return Material.BARRIER; // fallback if material not found
        }
    }

}
