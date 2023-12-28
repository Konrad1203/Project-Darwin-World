package model.animal;

import simulation.statistics.SimSettings;

import java.util.Random;

public class GenomeBackAndForth extends Genome {

    private int readingDirection = 1;

    public GenomeBackAndForth(SimSettings settings, Random random) {
        super(settings, random);
    }

    @Override
    public int getNext() {
        if (activePoint == length) {
            activePoint = 0;
            readingDirection *= -1;
        }
        int gen;
        if (readingDirection == 1) gen = genome[activePoint];
        else gen = genome[length-1- activePoint];
        activePoint++;
        return gen;
    }
}
