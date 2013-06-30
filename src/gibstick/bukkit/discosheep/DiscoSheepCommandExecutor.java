package gibstick.bukkit.discosheep;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

public class DiscoSheepCommandExecutor implements CommandExecutor {

	private DiscoSheep parent;

	public DiscoSheepCommandExecutor(DiscoSheep parent) {
		this.parent = parent;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			List<Player> players = new ArrayList<Player>();
			players.add((Player)sender);
			parent.startDisco(players);
		}
		return true;
	}
}
