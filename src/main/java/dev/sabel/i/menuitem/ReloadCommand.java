package dev.sabel.i.menuitem;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final MenuItem plugin;
    public ReloadCommand(MenuItem p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (plugin.loadConfig()) {
            sender.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "There was a fatal error in trying to reload. Check your config.");
        return true;
    }
}
