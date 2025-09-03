package me.kakaroot.rigPlugin.commands;

import me.kakaroot.rigPlugin.RigPlugin;
import me.kakaroot.rigPlugin.commands.subcommands.*;
import me.kakaroot.rigPlugin.managers.MsgManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class RigCommand implements CommandExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public RigCommand(RigPlugin plugin) {
        register(new GetWandCommand());
        register(new SaveRigCommand(plugin));
        register(new StartHeistCommand(plugin));
        register(new HelpCommand());
        register(new MenuCommand(plugin));
    }

    private void register(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    public Collection<SubCommand> getSubCommands() {
        return subCommands.values();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            SubCommand menu = subCommands.get("menu");

            if (hasNoPermissions(sender, menu.getPermissions())) {
                MsgManager.send(sender, "&cYou don't have permission to open the menu.");
                return true;
            }
            return menu.execute(sender, args);
        }

        SubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) {
            MsgManager.send(sender, "&cUnknown subcommand. Try /rig help");
            return true;
        }

        if (hasNoPermissions(sender, sub.getPermissions())) {
            MsgManager.send(sender, "&cYou don't have permission to use this command.");
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return sub.execute(sender, subArgs);
    }

    /**
     * Helper method to check if the sender has any of the given permissions.
     */
    private boolean hasNoPermissions(CommandSender sender, List<String> permissions) {
        // If no perms required, allow
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        String masterPerm = "rig.*";
        if (sender.hasPermission(masterPerm)) {
            return false;
        }

        for (String perm : permissions) {
            if (sender.hasPermission(perm)) {
                return false; // at least one matched
            }
        }

        return true;
    }

}