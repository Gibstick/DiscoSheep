/*
 * BaaBaaBlockSheep have you any wool?
 * Nope, event got cancelled.
 * Also listens to other events, not just sheep events
 */
package ca.gibstick.discosheep;

import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 *
 * @author Mauve
 */
public class BaaBaaBlockSheepEvents implements Listener {

	DiscoSheep parent;
	static DiscoSheepCommandExecutor CommExec;

	public BaaBaaBlockSheepEvents(DiscoSheep parent) {
		this.parent = parent;
	}

	// prevent sheep shearing
	@EventHandler
	public void onPlayerShear(PlayerShearEntityEvent e) {
		if (e.getEntity() instanceof Sheep) {
			for (DiscoParty party : parent.getParties()) {
				if (party.getSheep().contains((Sheep) e.getEntity())) {
					e.setCancelled(true);
				}
			}
		}
	}

	// actually make sheep invincible
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent e) {
		if (e.getEntity() instanceof Sheep) {
			for (DiscoParty party : parent.getParties()) {
				if (party.getSheep().contains((Sheep) e.getEntity())) {
					{
						party.jumpSheep((Sheep) e.getEntity()); // for kicks
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		String name = e.getPlayer().getName();
		parent.stopParty(name);
		// stop party on player quit or else it will CONTINUE FOR ETERNITY
	}
}
