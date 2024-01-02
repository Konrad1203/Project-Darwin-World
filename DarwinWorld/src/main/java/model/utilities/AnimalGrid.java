package model.utilities;

import simulation.statistics.SimSettings;
import model.animal.Animal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class AnimalGrid implements Iterable<List<Animal>> {

    private final List<List<Animal>> animals;
    private final int cols;

    public AnimalGrid(SimSettings settings) {
        cols = settings.width();
        int size = settings.height() * cols;
        animals = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            animals.add(new ArrayList<>());
        }
    }

    public List<Animal> get(Position pos) {
        return animals.get(pos.y() * cols + pos.x());
    }

    public void sortAnimals() {
        for (List<Animal> animalList : animals) {
            animalList.sort(Animal::compareTo);
        }
    }

    @Override
    public Iterator<List<Animal>> iterator() {
        return animals.iterator();
    }
}