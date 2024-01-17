import model.animal.Animal;
import org.junit.jupiter.api.Test;
import simulation.Simulation;
import simulation.statistics.SimSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnimalTest {

    Simulation sim = new Simulation(new SimSettings(
            10,10,
            10,10,3,"Equator",
            3,20,20,10,
            0,3,"Standard",
            5,"Back and forth", false, 500), null);

    @Test
    public void descendantsCountTest() {
        Animal a1 = new Animal(sim, 10);
        Animal child1 = new Animal(sim, 10);
        Animal child2 = new Animal(sim, 10);
        a1.processCopulation(child1);
        a1.processCopulation(child2);
        Animal child3 = new Animal(sim, 10);
        Animal child4 = new Animal(sim, 10);

        child1.processCopulation(child3);
        child2.processCopulation(child3);

        child1.processCopulation(child4);
        child2.processCopulation(child4);

        assertEquals(a1.getDescendantsCount(), 4); // na pewno nie 6
    }
}
