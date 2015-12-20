package me.cwang.discosheep;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by Charlie on 2015-08-15.
 */
public class PartyBuilder {
    // required
    protected Player player;
    // optional
    private int duration = AbstractParty.defaultDuration;
    private int radius = AbstractParty.defaultRadius;
    private int period = AbstractParty.defaultPeriod;
    private int sheep = AbstractParty.defaultSheep;
    private HashMap<EntityType, Integer> guests = AbstractParty.defaultGuestNumbers;
    private boolean dense = false;
    private boolean fireworks = false;
    private boolean lightning = false;
    private boolean jeb = false;
    private boolean pentatonic = false;
    private int babyness = 0;

    public PartyBuilder(Player player) {
        this.player = player;
    }

    public PartyBuilder duration(int duration) throws IllegalArgumentException {
        if (duration < 0 || duration > AbstractParty.maxDuration) {
            throw new IllegalArgumentException("nvalid duration");
        }
        this.duration = duration;
        return this;
    }

    public PartyBuilder radius(int radius) throws IllegalArgumentException {
        if (radius < 0 || radius > AbstractParty.maxRadius) {
            throw new IllegalArgumentException("Invalid radius");
        }
        this.radius = radius;
        return this;
    }

    public PartyBuilder dense() {
        this.dense = true;
        return this;
    }

    public PartyBuilder period(int period) throws IllegalArgumentException {
        if (period < 0 || period > AbstractParty.maxPeriod) {
            throw new IllegalArgumentException("invalid period");
        }
        this.period = period;
        return this;
    }

    public PartyBuilder sheep(int sheep) throws IllegalArgumentException {
        if (sheep < 0 || sheep > AbstractParty.maxSheep) {
            throw new IllegalArgumentException("invalid sheep number");
        }
        this.sheep = sheep;
        return this;
    }

    public PartyBuilder guests(String key, int n) throws IllegalArgumentException {
        key = key.toUpperCase();
        EntityType type = EntityType.UNKNOWN;
        try {
            type = EntityType.valueOf(key);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("invalid guest %s", key));
        }
        if (guests.containsKey(type)) {
            if (n <= AbstractParty.getMaxGuestNumbers().get(type) && n >= 0) { // so that /ds defaults can take 0 as arg
                guests.put(type, n);
                return this;
            }
        }
        throw new IllegalArgumentException(String.format("invalid number for %s", key));
    }

    public PartyBuilder noGuests() {
        guests.clear();
        return this;
    }

    public PartyBuilder fireworks() {
        fireworks = true;
        return this;
    }

    public PartyBuilder jeb() {
        jeb = true;
        return this;
    }

    public PartyBuilder lightning() {
        lightning = true;
        return this;
    }

    public AbstractParty build() {
        if (dense) {
            int denseRadius = (int) Math.floor(Math.sqrt(sheep / Math.PI));
            if (denseRadius > AbstractParty.maxRadius) {
                denseRadius = AbstractParty.maxRadius;
            }
            if (denseRadius < 1) {
                denseRadius = 1;
            }
            radius = denseRadius;
        }
        AbstractParty party = new BasicDiscoParty(player, duration, radius, period, sheep, guests);
        if (fireworks) party = new FireworkParty(party);
        if (lightning) party = new LightningParty(party);
        if (jeb) party = new JebParty(party);
        if (pentatonic) party = new PentatonicParty(party);
        if (babyness > 0) party = new BabyParty(party, babyness);

        return party;
    }

    public void pentatonic() {
        pentatonic = true;
    }

    public void baby(int babyness) {
        this.babyness = babyness;
    }

    public AbstractParty buildOther(Player newPlayer) {
        Player oldPlayer = player;
        player = newPlayer;
        AbstractParty otherParty = build();
        player = oldPlayer;
        return otherParty;
    }


}
