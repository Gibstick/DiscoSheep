package me.cwang.discosheep;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

/**
 * Created by Charlie on 2015-08-17.
 */
public class FireworkParty extends DiscoDecorator {
    Random r;

    public FireworkParty(AbstractParty p) {
        super(p);
        r = new Random();
    }


    private void randomizeFirework(Firework firework) {
        FireworkEffect.Builder effect = FireworkEffect.builder();
        FireworkMeta meta = firework.getFireworkMeta();

        // construct [1, 3] random colours
        int numColours = r.nextInt(3) + 1;
        Color[] colourArray = new Color[numColours];
        for (int i = 0; i < numColours; i++) {
            colourArray[i] = getColor(r.nextInt(17) + 1);
        }

        // randomize effects
        effect.withColor(colourArray);
        effect.flicker(r.nextDouble() < 0.5);
        effect.trail(r.nextDouble() < 0.5);
        effect.with(FireworkEffect.Type.values()[r.nextInt(FireworkEffect.Type.values().length)]);

        // set random effect and randomize power
        meta.addEffect(effect.build());
        meta.setPower(r.nextInt(2) + 1);

        // apply it to the given firework
        firework.setFireworkMeta(meta);
    }

    private void spawnRandomFireworkAtSheep(Sheep sheep) {
        Firework firework = (Firework) sheep.getWorld().spawnEntity(sheep.getEyeLocation(), EntityType.FIREWORK);
        randomizeFirework(firework);
    }

    @Override
    protected void updateSheep(Sheep sheep) {
        super.updateSheep(sheep);
        if (getState() % 8 == 0 && r.nextDouble() < 0.5) {
            spawnRandomFireworkAtSheep(sheep);
        }
    }
}
