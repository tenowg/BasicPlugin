
package com.thedemgel.basicplugin.component;

import java.util.UUID;
import org.spout.api.component.type.EntityComponent;


public class GroupComponent extends EntityComponent {
	private UUID groupUID;
	private String leader;

	@Override
	public void onAttached() {
		// 1) Check if group exists, if not, either create a new one, or 
		//    detach if logging back into a dead group.
		
		// 2) Add player to GroupHandler.
	}
	
	@Override
	public void onDetached() {
		// Tell master GroupHandler to remove member from group.
	}
	
	public void setLeader(String value) {
		this.leader = value;
	}
}
