import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * TeamTests
 *
 * Unit tests for Backend using a minimal in-memory implementation of
 * IterableSortedCollection<Song>. The FakeTree lets us exercise Backend
 * behavior (range bounds, year filter, energy ranking) without needing
 * a full tree data structure.
 */
public class TeamTests {

  /**
   * FakeTree
   *
   * A tiny test double for IterableSortedCollection<Song>.
   * - Stores songs in a List.
   * - When iterated, returns them in natural order (Collections.sort),
   *   then filters to the [min, max] window (inclusive) based on
   *   the Comparable<Song> sentinels previously set via setIteratorMin/Max.
   *
   * NOTE: Because we call Collections.sort(copy), this relies on Song's
   * natural ordering (Song.compareTo). If your real backend/tree orders by BPM,
   * make sure Song.compareTo uses BPM (or switch this to an explicit comparator).
   */
  private static class FakeTree implements IterableSortedCollection<Song> {

    // All inserted songs live here
    private final List<Song> data = new ArrayList<>();

    // Iterator bounds; null means "unbounded" on that side
    private Comparable<Song> min = null;
    private Comparable<Song> max = null;

    /** Insert a song into the collection. */
    @Override public void insert(Song s) { data.add(s); }

    /** Set the lower bound sentinel for iteration (null clears). */
    @Override public void setIteratorMin(Comparable<Song> min) { this.min = min; }

    /** Set the upper bound sentinel for iteration (null clears). */
    @Override public void setIteratorMax(Comparable<Song> max) { this.max = max; }

    /**
     * Iteration contract:
     * 1) copy → sort by natural order (Song.compareTo)
     * 2) include only elements s where (s >= min) and (s <= max), if set
     * 3) return an iterator over that filtered view
     */
    @Override
    public Iterator<Song> iterator() {
      // Defensive copy so tests don't mutate internal storage
      List<Song> copy = new ArrayList<>(data);
      // Natural-order sort (depends on Song.compareTo)
      Collections.sort(copy);

      // Build a view respecting [min, max] inclusively
      List<Song> view = new ArrayList<>();
      for (Song s : copy) {
        boolean okMin = (min == null) || (min.compareTo(s) <= 0); // s >= min
        boolean okMax = (max == null) || (max.compareTo(s) >= 0); // s <= max
        if (okMin && okMax) view.add(s);
      }
      return view.iterator();
    }
  }

  // System under test (SUT) and its fake dependency
  Backend backend;
  FakeTree tree;

  /**
   * Fresh backend + tree for each test to keep tests isolated and deterministic.
   */
  @BeforeEach
  void setup() {
    tree = new FakeTree();
    backend = new Backend(tree);
  }

  /**
   * Utility: write a CSV string to a temp file and return its Path.
   * Backend.readData(...) expects a filename, so tests create a real file.
   */
  private Path writeCsv(String s) throws IOException {
    Path p = Files.createTempFile("songs", ".csv");
    Files.writeString(p, s);
    return p;
  }

  /**
   * Small CSV fixture used by all tests.
   * Chosen values cover:
   * - inside/outside BPM ranges,
   * - years above/below filter threshold,
   * - different energies for fiveMost ordering.
   */
  private String csv() {
    return String.join("\n",
      "Title,Artist,Genre,Year,BPM,Energy,Dance,Loud,Live",
      "S1,A1,G1,2015,100,50,60,70,10",
      "S2,A2,G2,2019,110,80,55,65,20",
      "S3,A3,G3,2020,95,60,50,60,30",
      "S4,A4,G4,2017,130,90,45,55,40",
      "S5,A5,G5,2021,115,70,65,75,50",
      "S6,A6,G6,2010,105,85,40,50,60"
    ) + "\n";
  }

  /**
   * teamTest1
   * Verify that: readData + applyAndSetFilter(year > 2018) + getAndSetRange([95,115])
   * returns titles that satisfy BOTH constraints, ordered by speed (BPM).
   * Expected BPM order: 95(S3), 110(S2), 115(S5).
   */
  @Test
  void teamTest1() throws IOException {
    Path csv = writeCsv(csv());
    backend.readData(csv.toString());
    backend.applyAndSetFilter(2018);
    List<String> got = backend.getAndSetRange(95, 115);
    assertEquals(List.of("S3","S2","S5"), got);
  }

  /**
   * teamTest2
   * Verify that:
   * 1) applyAndSetFilter respects the most recent BPM range set by getAndSetRange.
   * 2) passing null clears the year filter and returns all in-range titles.
   *
   * With range [95,115]:
   *  - Year > 2018 → S3(95), S2(110), S5(115)
   *  - Cleared filter → all in-range ordered: S3, S1, S6, S2, S5
   */
  @Test
  void teamTest2() throws IOException {
    Path csv = writeCsv(csv());
    backend.readData(csv.toString());
    backend.getAndSetRange(95, 115);
    List<String> filtered = backend.applyAndSetFilter(2018);
    assertEquals(List.of("S3","S2","S5"), filtered);
    List<String> cleared = backend.applyAndSetFilter(null);
    assertEquals(List.of("S3","S1","S6","S2","S5"), cleared);
  }

  /**
   * teamTest3
   * Verify that fiveMost:
   * - honors the most recent BPM range,
   * - ranks by energy descending,
   * - excludes out-of-range high-energy songs.
   *
   * Range [95,110] → candidates: S3(60), S1(50), S6(85), S2(80).
   * Sorted by energy desc → S6, S2, S3, S1. S4(130 BPM) must be excluded.
   */
  @Test
  void teamTest3() throws IOException {
    Path csv = writeCsv(csv());
    backend.readData(csv.toString());
    backend.getAndSetRange(95, 110);
    backend.applyAndSetFilter(null);
    List<String> top = backend.fiveMost();
    assertEquals(List.of("S6","S2","S3","S1"), top);
    assertFalse(top.contains("S4"));
  }
}
