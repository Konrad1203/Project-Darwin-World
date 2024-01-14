package simulation;

import model.utilities.AnimalGrid;
import model.utilities.Position;
import model.animal.Animal;

import java.util.*;

public class SimMap {

    private final Simulation sim;
    private final AnimalGrid animalGrid;
    private final List<Animal> animalList = new LinkedList<>();
    private final Set<Position> plants = new HashSet<>() {
        @Override
        public boolean add(Position p) {
            addToPlantsCounter(p);
            return super.add(p);
        }
    };

    private final int[][] plantsCounter;
    private int maxPlantsCounter = 0;

    private final List<Animal> deadAnimalList = new ArrayList<>();

    public SimMap(Simulation simulation) {
        sim = simulation;
        animalGrid = new AnimalGrid(sim.settings);
        plantsCounter = new int[sim.settings.height()][sim.settings.width()];
    }

    public AnimalGrid getAnimalGrid() {
        return animalGrid;
    }

    public List<Animal> getAnimalList() {
        return animalList;
    }

    public List<Animal> getDeadAnimalList() {
        return deadAnimalList;
    }

    public Set<Position> getPlants() {
        return plants;
    }

    public int[][] getPlantsCounter() {
        return plantsCounter;
    }

    public int getMaxPlantsCounter() {
        return maxPlantsCounter;
    }

    private void addToPlantsCounter(Position p) {
        plantsCounter[p.y()][p.x()]++;
        maxPlantsCounter = Math.max(maxPlantsCounter, plantsCounter[p.y()][p.x()]);
    }

    public Animal animalAt(Position position) {
        if (!animalGrid.get(position).isEmpty()) {
            return animalGrid.get(position).stream().max(Comparator.comparing(Animal::getEnergy)).orElse(null);
        }
        return null;
    }

    public boolean isAnimal(Position position) {
        return animalAt(position) != null;
    }

    public boolean isPlant(Position position) {
        return plants.contains(position);
    }

    public boolean canMoveTo(Animal animal, Position position) {
        if (position.y() < 0 || position.y() >= sim.settings.height()) {
            animal.boundFromPole();
            return false;
        } else if (position.x() < 0 || position.x() >= sim.settings.width()) {
            animal.moveToOtherSide(position);
            return false;
        }
        return true;
    }

    public void spawnStartAnimals() {
        for (int i = 0; i < sim.settings.startAnimalsCount(); i++) {
            Animal animal = new Animal(sim);
            animalGrid.get(animal.getPosition()).add(animal);
            animalList.add(animal);
        }
    }

    public void spawnAnimalsFromList(List<Animal> animals) {
        for (Animal animal : animals) {
            animalGrid.get(animal.getPosition()).add(animal);
            animalList.add(animal);
        }
    }

    public void moveAnimals() {
        for (Animal animal : animalList) {
            animalGrid.get(animal.getPosition()).remove(animal);
            animal.move(this);
            animalGrid.get(animal.getPosition()).add(animal);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sim.settings.height(); i++) {
            for (int j = 0; j < sim.settings.width(); j++) {
                Position position = new Position(j, i);
                if ( isAnimal(position) ) result.append( "%s ".formatted( animalAt(position) ) );
                else if ( isPlant(position) ) result.append("w ");
                else result.append(". ");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
