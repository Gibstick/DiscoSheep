/*
 * BaaBaaBlockSheep have you any wool?
 * Nope, event got cancelled.
 * Also listens to other events, not just sheep events
 */
package ca.gibstick.discosheep;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Mauve
 */
public class GlobalEvents implements Listener {

	DiscoSheep parent;

	public GlobalEvents(DiscoSheep parent) {
		this.parent = parent;
	}


	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		String name = e.getPlayer().getName();
		parent.stopParty(name);
		// stop party on player quit or else it will CONTINUE FOR ETERNITY
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		parent.partyOnJoin(player);
	}
}
