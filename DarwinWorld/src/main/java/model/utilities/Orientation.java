package model.utilities;

public enum Orientation {
    N(0, new Position(0,-1)),
    NE(1, new Position(1,-1)),
    E(2, new Position(1,0)),
    SE(3, new Position(1,1)),
    S(4, new Position(0,1)),
    SW(5, new Position(-1,1)),
    W(6, new Position(-1,0)),
    NW(7, new Position(-1,-1));

    private final int value;
    private final Position vector;

    Orientation(int value, Position vector) {
        this.value = value;
        this.vector = vector;
    }

    public Position toVector() {
        return this.vector;
    }

    private static final Orientation[] convertToOrientation = values();

    public static Orientation getOrientationFromNumber(int n) {
        return convertToOrientation[n];
    }

    public Orientation rotate(int gen) {
        return getOrientationFromNumber((this.value + gen) % 8);
    }
}
