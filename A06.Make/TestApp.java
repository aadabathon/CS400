import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simple junit tests for the Make activity's AppMain program.
 */
public class TestApp {

    /**
     * Ensures that a planet's name is not Pluto.
     */
    @Test
    public void appPlanetNotPluto() {
	String planet = new AppMain().getPlanet();
	    assertTrue(!planet.equals("Pluto"), "Found a planet named Pluto.");
    }

    /**
     * Ensures that at least one out of 1000 planets is named Mercury.
     */
    @Test
    public void appPlanetIsMercury() {
	boolean foundMercury = false;
        for (int i = 0; i < 1000; i++)
            foundMercury |=  new AppMain().getPlanet().equals("Mercury");
        assertTrue(foundMercury, "Out of 1000 planets, none named Mercury");
    }
    
}
