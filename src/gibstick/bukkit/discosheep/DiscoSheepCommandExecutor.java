package gibstick.bukkit.discosheep;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

public class DiscoSheepCommandExecutor implements CommandExecutor {

	private DiscoSheep parent;

	public DiscoSheepCommandExecutor(DiscoSheep parent) {
		this.parent = parent;
	}
	
	private CommandSender curSender;
	
	private static final String PERMISSION_PARTY = "discosheep.party";
	private static final String PERMISSION_ALL = "discosheep.partyall";
	private static final String PERMISSION_FIREWORKS = "discosheep.fireworks";
	private static final String PERMISSION_STOP = "discosheep.stop";
	private static final String PERMISSION_SUPER= "disosheep.*";
	
	
	private boolean senderHasPerm(String permission) {
		return curSender.hasPermission(permission) || curSender.hasPermission(PERMISSION_SUPER);
	}
	
	private void noPermsMessage(String permission) {
		curSender.sendMessage(ChatColor.RED + "You do not have the permission node " + ChatColor.GRAY + permission);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		boolean isPlayer = false;
		boolean fireworks = false;
		int sheepNumber = 0;
		int radius = 0;
		this.curSender = sender;

		if (sender instanceof Player) {
			player = (Player) sender;
			isPlayer = true;
		}

		if (!senderHasPerm(PERMISSION_PARTY)) {
			noPermsMessage(PERMISSION_PARTY);
			return true;
		}

		for (int i = 1; i < args.length; i++) {
			switch (args[i]) {
				case "-fw":
					if (senderHasPerm(PERMISSION_FIREWORKS)) {
						fireworks = !fireworks;
					} else {
						noPermsMessage(PERMISSION_FIREWORKS);
					}
			}
		}

		if (args.length > 0) {
			switch (args[0]) {
				case "all":
					if (senderHasPerm(PERMISSION_ALL))
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						parent.startParty(p, fireworks);
						p.sendMessage(ChatColor.RED + "LET'S DISCO!");
					} else {
						noPermsMessage(PERMISSION_ALL);
					}
					return true;
				case "stop":
					if (senderHasPerm(PERMISSION_STOP)) {
						parent.stopAllParties();
					} else {
						noPermsMessage(PERMISSION_STOP);
					}
					return true;
				case "me":
					if (isPlayer) {
						parent.startParty(player, fireworks);
						return true;
					}
				default:
					sender.sendMessage(ChatColor.RED + "Invalid argument.");
					return true;
			}
		}

		return false;
	}
}
