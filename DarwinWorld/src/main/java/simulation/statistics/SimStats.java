package simulation.statistics;

import model.animal.Animal;
import model.utilities.Position;
import simulation.SimMap;
import simulation.Simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SimStats(int day, int animalCount, int deadAnimalCount, int plantCount, int freeFieldCount, List<Integer> popularGenotype,
                       double averageEnergyCount, double averageLifeSpan, double averageChildrenCount) {

    private static final char p = ';';

    private static final String header = "Day" + p + "AnimalCount" + p + "DeadAnimalCount" + p + "PlantCount" + p +
            "FreeFieldCount" + p + "PopularGenotype" + p + "AverageEnergyCount" + p +
            "AverageLifeSpan" + p + "AverageChildrenCount";

    public void printSimStatsToCSV(UUID id) {
        File file = new File("result-" + id + ".csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file,true))) {
            if (file.length() == 0) pw.println(header);
            pw.println("%d%s%d%s%d%s%d%s%d%s%s%s%.2f%s%.2f%s%.2f".formatted(this.day,p,this.animalCount,p,this.deadAnimalCount,p,this.plantCount,
                    p,this.freeFieldCount,p,this.popularGenotype,p,this.averageEnergyCount,p,this.averageLifeSpan,p,this.averageChildrenCount));
        } catch (IOException e) {
            System.out.println("Error during overwriting file: " + e);
        }
    }

    public static SimStats getStats(Simulation sim, int day, int animalCount, int deadAnimalCount, int plantCount) {
        SimMap map = sim.getMap();
        return new SimStats(day, animalCount, deadAnimalCount, plantCount, calculateFreeFieldsCount(sim),
                calculatePopularGenotype(map), calculateAverageEnergyCount(map),
                calculateAverageLifeSpan(map), calculateAverageChildrenCount(map));
    }

    private static int calculateFreeFieldsCount(Simulation sim) {
        int counter = 0;
        for (int row = 0; row < sim.settings.height(); row++) {
            for (int col = 0; col < sim.settings.width(); col++) {
                Position pos = new Position(col, row);
                if (!sim.getMap().isPlant(pos) && !sim.getMap().isAnimal(pos)) counter++;
            }
        }
        return counter;
    }

    private static List<Integer> calculatePopularGenotype(SimMap map) {
        int[] genomeCounter = new int[8];
        for (Animal animal : map.getAnimalList()) animal.getGenome().addGenomeCountsToList(genomeCounter);
        int maxIndex = 0;
        for (int i = 1; i < 8; i++) if (genomeCounter[i] > genomeCounter[maxIndex]) maxIndex = i;
        List<Integer> genomes = new ArrayList<>();
        genomes.add(maxIndex);
        for (int i = maxIndex+1; i < 8; i++) if (genomeCounter[maxIndex] == genomeCounter[i]) genomes.add(i);
        return genomes;
    }

    private static double calculateAverageEnergyCount(SimMap map) {
        double energySum = 0;
        for (Animal animal : map.getAnimalList()) energySum += animal.getEnergy();
        return energySum / map.getAnimalList().size();
    }

    private static double calculateAverageLifeSpan(SimMap map) {
        double lifeSpanSum = 0;
        for (Animal animal : map.getDeadAnimalList()) lifeSpanSum += animal.getDaysSurvived();
        return lifeSpanSum / map.getDeadAnimalList().size();
    }

    private static double calculateAverageChildrenCount(SimMap map) {
        double childrenCountSum = 0;
        for (Animal animal : map.getAnimalList()) childrenCountSum += animal.getChildrenCount();
        return childrenCountSum / map.getAnimalList().size();
    }
}

