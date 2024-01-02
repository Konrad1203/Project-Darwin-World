package model.planter;

import simulation.statistics.SimSettings;
import simulation.SimMap;
import model.utilities.Position;

import java.util.*;

public class PlanterOnDead extends Planter {

    private final LinkedList<Position> deadList = new LinkedList<>();
    private final LinkedList<Integer> deadListCount = new LinkedList<>();
    private final Set<Position> plantsOnDead = new HashSet<>();
    private final Set<Position> plantsOnSteppe = new HashSet<>();
    private final int[][] relationCount;

    public PlanterOnDead(SimSettings settings, SimMap map, Random random) {
        super(settings, map, random);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                plantsOnSteppe.add(new Position(col, row));
            }
        }
        relationCount = new int[height][width];
    }

    @Override
    public int getPlantsInJungleSize() {
        return plantsOnDead.size();
    }

    @Override
    public int getPlantsOnSteppeSize() {
        return plantsOnSteppe.size();
    }

    @Override
    public void spawnPlants(int jungleCount, int steppeCount) {
        spawnStartPlants(jungleCount, steppeCount);
        countDownDead();
        removeDecomposed();
    }

    @Override
    public void returnPosition(Position pos) {
        if (relationCount[pos.y()][pos.x()] == 0) plantsOnSteppe.add(pos);
        else plantsOnDead.add(pos);
    }

    public void addToDeadList(Position pos) {
        deadList.addLast(pos);
        deadListCount.addLast(30);

        for (int x = Math.max(pos.x()-1,0); x < Math.min(pos.x()+2,width); x++) {
            for (int y = Math.max(pos.y()-1,0); y < Math.min(pos.y()+2,height); y++) {
                Position loopPos = new Position(x, y);
                relationCount[loopPos.y()][loopPos.x()]++;
                if (plantsOnSteppe.contains(loopPos)) {
                    plantsOnSteppe.remove(loopPos);
                    plantsOnDead.add(loopPos);
                }
            }
        }
    }

    private void countDownDead() {
        ListIterator<Integer> iterator = deadListCount.listIterator();
        while (iterator.hasNext()) iterator.set(iterator.next() - 1);
    }

    private void removeDecomposed() {
        while ( !deadListCount.isEmpty() ) {
            if ( deadListCount.getFirst() == 0 ) {
                Position pos = deadList.removeFirst();
                deadListCount.removeFirst();

                for (int x = Math.max(pos.x()-1,0); x < Math.min(pos.x()+2,width); x++) {
                    for (int y = Math.max(pos.y()-1,0); y < Math.min(pos.y()+2,height); y++) {
                        Position loopPos = new Position(x, y);
                        relationCount[loopPos.y()][loopPos.x()]--;
                        if (plantsOnDead.contains(loopPos)) {
                            if (removeFromPlantsOnDead(loopPos)) plantsOnSteppe.add(loopPos);
                        }
                    }
                }
            } else break;
        }
    }

    private boolean removeFromPlantsOnDead(Position pos) {
        if (relationCount[pos.y()][pos.x()] == 0) {
            plantsOnDead.remove(pos);
            return true;
        } else return false;
    }

    @Override
    public Position getPosFromJungle() { // getPosFromDead
        Position loopPos = (Position) plantsOnDead.toArray()[random.nextInt(plantsOnDead.size())];
        plantsOnDead.remove(loopPos);
        return loopPos;
    }

    @Override
    public Position getPosFromSteppe() {
        Position loopPos = (Position) plantsOnSteppe.toArray()[random.nextInt(plantsOnSteppe.size())];
        plantsOnSteppe.remove(loopPos);
        return loopPos;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result.append( "%d ".formatted( relationCount[i][j] ) );
            }
            result.append("\n");
        }
        return result.toString();
    }

    @Override
    public boolean isInJungle(Position pos) {
        return relationCount[pos.y()][pos.x()] > 0;
    }

    public List<Position> getJungleList() {
        return plantsOnDead.stream().toList();
    }
}
