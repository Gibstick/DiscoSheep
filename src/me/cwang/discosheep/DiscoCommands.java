package me.cwang.discosheep;

import gnu.getopt.Getopt;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.StringTokenizer;

public class DiscoCommands implements CommandExecutor {

    private DiscoSheep plugin;

    public DiscoCommands() {
        this.plugin = DiscoSheep.getInstance();
    }

    // return portion of the array that contains space-separated args,
    // stopping at the end of the array or the next -switch
    private String[] getNextArgs(String[] args, int i) {
        int j = i;
        while (j < args.length && !args[j].startsWith("-")) {
            j++;
        }
        return Arrays.copyOfRange(args, i, j);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = null;
        boolean isPlayer = false;

        if (sender instanceof Player) {
            player = (Player) sender;
            isPlayer = true;
        } // check isPlayer before "stop" and "me" commands

        // no-arg subcommands
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stopall")) {
                return plugin.stopAllCommand(sender);
            } else if (args[0].equalsIgnoreCase("stop") && isPlayer) {
                return plugin.stopMeCommand(sender);
            } else if (args[0].equalsIgnoreCase("help")) {
                return plugin.helpCommand(sender);
            } else if (args[0].equalsIgnoreCase("reload")) {
                return plugin.reloadCommand(sender);
            } else if (args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("saveconfig")) {
                return plugin.saveConfigCommand(sender);
            } else if (args[0].equalsIgnoreCase("togglejoin")) {
                return plugin.togglePartyOnJoinCommand(sender);
            }
        }

        if (args.length == 0) {
            return plugin.helpCommand(sender);
        }

        PartyBuilder builder = new PartyBuilder(player);
        // ctor takes "program name" as first arg so we pass the sub-command as that
        // args then start at args[1] so we slice args[1:]
        Getopt g = new Getopt(args[0], Arrays.copyOfRange(args, 1, args.length), "n:t:p:r:g:lfjPb:");
        g.setOpterr(false); // do own error handling
        int c;
        while ((c = g.getopt()) != -1) {
            try {
                switch (c) {
                    case 'n':
                        builder.sheep(Integer.parseInt(g.getOptarg()));
                        break;
                    case 't':
                        builder.duration(DiscoSheep.toTicks(Integer.parseInt(g.getOptarg())));
                        break;
                    case 'p':
                        builder.period(Integer.parseInt(g.getOptarg()));
                        break;
                    case 'r':
                        try {
                            int radius = Integer.parseInt(g.getOptarg());
                            builder.radius(radius);
                        } catch (NumberFormatException e) {
                            if (g.getOptarg().equals("dense")) {
                                builder.dense();
                            } else throw e;
                        }
                        break;
                    case 'g':
                        String monsters = g.getOptarg();
                        if ("none".equals(monsters)) {
                            builder.noGuests();
                        } else if (sender.hasPermission((DiscoSheep.PERMISSION_SPAWNGUESTS))) {
                            StringTokenizer tokens = new StringTokenizer(monsters,",");
                            while(tokens.hasMoreTokens()) {
                                try {
                                    String[] pair = tokens.nextToken().split(":");
                                    builder.guests(pair[0], Integer.parseInt(pair[1]));
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    throw new IllegalArgumentException("a valid list of guest:number pairs was not found.");
                                }
                            }
                        }
                        break;
                    case 'f':
                        builder.fireworks();
                        break;
                    case 'l':
                        if (sender.hasPermission(DiscoSheep.PERMISSION_LIGHTNING))
                            builder.lightning();
                        break;
                    case 'j':
                        builder.jeb();
                        break;
                    case 'P':
                        builder.pentatonic();
                        break;
                    case 'b':
                        builder.baby(Integer.parseInt(g.getOptarg()));
                        break;
                    case '?': // handle invalid switch
                        // need to cast getOptopt() to char because it returns a string representation of an integer
                        sender.sendMessage(String.format("Invalid switch '%s' was found", (char)g.getOptopt()));
                        return false;
                }
            } catch (NumberFormatException e) {
                String badNumber = e.getMessage().replace("For input string: ", "");
                sender.sendMessage(String.format("Error: %s is not a valid number", badNumber));
                return false;
            } catch (IllegalArgumentException e) { // handle invalid arguments
                sender.sendMessage("Bad command: " + e.getMessage());
                return false;
            }
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("all")) {
                return plugin.partyAllCommand(sender, builder);
            } else if (args[0].equalsIgnoreCase("me") && isPlayer) {
                return plugin.partyCommand(player, builder);
            } else if (args[0].equalsIgnoreCase("other")) {
                return plugin.partyOtherCommand(getNextArgs(args, 1), sender, builder);
            } else if (args[0].equalsIgnoreCase("defaults")) {
                return plugin.setDefaultsCommand(sender, builder);
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid argument (certain commands do not work from console).");
                return false;
            }
        }

        return false;
    }
}
