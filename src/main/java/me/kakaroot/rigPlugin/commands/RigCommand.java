package me.kakaroot.rigPlugin.commands;

import me.kakaroot.rigPlugin.RigPlugin;
import me.kakaroot.rigPlugin.commands.subcommands.*;
import me.kakaroot.rigPlugin.managers.MsgManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.*;

public class RigCommand implements CommandExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public RigCommand(RigPlugin plugin) {
        register(new GetWandCommand());
        register(new SaveRigCommand(plugin));
        register(new StartHeistCommand(plugin));
        register(new HelpCommand(this));
        register(new MenuCommand(plugin));
        register(new RigReloadCommand(plugin));
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
            try {
                return menu.execute(sender, args);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
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
        try {
            return sub.execute(sender, subArgs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean hasNoPermissions(CommandSender sender, List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        String masterPerm = "rig.*";
        if (sender.hasPermission(masterPerm)) {
            return false;
        }

        for (String perm : permissions) {
            if (sender.hasPermission(perm)) {
                return false;
            }
        }

        return true;
    }

}