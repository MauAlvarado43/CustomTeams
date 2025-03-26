package com.vch.teams.expansions;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.vch.teams.TeamPlugin;
import com.vch.teams.managers.TeamManager;
import com.vch.teams.models.Team;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;

public class TeamExpansion extends PlaceholderExpansion {

    private final TeamPlugin plugin;
    private final TeamManager teamManager;

    public TeamExpansion(TeamPlugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
    }

    @Override
    public String getIdentifier() {
        return "vchteam";
    }

    @Override
    public String getAuthor() {
        return "VCH";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Team playerTeam = teamManager.getTeam(player);
        if(playerTeam == null) return "";
        return ChatColor.WHITE + "[" + playerTeam.getName() + ChatColor.WHITE + "]";
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        Team playerTeam = teamManager.getTeam(player);
        if(playerTeam == null) return "";
        return ChatColor.WHITE + "[" + playerTeam.getName() + ChatColor.WHITE + "]";
    }

}
