package me.kakaroot.rigPlugin.listeners;

import me.kakaroot.rigPlugin.RigPlugin;
import me.kakaroot.rigPlugin.inventories.ViewRigsInventory;
import me.kakaroot.rigPlugin.managers.MsgManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandListener implements Listener {

    private final RigPlugin plugin;

    public WandListener(RigPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWandUse(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getType() != Material.BLAZE_ROD) return;
        if (!e.getItem().hasItemMeta() || !e.getItem().getItemMeta().getDisplayName().contains("Rig Wand")) return;

        Player player = e.getPlayer();
        if (!player.hasPermission("rig.wand")) return;

        // Left click block
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block clickedBlock = e.getClickedBlock();
            if (clickedBlock == null) return;
            plugin.getRigManager().addChest(player.getUniqueId(), clickedBlock.getLocation());
            MsgManager.send(player, "&aChest location marked: " + clickedBlock.getLocation().toVector());
            e.setCancelled(true);
        }

        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) && player.isSneaking()) {
            e.setCancelled(true);
            ViewRigsInventory viewRigsInventory = new ViewRigsInventory(plugin.getRigManager(), plugin.getHeistManager());
            viewRigsInventory.open(player);
        }
    }

}