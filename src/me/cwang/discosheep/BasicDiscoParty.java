package me.cwang.discosheep;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class BasicDiscoParty extends AbstractParty {

    protected final DiscoSheep plugin;
    protected final Player player;
    // Instance properties
    private Random r;
    private ArrayList<Sheep> sheepList;
    private ArrayList<LivingEntity> guestList;
    private HashMap<EntityType, Integer> guestNumbers;
    private int duration;
    private final int radius;
    private final int period;
    private final int sheep;
    private final Location partyLocation;
    private PartyEvents partyEvents;
    private int state = 0; // basically our own tick system
    private final float volumeMultiplier;

    public BasicDiscoParty(Player player, int duration, int radius, int period, int sheep, HashMap<EntityType, Integer> guestNumbers) {
        super();
        this.plugin = DiscoSheep.getInstance();
        this.player = player;
        this.duration = duration;
        this.radius = radius;
        this.period = period;
        this.sheep = sheep;
        this.guestNumbers = guestNumbers;
        this.sheepList = new ArrayList<Sheep>();
        this.r = new Random();
        this.guestList = new ArrayList<LivingEntity>();
        this.partyLocation = player.getLocation();
        this.volumeMultiplier = Math.max(this.radius / 10, 1.0f);
    }

    @Override
    protected Player getPlayer() {
        return player;
    }

    /**
     * Make an entity jump (add some y velocity)
     *
     * @param entity
     */
    @Override
    protected void jump(Entity entity) {
        Vector orgVel = entity.getVelocity();
        Vector newVel = (new Vector()).copy(orgVel);
        newVel.add(new Vector(0, defaultSheepJump, 0));
        entity.setVelocity(newVel);
    }

    @Override
    public int getSheep() {
        return sheep;
    }

    @Override
    protected List<Sheep> getSheepList() {
        return sheepList;
    }

    @Override
    protected List<LivingEntity> getGuestList() {
        return guestList;
    }

    @Override
    protected HashMap<EntityType, Integer> getGuestMap() {
        return guestNumbers;
    }

    @Override
    protected void updateGuest(LivingEntity guest) {
        if (state % 2 == 0 && r.nextDouble() < 0.5) {
            jump(guest);
        }
    }

    @Override
    protected void updateSheep(Sheep sheep) {
        randomizeSheepColour(sheep);
        if (state % 2 == 0 && r.nextDouble() < 0.5) {
            jump(sheep);
        }
    }

    @Override
    protected void updateState() {
        duration -= period;
        state = state > 10000 ? 1 : state + 1;
    }

    @Override
    protected boolean isExpired() {
        return (duration <= 0);
    }

    @Override
    protected int getPeriod() {
        return period;
    }

    @Override
    protected int getState() {
        return state;
    }

    @Override
    protected float getVolumeMultiplier() {
        return volumeMultiplier;
    }

    @Override
    protected Location getLocation() {
        return partyLocation;
    }

    @Override
    protected Sheep spawnSheep() {
        World world = partyLocation.getWorld();
        Location loc = getRandomSpawnLocation(partyLocation.getBlockX(), player.getLocation().getBlockY(), partyLocation.getBlockZ(), world, radius);
        Sheep newSheep = (Sheep) world.spawnEntity(loc, EntityType.SHEEP);
        //newSheep.setColor(discoColours[(r.nextInt(discoColours.length))]);
        newSheep.setBreed(false);	// this prevents breeding - no event listener required
        newSheep.teleport(loc);	    // teleport is needed to set orientation
        getSheepList().add(newSheep);
        newSheep.setRemoveWhenFarAway(false);
        newSheep.setMetadata(DiscoSheep.METADATA_KEY, new FixedMetadataValue(plugin, true));
        return newSheep;
    }

    @Override
    protected Entity spawnGuest(EntityType type) {
        World world = partyLocation.getWorld();
        Location loc = getRandomSpawnLocation(partyLocation.getBlockX(), player.getLocation().getBlockY(), partyLocation.getBlockZ(), world, radius);
        LivingEntity newGuest = (LivingEntity) world.spawnEntity(loc, type);
        getGuestList().add(newGuest);
        newGuest.setRemoveWhenFarAway(false);
        newGuest.setMetadata(DiscoSheep.METADATA_KEY, new FixedMetadataValue(plugin, true));
        newGuest.setCanPickupItems(false);
        return newGuest;
    }

    /**
     * Setter for the static default settings. Takes the values from the current instance
     * and sets them as the defaults for all parties.
     */
    @Override
    public void setDefaultsFromCurrent() {
        AbstractParty.defaultDuration = this.duration;
        AbstractParty.defaultPeriod = this.period;
        AbstractParty.defaultRadius = this.radius;
        AbstractParty.defaultSheep = this.sheep;
        AbstractParty.defaultGuestNumbers = new HashMap<EntityType, Integer>(getGuestMap());
    }

    /**
     * Return a random Location within a radius around (x, y, z)
     * where y is the player's current height.
     *
     * @param x      party center
     * @param z      party center
     * @param world
     * @param radius
     * @return
     */
    public Location getRandomSpawnLocation(double x, double y, double z, World world, int radius) {
        Location loc;

		/* random point on circle with polar coordinates
         * random number must be square rooted to obtain uniform distribution
		 * otherwise the sheep are biased toward the centre */
        double rand = Math.sqrt(r.nextDouble()) * radius;
        double azimuth = r.nextDouble() * 2 * Math.PI; // radians
        x += rand * Math.cos(azimuth);
        z += rand * Math.sin(azimuth);

        loc = new Location(world, x, y, z);
        loc.setPitch(r.nextFloat() * 360 - 180);
        loc.setYaw(0);

        return loc;
    }

    /**
     * Set a random colour for a sheep
     *
     * @param sheep
     */
    protected void randomizeSheepColour(Sheep sheep) {
        sheep.setColor(discoColours[(r.nextInt(discoColours.length))]);
    }

    /**
     * Play sounds at the party's starting location.
     * These sounds are global and will play for all players.
     */
    @Override
    protected void playSounds() {
        partyLocation.getWorld().playSound(partyLocation, Sound.NOTE_BASS_DRUM, volumeMultiplier * 0.75f, 1.0f);
        if (this.state % 2 == 0) {
            partyLocation.getWorld().playSound(partyLocation, Sound.NOTE_SNARE_DRUM, volumeMultiplier * 0.8f, 1.0f);
        }

        if ((this.state + 1) % 8 == 0) {
            partyLocation.getWorld().playSound(partyLocation, Sound.NOTE_STICKS, volumeMultiplier * 1.0f, 1.0f);
        }
    }

    @Override
    protected void startListening() {
        // start listening
        partyEvents = new PartyEvents(this);
        plugin.getServer().getPluginManager().registerEvents(this.partyEvents, this.plugin);
    }

    @Override
    protected void stopListening() {
        HandlerList.unregisterAll(this.partyEvents);
    }
}
