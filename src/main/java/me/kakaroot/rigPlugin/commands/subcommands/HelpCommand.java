package me.kakaroot.rigPlugin.commands.subcommands;

import me.kakaroot.rigPlugin.commands.SubCommand;
import me.kakaroot.rigPlugin.commands.RigCommand;
import me.kakaroot.rigPlugin.managers.MsgManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand implements SubCommand {

    private final RigCommand rigCommand;

    public HelpCommand(RigCommand rigCommand) {
        this.rigCommand = rigCommand;
    }

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
        MsgManager.send(sender, "&6---- Rig Plugin Help ----");
        for (SubCommand sub : rigCommand.getSubCommands()) {
            if (rigCommand.hasNoPermissions(sender, sub.getPermissions())) {
                continue;
            }
            MsgManager.send(sender, "&e" + sub.getUsage() + " &7- " + sub.getDescription());
        }
        return true;
    }

    @Override
    public List<String> getPermissions() {
        return List.of("rig.help");
    }

}
