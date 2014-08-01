package ca.gibstick.discosheep;

import org.bukkit.Instrument;
import org.bukkit.Sound;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 *
 * @author Charlie
 */
public class PartyEvents implements Listener {

    DiscoSheep plugin = DiscoSheep.getInstance();
    DiscoParty party;
    /*
     * There will be multiple instances of PartyEvents,
     * and each instance will only listen for its own party.
     * That way, we don't have multiple instances iterating through
     * the entire parties hashmap redundantly, yet we can still
     * unregister the listeners when no parties are running.
     */

    public PartyEvents(DiscoParty party) {
        this.party = party;
    }

    // prevent sheep shearing
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerShear(PlayerShearEntityEvent e) {
        if (e.getEntity() instanceof Sheep) {
            if (party.getSheepSet().contains((Sheep) e.getEntity())) {
                e.setCancelled(true);
            }
        }
    }

    // actually make sheep and other guests invincible
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLivingEntityDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Sheep) {
            if (party.getSheepSet().contains((Sheep) e.getEntity())) {
                party.jump(e.getEntity()); // for kicks
                e.setCancelled(true);
            }
        }
        if (party.getGuestSet().contains(e.getEntity())) {
            party.jump(e.getEntity());
            e.setCancelled(true);
        }
    }

    // prevent uninvited guests from targetting players
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTargetLivingEntityEvent(EntityTargetEvent e) {
        if (party.getGuestSet().contains(e.getEntity())) {
            e.setCancelled(true);
        }
    }

    // prevent egg breeding
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
        if (party.getSheepSet().contains(e.getRightClicked()) || party.getGuestSet().contains(e.getRightClicked())) {
            e.setCancelled(true);
            e.getPlayer().playSound(e.getRightClicked().getLocation(), Sound.NOTE_BASS_GUITAR, 1.0f, party.getPentatonicNote());
        }
    }

    // prevent portal teleport
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPortalEvent(EntityPortalEvent e) {
        if (party.getSheepSet().contains(e.getEntity()) || party.getGuestSet().contains(e.getEntity())) {
            e.setCancelled(true);
        }
    }
    // prevent disco floor breaking
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent e) {
        if (party.getFloorBlocks().contains(e.getBlock())) {
            e.setCancelled(true);
        }
    }
}
