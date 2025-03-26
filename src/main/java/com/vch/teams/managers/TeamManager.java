package com.vch.teams.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.vch.teams.TeamPlugin;
import com.vch.teams.models.Team;
import com.vch.teams.models.TeamMember;
import com.vch.teams.status.TeamCommandStatus;

import me.neznamy.tab.api.TabAPI;

public final class TeamManager {

    private final TabAPI tabAPI;
    private final TeamPlugin plugin;
    private final File teamsFile;
    private final Gson gson = new Gson();
    private final Map<String, Team> teams = new HashMap<>();

    public TeamManager(TabAPI tabAPI, TeamPlugin plugin) {
        this.plugin = plugin;
        this.tabAPI = tabAPI;
        this.teamsFile = new File(plugin.getDataFolder(), "teams.json");
        loadTeams();
    }

    public String createTeam(Player owner, String teamName) {
        
        if(teams.containsKey(teamName)) return TeamCommandStatus.TEAM_ALREADY_EXISTS;

        Team previousTeam = getTeam(owner);
        if(previousTeam != null) return TeamCommandStatus.PLAYER_IS_OWNER;
        if(teamName.length() < 3 || teamName.length() > 20) return TeamCommandStatus.TEAM_NAME_LENGTH;

        Team team = new Team(teamName, TeamMember.fromPlayer(owner));
        teams.put(teamName, team);

        saveTeams();

        return TeamCommandStatus.TEAM_CREATED;

    }

    public String deleteTeam(Player owner) {
        
        Team team = getTeam(owner);
        if(team == null) return TeamCommandStatus.TEAM_DOES_NOT_EXIST;
        if(!team.getLeader().getUuid().equals(owner.getUniqueId())) return TeamCommandStatus.TEAM_NOT_OWNER;

        teams.remove(team.getName());
        saveTeams();

        return TeamCommandStatus.TEAM_DELETED;

    }

    public String addPlayer(Player owner, Player playerId) {

        Team team = getTeam(owner);
        if(team == null) return TeamCommandStatus.TEAM_DOES_NOT_EXIST;

        if(isPlayerInTeam(playerId))
            return TeamCommandStatus.PLAYER_ALREADY_IN_TEAM;

        if(!team.getLeader().getUuid().equals(owner.getUniqueId()))
            return TeamCommandStatus.PLAYER_IS_NOT_OWNER;

        if(team.getLeader().getUuid().equals(playerId.getUniqueId()))
            return TeamCommandStatus.PLAYER_IS_OWNER;

        if(getTeam(playerId) != null && getTeam(playerId).getLeader().getUuid().equals(owner.getUniqueId()))
            return TeamCommandStatus.PLAYER_ALREADY_IN_SAME_TEAM;
        
        team.addMember(TeamMember.fromPlayer(playerId));
        saveTeams();    

        return TeamCommandStatus.PLAYER_ADDED;

    }

    public String removePlayer(Player owner, Player member) {

        Team team = getTeam(owner);
        if(team == null) return TeamCommandStatus.TEAM_DOES_NOT_EXIST;

        if(team.getLeader().getUuid().equals(member.getUniqueId()))
            return TeamCommandStatus.PLAYER_IS_OWNER;

        boolean isInteam = false;
        for(TeamMember teamMember : team.getMembers())
            if(teamMember.getUuid().equals(member.getUniqueId()))
                isInteam = true;

        if(!isInteam) return TeamCommandStatus.PLAYER_NOT_IN_TEAM;
        team.removeMember(TeamMember.fromPlayer(member));
        saveTeams();

        return TeamCommandStatus.PLAYER_REMOVED;

    }

    public String removeSelfPlayer(Player member) {

        Team team = getTeam(member);
        if(team == null) return TeamCommandStatus.TEAM_DOES_NOT_EXIST;

        if(team.getLeader().getUuid().equals(member.getUniqueId()))
            return TeamCommandStatus.PLAYER_IS_OWNER;

        team.removeMember(TeamMember.fromPlayer(member));
        saveTeams();

        return TeamCommandStatus.PLAYER_REMOVED;

    }

    public String renameTeam(Player owner, String newName) {
        
        Team team = getTeam(owner);
        if(team == null) return TeamCommandStatus.TEAM_DOES_NOT_EXIST;
        if (!team.getLeader().getUuid().equals(owner.getUniqueId())) return TeamCommandStatus.TEAM_NOT_OWNER;
        if(teams.containsKey(newName)) return TeamCommandStatus.TEAM_ALREADY_EXISTS;

        teams.remove(team.getName());
        team.setName(newName);
        teams.put(newName, team);
        saveTeams();

        return TeamCommandStatus.TEAM_RENAMED;

    }

    public Team getTeam(OfflinePlayer player) {
        
        for(Team team : teams.values())
            for(TeamMember member : team.getMembers())
                if(member.getUuid().equals(player.getUniqueId()))
                    return team;

        return null;

    }

    public Team getTeam(Player player) {
        
        for(Team team : teams.values())
            for(TeamMember member : team.getMembers())
                if(member.getUuid().equals(player.getUniqueId()))
                    return team;

        return null;

    }

    public boolean isPlayerInTeam(Player player) {
        return getTeam(player) != null;
    }

    public boolean isPlayerOwner(Player player) {
        Team team = getTeam(player);
        if(team == null || !team.getLeader().getUuid().equals(player.getUniqueId())) return false;
        return true;
    }

    public void saveTeams() {
        if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        try (FileWriter writer = new FileWriter(teamsFile)) {
            gson.toJson(teams, writer);
        } catch (IOException e) {
            plugin.getLogger().info("Error al guardar los equipos.");
        }
    }

    public void loadTeams() {
        try {
            if(!teamsFile.exists()) return;
            try (FileReader reader = new FileReader(teamsFile)) {
                Type type = new com.google.gson.reflect.TypeToken<Map<String, Team>>(){}.getType();
                teams.putAll(gson.fromJson(reader, type));
            }
        } catch (IOException e) {
            plugin.getLogger().info("Error al cargar los equipos.");
        }
    }

}