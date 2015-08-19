package me.cwang.discosheep;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscoSheep extends JavaPlugin {

	static final String PERMISSION_PARTY = "discosheep.party.me";
	static final String PERMISSION_ALL = "discosheep.party.all";
	static final String PERMISSION_FIREWORKS = "discosheep.party.fireworks";
	static final String PERMISSION_STOPALL = "discosheep.admin.stopall";
	static final String PERMISSION_RELOAD = "discosheep.admin.reload";
	static final String PERMISSION_OTHER = "discosheep.party.other";
	static final String PERMISSION_CHANGEPERIOD = "discosheep.party.changeperiod";
	static final String PERMISSION_CHANGEDEFAULTS = "discosheep.admin.changedefaults";
	static final String PERMISSION_SAVECONFIG = "discosheep.admin.saveconfig";
	static final String PERMISSION_ONJOIN = "discosheep.party.onjoin";
	static final String PERMISSION_SPAWNGUESTS = "discosheep.party.spawnguests";
	static final String PERMISSION_TOGGLEPARTYONJOIN = "discosheep.admin.toggleonjoin";
	static final String PERMISSION_LIGHTNING = "discosheep.party.lightning";
    static final String METADATA_KEY = "discosheep123u1ohads";
    static final HashSet<EntityType> VALID_GUESTS = new HashSet<>(Arrays.asList( // list of valid guests
            EntityType.BAT,
            EntityType.BLAZE,
            EntityType.CAVE_SPIDER,
            EntityType.CHICKEN,
            EntityType.COW,
            EntityType.CREEPER,
            EntityType.ENDERMAN,
            EntityType.ENDERMITE,
            EntityType.GHAST,
            EntityType.GIANT,
            EntityType.IRON_GOLEM,
            EntityType.HORSE,
            EntityType.MAGMA_CUBE,
            EntityType.MUSHROOM_COW,
            EntityType.OCELOT,
            EntityType.PIG_ZOMBIE,
            EntityType.RABBIT,
            EntityType.SKELETON,
            EntityType.SLIME,
            EntityType.SPIDER,
            EntityType.SQUID,
            EntityType.VILLAGER,
            EntityType.WOLF,
            EntityType.ZOMBIE
    ));
	static boolean partyOnJoin = true;
	Map<String, AbstractParty> parties = new HashMap<>();
    static DiscoSheep instance = null;

    public static DiscoSheep getInstance() {
        if (instance == null) {
            instance = new DiscoSheep();
        }
        return instance;
    }

	@Override
	public void onEnable() {
        DiscoSheep.instance = this;
		getCommand("ds").setExecutor(new DiscoCommands());
		getServer().getPluginManager().registerEvents(new GlobalEvents(this), this);

		getConfig().addDefault("on-join.enabled", partyOnJoin);
		getConfig().addDefault("max.sheep", BasicDiscoParty.maxSheep);
		getConfig().addDefault("max.radius", BasicDiscoParty.maxRadius);
		getConfig().addDefault("max.duration", toSeconds_i(BasicDiscoParty.maxDuration));
		getConfig().addDefault("max.period-ticks", BasicDiscoParty.maxPeriod);
		getConfig().addDefault("min.period-ticks", BasicDiscoParty.minPeriod);
		getConfig().addDefault("default.sheep", BasicDiscoParty.defaultSheep);
		getConfig().addDefault("default.radius", BasicDiscoParty.defaultRadius);
		getConfig().addDefault("default.duration", toSeconds_i(BasicDiscoParty.defaultDuration));
		getConfig().addDefault("default.period-ticks", BasicDiscoParty.defaultPeriod);
		
		/*
		 * Iterate through all live entities and create default configuration values for them
		 */
		for (EntityType ent : EntityType.values()) {
			if (VALID_GUESTS.contains(ent)) {
				getConfig().addDefault("default.guests." + ent.toString(), 0);
				getConfig().addDefault("max.guests." + ent.toString(), 0);
			}
		}

		loadConfigFromDisk();
	}

	void loadConfigFromDisk() {
		getConfig().options().copyDefaults(true);
		saveConfig();

		partyOnJoin = getConfig().getBoolean("on-join.enabled");
		BasicDiscoParty.maxSheep = getConfig().getInt("max.sheep");
		BasicDiscoParty.maxRadius = getConfig().getInt("max.radius");
		BasicDiscoParty.maxDuration = toTicks(getConfig().getInt("max.duration"));
		BasicDiscoParty.maxPeriod = getConfig().getInt("max.period-ticks");
		BasicDiscoParty.minPeriod = getConfig().getInt("min.period-ticks");
		BasicDiscoParty.defaultSheep = getConfig().getInt("default.sheep");
		BasicDiscoParty.defaultRadius = getConfig().getInt("default.radius");
		BasicDiscoParty.defaultDuration = toTicks(getConfig().getInt("default.duration"));
		BasicDiscoParty.defaultPeriod = getConfig().getInt("default.period-ticks");


        // TODO: Catch IllegalArgumentException thrown by valueOf
		for (String key : getConfig().getConfigurationSection("default.guests").getKeys(false)) {
			AbstractParty.getDefaultGuestNumbers().put(EntityType.valueOf(key), getConfig().getInt("default.guests." + key));
		}

		for (String key : getConfig().getConfigurationSection("max.guests").getKeys(false)) {
			AbstractParty.getMaxGuestNumbers().put(EntityType.valueOf(key), getConfig().getInt("max.guests." + key));
		}

	}

	void reloadConfigFromDisk() {
		reloadConfig();
		loadConfigFromDisk();
	}

	void saveConfigToDisk() {
		getConfig().set("on-join.enabled", partyOnJoin);
		getConfig().set("default.sheep", BasicDiscoParty.defaultSheep);
		getConfig().set("default.radius", BasicDiscoParty.defaultRadius);
		getConfig().set("default.duration", toSeconds_i(BasicDiscoParty.defaultDuration));
		getConfig().set("default.period-ticks", BasicDiscoParty.defaultPeriod);

		for (Map.Entry<EntityType, Integer> entry : BasicDiscoParty.getDefaultGuestNumbers().entrySet()) {
			getConfig().set("default.guests." + entry.getKey(), entry.getValue());
		}

		saveConfig();
	}

	@Override
	public void onDisable() {
		this.stopAllParties(); // or else the parties will continue FOREVER
	}

	static int toTicks(double seconds) {
		return (int) Math.round(seconds * 20.0);
	}

	static double toSeconds(int ticks) {
		return (double) Math.round(ticks / 20.0);
	}

	static int toSeconds_i(int ticks) {
		return (int) Math.round(ticks / 20.0);
	}

	public void addParty(String player, AbstractParty party) {
        parties.put(player, party);
	}

	public synchronized ArrayList<AbstractParty> getParties() {
		return new ArrayList<>(parties.values());
	}

	public void stopParty(String name) {
		if (this.hasParty(name)) {
			this.getParty(name).stopDisco();
		}
	}

	public void stopAllParties() {
        for (AbstractParty p : getParties()) {
            p.stopDisco();
        }
	}

	public boolean hasParty(String name) { return parties.containsKey(name); }

	public AbstractParty getParty(String name) { return parties.get(name); }

	public void removeParty(String name) {
		if (this.hasParty(name)) {
			parties.remove(name);
		}
	}

	/*-- Actual commands begin here --*/
	boolean helpCommand(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW
                + "DiscoSheep Help\n"
                + ChatColor.GRAY
                + "  Subcommands\n"
                + ChatColor.WHITE + "me, stop, all, stopall, save, reload, togglejoin\n"
                + "other <players>: start a party for the space-delimited list of players\n"
                + "defaults: Change the default settings for parties (takes normal arguments)\n"
                + ChatColor.GRAY + "  Arguments\n"
                + ChatColor.WHITE + "-n <integer>: set the number of sheep per player that spawn\n"
                + "-t <integer>: set the party duration in seconds\n"
                + "-p <ticks>: set the number of ticks between each disco beat\n"
                + "-r <integer>: set radius of the area in which sheep can spawn\n"
                + "-g <mob:integer, mob:integer...>: set spawns for other mobs, eg. -g cow:5,pig:2\n"
                + "-l: enables lightning\n"
                + "-f: enables fireworks\n"
                + "-j: enables alternative method for setting sheep colours\n"
                + "-b <integer>: spawns a percentage of mobs as babies, if possible\n"
                + "-P: enables pentatonic backing track\n");
		return true;
	}

	boolean stopMeCommand(CommandSender sender) {
		stopParty(sender.getName());
		return true;
	}

	boolean stopAllCommand(CommandSender sender) {
        return checkPermissionsAndRun(new PermissibleCommand() {
            @Override
            public boolean run(CommandSender sender) {
                stopAllParties();
                return true;
            }
        }, sender, PERMISSION_STOPALL);
	}

	boolean partyCommand(Player player, final PartyBuilder builder) {
        return checkPermissionsAndRun(new PermissibleCommand() {
            @Override
            public boolean run(CommandSender s) {
                if (!hasParty(s.getName())) {
                    builder.build().startDisco();
                } else {
                    s.sendMessage(ChatColor.RED + "You already have a party.");
                }
                return true;
            }
        } , player, PERMISSION_PARTY);
	}

	boolean reloadCommand(CommandSender sender) {
        return checkPermissionsAndRun(new PermissibleCommand() {
            @Override
            public boolean run(CommandSender s) {
                reloadConfigFromDisk();
                s.sendMessage(ChatColor.GREEN + "DiscoSheep config reloaded from disk");
                return true;
            }
        }, sender, PERMISSION_RELOAD);
	}

	boolean partyOtherCommand(final String[] players, CommandSender sender, final PartyBuilder builder) {
        return checkPermissionsAndRun(new PermissibleCommand() {
            @Override
            public boolean run(CommandSender s) {
                Player p;
                for (String playerName : players) {
                    p = Bukkit.getServer().getPlayer(playerName);
                    if (p != null) {
                        if (!hasParty(p.getName())) {
                            builder.buildOther(p).startDisco();
                        }
                    } else {
                        s.sendMessage("Invalid player: " + playerName);
                    }
                }
                return true;
            }
        }, sender, PERMISSION_OTHER);
	}

	boolean partyAllCommand(CommandSender sender, final PartyBuilder builder) {
        return checkPermissionsAndRun(new PermissibleCommand() {
            @Override
            public boolean run(CommandSender s) {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (!hasParty(p.getName())) {
                        builder.buildOther(p).startDisco();
                        p.sendMessage(ChatColor.RED + "LET'S DISCO!!");
                    }
                }
                return true;
            }
        } , sender, PERMISSION_ALL);
	}

	void partyOnJoin(Player player) {
		if (!partyOnJoin) {
			return;
		}
		if (player.hasPermission(PERMISSION_ONJOIN)) {
            PartyBuilder builder = new PartyBuilder(player);
			builder.build().startDisco();
		}
	}

	boolean togglePartyOnJoinCommand(CommandSender sender) {
        return checkPermissionsAndRun(new PermissibleCommand() {
            @Override
            public boolean run(CommandSender s) {
                partyOnJoin = !partyOnJoin;
                if (partyOnJoin) {
                    s.sendMessage(ChatColor.GREEN + "DiscoSheep party on join functionality enabled.");
                } else {
                    s.sendMessage(ChatColor.GREEN + "DiscoSheep party on join functionality disabled.");
                }
                return true;
            }
        }, sender, PERMISSION_TOGGLEPARTYONJOIN);
	}

	boolean setDefaultsCommand(CommandSender sender, final PartyBuilder builder) {
        return checkPermissionsAndRun(new PermissibleCommand() {
            @Override
            public boolean run(CommandSender s) {
                builder.build().setDefaultsFromCurrent();
                s.sendMessage(ChatColor.GREEN + "DiscoSheep configured with new defaults (not yet saved to disk)");
                return true;
            }
        }, sender, PERMISSION_CHANGEDEFAULTS);
	}

	boolean saveConfigCommand(CommandSender sender) {
        return checkPermissionsAndRun(new PermissibleCommand() {
            @Override
            public boolean run(CommandSender s) {
                saveConfigToDisk();
                s.sendMessage(ChatColor.GREEN + "DiscoSheep config saved to disk");
                return true;
            }
        }, sender, PERMISSION_SAVECONFIG);
	}

    public interface PermissibleCommand {
        boolean run(CommandSender sender);
    }

    public boolean checkPermissionsAndRun(PermissibleCommand command, CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            return command.run(sender);
        } else {
            noPermsMessage(sender, permission);
            return false;
        }
    }

	void noPermsMessage(CommandSender sender, String permission) {
		sender.sendMessage(ChatColor.RED + "You do not have the permission node " + ChatColor.GRAY + permission);
	}
}
