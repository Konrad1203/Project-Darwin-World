package model.animal;

import simulation.statistics.SimSettings;
import java.util.Random;

public class Genome {

    protected final int[] genome;
    protected int activePoint;
    protected final int length;

    public Genome(SimSettings settings, Random random) {
        length = settings.genomeLength();
        genome = random.ints(length, 0, 8).toArray();
        activePoint = random.nextInt(length);
    }

    public int[] getGenomeList() {
        return genome;
    }

    public int getActivePoint() {
        return genome[activePoint%length];
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
}
