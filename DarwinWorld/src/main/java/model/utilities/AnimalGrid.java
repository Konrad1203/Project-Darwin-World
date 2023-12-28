package model.utilities;

import simulation.statistics.SimSettings;
import model.animal.Animal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class AnimalGrid implements Iterable<TreeSet<Animal>> {

    private final List<List<TreeSet<Animal>>> animals;
    private final int rows;
    private final int cols;

    public AnimalGrid(SimSettings settings) {
        rows = settings.height();
        cols = settings.width();
        animals = new ArrayList<>(rows);

        for (int i = 0; i < rows; i++) {
            List<TreeSet<Animal>> rowList = new ArrayList<>(cols);
            for (int j = 0; j < cols; j++) {
                rowList.add(new TreeSet<>());
            }
            animals.add(rowList);
        }
    }

    public TreeSet<Animal> get(Position position) {
        return animals.get(position.y()).get(position.x());
    }

    @Override
    public Iterator<TreeSet<Animal>> iterator() {
        return new Iterator<>() {
            private int currentRow = 0;
            private int currentColumn = 0;

            @Override
            public boolean hasNext() {
                return currentRow < rows && currentColumn < cols;
            }

            @Override
            public TreeSet<Animal> next() {
                TreeSet<Animal> currentSet = animals.get(currentRow).get(currentColumn++);
                if (currentColumn >= cols) {
                    currentColumn = 0;
                    currentRow++;
                }
                return currentSet;
            }
        };
    }
}

/*
public class AnimalGrid { //implements Iterable<TreeSet<Animal>> {

    private final TreeSet<Animal>[][] animals;
    private final int rows;
    private final int cols;

    public AnimalGrid(SimSettings settings) {
        rows = settings.height();
        cols = settings.width();
        animals = new TreeSet[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                animals[i][j] = new TreeSet<>();
            }
        }
    }

    public TreeSet<Animal> get(Position position) {
        return animals[position.y()][position.x()];
    }

    @Override
    public Iterator<TreeSet<Animal>> iterator() {
        return new Iterator<>() {
            private int currentRow = 0;
            private int currentColumn = 0;

            @Override
            public boolean hasNext() {
                return currentRow < rows && currentColumn < cols;
            }

            @Override
            public TreeSet<Animal> next() {
                TreeSet<Animal> currentSet = animals[currentRow][currentColumn++];
                if (currentColumn >= cols) {
                    currentColumn = 0;
                    currentRow++;
                }
                return currentSet;
            }
        };
    }
}
*/
