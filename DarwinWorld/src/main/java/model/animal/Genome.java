package model.animal;

import simulation.Simulation;

import java.util.Arrays;

public abstract class Genome {

    private final Simulation sim;
    protected final int[] genome;
    protected int activePoint;
    protected final int length;

    public Genome(Simulation simulation) {
        sim = simulation;
        length = sim.settings().genomeLength();
        genome = sim.random().ints(length, 0, 8).toArray();
        activePoint = sim.random().nextInt(length);
    }

    public Genome(Simulation simulation, int[] genomeList) {
        sim = simulation;
        length = sim.settings().genomeLength();
        genome = genomeList;
        activePoint = sim.random().nextInt(length);
    }

    public int[] getGenomeList() {
        return genome;
    }

    public abstract int getActivePoint();

    public abstract int getNext();

    public void addGenomeCountsToList(int[] genomeCounter) {
        for (int gen : genome) {
            genomeCounter[gen]++;
        }
    }

    public void mutate() {
        if (sim.settings().mutationVariant().equals("Standard")) {
            int mutationCount = sim.random().nextInt(sim.settings().minMutationCount(), sim.settings().maxMutationCount()+1);
            for (int i = 0; i < mutationCount; i++) {
                genome[sim.random().nextInt(length)] = sim.random().nextInt(0, 8);
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(genome);
    }
}
