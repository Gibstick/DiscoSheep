package ca.gibstick.discosheep;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscoSheep extends JavaPlugin {

    private static DiscoSheep instance;

    boolean partyOnJoin = true;
    Map<String, DiscoParty> parties = new HashMap<String, DiscoParty>();
    private CommandsManager<CommandSender> commands;

    public static DiscoSheep getInstance() {
        if (instance == null) {
            instance = new DiscoSheep();
            return instance;
        }
        return instance;
    }

    private void setupCommands() {
        this.commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender instanceof ConsoleCommandSender || sender.hasPermission(perm);
            }
        };
        CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, this.commands);
        cmdRegister.register(DiscoCommands.ParentCommand.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else if (e.getCause() instanceof IllegalArgumentException) {
                sender.sendMessage(ChatColor.RED + "Illegal argument (out of bounds or bad format).");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        } catch (com.sk89q.minecraft.util.commands.CommandException ex) {
            Logger.getLogger(DiscoSheep.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public void onEnable() {
        instance = this;
        //getCommand("ds").setExecutor(new DiscoSheepCommandExecutor(this));
        setupCommands();
        getServer().getPluginManager().registerEvents(new GlobalEvents(), this);

        getConfig().addDefault("on-join.enabled", partyOnJoin);
        getConfig().addDefault("max.sheep", DiscoParty.maxSheep);
        getConfig().addDefault("max.radius", DiscoParty.maxRadius);
        getConfig().addDefault("max.duration", toSeconds_i(DiscoParty.maxDuration));
        getConfig().addDefault("max.period-ticks", DiscoParty.maxPeriod);
        getConfig().addDefault("min.period-ticks", DiscoParty.minPeriod);
        getConfig().addDefault("default.sheep", DiscoParty.defaultSheep);
        getConfig().addDefault("default.radius", DiscoParty.defaultRadius);
        getConfig().addDefault("default.duration", toSeconds_i(DiscoParty.defaultDuration));
        getConfig().addDefault("default.period-ticks", DiscoParty.defaultPeriod);
        getConfig().addDefault("max.floor_size", DiscoParty.maxFloorSize);

        /*
         * Iterate through all live entities and create default configuration values for them
         * excludes bosses and other mobs that throw NPE
         */
        for (EntityType ent : EntityType.values()) {
            if (ent.isAlive()
                    && !ent.equals(EntityType.ENDER_DRAGON)
                    && !ent.equals(EntityType.WITHER)
                    && !ent.equals(EntityType.PLAYER)) {
                getConfig().addDefault("default.guests." + ent.toString(), 0);
                getConfig().addDefault("max.guests." + ent.toString(), 5);
            }
        }
        loadConfigFromDisk();
    }

    void loadConfigFromDisk() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        partyOnJoin = getConfig().getBoolean("on-join.enabled");
        DiscoParty.maxSheep = getConfig().getInt("max.sheep");
        DiscoParty.maxRadius = getConfig().getInt("max.radius");
        DiscoParty.maxDuration = toTicks(getConfig().getInt("max.duration"));
        DiscoParty.maxPeriod = getConfig().getInt("max.period-ticks");
        DiscoParty.minPeriod = getConfig().getInt("min.period-ticks");
        DiscoParty.defaultSheep = getConfig().getInt("default.sheep");
        DiscoParty.defaultRadius = getConfig().getInt("default.radius");
        DiscoParty.defaultDuration = toTicks(getConfig().getInt("default.duration"));
        DiscoParty.defaultPeriod = getConfig().getInt("default.period-ticks");
        DiscoParty.maxFloorSize = getConfig().getInt("max.floor_size");

        for (String key : getConfig().getConfigurationSection("default.guests").getKeys(false)) {
            DiscoParty.getDefaultGuestNumbers().put(key, getConfig().getInt("default.guests." + key));
        }

        for (String key : getConfig().getConfigurationSection("max.guests").getKeys(false)) {
            DiscoParty.getMaxGuestNumbers().put(key, getConfig().getInt("max.guests." + key));
        }
    }

    void reloadConfigFromDisk() {
        reloadConfig();
        loadConfigFromDisk();
    }

    void saveConfigToDisk() {
        getConfig().set("on-join.enabled", partyOnJoin);
        getConfig().set("default.sheep", DiscoParty.defaultSheep);
        getConfig().set("default.radius", DiscoParty.defaultRadius);
        getConfig().set("default.duration", toSeconds_i(DiscoParty.defaultDuration));
        getConfig().set("default.period-ticks", DiscoParty.defaultPeriod);

        for (Map.Entry<String, Integer> entry : DiscoParty.getDefaultGuestNumbers().entrySet()) {
            getConfig().set("default.guests." + entry.getKey(), entry.getValue());
        }
        saveConfig();
    }

    @Override
    public void onDisable() {
        this.stopAllParties(); // or else the parties will continue FOREVER
        instance = null;
        commands = null;
    }

    int toTicks(double seconds) {
        return (int) Math.round(seconds * 20.0);
    }

    double toSeconds(int ticks) {
        return (double) Math.round(ticks / 20.0);
    }

    int toSeconds_i(int ticks) {
        return (int) Math.round(ticks / 20.0);
    }

    public synchronized Map<String, DiscoParty> getPartyMap() {
        return this.parties;
    }

    public synchronized ArrayList<DiscoParty> getParties() {
        return new ArrayList<DiscoParty>(this.getPartyMap().values());
    }

    public void stopParty(String name) {
        if (this.hasParty(name)) {
            this.getParty(name).stopDisco();
        }
    }

    public void stopAllParties() {
        for (DiscoParty party : this.getParties()) {
            party.stopDisco();
        }
    }

    public boolean hasParty(String name) {
        return this.getPartyMap().containsKey(name);
    }

    public DiscoParty getParty(String name) {
        return this.getPartyMap().get(name);
    }

    public boolean toggleOnJoin() {
        partyOnJoin = !partyOnJoin;
        return partyOnJoin;
    }

    public void removeParty(String name) {
        if (this.hasParty(name)) {
            this.getPartyMap().remove(name);
        }
    }

    void partyOnJoin(Player player) {
        if (!partyOnJoin) {
            return;
        }
        if (player.hasPermission(DiscoCommands.PERMISSION_ONJOIN)) {
            DiscoParty party = new DiscoParty(player);
            party.startDisco();
        }
    }

    boolean clearGuests(DiscoParty party) {
        party.getGuestNumbers().clear();
        return true;
    }

    boolean noPermsMessage(CommandSender sender, String permission) {
        sender.sendMessage(ChatColor.RED + "You do not have the permission node " + ChatColor.GRAY + permission);
        return false;
    }
}
