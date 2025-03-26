package com.vch.teams.models;

import java.util.UUID;

import org.bukkit.entity.Player;

public class TeamMember {
  
  private UUID uuid;
  private String name;

  public TeamMember(UUID uuid, String name) {
    this.uuid = uuid;
    this.name = name;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public static TeamMember fromPlayer(Player player) {
    return new TeamMember(player.getUniqueId(), player.getName());
  }

}