package me.cwang.discosheep;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 * @author Charlie
 */
public class PartyEvents implements Listener {

    DiscoSheep plugin = DiscoSheep.getInstance();
    BasicDiscoParty party;
    /*
     * There will be multiple instances of PartyEvents,
     * and each instance will only listen for its own party.
     * That way, we don't have multiple instances iterating through
     * the entire parties hashmap redundantly, yet we can still
     * unregister the listeners when no parties are running.
     */

    public PartyEvents(BasicDiscoParty party) {
        this.party = party;
    }

    /**
     * Predicate that returns true if the Entity e is from the given DiscoParty.
     *
     * @param e The entity to check
     * @return True if the Entity is in any DiscoParty
     */
    private boolean isFromParty(Entity e) {
        return e.hasMetadata(DiscoSheep.METADATA_KEY);
    }

    /**
     * Handler that prevents sheep shearing
     *
     * @param e The event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerShear(PlayerShearEntityEvent e) {
        if (e.getEntity() instanceof Sheep && isFromParty(e.getEntity())) {
            e.setCancelled(true);
        }
    }

    /**
     * Make all party mobs invincible by cancelling the damage event.
     * Mobs will instead jump when taking damage.
     * @param e The damage event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLivingEntityDamageEvent(EntityDamageEvent e) {
        if (isFromParty(e.getEntity())) {
            party.jump(e.getEntity()); // for kicks
            e.setCancelled(true);
        }
    }

    /**
     * Pacify monsters that are part of parties by preventing them from targeting players.
     * @param e The target event.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTargetLivingEntityEvent(EntityTargetEvent e) {
        if (isFromParty(e.getEntity())) {
            e.setCancelled(true);
        }
    }

    /**
     * Prevent egg breeding and other shenanigans from party members by disabling all
     * right click events.
     * @param e The right click event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
        if (isFromParty(e.getRightClicked())) {
            e.setCancelled(true);
        }
    }

    /**
     * Prevent party members from going through portals (end, nether).
     * @param e The portal event.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPortalEvent(EntityPortalEvent e) {
        if (isFromParty(e.getEntity())) {
            e.setCancelled(true);
        }
    }

}
