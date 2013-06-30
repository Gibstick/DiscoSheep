package gibstick.bukkit.discosheep;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscoSheep extends JavaPlugin {

	private ArrayList<Sheep> sheepArray = new ArrayList<Sheep>();
	private DiscoUpdater updater = new DiscoUpdater(this);
	// radius for random sheep spawns around player
	private static int sheepSpawnRadius = 5;

	@Override
	public void onEnable() {
		getCommand("ds").setExecutor(new DiscoSheepCommandExecutor(this));
	}

	@Override
	public void onDisable() {
		// Watashi Wa Kawaii, Ne?
	}
	
	ArrayList<Sheep> getSheep(){
		return sheepArray;
	}

	void spawnSheep(World world, Location loc) {
		Sheep newSheep = (Sheep) world.spawnEntity(loc, EntityType.SHEEP);
		newSheep.setMaxHealth(10000);
		newSheep.setHealth(10000);
		getSheep().add(newSheep);
	}

	// Spawn some number of sheep next to given player
	void spawnSheep(Player player, int num) {
		Location loc;
		World world = player.getWorld();

		for (int i = 0; i < num; i++) {
			double x, y, z;

			// random x and z coordinates within a 5 block radius
			// safe y-coordinate
			x = -sheepSpawnRadius + (Math.random() * ((sheepSpawnRadius * 2) + 1)) + player.getLocation().getX();
			z = -sheepSpawnRadius + (Math.random() * ((sheepSpawnRadius * 2) + 1)) + player.getLocation().getZ();
			y = world.getHighestBlockYAt((int) x, (int) z);
			loc = new Location(world, x, y, z);
			spawnSheep(world, loc);
		}
	}

	// Mark all sheep in the sheep array for removal, then clear the array
	void removeAllSheep() {
		for (Sheep sheep: getSheep()) {
			sheep.remove();
		}
		getSheep().clear();
	}

	// Cycle colours of all sheep in the array
	void cycleSheepColours() {
		for (Sheep shep : getSheep()) {
			//sheepArray.get(i) something something
		}
	}

	void playSounds() {
		// TODO: generate list of players to send sounds to
	}

	void playSounds(Player player) {
		//TODO: Add sound playing here
	}

	//	Called after discosheep is stopped
	void cleanUp() {
		removeAllSheep();
	}

	void scheduleUpdate() {
		updater.runTaskLater((Plugin) updater, updater.frequency);
	}

	void startDisco(int frequency, int duration) {
		updater.start(frequency, duration);
	}

	void startDisco() {
		this.startDisco();
	}

	void stopDisco() {
		updater.stop();
	}
}
