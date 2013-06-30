package gibstick.bukkit.DiscoSheep;

import org.bukkit.plugin.java.JavaPlugin;

public final class DiscoSheep extends JavaPlugin {
    
    @Override
    public void onEnable(){
        getCommand("ds").setExecutor(new DiscoSheepCommandExecutor());
    }
    
    @Override
    public void onDisable(){
        
    }
}
