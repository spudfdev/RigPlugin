package me.kakaroot.rigPlugin.managers;

import me.kakaroot.rigPlugin.RigPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MsgManager {

    public static String prefix;

    public static void init(RigPlugin plugin) {
        String raw = plugin.getConfig().getString("prefix", "&6[Rig] ");
        if (!raw.endsWith("&r ")) raw += "&r ";
        prefix = colourise(raw);
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(prefix + colourise(message));
    }

    public static String colourise(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
