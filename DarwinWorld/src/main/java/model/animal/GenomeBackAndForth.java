package model.animal;

import simulation.Simulation;

public class GenomeBackAndForth extends Genome {

    private boolean readingDirection = true;

    public GenomeBackAndForth(Simulation simulation) {
        super(simulation);
    }

    public GenomeBackAndForth(Simulation simulation, int[] genomeList) {
        super(simulation, genomeList);
    }

    @Override
    public int getActivePoint() {
        return genome[readingDirection ? activePoint-1 : length-activePoint];
    }

    @Override
    public void setActivePoint(int activePoint) {
        super.setActivePoint(activePoint);
        readingDirection = true;
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
