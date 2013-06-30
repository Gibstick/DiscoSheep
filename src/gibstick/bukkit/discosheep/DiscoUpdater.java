package gibstick.bukkit.discosheep;

import org.bukkit.scheduler.BukkitRunnable;

public class DiscoUpdater extends BukkitRunnable {

	private DiscoSheep parent;

	public DiscoUpdater(DiscoSheep parent) {
		this.parent = parent;
	}

	@Override
	public void run() {
		parent.update();
	}
}
