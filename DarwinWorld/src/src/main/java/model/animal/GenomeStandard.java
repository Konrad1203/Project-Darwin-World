package model.animal;

import simulation.Simulation;

public class GenomeStandard extends Genome {

    public GenomeStandard(Simulation simulation) {
        super(simulation);
    }

    public GenomeStandard(Simulation simulation, int[] genomeList) {
        super(simulation, genomeList);
    }

    @Override
    public int getActivePoint() {
        return genome[activePoint - 1];
    }

    @Override
    public int getNext() {
        if (activePoint == length) activePoint = 0;
        int gen = genome[activePoint];
        activePoint++;
        return gen;
    }
}
