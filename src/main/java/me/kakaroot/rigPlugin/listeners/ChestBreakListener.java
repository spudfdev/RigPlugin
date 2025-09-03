package me.kakaroot.rigPlugin.listeners;

import me.kakaroot.rigPlugin.RigPlugin;
import me.kakaroot.rigPlugin.managers.MsgManager;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ChestBreakListener implements Listener {

    private final RigManager rigManager;

    public ChestBreakListener(RigPlugin plugin) {
        this.rigManager = plugin.getRigManager();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.CHEST) {
            boolean isRigChest = rigManager.getAllRigNames().stream()
                    .anyMatch(rigName -> rigManager.getChests(rigName).contains(block.getLocation()));

            if (isRigChest) {
                event.setCancelled(true);
                MsgManager.send(event.getPlayer(),"You cannot break a chest that is part of a rig! Remove it from the rig first.");
            }
        }
    }
}
