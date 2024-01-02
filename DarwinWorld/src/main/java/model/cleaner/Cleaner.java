package model.cleaner;

import simulation.SimMap;
import model.animal.Animal;
import model.utilities.AnimalGrid;
import simulation.Simulation;
import java.util.List;

public class Cleaner {

    private final List<Animal> animalList;
    private final AnimalGrid animalGrid;
    private final List<Animal> deadAnimalList;

    public Cleaner(SimMap map) {
        animalList = map.getAnimalList();
        animalGrid = map.getAnimalGrid();
        deadAnimalList = map.getDeadAnimalList();
    }

    public void clean() {
        animalList.removeIf(animal -> {
            if (animal.getEnergy() <= 0) {
                animalGrid.get(animal.getPosition()).remove(animal);
                deadAnimalList.add(animal);
                makeAdditionalThings(animal);
                animal.setDead();
                return true;
            } else return false;
        });
    }

    protected void makeAdditionalThings(Animal animal) {}
}
