/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.gibstick.discosheep;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Charlie
 */
public class DiscoCommands {

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

    static final String FLAGS = "n:t:p:r:lf";

    private static final DiscoSheep plugin = DiscoSheep.getInstance();

    public static class ParentCommand {

        @Command(aliases = {"discosheep", "ds"}, desc = "Main Discosheep Command", min = 0, max = -1)
        @NestedCommand(DiscoCommands.class)
        public static void DiscoCommand(final CommandContext args, CommandSender sender) throws CommandException {
        }
    }

    private static void parsePartyFlags(DiscoParty party, final CommandContext args, CommandSender sender) throws IllegalArgumentException {
        party.setDuration(args.getFlagInteger('t', DiscoParty.defaultDuration));
        party.setPeriod(args.getFlagInteger('p', DiscoParty.defaultPeriod));
        party.setSheep(args.getFlagInteger('n', DiscoParty.defaultSheep));

        // handle the special case of radius flag arg "dense"
        String radiusArg = args.getFlag('r', Integer.toString(DiscoParty.defaultRadius));
        if ("dense".equals(radiusArg)) {
            party.setDenseRadius(party.getSheep());
        } else {
            party.setRadius(Integer.parseInt(radiusArg));
        }

        if (sender.hasPermission(PERMISSION_FIREWORKS)) {
            party.setDoFireworks(args.hasFlag('f'));
        } else {
            plugin.noPermsMessage(sender, PERMISSION_FIREWORKS);
        }

        if (sender.hasPermission(PERMISSION_LIGHTNING)) {
            party.setDoLightning(args.hasFlag('l'));
        } else {
            plugin.noPermsMessage(sender, PERMISSION_LIGHTNING);
        }
    }

    @Command(aliases = {"test"}, desc = "Test command", usage = "No arguments", min = 0, max = 0)
    public static void test(final CommandContext args, CommandSender sender) throws CommandException {
        sender.sendMessage("TESTING");
    }

    @Command(aliases = {"stop", "stoppls", "wtf"}, desc = "Stop your own disco party", usage = "No arguments", min = 0, max = 0)
    public static void stopMeCommand(final CommandContext args, CommandSender sender) throws CommandException {
        plugin.stopParty(sender.getName());
    }

    @Command(aliases = {"stopall"}, desc = "Stop all disco parties on the server", usage = "No arguments", min = 0, max = 0)
    @CommandPermissions(value = PERMISSION_STOPALL)
    public static void stopAllCommand(final CommandContext args, CommandSender sender) throws CommandException {
        plugin.stopAllParties();
    }

    @Command(aliases = {"reload"}, desc = "Reload DiscoSheep configuration from file", usage = "No arguments", min = 0, max = 0)
    public static void reloadCommand(final CommandContext args, CommandSender sender) {
        plugin.reloadConfigFromDisk();
        sender.sendMessage(ChatColor.GREEN + "DiscoSheep config reloaded from file.");
    }

    @Command(
            aliases = {"me", "party", "partay", "turnup"},
            desc = "Start your own private DiscoParty",
            usage = "[optional flags]",
            min = 0,
            max = -1,
            flags = FLAGS
    )
    @CommandPermissions(value = PERMISSION_PARTY)
    public static void partyCommand(final CommandContext args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to have a party");
            return;
        }
        Player player = (Player) sender;
        if (!plugin.hasParty(player.getName())) {
            DiscoParty party = new DiscoParty(player);
            parsePartyFlags(party, args, sender);
            party.startDisco();
        } else {
            player.sendMessage(ChatColor.RED + "You already have a party.");
        }
    }

    @SuppressWarnings("deprecation")
    // UUIDs not necessary since DiscoSheep only lasts for one session at most
    // and permissions will handle onJoin DiscoSheep
    @Command(
            aliases = {"other", "yall"},
            desc = "Start a party for other players",
            usage = "[optional flags]",
            min = 0,
            max = -1,
            flags = FLAGS
    )
    @CommandPermissions(value = PERMISSION_OTHER)
    public static void partyOtherCommand(CommandContext args, CommandSender sender) {
        DiscoParty party = new DiscoParty();
        Player p;
        String players[] = args.getSlice(1);

        parsePartyFlags(party, args, sender);

        for (String playerName : players) {
            p = Bukkit.getServer().getPlayer(playerName);
            if (p != null) {
                if (!plugin.hasParty(p.getName())) {
                    DiscoParty individualParty = party.clone(p);
                    individualParty.startDisco();
                }
            } else {
                sender.sendMessage("Invalid player: " + playerName);
            }
        }
    }

    @Command(
            aliases = {"all", "allturnup"},
            desc = "Start a party for all players on the server",
            usage = "[optional flags]",
            min = 0,
            max = -1,
            flags = FLAGS
    )
    @CommandPermissions(value = PERMISSION_ALL)
    public static void partyAllCommand(final CommandContext args, CommandSender sender) {
        DiscoParty party = new DiscoParty();
        parsePartyFlags(party, args, sender);
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!plugin.hasParty(p.getName())) {
                DiscoParty individualParty = party.clone(p);
                individualParty.startDisco();
                p.sendMessage(ChatColor.RED + "LET'S DISCO!!");
            }
        }
    }

    @Command(
            aliases = {"togglejoin", "toggleonjoin"},
            desc = "Start a party for all players on the server",
            usage = "[optional flags]",
            min = 0,
            max = -1,
            flags = FLAGS
    )
    @CommandPermissions(value = PERMISSION_TOGGLEPARTYONJOIN)
    public static void togglePartyOnJoinCommand(final CommandContext args, CommandSender sender) {
        boolean result = plugin.toggleOnJoin();
        if (result) {
            sender.sendMessage(ChatColor.GREEN + "DiscoSheep party on join functionality enabled.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "DiscoSheep party on join functionality disabled.");
        }
    }

    @Command(
            aliases = {"defaults", "setdefaults"},
            desc = "Change the default party settings",
            usage = "[optional flags]",
            min = 0,
            max = -1,
            flags = FLAGS
    )
    @CommandPermissions(value = PERMISSION_CHANGEDEFAULTS)
    public static void setDefaultsCommand(final CommandContext args, CommandSender sender) {
        DiscoParty party = new DiscoParty();
        parsePartyFlags(party, args, sender);
        party.setDefaultsFromCurrent();
        sender.sendMessage(ChatColor.GREEN + "DiscoSheep configured with new defaults (not saved to disk yet)");
    }

    @Command(
            aliases = {"defaults", "setdefaults"},
            desc = "Change the default party settings",
            usage = "[optional flags]",
            min = 0,
            max = -1,
            flags = FLAGS
    )
    @CommandPermissions(value = PERMISSION_SAVECONFIG)
    public static void saveConfigCommand(final CommandContext args, CommandSender sender) {
        plugin.saveConfigToDisk();
        sender.sendMessage(ChatColor.GREEN + "DiscoSheep config saved to disk");
    }
}
