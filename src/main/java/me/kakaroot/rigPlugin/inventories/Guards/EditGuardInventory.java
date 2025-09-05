package me.kakaroot.rigPlugin.inventories.Guards;

import me.kakaroot.rigPlugin.RigPlugin;
import me.kakaroot.rigPlugin.inventories.ViewRigsInventory;
import me.kakaroot.rigPlugin.managers.ChatPromptManager;
import me.kakaroot.rigPlugin.managers.GUIManager;
import me.kakaroot.rigPlugin.managers.MsgManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EditGuardInventory {

    // List of mobs that can hold a sword
    private static final List<String> SWORD_CAPABLE_TYPES = Arrays.asList(
            "ZOMBIE",
            "HUSK",
            "DROWNED",
            "ZOMBIE_VILLAGER",
            "SKELETON",
            "STRAY",
            "WITHER_SKELETON",
            "PIGLIN",
            "VINDICATOR"
    );

    private static final List<String> SWORD_TYPES = Arrays.asList(
            "WOODEN_SWORD",
            "STONE_SWORD",
            "GOLDEN_SWORD",
            "IRON_SWORD",
            "DIAMOND_SWORD",
            "NETHERITE_SWORD"
    );


    public static void open(Player player, String rigName, int guardIndex, RigManager rigManager, ViewRigsInventory viewRigsInventory) {
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
            int currentIndex = SWORD_CAPABLE_TYPES.indexOf(type.toUpperCase());
            int nextIndex = (currentIndex + 1) % SWORD_CAPABLE_TYPES.size();
            String newType = SWORD_CAPABLE_TYPES.get(nextIndex);

            guard.put("type", newType);
            rigManager.updateGuardTemplate(rigName, guardIndex, guard);
            open(player, rigName, guardIndex, rigManager,viewRigsInventory); // refresh
        });

        // Change weapon button
        ItemStack weaponItem = GUIManager.createItem(Material.DIAMOND_SWORD, "§aWeapon: " + weapon, "§7Click to change weapon");
        gui.addItem(11, weaponItem);
        gui.setClickAction(11, (p, e) -> {
            int currentIndex = SWORD_TYPES.indexOf(weapon.toUpperCase());
            int nextIndex = (currentIndex + 1) % SWORD_TYPES.size();
            String newWeapon = SWORD_TYPES.get(nextIndex);

            guard.put("weapon", newWeapon);
            rigManager.updateGuardTemplate(rigName, guardIndex, guard);
            open(player, rigName, guardIndex, rigManager,viewRigsInventory);
        });


        // Edit armour button
        ItemStack armourItem = GUIManager.createItem(Material.IRON_CHESTPLATE, "§aArmour", "§7Click to edit armour");
        gui.addItem(12, armourItem);
        gui.setClickAction(12, (p, e) -> {
            EditGuardArmourInventory.open(player, rigName, guardIndex, rigManager, viewRigsInventory);
        });

        // Delete guard
        ItemStack deleteItem = GUIManager.createItem(Material.BARRIER, "§cDelete Guard", "§7Click to delete this guard");
        gui.addItem(13, deleteItem);
        gui.setClickAction(13, (p, e) -> {
            rigManager.removeGuardTemplate(rigName, guardIndex);
            ViewGuardsInventory.open(player, rigName, rigManager,viewRigsInventory);
        });

        // Rename button
        ItemStack rename = GUIManager.createItem(Material.NAME_TAG, "Rename Guard");
        ItemMeta renameMeta = rename.getItemMeta();
        if (renameMeta != null) {
            renameMeta.setLore(Arrays.asList("Click to rename this guard"));
            rename.setItemMeta(renameMeta);
        }
        gui.addItem(14, rename);
        gui.setClickAction(14, (p, e) -> {
            MsgManager.send(p, "Renaming guard: " + name);
            p.closeInventory();
            ChatPromptManager promptManager = new ChatPromptManager(RigPlugin.getInstance());
            promptManager.promptPlayer(p, "Enter a new name for this guard:", input -> {
                guard.put("name", input);

                rigManager.updateGuardTemplate(rigName, guardIndex, guard);

                MsgManager.send(p, "Guard renamed to: " + input);

                open(player, rigName, guardIndex, rigManager,viewRigsInventory);
            });
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
            ViewGuardsInventory.open(p,rigName, rigManager,viewRigsInventory);
        });




        gui.open(player);
    }
}