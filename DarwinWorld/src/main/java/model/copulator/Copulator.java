package model.copulator;

import model.animal.Animal;
import model.animal.Genome;
import simulation.Simulation;

import java.util.*;

public class Copulator {

    private final Simulation sim;
    private final int fullEnergyCount;
    private final int genomeLength;

    public Copulator(Simulation simulation){
        this.sim = simulation;
        this.fullEnergyCount = simulation.settings().fullEnergyCount();
        this.genomeLength = simulation.settings().genomeLength();
    }
    public void breedAnimals() {
        List<Animal> children = new ArrayList<>();
        sim.getMap().getAnimalGrid().forEach(animalList -> children.addAll(makeBabies(animalList)));
        sim.getMap().spawnAnimalsFromList(children);
    }
    public Animal[] getParents(List<Animal> animalList, int i) {
        if (animalList.size() < i + 2) return null;
        Animal animal1 = animalList.get(i);
        Animal animal2 = animalList.get(i + 1);
        if (animal1.getEnergy() < fullEnergyCount || animal2.getEnergy() < fullEnergyCount) return null;
        return new Animal[]{animal1, animal2};
    }

    public List<Animal> makeBabies(List<Animal> animalList) {
        int i = 0;
        List<Animal> children = new ArrayList<>();
        Animal[] parents = getParents(animalList, i);
        while (parents != null) {
            Genome genome = sim.genomeFactory().createGenome(getChildGenomeList(parents));
            genome.mutate();
            Animal child = new Animal(sim, sim.settings().energyLossToCopulate() * 2, parents[0].getPosition(), genome);
            parents[0].processCopulation(child);
            parents[1].processCopulation(child);
            children.add(child);
            i += 2;
            parents = getParents(animalList, i);
        }
        return children;
    }

    private int[] getChildGenomeList(Animal[] parents) {
        int order = sim.random().nextInt(0,2);
        int[] energies = new int[] {parents[0].getEnergy(), parents[1].getEnergy()};
        int pivot = (genomeLength * energies[order]) / (energies[0] + energies[1]);
        int[] childGenomeList = new int[genomeLength];
        System.arraycopy(parents[order].getGenome().getGenomeList(), 0, childGenomeList, 0, pivot);
        System.arraycopy(parents[(order + 1) % 2].getGenome().getGenomeList(), pivot, childGenomeList, pivot, genomeLength - pivot);
        return childGenomeList;
    }
}
