package simulation.statistics;

import model.animal.Animal;

public record AnimalStats(int[] genome, int activeGenome, int energyCount, int plantsEaten,
                          int childrenCount, int descendantsCount, int daysSurvived, boolean isDead) {

    public static AnimalStats getAnimalStats(Animal a) {
        return new AnimalStats(a.getGenome().getGenomeList(), a.getGenome().getActivePoint(),
                a.getEnergy(), a.getPlantsEaten(), a.getChildrenCount(), a.getDescendantsCount(),
                a.getDaysSurvived(), a.isDead());
    }
}
