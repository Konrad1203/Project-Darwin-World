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
    private final Simulation simulation;

    public Cleaner(SimMap map, Simulation simulation) {
        animalList = map.getAnimalList();
        animalGrid = map.getAnimalGrid();
        deadAnimalList = map.getDeadAnimalList();
        this.simulation = simulation;
    }

    public void clean() {
        animalList.removeIf(animal -> {
            if (animal.getEnergy() == 0) {
                animalGrid.get(animal.getPosition()).remove(animal);
                deadAnimalList.add(animal);
                makeAdditionalThings(animal);
                animal.setDeadDay(simulation.getDay());
                return true;
            } else return false;
        });
        /*
        Iterator<Animal> iterator = animalList.iterator();
        while (iterator.hasNext()) {
            Animal animal = iterator.next();
            if (animal.getEnergy() == 0) {
                animalGrid.get(animal.getPosition()).remove(animal);
                deadAnimalList.add(animal);
                makeAdditionalThings(animal);
                iterator.remove();
            }
        }

         */
    }

    protected void makeAdditionalThings(Animal animal) {}
}
