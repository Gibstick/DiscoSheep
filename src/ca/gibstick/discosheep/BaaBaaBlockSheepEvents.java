/*
 * BaaBaaBlockSheep have you any wool?
 * Nope, event got cancelled.
 * Also listens to other events, not just sheep events
 */
package ca.gibstick.discosheep;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 *
 * @author Mauve
 */
public class BaaBaaBlockSheepEvents implements Listener {

	DiscoSheep parent;

	public BaaBaaBlockSheepEvents(DiscoSheep parent) {
		this.parent = parent;
	}

	// prevent sheep shearing
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerShear(PlayerShearEntityEvent e) {
		if (e.getEntity() instanceof Sheep) {
			for (DiscoParty party : parent.getParties()) {
				if (party.getSheepList().contains((Sheep) e.getEntity())) {
					e.setCancelled(true);
				}
			}
		}
	}

	// actually make sheep and other guests invincible
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onLivingEntityDamageEvent(EntityDamageEvent e) {
		if (e.getEntity() instanceof Sheep) {
			for (DiscoParty party : parent.getParties()) {
				if (party.getSheepList().contains((Sheep) e.getEntity())) {
					{
						party.jump((LivingEntity) e.getEntity()); // for kicks
						e.setCancelled(true);
					}
				}
			}
		}

		for (DiscoParty party : parent.getParties()) {
			if (party.getGuestList().contains(e.getEntity())) {
				party.jump((LivingEntity) e.getEntity());
				e.setCancelled(true);
			}
		}

	}

	// prevent uninvited guests from targetting players
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityTargetLivingEntityEvent(EntityTargetEvent e) {
		for (DiscoParty party : parent.getParties()) {
			if (party.getGuestList().contains(e.getEntity())) { // safe; event is only triggered by LivingEntity targetting LivingEntity
				e.setCancelled(true);
			}
		}
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
