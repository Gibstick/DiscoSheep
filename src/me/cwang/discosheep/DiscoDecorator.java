package me.cwang.discosheep;

import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Charlie on 2015-08-13.
 */
public class DiscoDecorator extends AbstractParty {
    protected final AbstractParty decoratedParty;

    public DiscoDecorator(AbstractParty p) {
        decoratedParty = p;
    }


    @Override
    protected Player getPlayer() {
        return decoratedParty.getPlayer();
    }

    @Override
    protected void jump(Entity entity) {
        decoratedParty.jump(entity);
    }

    @Override
    public int getSheep() {
        return decoratedParty.getSheep();
    }

    @Override
    protected List<Sheep> getSheepList() {
        return decoratedParty.getSheepList();
    }

    @Override
    protected List<LivingEntity> getGuestList() {
        return decoratedParty.getGuestList();
    }

    @Override
    protected HashMap<EntityType, Integer> getGuestMap() {
        return decoratedParty.getGuestMap();
    }

    @Override
    protected int getPeriod() { return decoratedParty.getPeriod(); }

    @Override
    protected int getState() { return decoratedParty.getState(); }

    @Override
    protected Location getLocation() {
        return decoratedParty.getLocation();
    }

    @Override
    protected Sheep spawnSheep() {
        return decoratedParty.spawnSheep();
    }

    @Override
    protected boolean isExpired() {
        return decoratedParty.isExpired();
    }

    @Override
    protected Entity spawnGuest(EntityType type) {
        return decoratedParty.spawnGuest(type);
    }

    @Override
    void setDefaultsFromCurrent() {
        decoratedParty.setDefaultsFromCurrent();
    }

    @Override
    protected void updateState() { decoratedParty.updateState(); }

    @Override
    protected void updateSheep(Sheep sheep) {
        decoratedParty.updateSheep(sheep);
    }

    @Override
    protected void updateGuest(LivingEntity guest) {
        decoratedParty.updateGuest(guest);
    }

    @Override
    protected void startListening() {
        decoratedParty.startListening();
    }

    @Override
    protected void stopListening() { decoratedParty.stopListening(); }

    @Override
    protected void playSounds() {
        decoratedParty.playSounds();
    }
}
