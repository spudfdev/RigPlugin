package me.kakaroot.rigPlugin.commands.subcommands;

import me.kakaroot.rigPlugin.RigPlugin;
import me.kakaroot.rigPlugin.commands.SubCommand;
import me.kakaroot.rigPlugin.inventories.ViewRigsInventory;
import me.kakaroot.rigPlugin.managers.MsgManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MenuCommand implements SubCommand {

    private final RigPlugin plugin;

    public MenuCommand(RigPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "menu";
    }

    @Override
    public String getDescription() {
        return "Opens the rigs menu";
    }

    @Override
    public String getUsage() {
        return "/rig menu";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player p)) {
            MsgManager.send(sender, "&cYou must be a player to use this command!");
            return true;
        }

        ViewRigsInventory viewRigsInventory = new ViewRigsInventory(plugin.getRigManager(), plugin.getHeistManager());
        viewRigsInventory.open(p);
        return true;
    }

    @Override
    public List<String> getPermissions() {
        return List.of("rig.menu");
    }
}
