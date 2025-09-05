package me.kakaroot.rigPlugin.inventories;

import me.kakaroot.rigPlugin.RigPlugin;
import me.kakaroot.rigPlugin.inventories.Guards.ViewGuardsInventory;
import me.kakaroot.rigPlugin.managers.ChatPromptManager;
import me.kakaroot.rigPlugin.managers.GUIManager;
import me.kakaroot.rigPlugin.managers.MsgManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
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
        GUIManager gui = new GUIManager("Edit Rig", 2, uuid);

        // Rename button
        ItemStack rename = GUIManager.createItem(Material.NAME_TAG, "Rename Rig");
        ItemMeta renameMeta = rename.getItemMeta();
        if (renameMeta != null) {
            renameMeta.setLore(List.of("Click to rename this rig"));
            rename.setItemMeta(renameMeta);
        }
        gui.addItem(2, rename);
        gui.setClickAction(2, (p, e) -> {
            MsgManager.send(p, "Renaming rig: " + rigName);
            player.closeInventory();
            ChatPromptManager promptManager = new ChatPromptManager(RigPlugin.getInstance());
            promptManager.promptPlayer(p, "Enter a new name for this rig:", input -> {
                rigManager.setName(rigName, input);
                MsgManager.send(p, "Rig renamed to: " + input);
                open(player, input);
            });
        });

        // Delete button
        ItemStack delete = GUIManager.createItem(Material.BARRIER, "Delete Rig");
        ItemMeta deleteMeta = delete.getItemMeta();
        if (deleteMeta != null) {
            deleteMeta.setLore(List.of("Click to delete this rig"));
            delete.setItemMeta(deleteMeta);
        }
        gui.addItem(3, delete);
        gui.setClickAction(3, (p, e) -> {
            MsgManager.send(p, "Deleting rig: " + rigName);
            rigManager.deleteRig(rigName);
            viewRigsInventory.open(player);
        });

        // Edit Loot button
        ItemStack editLoot = GUIManager.createItem(Material.CHEST, "Edit Loot");
        ItemMeta lootMeta = editLoot.getItemMeta();
        if (lootMeta != null) {
            lootMeta.setLore(List.of("Click to edit this rig's loot"));
            editLoot.setItemMeta(lootMeta);
        }
        gui.addItem(4, editLoot);
        gui.setClickAction(4, (p, e) -> {
            MsgManager.send(p, "Editing loot for rig: " + rigName);
            LootEditorInventory.open(p, rigName, rigManager,viewRigsInventory);
        });

        // Edit Guards button
        ItemStack editGuards = GUIManager.createItem(Material.IRON_SWORD, "Edit Guards");
        ItemMeta guardMeta = editGuards.getItemMeta();
        if (guardMeta != null) {
            guardMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            guardMeta.setLore(List.of("Click to edit this rig's guards"));
            editGuards.setItemMeta(guardMeta);
        }
        gui.addItem(5, editGuards);
        gui.setClickAction(5, (p, e) -> {
            MsgManager.send(p, "Viewing guards for rig: " + rigName);
            ViewGuardsInventory.open(p, rigName, rigManager,viewRigsInventory);
        });

// Frequency button
        ItemStack freqButton = GUIManager.createItem(Material.CLOCK, "Edit Frequency");
        ItemMeta freqMeta = freqButton.getItemMeta();
        if (freqMeta != null) {
            freqMeta.setLore(Arrays.asList(
                    "§7Current frequency: every " + rigManager.getFrequency(rigName) + " minutes",
                    "§eLeft-click: Increase by 5 minutes",
                    "§eRight-click: Decrease by 5 minutes",
                    "§eShift-left: Increase by 1 minute",
                    "§eShift-right: Decrease by 1 minute"
            ));
            freqButton.setItemMeta(freqMeta);
        }
        gui.addItem(6, freqButton);
        gui.setClickAction(6, (p, e) -> {
            int current = rigManager.getFrequency(rigName);

            switch (e.getClick()) {
                case LEFT -> {
                    rigManager.setFrequency(rigName, current + 5);
                    MsgManager.send(p, "§aRig frequency for " + rigName + " increased to " + (current + 5) + " minutes.");
                }
                case RIGHT -> {
                    int newVal = Math.max(1, current - 5);
                    rigManager.setFrequency(rigName, newVal);
                    MsgManager.send(p, "§cRig frequency for " + rigName + " decreased to " + newVal + " minutes.");
                }
                case SHIFT_LEFT -> {
                    rigManager.setFrequency(rigName, current + 1);
                    MsgManager.send(p, "§aRig frequency for " + rigName + " increased to " + (current + 1) + " minutes.");
                }
                case SHIFT_RIGHT -> {
                    int newVal = Math.max(1, current - 1);
                    rigManager.setFrequency(rigName, newVal);
                    MsgManager.send(p, "§cRig frequency for " + rigName + " decreased to " + newVal + " minutes.");
                }
            }

            open(player, rigName);
        });

        // Edit Guard Count button
        ItemStack editGuardCount = GUIManager.createItem(Material.PLAYER_HEAD, "Edit Guard Count");
        ItemMeta countMeta = editGuardCount.getItemMeta();
        if (countMeta != null) {
            countMeta.setLore(Arrays.asList(
                    "§7Current guard count: " + rigManager.getGuardCount(rigName),
                    "Click to change guard count"
            ));
            editGuardCount.setItemMeta(countMeta);
        }
        gui.addItem(11, editGuardCount);
        gui.setClickAction(11, (p, e) -> {
            player.closeInventory();
            ChatPromptManager promptManager = new ChatPromptManager(RigPlugin.getInstance());
            promptManager.promptPlayer(p, "Enter new guard count for this rig:", input -> {
                try {
                    int newCount = Integer.parseInt(input);
                    rigManager.setGuardCount(rigName, Math.max(0, newCount));
                    MsgManager.send(p, "Guard count set to " + newCount);
                    open(player, rigName);
                } catch (NumberFormatException ex) {
                    MsgManager.send(p, "§cInvalid number entered!");
                    open(player, rigName);
                }
            });
        });

// Edit Guard Radius button
        ItemStack editGuardRadius = GUIManager.createItem(Material.COMPASS, "Edit Guard Radius");
        ItemMeta radiusMeta = editGuardRadius.getItemMeta();
        if (radiusMeta != null) {
            radiusMeta.setLore(Arrays.asList(
                    "§7Current guard radius: " + rigManager.getGuardRadius(rigName),
                    "Click to change guard radius"
            ));
            editGuardRadius.setItemMeta(radiusMeta);
        }
        gui.addItem(12, editGuardRadius);
        gui.setClickAction(12, (p, e) -> {
            player.closeInventory();
            ChatPromptManager promptManager = new ChatPromptManager(RigPlugin.getInstance());
            promptManager.promptPlayer(p, "Enter new guard radius for this rig:", input -> {
                try {
                    int newRadius = Integer.parseInt(input);
                    rigManager.setGuardRadius(rigName, Math.max(0, newRadius));
                    MsgManager.send(p, "Guard radius set to " + newRadius);
                    open(player, rigName);
                } catch (NumberFormatException ex) {
                    MsgManager.send(p, "§cInvalid number entered!");
                    open(player, rigName);
                }
            });
        });


        // Back button
        ItemStack back = GUIManager.createItem(Material.ARROW, "§eBack");
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setLore(List.of("Click to go back to the rigs list"));
            back.setItemMeta(backMeta);
        }
        gui.addItem(17, back);
        gui.setClickAction(17, (p, e) -> {
            viewRigsInventory.open(p);
        });




        gui.open(player);
    }
}