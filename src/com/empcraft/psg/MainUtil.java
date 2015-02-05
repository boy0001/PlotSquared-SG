package com.empcraft.psg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MainUtil {

    public static boolean sendMessage(final String msg) {
        return sendMessage(null, msg);
    }

    public static boolean sendMessage(final Player plr, final String msg) {
        if ((msg.length() > 0)) {
            if (plr == null) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            } else {
                plr.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
        return true;
    }

    public static boolean hasPermission(final Player player, final String perm) {
        if ((player == null) || player.isOp()) {
            return true;
        }
        if (player.hasPermission(perm)) {
            return true;
        }
        final String[] nodes = perm.split("\\.");
        final StringBuilder n = new StringBuilder();
        for (int i = 0; i < (nodes.length - 1); i++) {
            n.append(nodes[i] + ".");
            if (player.hasPermission(n + "*")) {
                return true;
            }
        }
        return false;
    }

    public static String colorize(final String line) {
        return ChatColor.translateAlternateColorCodes('&', line);
    }
}
