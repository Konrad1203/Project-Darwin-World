package model.animal;

import simulation.statistics.SimSettings;

import java.util.Random;

public class GenomeBackAndForth extends Genome {

    private boolean readingDirection = true;

    public GenomeBackAndForth(SimSettings settings, Random random) {
        super(settings, random);
    }

    @Override
    public int getActivePoint() {
        return genome[readingDirection ? activePoint-1 : length-activePoint];
    }

    @Override
    public int getNext() {
        if (activePoint == length) {
            activePoint = 0;
            readingDirection = !readingDirection;
        }
        int gen;
        if (readingDirection) gen = genome[activePoint];
        else gen = genome[length-1- activePoint];
        activePoint++;
        return gen;
    }
}
