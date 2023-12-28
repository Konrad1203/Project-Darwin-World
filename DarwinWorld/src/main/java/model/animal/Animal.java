package model.animal;

import simulation.statistics.SimSettings;
import simulation.SimMap;
import model.utilities.Orientation;
import model.utilities.Position;

import java.util.*;

public class Animal implements Comparable<Animal> {

    private Position position;
    private Orientation orientation;
    private final Genome genome;
    private int energy;
    private final List<Animal> children = new ArrayList<>();
    private int daysSurvived = 0;
    private int plantsEaten = 0;
    private int deadDay;
    private final UUID uuid = UUID.randomUUID();
    private final SimSettings settings;

    public Animal(SimSettings settings, Random random) {
        this.settings = settings;
        position = new Position(random.nextInt(settings.width()), random.nextInt(settings.height()));
        orientation = Orientation.getOrientationFromNumber(random.nextInt(8));
        energy = settings.startEnergyCount();
        if ( settings.genomeVariant().equals("Back and forth") ) genome = new GenomeBackAndForth(settings, random);
        else genome = new Genome(settings, random);
    }

    public Position getPosition() {
        return position;
    }

    public int getEnergy() {
        return this.energy;
    }

    public UUID getID() {
        return uuid;
    }

    public Genome getGenome() {
        return genome;
    }

    public int getDaysSurvived() {
        return daysSurvived;
    }

    public int getChildrenCount() {
        return children.size();
    }

    public int getPlantsEaten() {
        return plantsEaten;
    }

    public int getDeadDay() {
        return deadDay;
    }

    public void setDeadDay(int day) {
        deadDay = day;
    }

    public void consumePlant() {
        energy += settings.energyFromPlant();
        plantsEaten++;
    }

    public void move(SimMap validator) {
        orientation = orientation.rotate(genome.getNext());
        Position tempPos = position.add(orientation.toVector());
        if (validator.canMoveTo(this, tempPos)) position = tempPos;
        energy--; daysSurvived++;
    }

    public void moveToOtherSide(Position tempPos) {
        if (tempPos.x() < 0) position = new Position(settings.width()-1, tempPos.y());
        else position = new Position(0, tempPos.y());
    }

    public void boundFromPole() {
        orientation = orientation.rotate(4);
    }

    @Override
    public String toString() {
        return String.valueOf(energy);
    }

    @Override
    public int compareTo(Animal o) {
        if (this.energy == o.energy) {
            if (this.daysSurvived == o.daysSurvived) {
                if (this.children.size() == o.children.size()) {
                    return o.uuid.compareTo(uuid);
                } else return o.children.size() - this.children.size();
            } else return o.daysSurvived - this.daysSurvived;
        } else return o.energy - this.energy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return uuid.compareTo(((Animal) o).uuid) == 0;
    }

    public int getDescendantsCount() {
        int counter = 0;
        for (Animal child : children) {
            counter++;
            counter += child.getChildrenCount();
        }

        return counter;
    }
}
