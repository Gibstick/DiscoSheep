package gibstick.bukkit.discosheep;

import org.bukkit.scheduler.BukkitRunnable;

public class DiscoUpdater extends BukkitRunnable {

    private final int defaultDuration = 1000;// ticks
    private final int defaultFrequency = 20;// ticks per state change
    int frequency = 0, duration = 0;
    private DiscoSheep parent;

    public DiscoUpdater(DiscoSheep parent) {
        this.parent = parent;
    }

    public void stop() {
        this.duration = 0;
        parent.cleanUp();
    }

    public void start(int duration, int frequency) {
        this.frequency = this.defaultFrequency;
        this.duration = this.defaultDuration;
        parent.scheduleUpdate();
    }

    @Override
    public void run() {
        if (duration > 0) {
            parent.cycleSheepColours();
            parent.playSounds();
            duration -= frequency;
            parent.scheduleUpdate();
        } else {
            this.stop();
        }
    }
}
