package me.cwang.discosheep;

import org.bukkit.entity.Sheep;

/**
 * Created by Charlie on 2015-08-16.
 * This DiscoParty toggles sheep colours by setting names to jeb_
 */
public class JebParty extends DiscoDecorator {
    public JebParty(AbstractParty p) {
        super(p);
    }

    /**
     * Spawns a sheep and sets its name to "jeb_", using the easter egg to change colours
     * This feature has been around for long enough so it should be safe to use.
     * @return The sheep that was spawned
     */
    @Override
    protected Sheep spawnSheep() {
        Sheep sheep = super.spawnSheep();
        sheep.setCustomName("jeb_");
        sheep.setCustomNameVisible(false);
        return sheep;
    }
}
