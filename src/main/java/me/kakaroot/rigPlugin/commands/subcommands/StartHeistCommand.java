package me.kakaroot.rigPlugin.commands.subcommands;

import me.kakaroot.rigPlugin.RigPlugin;
import me.kakaroot.rigPlugin.commands.SubCommand;
import me.kakaroot.rigPlugin.managers.MsgManager;
import org.bukkit.command.CommandSender;

public class StartHeistCommand implements SubCommand {

    private final RigPlugin plugin;

    public StartHeistCommand(RigPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Starts a heist";
    }

    @Override
    public String getUsage() {
        return "/rig start <rig-name>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MsgManager.send(sender, "&cUsage: " + getUsage());
            return true;
        }

        String rigName = args[0];

        if (plugin.getRigManager().getChests(rigName).isEmpty()) {
            MsgManager.send(sender, "&cRig '" + rigName + "' has no chests or does not exist!");
            return true;
        }

        plugin.getHeistManager().startHeist(rigName);
        MsgManager.send(sender, "&aHeist started for rig '&6" + rigName + "&a'!");

        return true;
    }
}
