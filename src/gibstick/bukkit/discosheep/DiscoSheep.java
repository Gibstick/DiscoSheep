package gibstick.bukkit.DiscoSheep;

import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Sheep;

public final class DiscoSheep extends JavaPlugin {
    
    private ArrayList<Sheep> SheepArray = new ArrayList<>();
    
    @Override
    public void onEnable(){
        getCommand("ds").setExecutor(new DiscoSheepCommandExecutor(this));
    }
    
    @Override
    public void onDisable(){
        
    } 
    
    
}
