package model.planter;

import simulation.Simulation;
import model.utilities.Position;

import java.util.Random;
import java.util.Set;

public abstract class Planter {

    protected final Set<Position> mapPlants;
    protected final int width;
    protected final int height;
    protected final Random random;
    protected final int startJungleCount;
    protected final int startSteppeCount;
    protected final int jungleCount;
    protected final int steppeCount;


    public Planter(Simulation simulation) {
        this.random = simulation.random();
        mapPlants = simulation.getMap().getPlants();
        width = simulation.settings().width();
        height = simulation.settings().height();
        startJungleCount = divideTo80_20(simulation.settings().startPlantsCount());
        startSteppeCount = simulation.settings().startPlantsCount() - startJungleCount;
        jungleCount = divideTo80_20(simulation.settings().dailyPlantsGrowCount());
        steppeCount = simulation.settings().dailyPlantsGrowCount() - jungleCount;
    }

    private int divideTo80_20(int count) {
        if (count % 5 == 0) return 4 * count / 5;
        else {
            double n = count * 0.8;
            if ( 0.2 - Math.floor(n) / count < Math.ceil(n) / count - 0.2 ) return (int) Math.floor(n);
            else return (int) Math.ceil(n);
        }
    }

    public void spawnStartPlants() {
        spawnStartPlants(startJungleCount, startSteppeCount);
    }

    public void spawnStartPlants(int jungleCount, int steppeCount) {
        if (jungleCount <= getPlantsInJungleSize()) {
            for (int i = 0; i < jungleCount; i++) mapPlants.add(getPosFromJungle());
            for (int i = 0; i < Math.min(steppeCount, getPlantsOnSteppeSize()); i++) mapPlants.add(getPosFromSteppe());
        } else {
            int n = getPlantsInJungleSize();
            if (n != 0) for (int i = 0; i < n; i++) mapPlants.add(getPosFromJungle());
            for (int i = 0; i < Math.min(steppeCount+jungleCount-n, getPlantsOnSteppeSize()); i++) mapPlants.add(getPosFromSteppe());
        }
    }

    public void spawnPlants() {
        spawnPlants(jungleCount, steppeCount);
    }

    public abstract void spawnPlants(int jungleCount, int steppeCount);

    public abstract void returnPosition(Position position);

    public abstract int getPlantsInJungleSize();

    public abstract int getPlantsOnSteppeSize();

    public abstract Position getPosFromJungle();

    public abstract Position getPosFromSteppe();

    public abstract boolean isInJungle(Position position);
}
