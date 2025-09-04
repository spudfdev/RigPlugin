package me.kakaroot.rigPlugin.inventories.Guards;

import me.kakaroot.rigPlugin.managers.GUIManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ViewGuardsInventory {

    public static void open(Player player, String rigName, RigManager rigManager) {
        UUID uuid = player.getUniqueId();
        GUIManager gui = new GUIManager("View Guards for " + rigName, 6, uuid);

        List<Map<?, ?>> guardTemplates = rigManager.getGuardTemplates(rigName);

        int slot = 0;
        for (Map<?, ?> template : guardTemplates) {
            String type = template.containsKey("type") ? template.get("type").toString() : "ZOMBIE";
            String name = template.containsKey("name") ? template.get("name").toString() : "Guard";
            List<String> armour = template.containsKey("armour") ? (List<String>) template.get("armour") : List.of();
            String weapon = template.containsKey("weapon") ? template.get("weapon").toString() : "IRON_SWORD";

            ItemStack item = Material.getMaterial(type.toUpperCase()) != null
                    ? new ItemStack(Material.getMaterial(type.toUpperCase()))
                    : new ItemStack(Material.ZOMBIE_SPAWN_EGG); // fallback

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
            gui.setClickAction(slot, (p, e) -> {
                EditGuardInventory.open(p, rigName, guardIndex, rigManager);
            });
            slot++;
        }
        gui.open(player);
    }
}