package gibstick.bukkit.discosheep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscoSheep extends JavaPlugin {

	Map<String, DiscoParty> parties = new HashMap<String, DiscoParty>();
	private BaaBaaBlockSheepEvents blockEvents = new BaaBaaBlockSheepEvents(this);
	FileConfiguration config;

	@Override
	public void onEnable() {
		getCommand("ds").setExecutor(new DiscoSheepCommandExecutor(this));
		getServer().getPluginManager().registerEvents(blockEvents, this);

		if (config == null) {
			config = this.getConfig();
		}

		config.addDefault("max.sheep", DiscoParty.maxSheep);
		config.addDefault("max.radius", DiscoParty.maxRadius);
		config.addDefault("max.duration", toSeconds_i(DiscoParty.maxDuration));
		config.addDefault("max.period-ticks", DiscoParty.maxPeriod);
		config.addDefault("min.period-ticks", DiscoParty.minPeriod);
		config.addDefault("default.sheep", DiscoParty.defaultSheep);
		config.addDefault("default.radius", DiscoParty.defaultRadius);
		config.addDefault("default.duration", toSeconds_i(DiscoParty.defaultDuration));
		config.addDefault("default.period-ticks", DiscoParty.defaultPeriod);

		loadConfigFromDisk();
	}

	void loadConfigFromDisk() {
		getConfig().options().copyDefaults(true);
		saveConfig();

		DiscoParty.maxSheep = getConfig().getInt("max.sheep");
		DiscoParty.maxRadius = getConfig().getInt("max.radius");
		DiscoParty.maxDuration = toTicks(getConfig().getInt("max.duration"));
		DiscoParty.maxPeriod = getConfig().getInt("max.period-ticks");
		DiscoParty.minPeriod = getConfig().getInt("min.period-ticks");
		DiscoParty.defaultSheep = getConfig().getInt("default.sheep");
		DiscoParty.defaultRadius = getConfig().getInt("default.radius");
		DiscoParty.defaultDuration = toTicks(getConfig().getInt("default.duration"));
		DiscoParty.defaultPeriod = getConfig().getInt("default.period-ticks");
	}

	void reloadConfigFromDisk() {
		reloadConfig();
		loadConfigFromDisk();
	}

	@Override
	public void onDisable() {
		this.stopAllParties();
		this.config = null;
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

	public synchronized List<DiscoParty> getParties() {
		return new ArrayList(this.getPartyMap().values());
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

	public void removeParty(String name) {
		if (this.hasParty(name)) {
			this.getPartyMap().remove(name);
		}
	}

	public void startParty(Player player, int duration, int sheepAmount, int radius, int period, boolean fireworksEnabled) {
		if (!hasParty(player.getName())) {
			DiscoParty ds = new DiscoParty(this, player);
			ds.setDuration(duration);
			ds.setSheep(sheepAmount);
			ds.setRadius(radius);
			ds.setPeriod(period);
			ds.setDoFireworks(fireworksEnabled);
			ds.startDisco();
		}
	}
}
