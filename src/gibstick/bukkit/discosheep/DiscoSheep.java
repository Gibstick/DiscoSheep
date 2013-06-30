package gibstick.bukkit.DiscoSheep;

import java.util.ArrayList;
import java.lang.Math;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Sheep;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class DiscoSheep extends JavaPlugin {

    private ArrayList<Sheep> sheepArray = new ArrayList<>();
	private DiscoUpdater updater = new DiscoUpdater();
	
    @Override
    public void onEnable() {
        getCommand("ds").setExecutor(new DiscoSheepCommandExecutor(this));
    }

    @Override
    public void onDisable() {
        // Watashi Wa Kawaii, Ne?
    }

    public void spawnSheep(World world, Location loc) {
        Sheep newSheep = (Sheep) world.spawnEntity(loc, EntityType.SHEEP);
        newSheep.setMaxHealth(10000);
        newSheep.setHealth(10000);
        sheepArray.add(newSheep);
    }

    // Spawn some number of sheep next to given player
    public void spawnSheep(Player player, int num) {
        Location loc;
        World world = player.getWorld();

        for (int i = 0; i < num; i++) {
            double x, y, z;

            // random x and z coordinates within a 5 block radius
            // safe y-coordinate
            x = -5 + (Math.random() * ((5 - (-5)) + 1)) + player.getLocation().getX();
            z = -5 + (Math.random() * ((5 - (-5)) + 1)) + player.getLocation().getZ();
            y = world.getHighestBlockYAt((int) x, (int) z);
            loc = new Location(world, x, y, z);
            
            spawnSheep(world, loc);
        }
    }

    // Mark all sheep in the sheep array for removal
    public void removeAllSheep() {
        for (int i = 0; i < sheepArray.size(); i++) {
            sheepArray.get(i).remove();
        }
        sheepArray.clear();
    }

    // Cycle colours of all sheep in the array
    public void cycleSheepColours() {
        for (int i = 0; i < sheepArray.size(); i++) {
            //sheepArray.get(i) something something
        }
    }
}
