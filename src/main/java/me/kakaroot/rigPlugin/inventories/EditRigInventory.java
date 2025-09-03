package me.kakaroot.rigPlugin.inventories;

import me.kakaroot.rigPlugin.inventories.Guards.ViewGuardsInventory;
import me.kakaroot.rigPlugin.managers.GUIManager;
import me.kakaroot.rigPlugin.managers.MsgManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EditRigInventory {

    private final RigManager rigManager;
    private final ViewRigsInventory viewRigsInventory;

    public EditRigInventory(RigManager rigManager, ViewRigsInventory viewRigsInventory) {
        this.rigManager = rigManager;
        this.viewRigsInventory = viewRigsInventory;
    }


    public void open(Player player, String rigName) {
        UUID uuid = player.getUniqueId();
        GUIManager gui = new GUIManager("Edit Rig", 1, uuid);

        // Rename button
        ItemStack rename = GUIManager.createItem(Material.NAME_TAG, "Rename Rig");
        ItemMeta renameMeta = rename.getItemMeta();
        if (renameMeta != null) {
            renameMeta.setLore(Arrays.asList("Click to rename this rig"));
            rename.setItemMeta(renameMeta);
        }
        gui.addItem(0, rename);
        gui.setClickAction(0, (p, e) -> {
            MsgManager.send(p,"(todo add chat prompts) Renaming rig: " + rigName);
            // Open rename input (maybe a chat prompt)
        });

        // Delete button
        ItemStack delete = GUIManager.createItem(Material.BARRIER, "Delete Rig");
        ItemMeta deleteMeta = delete.getItemMeta();
        if (deleteMeta != null) {
            deleteMeta.setLore(Arrays.asList("Click to delete this rig"));
            delete.setItemMeta(deleteMeta);
        }
        gui.addItem(1, delete);
        gui.setClickAction(1, (p, e) -> {
            MsgManager.send(p,"Deleting rig: " + rigName);
            rigManager.deleteRig(rigName);
            viewRigsInventory.open(player);
        });

        // Placeholder for Edit Loot button
        ItemStack editLoot = GUIManager.createItem(Material.CHEST, "Edit Loot");
        ItemMeta lootMeta = editLoot.getItemMeta();
        if (lootMeta != null) {
            lootMeta.setLore(Arrays.asList("Click to edit this rig's loot"));
            editLoot.setItemMeta(lootMeta);
        }
        gui.addItem(2, editLoot);
        gui.setClickAction(2, (p, e) -> {
            MsgManager.send(p,"Editing loot for rig: " + rigName);
            // Open LootEditorInventory
            LootEditorInventory.open(p,rigName,rigManager);
        });

        // Placeholder for Edit Guards button
        ItemStack editGuards = GUIManager.createItem(Material.IRON_SWORD, "Edit Guards");
        ItemMeta guardMeta = editGuards.getItemMeta();
        if (guardMeta != null) {
            guardMeta.setLore(Arrays.asList("Click to edit this rig's guards"));
            editGuards.setItemMeta(guardMeta);
        }
        gui.addItem(3, editGuards);
        gui.setClickAction(3, (p, e) -> {
            MsgManager.send(p,"Viewing guards for rig: " + rigName);
            ViewGuardsInventory.open(p,rigName,rigManager);
        });

        // Button to edit frequency
        ItemStack freqButton = GUIManager.createItem(Material.CLOCK, "Edit Frequency");
        ItemMeta freqMeta = freqButton.getItemMeta();
        if (freqMeta != null) {
            freqMeta.setLore(Arrays.asList(
                    "§7Current frequency: every " + rigManager.getFrequency(rigName) + " minutes",
                    "§eLeft-click to increase by 5 minutes",
                    "§eRight-click to decrease by 5 minutes"
            ));
            freqButton.setItemMeta(freqMeta);
        }
        gui.addItem(4, freqButton);
        gui.setClickAction(4, (p, e) -> {
            int current = rigManager.getFrequency(rigName);

            if (e.getClick().isLeftClick()) {
                rigManager.setFrequency(rigName, current + 5);
                MsgManager.send(p,"§aRig frequency for " + rigName + " increased to " + (current + 5) + " minutes.");
            } else if (e.getClick().isRightClick()) {
                int newVal = Math.max(5, current - 5); // don’t go below 5 minutes
                rigManager.setFrequency(rigName, newVal);
                MsgManager.send(p,"§cRig frequency for " + rigName + " decreased to " + newVal + " minutes.");
            }

            open(player, rigName); // Refresh GUI to show updated value
        });





        gui.open(player);
    }
}