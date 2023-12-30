package simulation;

import model.utilities.AnimalGrid;
import model.utilities.Position;
import model.animal.Animal;
import simulation.statistics.SimSettings;

import java.util.*;

public class SimMap {

    private final SimSettings settings;
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

    public SimMap(SimSettings settings) {
        this.settings = settings;
        animalGrid = new AnimalGrid(settings);
        plantsCounter = new int[settings.height()][settings.width()];
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
        if (!animalGrid.get(position).isEmpty()) return animalGrid.get(position).first();
        return null;
    }

    public boolean isAnimal(Position position) {
        return animalAt(position) != null;
    }

    public boolean isPlant(Position position) {
        return plants.contains(position);
    }

    public boolean canMoveTo(Animal animal, Position position) {
        if (position.y() < 0 || position.y() >= settings.height()) {
            animal.boundFromPole();
            return false;
        } else if (position.x() < 0 || position.x() >= settings.width()) {
            animal.moveToOtherSide(position);
            return false;
        }
        return true;
    }

    public void spawnStartAnimals(Random random) {
        for (int i = 0; i < settings.startAnimalsCount(); i++) {
            Animal animal = new Animal(settings, random);
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
        for (int i = 0; i < settings.height(); i++) {
            for (int j = 0; j < settings.width(); j++) {
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
