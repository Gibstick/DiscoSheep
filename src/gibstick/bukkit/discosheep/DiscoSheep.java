package gibstick.bukkit.DiscoSheep;

import java.util.ArrayList;
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
        
        for (int i = 0; i < num; i++) {
            
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
	
	public void playSounds(){
		// TODO: generate list of players to send sounds to
	}
	
	public void playSounds(Player player){
		//TODO: Add sound playing here
	}
	
	/*
		Called after discosheep is stopped
	*/
	public void cleanUp(){
		removeAllSheep();
	}
	
	void scheduleUpdate(){
		updater.runTaskLater(updater,frequency);
	}
	
	public void startDisco(int frequency, int duration){
		updater.start(frequency, duration);
	}
	
	public void startDisco(){
		this.startDisco();
	}
	
	public void stopDisco(){
		updater.stop();
	}
}
