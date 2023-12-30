package simulation.statistics;

public record AnimalStats(int[] genome, int activeGenome, int energyCount, int plantsEaten,
                          int childrenCount, int descendantsCount, int daysSurvived, boolean isDead) {
}
