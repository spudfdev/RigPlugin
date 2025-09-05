package me.kakaroot.rigPlugin.inventories;

import me.kakaroot.rigPlugin.managers.GUIManager;
import me.kakaroot.rigPlugin.managers.HeistManager;
import me.kakaroot.rigPlugin.managers.MsgManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class ViewRigsInventory {

    private final RigManager rigManager;
    private final HeistManager heistManager;

    public ViewRigsInventory(RigManager rigManager, HeistManager heistManager) {
        this.rigManager = rigManager;
        this.heistManager = heistManager;
    }

    public void open(Player player) {
        UUID uuid = player.getUniqueId();
        GUIManager gui = new GUIManager("View Rigs", 6, uuid);

        Set<String> rigs = rigManager.getAllRigNames();

        int slot = 0;
        for (String rigName : rigs) {
            Material displayMaterial = rigManager.getChests(rigName).isEmpty() ? Material.BARRIER : Material.CHEST;
            ItemStack rigItem = GUIManager.createItem(displayMaterial, rigName);
            ItemMeta meta = rigItem.getItemMeta();
            int frequency = rigManager.getFrequency(rigName);
            if (meta != null) {
                meta.setLore(Arrays.asList(
                        "§7Left-Click: Start Rig",
                        "§7Right-Click: Edit Rig",
                        "§7Shift + Right-Click: Warp",
                        "§fFrequency: " + frequency
                ));
                rigItem.setItemMeta(meta);
            }
            gui.addItem(slot, rigItem);

            gui.setClickAction(slot, (p, e) -> {
                ClickType click = e.getClick();

                if (click.isLeftClick()) {
                    // Start heist
                    MsgManager.send(p,"Starting rig: " + rigName);
                    heistManager.startHeist(rigName);
                } else if (click.isRightClick()) {
                    if (click == ClickType.SHIFT_RIGHT) {
                        if (!rigManager.getChests(rigName).isEmpty()) {
                            p.teleport(rigManager.getChests(rigName).getFirst());
                            MsgManager.send(p,"§aWarped to rig: " + rigName);
                        } else {
                            MsgManager.send(p,"§cNo chests found for this rig!");
                        }
                    } else {
                        // Edit rig
                        MsgManager.send(p, "Editing rig: " + rigName);
                        new EditRigInventory(rigManager, this).open(p, rigName);
                    }
                }
            });

            slot++;
            if (slot >= gui.getInventory().getSize()) break;
        }

        gui.open(player);
    }
}