package me.kakaroot.rigPlugin.commands;

import org.bukkit.command.CommandSender;

public interface SubCommand {
    String getName();        // subcommand name, e.g. "wand"
    String getDescription(); // for help
    String getUsage();       // usage string
    boolean execute(CommandSender sender, String[] args);
}
