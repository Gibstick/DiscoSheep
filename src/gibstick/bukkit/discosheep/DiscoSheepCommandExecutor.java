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
	private static final String PERMISSION_PARTY = "discosheep.party";
	private static final String PERMISSION_ALL = "discosheep.partyall";
	private static final String PERMISSION_FIREWORKS = "discosheep.fireworks";
	private static final String PERMISSION_STOP = "discosheep.stop";
	private static final String PERMISSION_SUPER = "disosheep.*";

	private boolean senderHasPerm(CommandSender sender, String permission) {
		return sender.hasPermission(permission) || sender.hasPermission(PERMISSION_SUPER);
	}

	private void noPermsMessage(CommandSender sender, String permission) {
		sender.sendMessage(ChatColor.RED + "You do not have the permission node " + ChatColor.GRAY + permission);
	}

	private boolean parseNextArg(String[] args, int i, String compare) {
		if (i < args.length - 1) {
			return args[i + 1].equalsIgnoreCase(compare);
		}
		return false;
	}

	private int parseNextIntArg(String[] args, int i) {
		if (i < args.length - 1) {
			return Integer.parseInt(args[i + 1]);
		}
		return -1;
	}

	private Double parseNextDoubleArg(String[] args, int i) {
		if (i < args.length - 1) {
			return Double.parseDouble(args[i + 1]);
		}
		return -1.0d;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		boolean isPlayer = false;
		boolean fireworks = false;
		int sheepNumber = DiscoParty.defaultSheep;
		int radius = DiscoParty.defaultRadius;
		int duration = DiscoParty.defaultDuration;
		int period = DiscoParty.defaultPeriod;

		if (sender instanceof Player) {
			player = (Player) sender;
			isPlayer = true;
		}

		if (!senderHasPerm(sender, PERMISSION_PARTY)) {
			noPermsMessage(sender, PERMISSION_PARTY);
			return true;
		}

		for (int i = 1; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("-fw")) {
				if (senderHasPerm(sender, PERMISSION_FIREWORKS)) {
					fireworks = !fireworks;
				} else {
					noPermsMessage(sender, PERMISSION_FIREWORKS);
				}
			} else if (args[i].equalsIgnoreCase("-r")) {
				radius = parseNextIntArg(args, i);

				if (radius < 1 || radius > DiscoParty.maxRadius) {
					sender.sendMessage("Radius must be an integer within the range [1, "
							+ DiscoParty.maxRadius + "]");
					return true;
				}
			} else if (args[i].equalsIgnoreCase("-n")) {
				sheepNumber = parseNextIntArg(args, i);

				if (sheepNumber < 1 || sheepNumber > DiscoParty.maxSheep) {
					sender.sendMessage("The number of sheep must be an integer within the range [1, "
							+ DiscoParty.maxSheep + "]");
					return true;
				}
			} else if (args[i].equalsIgnoreCase("-t")) {
				duration = parseNextIntArg(args, i);

				if (duration < 1 || duration > parent.toSeconds(DiscoParty.maxDuration)) {
					sender.sendMessage("The duration in ticks must be an integer within the range [1, "
							+ parent.toSeconds(DiscoParty.maxDuration) + "]");
					return true;
				}
			} else if (args[i].equalsIgnoreCase("-p")) {
				period = parseNextIntArg(args, i);

				if (period < DiscoParty.minPeriod || period > DiscoParty.maxPeriod) {
					sender.sendMessage(
							"The period in ticks must be within the range ["
							+ DiscoParty.minPeriod + ", "
							+ DiscoParty.maxPeriod + "]");
					return true;
				}
			}
		}

		if (args.length > 0) {

			if (args[0].equalsIgnoreCase("all")) {
				if (senderHasPerm(sender, PERMISSION_ALL)) {
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						parent.startParty(p, duration, sheepNumber, radius, period, fireworks);
						p.sendMessage(ChatColor.RED + "LET'S DISCO!");
					}
				} else {
					noPermsMessage(sender, PERMISSION_ALL);
				}
				return true;
			} else if (args[0].equalsIgnoreCase("stop")) {
				if (senderHasPerm(sender, PERMISSION_STOP)) {
					parent.stopAllParties();
				} else {
					noPermsMessage(sender, PERMISSION_STOP);
				}
				return true;
			} else if (args[0].equalsIgnoreCase("me")) {
				if (isPlayer) {
					parent.startParty(player, duration, sheepNumber, radius, period, fireworks);
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid argument.");
				return true;
			}

		}

		return false;
	}
}
