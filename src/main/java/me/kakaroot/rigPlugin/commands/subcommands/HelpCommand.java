package me.kakaroot.rigPlugin.commands.subcommands;

import me.kakaroot.rigPlugin.commands.SubCommand;
import me.kakaroot.rigPlugin.managers.MsgManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand implements SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays the help menu";
    }

    @Override
    public String getUsage() {
        return "/rig help";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        MsgManager.send(sender,"Plugin Help Menu...");
        return true;
    }

    @Override
    public List<String> getPermissions() {
        return List.of("rig.help");
    }
}
