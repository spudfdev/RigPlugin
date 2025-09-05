package me.kakaroot.rigPlugin.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.List;

public interface SubCommand {
    String getName();        // subcommand name
    String getDescription();
    String getUsage();       // usage string
    boolean execute(CommandSender sender, String[] args) throws IOException, InvalidConfigurationException;

    List<String> getPermissions();
}
