import model.animal.Genome;
import simulation.Simulation;
import simulation.statistics.SimSettings;
import model.animal.GenomeStandard;
import model.animal.GenomeBackAndForth;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;


public class GenomeTest {

    @Test
    public void checkReading() {

        SimSettings settings = new SimSettings(
                10,10,
                10,10,3,"Equator",
                3,20,20,10,
                0,3,"Standard",
                5,"Back and forth", false, 500);


        Genome genome = new GenomeBackAndForth(new Simulation(settings, null), new int[]{1,2,3,4,5});
        genome.setActivePoint(0);
        int[] readArray = new int[10];
        for (int i = 0; i < 10; i++) readArray[i] = genome.getNext();
        assertArrayEquals(readArray, new int[]{1,2,3,4,5,5,4,3,2,1});

        genome.setActivePoint(3);
        for (int i = 0; i < 10; i++) readArray[i] = genome.getNext();
        assertArrayEquals(readArray, new int[]{4,5,5,4,3,2,1,1,2,3});


        genome = new GenomeStandard(new Simulation(settings, null), new int[]{1,2,3,4,5});
        genome.setActivePoint(2);
        for (int i = 0; i < 10; i++) readArray[i] = genome.getNext();
        assertArrayEquals(readArray, new int[]{3,4,5,1,2,3,4,5,1,2});
    }
}
