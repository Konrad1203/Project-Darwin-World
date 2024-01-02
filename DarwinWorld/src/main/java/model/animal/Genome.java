package model.animal;

import simulation.statistics.SimSettings;

import java.util.Arrays;
import java.util.Random;

public class Genome {

    protected final int[] genome;
    protected int activePoint;
    protected final int length;
    private final SimSettings settings;
    private final Random random;

    public Genome(SimSettings settings, Random random) {
        this.settings = settings;
        this.random = random;
        length = settings.genomeLength();
        genome = random.ints(length, 0, 8).toArray();
        activePoint = random.nextInt(length);
    }

    public Genome(SimSettings settings, Random random, int[] genomeList) {
        this.settings = settings;
        this.random = random;
        length = settings.genomeLength();
        genome = genomeList;
        activePoint = random.nextInt(length);
    }

    public int[] getGenomeList() {
        return genome;
    }

    public int getActivePoint() {
        return genome[activePoint-1];
    }

    public int getNext() {
        if (activePoint == length) activePoint = 0;
        int gen = genome[activePoint];
        activePoint++;
        return gen;
    }

    public void addGenomeCountsToList(int[] genomeCounter) {
        for (int gen : genome) {
            genomeCounter[gen]++;
        }
    }

    public void mutate() {
        if (settings.mutationVariant().equals("Standard")) {
            int mutationCount = random.nextInt(settings.minMutationCount(), settings.maxMutationCount()+1);
            for (int i = 0; i < mutationCount; i++) {
                genome[random.nextInt(length)] = random.nextInt(0, 8);
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(genome);
    }
}
