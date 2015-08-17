package me.cwang.discosheep;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

import java.util.Random;

/**
 * Created by Charlie on 2015-08-13.
 */
public class LightningParty extends DiscoDecorator {
    private int count; // maximum number of lightning strikes

    public LightningParty(AbstractParty p) {
        super(p);
        count = super.getSheep() / 4;
    }

    /**
     * Strike a certain percentage of the spawned sheep with lightning effects.
     * The lightning effects should be purely visual, ie. not set anything on fire.
     * @return The sheep that was spawned.
     */
    @Override
    protected Sheep spawnSheep() {
        Sheep sheep = super.spawnSheep();
        if (count > 0) {
            --count;
            sheep.getWorld().strikeLightningEffect(sheep.getLocation());
        }
        return sheep;
    }

    /**
     * Strike all the non-sheep guests with lightning effects.
     * The lightning effects hsould be purely visual ie. not set anything on fire.
     * @param type The type of entity to spawn.
     * @return The spawned entity.
     */
    @Override
    protected Entity spawnGuest(EntityType type) {
        Entity guest = super.spawnGuest(type);
        guest.getWorld().strikeLightningEffect(guest.getLocation());
        return guest;
    }

    /**
     * Strike sheep with lightning 0.05% of the time.
     */
    @Override
    protected void updateSheep(Sheep sheep) {
        super.updateSheep(sheep);
        Random r = new Random();
        if (r.nextDouble() < 0.005) {
            sheep.getWorld().strikeLightningEffect(sheep.getLocation());
        }
    }

}

