import model.utilities.Orientation;
import model.utilities.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionTest {

    @Test
    public void checkAdd() {
        Position p1 = new Position(1,5);
        Position p2 = new Position(-5,12);

        Position result = new Position(-4,17);

        assertEquals(result, p1.add(p2));
        assertEquals(result, p2.add(p1));
    }

    @Test
    public void movingTest() {
        Position p = new Position(3,3);
        Orientation o = Orientation.E;
        assertEquals(new Position(4,3), p.add(o.toVector()));

        p = new Position(0,0);
        o = Orientation.NW;
        assertEquals(new Position(-1,-1), p.add(o.toVector()));

        p = new Position(-1,1);
        o = Orientation.SE;
        assertEquals(new Position(0,2), p.add(o.toVector()));
    }
}
