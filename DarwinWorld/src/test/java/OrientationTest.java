import model.utilities.Orientation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrientationTest {

    @Test
    public void checkRotation() {
        assertEquals(Orientation.NW.rotate(3), Orientation.E);
        assertEquals(Orientation.NW.rotate(4), Orientation.SE);
        assertEquals(Orientation.N.rotate(5), Orientation.SW);
        assertEquals(Orientation.NE.rotate(8), Orientation.NE);
        assertEquals(Orientation.NW.rotate(3), Orientation.getOrientationFromNumber(2));
    }

    @Test
    public void checkGetOrientationFromNumber() {
        assertEquals(Orientation.N, Orientation.getOrientationFromNumber(0));
        assertEquals(Orientation.NE, Orientation.getOrientationFromNumber(1));
        assertEquals(Orientation.E, Orientation.getOrientationFromNumber(2));
        assertEquals(Orientation.SE, Orientation.getOrientationFromNumber(3));
        assertEquals(Orientation.SW, Orientation.getOrientationFromNumber(5));
    }
}
