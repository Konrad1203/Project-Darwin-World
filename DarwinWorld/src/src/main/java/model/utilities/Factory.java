package model.utilities;

import model.animal.GenomeBackAndForthFactory;
import model.animal.GenomeFactory;
import model.animal.GenomeStandardFactory;
import simulation.Simulation;

public class Factory {

    private final GenomeFactory genomeFactory;

    public Factory(Simulation simulation) {
        if (simulation.settings().genomeVariant().equals("Back and forth"))
            genomeFactory = new GenomeBackAndForthFactory(simulation);
        else
            genomeFactory = new GenomeStandardFactory(simulation);
    }

    public GenomeFactory genomeFactory() {
        return genomeFactory;
    }
}
