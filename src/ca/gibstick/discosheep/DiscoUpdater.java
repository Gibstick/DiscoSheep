package ca.gibstick.discosheep;

import org.bukkit.scheduler.BukkitRunnable;

public class DiscoUpdater extends BukkitRunnable {

	private DiscoParty parent;

	public DiscoUpdater(DiscoParty parent) {
		this.parent = parent;
	}

	@Override
	public void run() {
		parent.update();
	}
}
