package ca.gibstick.discosheep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

/**
 *
 * @author Georgiy
 */
public class DiscoParty {

	private DiscoSheep ds;
	private Player player;
	private ArrayList<Sheep> sheepList = new ArrayList<Sheep>();
	private ArrayList<LivingEntity> guestList = new ArrayList<LivingEntity>();
	static int defaultDuration = 300; // ticks for entire party
	static int defaultPeriod = 10; // ticks per state change
	static int defaultRadius = 5;
	static int defaultSheep = 10;
	static float defaultSheepJump = 0.35f;
	static int maxDuration = 2400; // 120 seconds
	static int maxSheep = 100;
	static int maxRadius = 100;
	static int minPeriod = 5;	// 0.25 seconds
	static int maxPeriod = 40;	// 2.0 seconds
	private HashMap<String, Integer> guestNumbers = new HashMap<String, Integer>();
	static HashMap<String, Integer> defaultGuestNumbers = new HashMap<String, Integer>();
	private boolean doFireworks = false;
	private boolean doJump = true;
	private int duration, period, radius, sheep;
	private int state = 0; // basically our own tick system
	private DiscoUpdater updater;
	private static final DyeColor[] discoColours = {
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

	public DiscoParty(DiscoSheep parent, Player player) {
		this.ds = parent;
		this.player = player;
		this.duration = DiscoParty.defaultDuration;
		this.period = DiscoParty.defaultPeriod;
		this.radius = DiscoParty.defaultRadius;
		this.sheep = DiscoParty.defaultSheep;

		this.guestNumbers = DiscoParty.getDefaultGuestNumbers();
	}

	public DiscoParty(DiscoSheep parent) {
		this.ds = parent;
		this.duration = DiscoParty.defaultDuration;
		this.period = DiscoParty.defaultPeriod;
		this.radius = DiscoParty.defaultRadius;
		this.sheep = DiscoParty.defaultSheep;
		this.guestNumbers = DiscoParty.getDefaultGuestNumbers();
	}

	// copy but with new player
	// used for /ds other and /ds all
	public DiscoParty DiscoParty(Player player) {
		DiscoParty newParty = new DiscoParty(this.ds, player);
		newParty.doFireworks = this.doFireworks;
		newParty.duration = this.duration;
		newParty.period = this.period;
		newParty.radius = this.radius;
		newParty.sheep = this.sheep;
		return newParty;
	}

	ArrayList<Sheep> getSheepList() {
		return sheepList;
	}

	ArrayList<LivingEntity> getGuestList() {
		return guestList;
	}

	public static HashMap<String, Integer> getDefaultGuestNumbers() {
		return defaultGuestNumbers;
	}

	public int getSheep() {
		return this.sheep;
	}

	public DiscoParty setPlayer(Player player) {
		if (player != null) {
			this.player = player;
			return this;
		} else {
			throw new NullPointerException();
		}
	}

	public DiscoParty setDuration(int duration) throws IllegalArgumentException {
		if (duration <= DiscoParty.maxDuration && duration > 0) {
			this.duration = duration;
			return this;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public DiscoParty setPeriod(int period) throws IllegalArgumentException {
		if (period >= DiscoParty.minPeriod && period <= DiscoParty.maxPeriod) {
			this.period = period;
			return this;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public DiscoParty setRadius(int radius) throws IllegalArgumentException {
		if (radius <= DiscoParty.maxRadius && radius > 0) {
			this.radius = radius;
			return this;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public DiscoParty setDenseRadius(int sheepNo) throws IllegalArgumentException {
		Integer r = (int) Math.floor(Math.sqrt(sheep / Math.PI));
		if (r > DiscoParty.maxRadius) {
			r = DiscoParty.maxRadius;
		}
		if (r < 1) {
			r = 1;
		}

		this.setRadius(r);
		return this;
	}

	public DiscoParty setSheep(int sheep) throws IllegalArgumentException {
		if (sheep <= DiscoParty.maxSheep && sheep > 0) {
			this.sheep = sheep;
			return this;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public DiscoParty setDoFireworks(boolean doFireworks) {
		this.doFireworks = doFireworks;
		return this;
	}

	// use current settings as new defaults
	public DiscoParty setDefaultsFromCurrent() {
		DiscoParty.defaultDuration = this.duration;
		DiscoParty.defaultPeriod = this.period;
		DiscoParty.defaultRadius = this.radius;
		DiscoParty.defaultSheep = this.sheep;
		return this;
	}

	Location getRandomSpawnLocation(double x, double z, World world, int spawnRadius) {
		Location loc;

		double y;


		/* random point on circle with polar coordinates
		 * random number must be square rooted to obtain uniform distribution
		 * otherwise the sheep are biased toward the centre */
		double r = Math.sqrt(Math.random()) * spawnRadius;
		double azimuth = Math.random() * 2 * Math.PI; // radians
		x += r * Math.cos(azimuth);
		z += r * Math.sin(azimuth);
		y = world.getHighestBlockYAt((int) x, (int) z);

		loc = new Location(world, x, y, z);
		loc.setPitch((float) Math.random() * 360 - 180);
		loc.setYaw(0);

		return loc;
	}

	// Spawn some number of guests next to given player
	void spawnAll(int sheep, int spawnRadius) {
		Location loc;
		World world = player.getWorld();


		double x = player.getLocation().getX();
		double z = player.getLocation().getZ();
		for (int i = 0; i < sheep; i++) {
			loc = getRandomSpawnLocation(x, z, world, spawnRadius);
			spawnSheep(world, loc);
		}

		// loop through hashmap of other guests and spawn accordingly
		for (Map.Entry entry : guestNumbers.entrySet()) {
			EntityType ent = EntityType.fromName((String) entry.getKey());
			int num = (Integer) entry.getValue();

			for (int i = 0; i < num; i++) {
				loc = getRandomSpawnLocation(x, z, world, spawnRadius);
				spawnGuest(world, loc, ent);
			}
		}
	}

	void spawnSheep(World world, Location loc) {
		Sheep newSheep = (Sheep) world.spawnEntity(loc, EntityType.SHEEP);
		newSheep.setColor(discoColours[(int) (Math.random() * (discoColours.length - 1))]);
		newSheep.setBreed(false);	// this prevents breeding - no event listener required
		newSheep.teleport(loc);	// teleport is needed to set orientation
		getSheepList().add(newSheep);
	}

	void spawnGuest(World world, Location loc, EntityType type) {
		LivingEntity newGuest = (LivingEntity) world.spawnEntity(loc, type);
		getGuestList().add(newGuest);
		ds.getLogger().log(Level.INFO, "SPAWNING GUEST");
	}

	// Mark all guests for removal, then clear the array
	void removeAll() {
		for (Sheep sheeple : getSheepList()) {
			sheeple.remove();
		}
		for (LivingEntity guest : getGuestList()) {
			guest.remove();
		}
		getSheepList().clear();
		getGuestList().clear();
	}

	// Set a random colour for all sheep in array
	void randomizeSheepColour(Sheep sheep) {
		sheep.setColor(discoColours[(int) Math.round(Math.random() * (discoColours.length - 1))]);
	}

	void jumpSheep(Sheep sheep) {
		Vector orgVel = sheep.getVelocity();
		Vector newVel = (new Vector()).copy(orgVel);
		newVel.add(new Vector(0, defaultSheepJump, 0));
		sheep.setVelocity(newVel);
	}

	// WHY ISN'T THERE A Color.getValue() ?!?!?!?!
	private Color getColor(int i) {
		Color c = null;
		if (i == 1) {
			c = Color.AQUA;
		}
		if (i == 2) {
			c = Color.BLACK;
		}
		if (i == 3) {
			c = Color.BLUE;
		}
		if (i == 4) {
			c = Color.FUCHSIA;
		}
		if (i == 5) {
			c = Color.GRAY;
		}
		if (i == 6) {
			c = Color.GREEN;
		}
		if (i == 7) {
			c = Color.LIME;
		}
		if (i == 8) {
			c = Color.MAROON;
		}
		if (i == 9) {
			c = Color.NAVY;
		}
		if (i == 10) {
			c = Color.OLIVE;
		}
		if (i == 11) {
			c = Color.ORANGE;
		}
		if (i == 12) {
			c = Color.PURPLE;
		}
		if (i == 13) {
			c = Color.RED;
		}
		if (i == 14) {
			c = Color.SILVER;
		}
		if (i == 15) {
			c = Color.TEAL;
		}
		if (i == 16) {
			c = Color.WHITE;
		}
		if (i == 17) {
			c = Color.YELLOW;
		}

		return c;
	}

	void updateAllSheep() {
		for (Sheep sheeple : getSheepList()) {
			randomizeSheepColour(sheeple);

			if (doFireworks && state % 8 == 0) {
				if (Math.random() < 0.50) {
					spawnRandomFireworkAtSheep(sheeple);
				}
			}

			if (doJump) {
				if (state % 2 == 0) {
					if (Math.random() < 0.5) {
						jumpSheep(sheeple);
					}
				}
			}
		}
	}

	void playSounds() {
		player.playSound(player.getLocation(), Sound.NOTE_BASS_DRUM, 1.0f, 1.0f);
		if (this.state % 2 == 0) {
			player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 1.0f, 1.0f);
		}
		if (this.state % 4 == 0) {
			player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0f, 1.0f);
		}
		player.playSound(player.getLocation(), Sound.BURP, 0.5f, (float) Math.random() + 1);
	}

	void randomizeFirework(Firework firework) {
		Random r = new Random();
		Builder effect = FireworkEffect.builder();
		FireworkMeta meta = firework.getFireworkMeta();

		// construct [1, 3] random colours
		int numColours = r.nextInt(3) + 1;
		Color[] colourArray = new Color[numColours];
		for (int i = 0; i < numColours; i++) {
			colourArray[i] = getColor(r.nextInt(17) + 1);
		}

		// randomize effects
		effect.withColor(colourArray);
		effect.flicker(r.nextDouble() < 0.5);
		effect.trail(r.nextDouble() < 0.5);
		effect.with(FireworkEffect.Type.values()[r.nextInt(FireworkEffect.Type.values().length)]);

		// set random effect and randomize power
		meta.addEffect(effect.build());
		meta.setPower(r.nextInt(2) + 1);

		// apply it to the given firework
		firework.setFireworkMeta(meta);
	}

	void spawnRandomFireworkAtSheep(Sheep sheep) {
		Firework firework = (Firework) sheep.getWorld().spawnEntity(sheep.getEyeLocation(), EntityType.FIREWORK);
		randomizeFirework(firework);
	}

	void update() {
		if (duration > 0) {
			updateAllSheep();
			playSounds();
			duration -= period;
			this.scheduleUpdate();
			if (state < 10000) {
				this.state++;
			} else {
				state = 1; // prevent overflow
			}
		} else {
			this.stopDisco();
		}
	}

	void scheduleUpdate() {
		updater = new DiscoUpdater(this);
		updater.runTaskLater(ds, this.period);
	}

	@Deprecated
	void startDisco(int duration, int sheepAmount, int radius, int period, boolean fireworks) {
		if (this.duration > 0) {
			stopDisco();
		}
		this.spawnAll(sheepAmount, radius);
		this.doFireworks = fireworks;
		this.period = period;
		this.duration = duration;
		this.scheduleUpdate();
		ds.getPartyMap().put(this.player.getName(), this);
	}

	void startDisco() {
		this.spawnAll(sheep, radius);
		this.scheduleUpdate();
		ds.getPartyMap().put(this.player.getName(), this);
	}

	void stopDisco() {
		removeAll();
		this.duration = 0;
		if (updater != null) {
			updater.cancel();
		}
		updater = null;
		ds.getPartyMap().remove(this.player.getName());
	}
}
