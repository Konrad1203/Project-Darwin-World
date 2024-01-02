package model.util;

import model.planter.PlanterOnDead;
import model.utilities.Position;
import org.junit.jupiter.api.Test;
import simulation.SimMap;
import simulation.statistics.SimSettings;

import java.util.Random;

public class PlanterOnDeadTest {

    @Test
    public void test() {

        SimSettings settings = new SimSettings(
                10,10,
                10,10,3,"ez",
                3,20,20,10,
                0,3,"123",
                5,"Back and forth", false, 500);
        SimMap map = new SimMap(settings, null);
        Random random = new Random();

        PlanterOnDead planter = new PlanterOnDead(settings, map, random);

        planter.addToDeadList(new Position(6,4));
        planter.addToDeadList(new Position(4,4));
        planter.addToDeadList(new Position(5,6));

        System.out.println(planter);
    }
}
