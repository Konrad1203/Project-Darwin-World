package model.planter;

import simulation.Simulation;
import model.utilities.Position;

import java.util.ArrayList;
import java.util.List;

public class PlanterEquator extends Planter {

    private final List<Position> plantsOnSteppe = new ArrayList<>();
    private final List<Position> plantsInJungle = new ArrayList<>();
    private final int steppeSize;
    private final int jungleSize;

    public PlanterEquator(Simulation simulation) {
        super(simulation);

        steppeSize = getEquatorSteppeSize();
        jungleSize = height - 2*steppeSize;

        for (int j = 0; j < width; j++) {
            for (int i = 0; i < steppeSize; i++) plantsOnSteppe.add(new Position(j,i));
            for (int i = steppeSize; i < height - steppeSize; i++) plantsInJungle.add(new Position(j,i));
            for (int i = height - steppeSize; i < height; i++) plantsOnSteppe.add(new Position(j,i));
        }
    }

    private int getEquatorSteppeSize() {
        if (height % 5 == 0) return 4 * height / 10;
        else {
            double n = height * 0.8;
            if ( Math.floor(n) % 2 == 0 ) return (int) Math.floor(n) / 2;
            else return (int) Math.ceil(n) / 2;
        }
    }

    @Override
    public int getPlantsInJungleSize() {
        return plantsInJungle.size();
    }

    @Override
    public int getPlantsOnSteppeSize() {
        return plantsOnSteppe.size();
    }

    @Override
    public void spawnPlants(int jungleCount, int steppeCount) {
        spawnStartPlants(jungleCount, steppeCount);
    }

    @Override
    public void returnPosition(Position pos) {
        if (pos.y() < steppeSize || pos.y() >= steppeSize + jungleSize) plantsOnSteppe.add(pos);
        else plantsInJungle.add(pos);
    }

    @Override
    public Position getPosFromJungle() {
        return plantsInJungle.remove( random.nextInt(plantsInJungle.size()) );
    }

    @Override
    public Position getPosFromSteppe() {
        return plantsOnSteppe.remove( random.nextInt(plantsOnSteppe.size()) );
    }

    @Override
    public boolean isInJungle(Position pos) {
        return pos.y() >= steppeSize && pos.y() < steppeSize + jungleSize;
    }
}
