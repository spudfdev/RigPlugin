package me.kakaroot.rigPlugin.commands.subcommands;

import me.kakaroot.rigPlugin.RigPlugin;
import me.kakaroot.rigPlugin.commands.SubCommand;
import me.kakaroot.rigPlugin.managers.MsgManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SaveRigCommand implements SubCommand {

    private final RigPlugin plugin;

    public SaveRigCommand(RigPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "Saves a rig with the selected chests";
    }

    @Override
    public String getUsage() {
        return "/rig save <rigName>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            MsgManager.send(sender, "&cOnly players can save rigs!");
            return true;
        }

        if (args.length < 1) {
            MsgManager.send(p, "&cUsage: " + getUsage());
            return true;
        }

        String rigName = args[0];

        // Save the rig using RigManager
        plugin.getRigManager().saveRig(p.getUniqueId(), rigName);

        MsgManager.send(p, "&aRig '&6" + rigName + "&a' saved successfully.");

        return true;
    }

    @Override
    public List<String> getPermissions() {
        return List.of("rig.save");
    }
}