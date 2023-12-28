package simulation.statistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public record SimStats(int day, int animalCount, int deadAnimalCount, int plantCount, int freeFieldCount, String popularGenotype,
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


}

