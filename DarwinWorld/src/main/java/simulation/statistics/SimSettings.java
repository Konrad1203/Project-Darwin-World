package simulation.statistics;

import java.io.Serializable;

public record SimSettings(
        int height, int width,
        int startPlantsCount, int energyFromPlant, int dailyPlantsGrowCount, String plantsGrowVariant,
        int startAnimalsCount, int startEnergyCount, int fullEnergyCount, int energyLossToCopulate,
        int minMutationCount, int maxMutationCount, String mutationVariant,
        int genomeLength, String genomeVariant, boolean saveStats, int frameTime) implements Serializable {
}
