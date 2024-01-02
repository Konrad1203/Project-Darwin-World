package model.cleaner;

import simulation.SimMap;
import model.animal.Animal;
import model.planter.Planter;
import model.planter.PlanterOnDead;

public class CleanerOnDead extends Cleaner {

    private final PlanterOnDead planter;

    public CleanerOnDead(SimMap map, Planter planter) {
        super(map);
        this.planter = (PlanterOnDead) planter;
    }

    @Override
    protected void makeAdditionalThings(Animal animal) {
        planter.addToDeadList( animal.getPosition() );
    }
}
