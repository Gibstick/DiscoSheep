package me.cwang.discosheep;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Charlie on 2015-08-13.
 */
public abstract class AbstractParty {
    public static final DyeColor[] discoColours = {
            DyeColor.RED,
            DyeColor.ORANGE,
            DyeColor.YELLOW,
            DyeColor.GREEN,
            DyeColor.BLUE,
            DyeColor.LIGHT_BLUE,
            DyeColor.PINK,
            DyeColor.MAGENTA,
            DyeColor.LIME,
            DyeColor.CYAN,
            DyeColor.PURPLE,
            DyeColor.BLACK,
            DyeColor.WHITE
    };

    // Static properties
    static HashMap<EntityType, Integer> defaultGuestNumbers = new HashMap<>();
    static int defaultDuration = 300; // ticks for entire party
    static int defaultPeriod = 10; // ticks per state change
    static int defaultRadius = 5;
    static int defaultSheep = 10;
    static float defaultSheepJump = 0.35f;
    static int maxDuration = 2400; // 120 seconds
    static int maxSheep = 100;
    static int maxRadius = 100;
    static int minPeriod = 5;    // 0.25 seconds
    static int maxPeriod = 40;    // 2.0 seconds
    static HashMap<EntityType, Integer> maxGuestNumbers = new HashMap<>();
    protected BukkitRunnable updater;

    public AbstractParty() {
    }

    static HashMap<EntityType, Integer> getDefaultGuestNumbers() {
        return defaultGuestNumbers;
    }

    static HashMap<EntityType, Integer> getMaxGuestNumbers() {
        return maxGuestNumbers;
    }

    protected abstract Player getPlayer();

    protected abstract void jump(Entity entity);

    public abstract int getSheep();

    protected abstract List<Sheep> getSheepList();

    protected abstract List<LivingEntity> getGuestList();

    protected abstract HashMap<EntityType, Integer> getGuestMap();

    protected abstract int getPeriod();

    protected abstract int getState();

    protected abstract float getVolumeMultiplier();

    protected abstract Location getLocation();

    protected abstract Sheep spawnSheep();

    protected abstract boolean isExpired();

    protected abstract Entity spawnGuest(EntityType type);

    protected final void spawnAll() {
        for (int i = 0; i < getSheep(); ++i) spawnSheep();

        for (Map.Entry<EntityType, Integer> entry : getGuestMap().entrySet()) {
            for (int i = 0; i < entry.getValue(); ++i) spawnGuest(entry.getKey());
        }
    }

    protected final void removeAll() {
        for (Sheep sheep : getSheepList()) {
            sheep.removeMetadata(DiscoSheep.METADATA_KEY, DiscoSheep.getInstance());
            sheep.remove();
        }

        for (LivingEntity g : getGuestList()) {
            g.removeMetadata(DiscoSheep.METADATA_KEY, DiscoSheep.getInstance());
            g.remove();
        }

        getSheepList().clear();
        getGuestList().clear();
    }

    /**
     * Setter for the static default settings. Takes the values from the current instance
     * and sets them as the defaults for all parties.
     */
    abstract void setDefaultsFromCurrent();


    /**
     * Map an integer i to a Color enum value
     *
     * @param i
     * @return A Color enum
     */
    public final Color getColor(int i) {
        Color c = null;
        switch (i) {
            case 1:
                c = Color.AQUA;
                break;
            case 2:
                c = Color.BLACK;
                break;
            case 3:
                c = Color.BLUE;
                break;
            case 4:
                c = Color.FUCHSIA;
                break;
            case 5:
                c = Color.GRAY;
                break;
            case 6:
                c = Color.GREEN;
                break;
            case 7:
                c = Color.LIME;
                break;
            case 8:
                c = Color.MAROON;
                break;
            case 9:
                c = Color.NAVY;
                break;
            case 10:
                c = Color.OLIVE;
                break;
            case 11:
                c = Color.ORANGE;
                break;
            case 12:
                c = Color.PURPLE;
                break;
            case 13:
                c = Color.RED;
                break;
            case 14:
                c = Color.SILVER;
                break;
            case 15:
                c = Color.TEAL;
                break;
            case 16:
                c = Color.WHITE;
                break;
            case 17:
                c = Color.YELLOW;
                break;
        }

        return c;
    }

    /**
     * Advance the internal tick state of the party by 1.
     */
    protected abstract void updateState();

    /**
     * Method called on all sheep to update the sheep. Could involve colour update,
     * a jump, etc.
     *
     * @param sheep the sheep to update
     */
    protected abstract void updateSheep(Sheep sheep);

    /**
     * Method called on all guests for updates. Could involve jumping, teleporting, etc.
     *
     * @param guest The mob to update.
     */
    protected abstract void updateGuest(LivingEntity guest);

    /**
     * Template method that updates all guests using the above functions.
     */
    protected final void updateAllGuests() {
        for (Sheep sheeple : getSheepList()) {
            updateSheep(sheeple);
        }

        for (LivingEntity guest : getGuestList()) {
            updateGuest(guest);
        }
    }

    /**
     * Schedule an update to run after a constant period. This drives the party.
     */
    protected final void scheduleUpdate() {
        updater = new BukkitRunnable() {
            @Override
            public void run() {
                updateParty();
            }
        };
        updater.runTaskLater(DiscoSheep.getInstance(), getPeriod());
    }

    /**
     * Register the event handler to start listening for the party's events, such as sheep damage.
     */
    protected abstract void startListening();

    /**
     * Unregister the event handler
     */
    protected abstract void stopListening();

    /**
     * Plays the sound for a party.
     */
    protected abstract void playSounds();

    /**
     * Template method that performs one update on the entire party.
     */
    protected final void updateParty() {
        if (!isExpired()) {
            updateAllGuests();
            playSounds();
            scheduleUpdate();
            updateState();
        } else {
            stopDisco();
        }
    }


    /**
     * Initialize things and start a BasicDiscoParty.
     */
    public final void startDisco() {
        spawnAll();
        updateParty();
        DiscoSheep.getInstance().addParty(getPlayer().getName(), this);
        startListening();
    }

    /**
     * Stop a BasicDiscoParty. Can be called more than once.
     */
    public final void stopDisco() {
        removeAll();
        if (updater != null) {
            updater.cancel();
        }
        updater = null;
        DiscoSheep.getInstance().removeParty(getPlayer().getName());
        stopListening();
    }
}
