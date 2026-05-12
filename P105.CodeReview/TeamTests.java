import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class TeamTests {
	private static class FakeTree implements IterableSortedCollection<Song> { //we make this nice dummy static nested class to perform our tests with, while the underlying structure doesn't follow a BST it will work for testing purposes.

		private final List<Song> data = new ArrayList<>();
		private Comparable<Song> min = null; 
		private Comparable<Song> max = null; 
		
		@Override public boolean contains(Comparable<Song> input) {return false;}
		@Override public int size() {return data.size();}
    		@Override public void clear() { // Because we implement the abstract interface we must override abstract methods, even if we dont use them.
      			data.clear();
      			min = max = null;
    		}
    		@Override public boolean isEmpty(){return data.isEmpty();}


		@Override public void insert(Song s) { data.add(s); }

		@Override public void setIteratorMin(Comparable<Song> min) { this.min = min; }

		@Override public void setIteratorMax(Comparable<Song> max) { this.max = max; }

		@Override
		public Iterator<Song> iterator() { // Override iterator to build a "view" that repsects our min and max range in an inclusive manner.

			List<Song> copy = new ArrayList<>(data);
			Collections.sort(copy);
	
			List<Song> view = new ArrayList<>();

			for (Song s : copy) {
				boolean okMin = (min == null) || (min.compareTo(s) <= 0);
				boolean okMax = (max == null) || (max.compareTo(s) >= 0);
				if (okMin && okMax) {view.add(s);}
			}
		return view.iterator();
	}
}

  BackendInterface backend; // Backend and FakeTree to implement with, both of these are instantiated within the setup method below.
  FakeTree tree;

  @BeforeEach 
  void setup() { 
    tree = new FakeTree();
    backend = new Backend(tree);
  }

  private Path writeCsv(String s) throws IOException { //Use a temporary file to store our mock data set (so the readData() method gets an actual file) and return its path.
    Path p = Files.createTempFile("songs", ".csv");
    Files.writeString(p, s);
    return p;
  }

  private String csv() { // Mock data set to run our tests on.
    return String.join("\n",
      "title,artist,top genre,year,bpm,nrgy,dnce,dB,live",
      "S1,A1,G1,2015,100,50,60,70,10,",
      "S2,A2,G2,2019,110,80,55,65,20,",
      "S3,A3,G3,2020,95,60,50,60,30,",
      "S4,A4,G4,2017,130,90,45,55,40,",
      "S5,A5,G5,2021,115,70,65,75,50,",
      "S6,A6,G6,2010,105,85,40,50,60,"
    ) + "\n";
  }

  /**
   * teamTest1 tests to see if two constraints placed upon the return list by getRange and SetFilter methods
   * are reflected in the assigned list "got"
   */

  @Test
  public void teamTest1() throws IOException {
    Path csv = writeCsv(csv());
    backend.readData(csv.toString());
    backend.applyAndSetFilter(2018);
    List<String> got = backend.getAndSetRange(95, 115);
    assertEquals(List.of("S3","S2","S5"), got);
  }

    /**
   * teamTest2 tests applyAndSetFilter to see if it respects the most recent BPM range set by getAndSetRange,
   * and if passing null clears the year filter and returns all in-range titles.
   */

  @Test
  public void teamTest2() throws IOException {
    Path csv = writeCsv(csv());
    backend.readData(csv.toString());
    backend.getAndSetRange(95, 115);
    List<String> filtered = backend.applyAndSetFilter(2018);
    assertEquals(List.of("S3","S2","S5"), filtered);
    List<String> cleared = backend.applyAndSetFilter(null);
    assertEquals(List.of("S3","S1","S6","S2","S5"), cleared);
  }


   /**
   * teamTest3 tests fiveMost to see if it honors the most recent BPM range,
   * Ranks by energy in descending order,
   * And excludes out of range energy songs.
   */

  @Test
  public void teamTest3() throws IOException {
    Path csv = writeCsv(csv());
    backend.readData(csv.toString());
    backend.getAndSetRange(95, 110);
    backend.applyAndSetFilter(null);
    List<String> top = backend.fiveMost();
    assertEquals(Set.of("S6","S2","S3","S1"), Set.copyOf(top));
    assertFalse(top.contains("S4"));
  }
}
