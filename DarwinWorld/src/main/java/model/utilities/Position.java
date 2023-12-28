package model.utilities;

public record Position(int x, int y) {

    public Position add(Position vector) {
        return new Position(this.x + vector.x, this.y + vector.y);
    }


    @Override
    public String toString() {
        return "(%d,%d)".formatted(x, y);
    }
}
