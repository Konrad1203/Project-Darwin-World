package model.util;

import org.junit.jupiter.api.Test;
import simulation.statistics.SimSettings;
import model.animal.Animal;

import java.util.Random;
import java.util.TreeSet;

public class AnimalTest {

    SimSettings settings = new SimSettings(
            10,10,
            10,10,3,"ez",
            3,20,20,10,
            0,3,"123",
            5,"Back and forth", false, 500);

    private final TreeSet<Animal> set = new TreeSet<>();

    @Test
    public void test() {
        Random random = new Random(20);
        set.add(new Animal(settings, random));
        set.add(new Animal(settings, random));
        set.add(new Animal(settings, random));
        set.add(new Animal(settings, random));
        set.add(new Animal(settings, random));

        System.out.printf("set: " + set + "\n");
        System.out.printf(set.first().toString());
    }
}
