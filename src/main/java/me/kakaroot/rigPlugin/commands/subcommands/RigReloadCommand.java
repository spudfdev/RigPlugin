package me.kakaroot.rigPlugin.commands.subcommands;

import me.kakaroot.rigPlugin.RigPlugin;
import me.kakaroot.rigPlugin.commands.SubCommand;
import me.kakaroot.rigPlugin.managers.RigManager;
import me.kakaroot.rigPlugin.managers.MsgManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.List;

public class RigReloadCommand implements SubCommand {

    private final RigPlugin plugin;

    public RigReloadCommand(RigPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the rig configuration and resets relevant managers.";
    }

    @Override
    public String getUsage() {
        return "/rig reload";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws IOException, InvalidConfigurationException {
        RigManager rigManager = plugin.getRigManager();
        if (rigManager != null) {
            rigManager.reload();
            MsgManager.send(sender, "&aRig configuration reloaded successfully!");
        } else {
            MsgManager.send(sender, "&cFailed to reload rig configuration.");
        }
        return true;
    }

    @Override
    public List<String> getPermissions() {
        return List.of("rig.reload");
    }
}
