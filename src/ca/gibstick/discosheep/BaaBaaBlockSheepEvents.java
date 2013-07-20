/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.gibstick.discosheep;

import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
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
						party.jumpSheep((Sheep) e.getEntity());
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
	}
}
