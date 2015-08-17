package me.cwang.discosheep;

import org.bukkit.Sound;

import java.util.Random;

/**
 * Created by Charlie on 2015-08-17.
 */
public class PentatonicParty extends DiscoDecorator {

    Random r = new Random();

    private static final float[] pentatonicNotes = {
            1.0f,
            1.125f,
            1.25f,
            1.5f,
            1.667f,
            2.0f
    };

    public PentatonicParty(AbstractParty p) {
        super(p);
    }

    float getPentatonicNote() {
        return pentatonicNotes[r.nextInt(pentatonicNotes.length)];
    }

    @Override
    protected void playSounds() {
        super.playSounds();
        getLocation().getWorld().playSound(getLocation(), Sound.NOTE_PIANO, 1.0f, getPentatonicNote());
    }
}
