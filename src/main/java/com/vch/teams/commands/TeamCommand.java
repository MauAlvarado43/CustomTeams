package com.vch.teams.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.vch.teams.TeamPlugin;
import com.vch.teams.managers.TeamManager;
import com.vch.teams.models.Team;
import com.vch.teams.models.TeamMember;
import com.vch.teams.status.TeamCommandStatus;

public class TeamCommand implements CommandExecutor {

    private final TeamPlugin plugin;

    public TeamCommand(TeamPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            if (sender != null) {
                sender.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "Este comando solo puede ser usado por jugadores.");
            }
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.YELLOW + "Uso: /team <create|delete|add|kick|leave|setname|info> [args]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                createCommand(player, args);
                break;

            case "delete":
                deleteCommand(player);
                break;

            case "add":
                addCommand(player, args);
                break;

            case "kick":
                kickCommand(player, args);
                break;
            case "leave":
                leaveCommand(player);
                break;
            case "setname":
                setNameCommand(player, args);
                break;
            case "info":
                infoCommand(player);
                break;
            default:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.YELLOW + "Comando desconocido. Usa: /team <create|delete|add|kick|leave|setname|info> [args]");
                break;
        }

        return true;

    }

    public void createCommand(Player player, String[] args) {

        TeamManager teamManager = plugin.getTeamManager();

        if (args.length < 2) {
            player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "Uso: /team create <nombre>");
            return;
        }

        String teamName = String.join(" ", args).substring(7);
        String status = teamManager.createTeam(player, teamName);

        switch (status) {
            case TeamCommandStatus.TEAM_CREATED:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.GREEN + "Equipo '" + teamName + "' creado.");
                break;
            case TeamCommandStatus.TEAM_NAME_LENGTH:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "El nombre del equipo debe tener entre 3 y 20 caracteres.");
                break;
            case TeamCommandStatus.PLAYER_IS_OWNER:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "Ya eres dueño de un equipo.");
                break;
            case TeamCommandStatus.TEAM_ALREADY_EXISTS:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No se pudo crear el equipo. Ya existe un equipo con ese nombre.");
                break;
            default:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No se pudo crear el equipo.");
                break;
        }

    }

    public void deleteCommand(Player player) {

        TeamManager teamManager = plugin.getTeamManager();
        String status = teamManager.deleteTeam(player);
        switch (status) {
            case TeamCommandStatus.TEAM_DELETED:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.GREEN + "Equipo eliminado.");
                break;
            case TeamCommandStatus.TEAM_DOES_NOT_EXIST:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No tienes un equipo.");
                break;
            case TeamCommandStatus.TEAM_NOT_OWNER:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "Como no eres el dueño del equipo, no puedes eliminarlo.");
                break;
            default:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No se pudo eliminar el equipo.");
                break;
        }

    }

    public void addCommand(Player player, String[] args) {

        TeamManager teamManager = plugin.getTeamManager();

        if (args.length < 2) {
            player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "Uso: /team add <jugador>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "Jugador no encontrado.");
            return;
        }

        String status = teamManager.addPlayer(player, target);
        switch (status) {
            case TeamCommandStatus.PLAYER_ADDED:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.GREEN + target.getName() + " ha sido añadido al equipo.");
                target.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.GREEN + "Has sido añadido al equipo de " + player.getName() + ".");
                break;
            case TeamCommandStatus.TEAM_DOES_NOT_EXIST:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No tienes un equipo.");
                break;
            case TeamCommandStatus.PLAYER_ALREADY_IN_TEAM:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "El jugador ya está en un equipo.");
                break;
            case TeamCommandStatus.PLAYER_IS_NOT_OWNER:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No tienes permiso para añadir jugadores.");
                break;
            case TeamCommandStatus.PLAYER_IS_OWNER:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No puedes añadirte a ti mismo al equipo.");
                break;
            case TeamCommandStatus.PLAYER_ALREADY_IN_SAME_TEAM:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "El jugador ya está en tu equipo.");
                break;
            default:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No se pudo añadir al jugador.");
                break;
        }

    }

    public void kickCommand(Player player, String[] args) {

        TeamManager teamManager = plugin.getTeamManager();

        if (args.length < 2) {
            player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "Uso: /team kick <jugador>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "Jugador no encontrado.");
            return;
        }

        String status = teamManager.removePlayer(player, target);
        switch (status) {
            case TeamCommandStatus.PLAYER_REMOVED:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.GREEN + target.getName() + " ha sido expulsado del equipo.");
                target.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "Has sido expulsado del equipo de " + player.getName() + ".");
                break;
            case TeamCommandStatus.TEAM_DOES_NOT_EXIST:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No tienes un equipo.");
                break;
            case TeamCommandStatus.PLAYER_NOT_IN_TEAM:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "El jugador no está en tu equipo.");
                break;
            case TeamCommandStatus.PLAYER_IS_OWNER:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No puedes expulsarte a ti mismo del equipo.");
                break;
            default:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No se pudo expulsar al jugador.");
                break;
        }

    }

    public void leaveCommand(Player player) {

        TeamManager teamManager = plugin.getTeamManager();
        String status = teamManager.removeSelfPlayer(player);
        switch (status) {
            case TeamCommandStatus.PLAYER_REMOVED:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.GREEN + "Has abandonado el equipo.");
                break;
            case TeamCommandStatus.TEAM_DOES_NOT_EXIST:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No tienes un equipo.");
                break;
            case TeamCommandStatus.PLAYER_IS_OWNER:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No puedes abandonar el equipo siendo el dueño.");
                break;
            default:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No se pudo abandonar el equipo.");
                break;
        }

    }

    public void setNameCommand(Player player, String[] args) {

        TeamManager teamManager = plugin.getTeamManager();

        if (args.length < 2) {
            player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "Uso: /team setname <nuevo_nombre>");
            return;
        }

        String newName = String.join(" ", args).substring(9);
        String status = teamManager.renameTeam(player, newName);
        switch (status) {
            case TeamCommandStatus.TEAM_RENAMED:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.GREEN + "El equipo ahora se llama '" + newName + "'.");
                break;
            case TeamCommandStatus.TEAM_DOES_NOT_EXIST:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No tienes un equipo.");
                break;
            case TeamCommandStatus.TEAM_NOT_OWNER:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No puedes cambiar el nombre del equipo.");
                break;
            case TeamCommandStatus.TEAM_ALREADY_EXISTS:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "Ya existe un equipo con ese nombre.");
                break;
            default:
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No se pudo cambiar el nombre del equipo.");
                break;
        }

    }

    public void infoCommand(Player player) {

        TeamManager teamManager = plugin.getTeamManager();
        Team team = teamManager.getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.RED + "No tienes un equipo.");
        } else {

            player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.GREEN + "Equipo: " + team.getName());
            player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.GREEN + "Dueño: " + team.getLeader().getName());

            if (!team.getMembers().isEmpty()) {
                player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.GREEN + "Miembros:");
                for (TeamMember member : team.getMembers()) {
                    player.sendMessage(ChatColor.BLUE + "[Teams] " + ChatColor.GREEN + "- " + member.getName());
                }
            }

        }

    }

}
