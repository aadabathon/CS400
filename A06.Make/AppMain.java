/**
 * Simple Application that prints out "Hello Mercury" when run.
 */
public class AppMain {

    /**
     * Entry point for this application.
     */
    public static void main(String[] args) {
	    System.out.println("Hello " + new AppMain().getPlanet());
    }

    /**
     * Returns the name of a planet.
     * returns "Mercury"
     */
    public String getPlanet() {
	    return "Mercury";
    }

}
