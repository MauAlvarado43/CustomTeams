package com.vch.teams.models;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private String name;
    private final TeamMember leader;
    private final List<TeamMember> members;

    public Team(String name, TeamMember leader) {
        this.name = name;
        this.leader = leader;
        this.members = new ArrayList<>();
        this.members.add(leader);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamMember getLeader() {
        return leader;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public void addMember(TeamMember player) {
        
        for(TeamMember member : members)
            if(member.getUuid().equals(player.getUuid()))
                return;

        members.add(player);
        
    }

    public void removeMember(TeamMember player) {
        for(TeamMember member : members)
            if(member.getUuid().equals(player.getUuid())) {
                members.remove(member);
                return;
            }
    }

    public boolean isLeader(TeamMember player) {
        return leader.getUuid().equals(player.getUuid());
    }

}