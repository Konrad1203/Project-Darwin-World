package model.animal;

import simulation.Simulation;

public class GenomeStandardFactory implements GenomeFactory {

    private final Simulation simulation;

    public GenomeStandardFactory(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Genome createGenome() {
        return new GenomeStandard(simulation);
    }

    @Override
    public Genome createGenome(int[] genomeList) {
        return new GenomeStandard(simulation, genomeList);
    }
}
