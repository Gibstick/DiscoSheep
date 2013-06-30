package gibstick.bukkit.DiscoSheep;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;


public class DiscoSheepCommandExecutor implements CommandExecutor {
    
    private DiscoSheep plugin;
    
    public DiscoSheepCommandExecutor(DiscoSheep plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        return true;
    }
    
}
