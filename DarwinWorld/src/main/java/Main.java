import javafx.application.Application;
import model.animal.Animal;
import simulation.SimApp;
import simulation.statistics.SimSettings;

import java.util.Random;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) {
        System.out.println("Działa :>");
        Application.launch(SimApp.class, args);
    }
}