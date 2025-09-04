package me.kakaroot.rigPlugin.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    String getName();        // subcommand name
    String getDescription();
    String getUsage();       // usage string
    boolean execute(CommandSender sender, String[] args);

    List<String> getPermissions();
}
