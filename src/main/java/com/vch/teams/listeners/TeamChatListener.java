package com.vch.teams.listeners;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.vch.teams.TeamPlugin;
import com.vch.teams.managers.TeamManager;
import com.vch.teams.models.Team;
import com.vch.teams.models.TeamMember;

public class TeamChatListener implements Listener {

    private final TeamPlugin plugin;
    private final double chatRadius = 50.0;

    public TeamChatListener(TeamPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        Player sender = event.getPlayer();
        String message = event.getMessage();
        TeamManager teamManager = plugin.getTeamManager();
        Team senderTeam = teamManager.getTeam(sender);

        plugin.getLogger().log(Level.INFO, "Player {0}: {1}", new Object[]{sender.getName(), message});

        event.setCancelled(true);

        if(message.startsWith("@")) {

            if (senderTeam == null) {
                sender.sendMessage(ChatColor.RED + "No estás en un equipo.");
                return;
            }

            String teamMessage = ChatColor.GREEN + "[TEAM] " + sender.getName() + ": " + ChatColor.WHITE + message.substring(1);
            for(TeamMember member : senderTeam.getMembers()) {
                Player player = Bukkit.getPlayer(member.getUuid());
                if(player != null) player.sendMessage(teamMessage);
            }

        } 
        else if(message.startsWith("!")) {
            
            String globalMessage = ChatColor.AQUA + "[GLOBAL] " + sender.getName() + ": " + ChatColor.WHITE + message.substring(1);
            Bukkit.broadcastMessage(globalMessage);

        }
        else {

            String localMessage = ChatColor.GRAY + sender.getName() + ": " + ChatColor.WHITE + message;
            for(Player nearby : sender.getWorld().getPlayers()) {
                if(nearby.getLocation().distance(sender.getLocation()) <= chatRadius)
                    nearby.sendMessage(localMessage);
            }

        }
        
    }

}