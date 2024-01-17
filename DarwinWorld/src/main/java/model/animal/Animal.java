package model.animal;

import simulation.Simulation;
import simulation.SimMap;
import model.utilities.Orientation;
import model.utilities.Position;

import java.util.*;

public class Animal implements Comparable<Animal> {

    private final Simulation sim;
    private final UUID uuid = UUID.randomUUID();
    private Position position;
    private Orientation orientation;
    private final Genome genome;
    private int energy;
    private final List<Animal> children = new ArrayList<>();
    private int daysSurvived = 0;
    private int plantsEaten = 0;
    private boolean isDead = false;

    public Animal(Simulation simulation, int startEnergy) {
        this.sim = simulation;
        this.position = new Position(sim.random().nextInt(sim.settings().width()), sim.random().nextInt(sim.settings().height()));
        this.orientation = Orientation.getOrientationFromNumber(sim.random().nextInt(8));
        this.energy = startEnergy;
        this.genome = sim.genomeFactory().createGenome();
    }

    public Animal(Simulation simulation, int startEnergy, Position position, Genome genome) {
        this.sim = simulation;
        this.position = position;
        this.orientation = Orientation.getOrientationFromNumber(sim.random().nextInt(8));
        this.energy = startEnergy;
        this.genome = genome;
    }

    public Position getPosition() {
        return position;
    }

    public int getEnergy() {
        return this.energy;
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

    public boolean isDead() {
        return isDead;
    }

    public void setDead() {
        isDead = true;
    }

    public void consumePlant() {
        energy += sim.settings().energyFromPlant();
        plantsEaten++;
    }

    public void processCopulation(Animal child) {
        energy -= sim.settings().energyLossToCopulate();
        children.add(child);
    }

    public void move(SimMap validator) {
        orientation = orientation.rotate(genome.getNext());
        Position tempPos = position.add(orientation.toVector());
        if (validator.canMoveTo(this, tempPos)) position = tempPos;
        energy--; daysSurvived++;
    }

    public void moveToOtherSide(Position tempPos) {
        if (tempPos.x() < 0) position = new Position(sim.settings().width()-1, tempPos.y());
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
                    return o.uuid.compareTo(this.uuid);
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

    // Nie do końca poprawna, ale zdecydowanie szybsza
    public int getDescendantsCount() {
        int counter = 0;
        for (Animal child : children) {
            counter++;
            counter += child.getChildrenCount();
        }
        return counter;
    }

    // Poprawna, ale niesamowicie obciążająca dla programu i nie bardzo idzie ją zoptymalizować :/
    public int oldGetDescendantsCount() {
        HashSet<Animal> descendantsSet = new HashSet<>(children.size());
        for (Animal child : children) child.putYourselfAndChildrenToSet(descendantsSet);
        return descendantsSet.size();
    }

    private void putYourselfAndChildrenToSet(Set<Animal> set) {
        set.add(this);
        if (!children.isEmpty()) for (Animal child : children) child.putYourselfAndChildrenToSet(set);
    }
}
