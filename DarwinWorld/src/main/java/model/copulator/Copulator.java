package model.copulator;

import model.animal.Animal;
import simulation.SimMap;
import simulation.statistics.SimSettings;
import java.util.*;

public class Copulator {

    private final SimSettings settings;
    private final int fullEnergyCount;
    private final int genomeLength;
    private final Random random;
    private final SimMap map;

    public Copulator(SimSettings settings, SimMap map, Random random){
        this.settings = settings;
        this.fullEnergyCount = settings.fullEnergyCount();
        this.genomeLength = settings.genomeLength();
        this.random = random;
        this.map = map;
    }
    public void breedAnimals() {
        List<Animal> children = new ArrayList<>();
        Animal child;
        for (List<Animal> animalList : map.getAnimalGrid()) {
            child = makeBaby(getParents(animalList));
            if (child != null) children.add(child);
        }
        map.spawnAnimalsFromList(children);
    }
    public Animal[] getParents(List<Animal> animalList) {
        if (animalList.size() < 2) return null;
        Animal animal1 = animalList.get(0);
        Animal animal2 = animalList.get(1);
        if (animal1.getEnergy() < fullEnergyCount || animal2 == null || animal2.getEnergy() < fullEnergyCount) return null;
        return new Animal[]{animal1, animal2};
    }

    public Animal makeBaby(Animal[] parents) {
        if (parents == null) return null;
        int order = random.nextInt(0,2);
        int[] energies = new int[] {parents[0].getEnergy(), parents[1].getEnergy()};
        int pivot = (genomeLength * energies[order]) / (energies[0] + energies[1]);
        int[] childGenomeList = new int[genomeLength];
        System.arraycopy(parents[order].getGenome().getGenomeList(), 0, childGenomeList, 0, pivot);
        System.arraycopy(parents[(order + 1) % 2].getGenome().getGenomeList(), pivot, childGenomeList, pivot, genomeLength - pivot);
        Animal child = new Animal(settings, random, parents[0].getPosition(), childGenomeList);
        parents[0].loseEnergyFromCopulation();
        parents[0].addChild(child);
        parents[1].loseEnergyFromCopulation();
        parents[1].addChild(child);
        return child;
    }
}
