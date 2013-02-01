
package com.thedemgel.basicplugin.group;

import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import org.spout.api.entity.Player;


public class Group {
	private ConcurrentSkipListSet<String> members = new ConcurrentSkipListSet<String>();
	private UUID groupId;
	
	public Group() {
		this.groupId = UUID.randomUUID();
	}
	
	public Group(UUID groupId) {
		this.groupId = groupId;
	}
	
	public UUID getUUID() {
		return groupId;
	}
	
	public boolean isMember(Player player) {
		return members.contains(player.getName());
	}
	
	public void addMember(Player player) {
		members.add(player.getName());
	}
	
	public void removeMember(Player player) {
		members.remove(player.getName());
	}
}
