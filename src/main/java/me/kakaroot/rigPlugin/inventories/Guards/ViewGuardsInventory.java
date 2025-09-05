package me.kakaroot.rigPlugin.inventories.Guards;

import me.kakaroot.rigPlugin.inventories.EditRigInventory;
import me.kakaroot.rigPlugin.inventories.ViewRigsInventory;
import me.kakaroot.rigPlugin.managers.GUIManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ViewGuardsInventory {

    @SuppressWarnings("unchecked")
    public static void open(Player player, String rigName, RigManager rigManager, ViewRigsInventory viewRigsInventory) {
        UUID uuid = player.getUniqueId();
        GUIManager gui = new GUIManager("View Guards for " + rigName, 6, uuid);

        List<Map<String, Object>> guardTemplates =
                rigManager.getGuardTemplates(rigName);

        int slot = 0;
        for (Map<String, Object> template : guardTemplates) {
            String type = template.getOrDefault("type", "ZOMBIE").toString();
            String name = template.getOrDefault("name", "Guard").toString();
            List<String> armour = (List<String>) template.getOrDefault("armour", List.of());
            String weapon = template.getOrDefault("weapon", "IRON_SWORD").toString();

            Material spawnEgg = Material.getMaterial(type.toUpperCase() + "_SPAWN_EGG");
            ItemStack item = spawnEgg != null ? new ItemStack(spawnEgg) : new ItemStack(Material.ZOMBIE_SPAWN_EGG);


            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e" + name + " (" + type + ")");
                meta.setLore(List.of(
                        "§7Armour: " + String.join(", ", armour),
                        "§7Weapon: " + weapon,
                        "§eClick to edit"
                ));
                item.setItemMeta(meta);
            }

            gui.addItem(slot, item);

            int guardIndex = slot;
            gui.setClickAction(slot, (p, e) -> EditGuardInventory.open(p, rigName, guardIndex, rigManager,viewRigsInventory));
            slot++;
        }

        // Add button to create a new guard
        ItemStack addGuard = GUIManager.createItem(
                Material.EMERALD_BLOCK,
                "§a➕ Add New Guard",
                "§7Click to create a new guard"
        );
        gui.addItem(53, addGuard);
        gui.setClickAction(53, (p, e) -> {
            // Default guard values
            Map<String, Object> newGuard = new HashMap<>();
            newGuard.put("type", "ZOMBIE");
            newGuard.put("name", "Guard");
            newGuard.put("weapon", "IRON_SWORD");

            List<String> armour = new ArrayList<>(Arrays.asList("AIR", "AIR", "AIR", "AIR"));
            newGuard.put("armour", armour);

            rigManager.addGuardTemplate(rigName, newGuard);

            // Open the editor for the new guard
            int newIndex = rigManager.getGuardTemplates(rigName).size() - 1;
            EditGuardInventory.open(p, rigName, newIndex, rigManager,viewRigsInventory);
        });

        // Back button
        ItemStack back = GUIManager.createItem(Material.ARROW, "§eBack to Edit Rig");
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setLore(List.of("Click to go back to editing this rig"));
            back.setItemMeta(backMeta);
        }
        gui.addItem(45, back);
        gui.setClickAction(45, (p, e) -> {
            EditRigInventory editRig = new EditRigInventory(rigManager, viewRigsInventory);
            editRig.open(p, rigName);
        });


        gui.open(player);
    }
}
