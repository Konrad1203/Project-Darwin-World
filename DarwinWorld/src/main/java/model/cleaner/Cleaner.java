package model.cleaner;

import simulation.SimMap;
import model.animal.Animal;

public class Cleaner {

    private final SimMap map;

    public Cleaner(SimMap map) {
        this.map = map;
    }

    public void clean() {
        map.getAnimalList().removeIf(animal -> {
            if (animal.getEnergy() <= 0) {
                map.getAnimalGrid().get(animal.getPosition()).remove(animal);
                map.registerDeath(animal.getDaysSurvived());
                makeAdditionalThings(animal);
                animal.setDead();
                return true;
            } else return false;
        });
    }

    protected void makeAdditionalThings(Animal animal) {}
}
