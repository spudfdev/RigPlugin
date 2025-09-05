package me.kakaroot.rigPlugin.listeners;

import me.kakaroot.rigPlugin.RigPlugin;
import me.kakaroot.rigPlugin.managers.RigManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.block.BlockExplodeEvent;

import java.util.Iterator;

public class RigBreakListener implements Listener {

    private final RigManager rigManager;
    private final RigPlugin plugin;

    public RigBreakListener(RigPlugin plugin) {
        this.plugin = plugin;
        this.rigManager = plugin.getRigManager();
    }

    // Prevent manual chest breaking
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!plugin.getConfig().getBoolean("rigs-indestructible", false)) return;

        Block block = event.getBlock();
        if (block.getType() != Material.CHEST) return;

        boolean isRigChest = rigManager.getAllRigNames().stream()
                .anyMatch(rigName -> rigManager.getChests(rigName).contains(block.getLocation()));

        if (isRigChest) {
            event.setCancelled(true);
        }
    }

    // Prevent chests being destroyed by explosions
    @EventHandler
    public void onEntityExplosion(EntityExplodeEvent event) {
        if (!plugin.getConfig().getBoolean("rigs-indestructible", false)) return;

        protectRigChests(event.blockList().iterator());
    }

    @EventHandler
    public void onBlockExplosion(BlockExplodeEvent event) {
        if (!plugin.getConfig().getBoolean("rigs-indestructible", false)) return;

        protectRigChests(event.blockList().iterator());
    }

    private void protectRigChests(Iterator<Block> blocks) {
        while (blocks.hasNext()) {
            Block block = blocks.next();
            if (block.getType() != Material.CHEST) continue;

            boolean isRigChest = rigManager.getAllRigNames().stream()
                    .anyMatch(rigName -> rigManager.getChests(rigName).contains(block.getLocation()));

            if (isRigChest) {
                blocks.remove();
            }
        }
    }
}