package simulation;

import GUI.SimPresenter;
import model.animal.Animal;
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

    private final UUID uuid = UUID.randomUUID();
    public final SimSettings settings;
    private final SimPresenter presenter;
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

    public Simulation(SimSettings settings, SimPresenter pr) {

        this.settings = settings;
        this.presenter = pr;
        presenter.setSimulation(this);
        this.map = new SimMap(settings);
        Random random = new Random();

        if (settings.plantsGrowVariant().equals("Equator")) {

            planter = new PlanterEquator(settings, map, random);
            cleaner = new Cleaner(map);

        } else if (settings.plantsGrowVariant().equals("On dead animals")) {

            planter = new PlanterOnDead(settings, map, random);
            cleaner = new CleanerOnDead(map, planter);

        } else throw new IllegalArgumentException("Wrong argument: %s".formatted(settings.plantsGrowVariant()));

        feeder = new Feeder(map, planter);
        copulator = new Copulator(settings, map, random);

        planter.spawnStartPlants();
        map.spawnStartAnimals(random);
        map.getAnimalGrid().sortAnimals();
        updatePresenter();
    }

    @Override
    public void run() {
        while ( !map.getAnimalList().isEmpty() && ableToRun ) {
            try {
                synchronized (lock) { while (paused) lock.wait(); }
                Thread.sleep(settings.frameTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            day++;
            map.moveAnimals();
            cleaner.clean();
            map.getAnimalGrid().sortAnimals();
            feeder.plantEating();
            copulator.breedAnimals();
            planter.spawnPlants();
            updatePresenter();
        }
    }

    public UUID getID() {
        return uuid;
    }

    public Optional<Animal> getTrackedAnimal() {
        return Optional.ofNullable(trackedAnimal);
    }

    public void setTrackedAnimal(Animal animal) {
        trackedAnimal = animal;
    }

    private void updatePresenter() {
        presenter.update(map, planter, createSimStats(), createTrackedAnimalStats(trackedAnimal));

    }
    public AnimalStats createTrackedAnimalStats(Animal a) {
        if (a != null) return new AnimalStats(a.getGenome().getGenomeList(), a.getGenome().getActivePoint(),
                a.getEnergy(), a.getPlantsEaten(), a.getChildrenCount(), a.getDescendantsCount(),
                a.getDaysSurvived(), a.isDead());
        else return null;
    }

    private SimStats createSimStats() {
        return new SimStats(day, map.getAnimalList().size(), map.getDeadAnimalList().size(), map.getPlants().size(),
                calculateFreeFieldsCount(), calculatePopularGenotype(), calculateAverageEnergyCount(),
                calculateAverageLifeSpan(), calculateAverageChildrenCount());
    }

    private int calculateFreeFieldsCount() {
        int counter = 0;
        for (int row = 0; row < settings.height(); row++) {
            for (int col = 0; col < settings.width(); col++) {
                Position pos = new Position(col, row);
                if (!map.isPlant(pos) && !map.isAnimal(pos)) counter++;
            }
        }
        return counter;
    }

    private List<Integer> calculatePopularGenotype() {
        int[] genomeCounter = new int[8];
        for (Animal animal : map.getAnimalList()) animal.getGenome().addGenomeCountsToList(genomeCounter);
        int maxIndex = 0;
        for (int i = 1; i < 8; i++) if (genomeCounter[i] > genomeCounter[maxIndex]) maxIndex = i;
        List<Integer> genomes = new ArrayList<>();
        genomes.add(maxIndex);
        for (int i = maxIndex+1; i < 8; i++) if (genomeCounter[maxIndex] == genomeCounter[i]) genomes.add(i);
        return genomes;
    }

    private double calculateAverageEnergyCount() {
        double energySum = 0;
        for (Animal animal : map.getAnimalList()) energySum += animal.getEnergy();
        return energySum / map.getAnimalList().size();
    }

    private double calculateAverageLifeSpan() {
        double lifeSpanSum = 0;
        for (Animal animal : map.getDeadAnimalList()) lifeSpanSum += animal.getDaysSurvived();
        return lifeSpanSum / map.getDeadAnimalList().size();
    }

    private double calculateAverageChildrenCount() {
        double childrenCountSum = 0;
        for (Animal animal : map.getAnimalList()) childrenCountSum += animal.getChildrenCount();
        return childrenCountSum / map.getAnimalList().size();
    }

    public List<Animal> getAnimalsFromCell(Position position) {
        return map.getAnimalGrid().get(position).stream().toList();
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
