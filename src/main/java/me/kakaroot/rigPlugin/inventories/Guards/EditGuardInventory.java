package me.kakaroot.rigPlugin.inventories.Guards;

import me.kakaroot.rigPlugin.managers.GUIManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EditGuardInventory {

    public static void open(Player player, String rigName, int guardIndex, RigManager rigManager) {
        GUIManager gui = new GUIManager("Edit Guard", 6, player.getUniqueId());

        Map<String, Object> guard = (Map<String, Object>) rigManager.getGuardTemplates(rigName).get(guardIndex);

        String type = (String) guard.getOrDefault("type", "ZOMBIE");
        String name = (String) guard.getOrDefault("name", "Guard");
        String weapon = (String) guard.getOrDefault("weapon", "IRON_SWORD");
        List<String> armour = (List<String>) guard.getOrDefault("armour", List.of());

        // Change type button
        ItemStack typeItem = GUIManager.createItem(Material.NAME_TAG, "§aType: " + type, "§7Click to change type");
        gui.addItem(10, typeItem);
        gui.setClickAction(10, (p, e) -> {
            // Example: cycle through types (ZOMBIE → SKELETON → PIGLIN → ZOMBIE)
            String newType = switch (type) {
                case "ZOMBIE" -> "SKELETON";
                case "SKELETON" -> "PIGLIN";
                default -> "ZOMBIE";
            };
            guard.put("type", newType);
            rigManager.updateGuardTemplate(rigName, guardIndex, guard);
            open(player, rigName, guardIndex, rigManager); // refresh
        });

        // Change weapon button
        ItemStack weaponItem = GUIManager.createItem(Material.DIAMOND_SWORD, "§aWeapon: " + weapon, "§7Click to change weapon");
        gui.addItem(11, weaponItem);
        gui.setClickAction(11, (p, e) -> {
            // Example: toggle between IRON_SWORD and DIAMOND_SWORD
            String newWeapon = weapon.equals("IRON_SWORD") ? "DIAMOND_SWORD" : "IRON_SWORD";
            guard.put("weapon", newWeapon);
            rigManager.updateGuardTemplate(rigName, guardIndex, guard);
            open(player, rigName, guardIndex, rigManager);
        });

        // Edit armour button
        ItemStack armourItem = GUIManager.createItem(Material.IRON_CHESTPLATE, "§aArmour", "§7Click to edit armour");
        gui.addItem(12, armourItem);
        gui.setClickAction(12, (p, e) -> {
            EditGuardArmourInventory.open(player, rigName, guardIndex, rigManager);
        });

        // Delete guard
        ItemStack deleteItem = GUIManager.createItem(Material.BARRIER, "§cDelete Guard", "§7Click to delete this guard");
        gui.addItem(13, deleteItem);
        gui.setClickAction(13, (p, e) -> {
            rigManager.removeGuardTemplate(rigName, guardIndex);
            ViewGuardsInventory.open(player, rigName, rigManager); // back to list
        });

        // Rename button
        ItemStack rename = GUIManager.createItem(Material.NAME_TAG, "Rename Guard");
        ItemMeta renameMeta = rename.getItemMeta();
        if (renameMeta != null) {
            renameMeta.setLore(Arrays.asList("Click to rename this rig"));
            rename.setItemMeta(renameMeta);
        }
        gui.addItem(14, rename);
        gui.setClickAction(0, (p, e) -> {
            p.sendMessage("Renaming guard: " + rigName);
            // Open rename input (maybe a chat prompt)
        });

        gui.open(player);
    }
}
