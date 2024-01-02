package model.util;

import simulation.statistics.SimSettings;
import model.animal.Genome;
import model.animal.GenomeBackAndForth;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class GenomeTest {

    @Test
    public void checkReading() {

        Random random = new Random();

        SimSettings settings = new SimSettings(
                10,10,
                10,10,3,"ez",
                3,20,20,10,
                0,3,"123",
                5,"Back and forth", false, 500);


        Genome genome = new GenomeBackAndForth(settings, random);

        for (int i = 0; i < 10; i++) System.out.printf("%d ",genome.getNext());
        System.out.println();

        genome = new Genome(settings, random);

        for (int i = 0; i < 10; i++) System.out.printf("%d ",genome.getNext());
        System.out.println();
    }
}
