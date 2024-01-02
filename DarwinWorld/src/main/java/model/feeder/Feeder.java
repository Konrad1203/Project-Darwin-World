package model.feeder;

import simulation.SimMap;
import model.planter.Planter;
import model.utilities.Position;

import java.util.Iterator;

public class Feeder {

    private final SimMap map;
    private final Planter planter;

    public Feeder(SimMap map, Planter planter) {
        this.map = map;
        this.planter = planter;
    }

    public void plantEating() {
        Iterator<Position> iterator = map.getPlants().iterator();
        while (iterator.hasNext()) {
            Position pos = iterator.next();
            if (!map.getAnimalGrid().get(pos).isEmpty()) {
                map.getAnimalGrid().get(pos).first().consumePlant();
                iterator.remove();
                planter.returnPosition(pos);
            }
        }
    }
}
