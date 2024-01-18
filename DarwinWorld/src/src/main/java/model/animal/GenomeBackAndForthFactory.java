package model.animal;

import simulation.Simulation;

public class GenomeBackAndForthFactory implements GenomeFactory {

    private final Simulation simulation;

    public GenomeBackAndForthFactory(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Genome createGenome() {
        return new GenomeBackAndForth(simulation);
    }

    @Override
    public Genome createGenome(int[] genomeList) {
        return new GenomeBackAndForth(simulation, genomeList);
    }
}
