package me.kakaroot.rigPlugin.commands.subcommands;

import me.kakaroot.rigPlugin.commands.SubCommand;
import me.kakaroot.rigPlugin.managers.MsgManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.List;

public class GetWandCommand implements SubCommand {

    @Override
    public String getName() {
        return "wand";
    }

    @Override
    public String getDescription() {
        return "Gives wand for making rig";
    }

    @Override
    public String getUsage() {
        return "/rig wand";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player p)) {
            MsgManager.send(sender, "You must be a player to use this command!");
            return true;
        }

        p.getInventory().addItem(getRigWand());
        MsgManager.send(p,"&aYou received the Rig Wand!");
        return true;
    }

    private ItemStack getRigWand() {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName(MsgManager.colourise("&6Rig Wand"));
        meta.setLore(Arrays.asList("&7Left Click = Mark Chest", "Shift-Right Click = Open Menu"));
        wand.setItemMeta(meta);
        return wand;
    }

    @Override
    public List<String> getPermissions() {
        return List.of("rig.wand");
    }
}
