package simulation.model;

import simulation.GUI.SimPresenter;
import simulation.model.random.RandomPositionGenerator;
import simulation.model.random.SteppeGrassPosition;
import simulation.model.util.AnimalGrid;
import simulation.model.util.Position;
import simulation.model.animal.Animal;

import java.util.*;

public class SimulationMap {

    private final SimSettings settings;
    private final SimulationMap simMap = this;
    private final SimPresenter presenter;
    private final AnimalGrid animalGrid;
    private final List<Animal> animalList = new LinkedList<>();
    private final Set<Position> plants = new HashSet<>();
    private final Random random = new Random();

    public SimulationMap(SimSettings settings, SimPresenter presenter) {
        this.settings = settings;
        animalGrid = new AnimalGrid(settings);
        this.presenter = presenter;

        for (int i = 0; i < settings.startAnimalsCount(); i++) {
            Animal animal = new Animal(settings, random);
            animalGrid.get(animal.getPosition()).add(animal);
            animalList.add(animal);
        }

        RandomPositionGenerator grassGen = new SteppeGrassPosition(settings, settings.startPlantsCount());
        for (Position position : grassGen) {
            plants.add(position);
        }

        Thread thread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(settings.frameTime());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                clearDead();
                moveAnimals();
                plantEating();
                //Copulation
                //newPlants
                presenter.printMap(simMap);
                if (animalList.isEmpty()) break;
            }
        });

        thread.start();
    }
    private void moveAnimals() {
        for (Animal animal : animalList) {
            animalGrid.get(animal.getPosition()).remove(animal);
            animal.move(this);
            animalGrid.get(animal.getPosition()).add(animal);
        }
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

    public void plantEating() {
        Iterator<Position> iterator = plants.iterator();
        while (iterator.hasNext()) {
            Position pos = iterator.next();
            if (!animalGrid.get(pos).isEmpty()) {
                animalGrid.get(pos).first().consumePlant();
                iterator.remove();
            }
        }
    }

    private void clearDead() {
        Iterator<Animal> iterator = animalList.iterator();
        while (iterator.hasNext()) {
            Animal animal = iterator.next();
            if (animal.getEnergy() <= 0) {
                animalGrid.get(animal.getPosition()).remove(animal);
                iterator.remove();
            }
        }
    }

    public void getAnimalsFromCell(Position position) {
        System.out.printf("%s -> %d. Sum: %d, %d\n", position, animalGrid.get(position).size(), getAnimalsCountFromGrid(), animalList.size());
        presenter.showList( animalGrid.get(position).stream().toList() );
    }

    private int getAnimalsCountFromGrid() {
        int sum = 0;
        for (Set<Animal> set : animalGrid) sum += set.size();
        return sum;
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
