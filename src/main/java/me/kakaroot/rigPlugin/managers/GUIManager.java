package me.kakaroot.rigPlugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager implements InventoryHolder {

    private final Inventory inv;
    private final UUID uuid;
    private final Map<Integer, ClickAction> clickActions = new HashMap<>();

    public GUIManager(String name, int rows, UUID uuid) {
        this.uuid = uuid;
        if (rows < 1) {
            rows = 1;
        } else if (rows > 6) {
            rows = 6;
        }
        this.inv = Bukkit.createInventory(this, rows * 9, name);
    }

    public static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createItem(Material material, String name, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(loreLines)); // converts all strings to a list
            item.setItemMeta(meta);
        }
        return item;
    }


    public void addItem(int slot, ItemStack item) {
        inv.setItem(slot, item);
    }

    public void setClickAction(int slot, ClickAction action) {
        clickActions.put(slot, action);
    }

    public void open(Player player) {
        player.openInventory(inv);
    }

    public UUID getID() {
        return uuid;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        if (slot >= 0 && slot < inv.getSize()) {
            if (clickActions.containsKey(slot)) {
                event.setCancelled(true);
                clickActions.get(slot).execute(player, event);
            }
        }
    }

    public static Listener getListener() {
        return new KGUIListener();
    }

    private static class KGUIListener implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            Inventory clickedInventory = e.getClickedInventory();
            if (clickedInventory == null || !(clickedInventory.getHolder() instanceof GUIManager)) return;
            GUIManager gui = (GUIManager) clickedInventory.getHolder();
            gui.handleClick(e);
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            Inventory inv = e.getInventory();
            if (inv.getHolder() instanceof GUIManager) {
                GUIManager gui = (GUIManager) inv.getHolder();
                // Handle inventory close if needed
            }
        }
    }

    @FunctionalInterface
    public interface ClickAction {
        void execute(Player player, InventoryClickEvent event);
    }
}
