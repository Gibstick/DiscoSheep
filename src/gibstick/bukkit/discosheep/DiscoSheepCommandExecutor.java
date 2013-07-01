package gibstick.bukkit.discosheep;

import org.bukkit.Bukkit;
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
		if (args.length == 1) {
			if ("all".equals(args[0])) {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					parent.startParty(player);
				}
			}
			else if ("stop".equals(args[0])) {
				parent.stopAllParties();
			} else {
				sender.sendMessage("Invalid argument.");
				return true;
			}
		} else {
			if (sender instanceof Player) {
				parent.startParty((Player) sender);
			}
		}
		return true;
	}
}
