package gibstick.bukkit.DiscoSheep;

import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Sheep;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.World;

public final class DiscoSheep extends JavaPlugin {
    
    private ArrayList<Sheep> sheepArray = new ArrayList<>();
    
    @Override
    public void onEnable(){
        getCommand("ds").setExecutor(new DiscoSheepCommandExecutor(this));
    }
    
    @Override
    public void onDisable(){
        
    } 
    
    public void spawnSheep(World world, Location loc) {
        sheepArray.add((Sheep)world.spawnEntity(loc, EntityType.SHEEP));
    }
    
    
}
