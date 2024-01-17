package simulation;

import GUI.SimPresenter;
import model.animal.*;
import model.cleaner.Cleaner;
import model.cleaner.CleanerOnDead;
import model.copulator.Copulator;
import model.feeder.Feeder;
import model.planter.Planter;
import model.planter.PlanterEquator;
import model.planter.PlanterOnDead;
import model.utilities.*;
import simulation.statistics.AnimalStats;
import simulation.statistics.SimSettings;
import simulation.statistics.SimStats;

import java.util.*;

public class Simulation implements Runnable {

    private final Random random = new Random();
    private final SimSettings settings;
    private final Factory factory;
    private final SimPresenter presenter;
    private final UUID uuid = UUID.randomUUID();
    private final SimMap map;
    private final Planter planter;
    private final Feeder feeder;
    private final Cleaner cleaner;
    private final Copulator copulator;
    private Animal trackedAnimal;
    private boolean ableToRun = true;
    private final Object lock = new Object();
    private boolean paused;
    private int day = 0;

    public Simulation(SimSettings settings, SimPresenter presenter) {

        this.settings = settings;
        this.presenter = presenter;
        if (presenter != null) this.presenter.setupPresenter(this);
        this.map = new SimMap(this);

        if (settings.plantsGrowVariant().equals("Equator")) {

            planter = new PlanterEquator(this);
            cleaner = new Cleaner(map);

        } else if (settings.plantsGrowVariant().equals("On dead animals")) {

            planter = new PlanterOnDead(this);
            cleaner = new CleanerOnDead(map, planter);

        } else throw new IllegalArgumentException("Wrong argument: %s".formatted(settings.plantsGrowVariant()));

        feeder = new Feeder(map, planter);
        copulator = new Copulator(this);
        factory = new Factory(this);

        planter.spawnStartPlants();
        map.spawnStartAnimals();
        map.getAnimalGrid().sortAnimals();
        updatePresenter();
    }

    public Random random() {
        return random;
    }

    public SimSettings settings() {
        return settings;
    }

    public GenomeFactory genomeFactory() {
        return factory.genomeFactory();
    }

    @Override
    public void run() {
        while (!map.getAnimalList().isEmpty() && ableToRun) {
            try {
                synchronized (lock) { while (paused) lock.wait(); }
                Thread.sleep(settings.frameTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            processDay();
            updatePresenter();
        }
    }

    private void processDay() {
        day++;
        map.moveAnimals();
        cleaner.clean();
        map.getAnimalGrid().sortAnimals();
        feeder.plantEating();
        copulator.breedAnimals();
        planter.spawnPlants();
    }

    public UUID getID() {
        return uuid;
    }

    public SimMap getMap() {
        return map;
    }

    public Optional<Animal> getTrackedAnimal() {
        return Optional.ofNullable(trackedAnimal);
    }

    public void setTrackedAnimal(Animal animal) {
        trackedAnimal = animal;
    }

    private void updatePresenter() {
        if (presenter != null) presenter.update(map, planter, createSimStats(), (trackedAnimal != null ? AnimalStats.getAnimalStats(trackedAnimal) : null));
    }

    private SimStats createSimStats() {
        return SimStats.getStats(this, day, map.getAnimalList().size(), map.getDeadAnimalList().size(), map.getPlants().size());
    }

    public List<Animal> getAnimalsFromCell(Position position) {
        map.getAnimalGrid().get(position).sort(Animal::compareTo);
        return map.getAnimalGrid().get(position);
    }

    public void stop() {
        ableToRun = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public void pauseAnimation() {
        if (paused) { paused = false; synchronized (lock) { lock.notify(); } }
        else paused = true;
    }

    public int getMostEnergy() {
        int maxEnergy = 0;
        for (Animal animal : map.getAnimalList()) maxEnergy = Math.max(maxEnergy, animal.getEnergy());
        return maxEnergy;
    }
}
