package com.vch.teams.completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TeamTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();
        
        if (command.getName().equalsIgnoreCase("team")) {
            if (args.length == 1) {
                completions.add("create");
                completions.add("delete");
                completions.add("add");
                completions.add("leave");
                completions.add("kick");
                completions.add("setname");
                completions.add("info");
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("kick")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                }
            }
        }

        return completions;

    }

}