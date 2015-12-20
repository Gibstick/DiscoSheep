package me.cwang.discosheep;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

/**
 * Created by Charlie on 2015-08-17.
 */
public class BabyParty extends DiscoDecorator {
    private int guestBabyCount;
    private int sheepBabyCount;

    public BabyParty(AbstractParty p, int babyness) {
        super(p);
        sheepBabyCount = (int) ((babyness / 100.0d) * getSheep());
        int totalGuests = 0;
        for (int i : getGuestMap().values()) {
            totalGuests += i;
        }
        guestBabyCount = (int) ((babyness / 100.0d) * totalGuests);
    }

    @Override
    protected Entity spawnGuest(EntityType type) {
        Entity guest = super.spawnGuest(type);
        if (guest instanceof Ageable && guestBabyCount > 0) {
            Ageable baby = (Ageable) guest;
            baby.setBaby();
            --guestBabyCount;
        }
        return guest;
    }

    @Override
    protected Sheep spawnSheep() {
        Sheep sheep = super.spawnSheep();
        if (sheepBabyCount > 0) {
            sheep.setBaby();
            --sheepBabyCount;
        }
        return sheep;
    }
}
