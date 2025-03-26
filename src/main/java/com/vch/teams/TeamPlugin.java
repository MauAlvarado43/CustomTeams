package com.vch.teams;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.vch.teams.commands.TeamCommand;
import com.vch.teams.completers.TeamTabCompleter;
import com.vch.teams.expansions.TeamExpansion;
import com.vch.teams.listeners.TeamChatListener;
import com.vch.teams.managers.TeamManager;

import me.neznamy.tab.api.TabAPI;

public class TeamPlugin extends JavaPlugin {

    private TeamManager teamManager;
    private TabAPI tabAPI;

    @Override
    public void onEnable() {

        this.tabAPI = TabAPI.getInstance();
        if(this.tabAPI == null) {
            getLogger().warning("TAB Plugin no encontrado, el sistema de equipos no funcionará correctamente.");
            return;
        }

        this.teamManager = new TeamManager(this.tabAPI, this);
        getCommand("team").setTabCompleter(new TeamTabCompleter());
        getCommand("team").setExecutor(new TeamCommand(this));
        getServer().getPluginManager().registerEvents(new TeamChatListener(this), this);
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TeamExpansion(this, teamManager).register();
            System.out.println("TeamExpansion registrado!");
        }
        else {
            getLogger().warning("PlaceholderAPI no encontrado, el sistema de equipos no funcionará correctamente.");
        }
        
        getLogger().info("TeamPlugin activado!");
        
    }

    @Override
    public void onDisable() {
        getLogger().info("TeamPlugin desactivado!");
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

}