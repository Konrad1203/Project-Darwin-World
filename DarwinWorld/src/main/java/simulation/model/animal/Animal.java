package simulation.model.animal;

import simulation.model.SimSettings;
import simulation.model.SimulationMap;
import simulation.model.util.Orientation;
import simulation.model.util.Position;

import java.util.*;

public class Animal implements Comparable<Animal> {

    private Position position;
    private Orientation orientation;
    private final Genome genome;
    private int energy;
    private final List<Animal> children = new ArrayList<>();
    private int daysSurvived = 0;
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

    public void consumePlant() {
        energy += settings.energyFromPlant();
    }

    public void move(SimulationMap validator) {
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
        orientation = orientation.reflection();
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
}
