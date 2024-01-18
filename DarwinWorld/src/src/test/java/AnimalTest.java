import com.sun.javafx.geom.AreaOp;
import model.animal.Animal;
import model.animal.GenomeStandard;
import model.copulator.Copulator;
import model.utilities.Position;
import org.junit.jupiter.api.Test;
import simulation.Simulation;
import simulation.statistics.SimSettings;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class AnimalTest {

    Simulation sim = new Simulation(new SimSettings(
            10,10,
            10,10,3,"Equator",
            3,40,20,30,
            0,3,"Standard",
            5,"Back and forth", false, 500), null);

    Copulator copulator = new Copulator(sim);
    @Test
    public void descendantsCountTest() {
        /*Animal a1 = new Animal(sim, 10);
        Animal child1 = new Animal(sim, 10);
        Animal child2 = new Animal(sim, 10);
        a1.processCopulation(child1);
        a1.processCopulation(child2);
        Animal child3 = new Animal(sim, 10);
        Animal child4 = new Animal(sim, 10);

        child1.processCopulation(child3);        child2.processCopulation(child3);

        child1.processCopulation(child4);
        child2.processCopulation(child4);

        assertEquals(a1.getDescendantsCount(), 4); // na pewno nie 6*/

        List<Animal> parents = new ArrayList<>();
        parents.add(new Animal(sim,100,new Position(0,0),new GenomeStandard(sim,new int[]{0,0,0,0,0}),null));
        parents.add(new Animal(sim,100,new Position(0,0),new GenomeStandard(sim,new int[]{0,0,0,0,0}),null));
        List<Animal> children = copulator.makeBabies(parents);
        children.addAll(copulator.makeBabies(parents));
        List<Animal> grandchildren = copulator.makeBabies(children);
        grandchildren.addAll(copulator.makeBabies(children));
        assertEquals(4,parents.get(0).getDescendantsCount());
    }
}
