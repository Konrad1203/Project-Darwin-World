package simulation.statistics;

import java.io.Serializable;

public record SimSettings(
        int height, int width,
        int startPlantsCount, int energyFromPlant, int dailyPlantsGrowCount, String plantsGrowVariant,
        int startAnimalsCount, int startEnergyCount, int fullEnergyCount, int energyLossToCopulate,
        int minMutationCount, int maxMutationCount, String mutationVariant,
        int genomeLength, String genomeVariant, boolean saveStats, int frameTime) implements Serializable {

    public static final SimSettings STANDARD_SETTINGS = new SimSettings(
            10,10,
            10,10,3,"Equator",
            10,20,15,10,
            0,3,"Standard",
            10,"Standard", false, 250);
}
