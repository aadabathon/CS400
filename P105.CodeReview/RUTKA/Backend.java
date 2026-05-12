import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * This class implements the backend for different types of data regarding to songs
 * @author Mason Rutka
 */
public class Backend implements BackendInterface{

  private IterableSortedCollection<Song> tree;
  private Integer rangeLow;
  private Integer rangeHigh;
  private Integer yearFilter;

  /**
   * Constructor used to initialize tree and other private fields
   * @param tree
   */
  public Backend(IterableSortedCollection<Song> tree) {
    this.tree = tree;
    this.rangeLow = null;
    this.rangeHigh = null;
    this.yearFilter = null;
  }

  /**
   * Loads data from the .csv file referenced by filename.  You can rely
   * on the exact headers found in the provided songs.csv, but you should
   * not rely on them always being presented in this order or on there
   * not being additional columns describing other song qualities.
   * After reading songs from the file, the songs are inserted into
   * the tree passed to this backend' constructor.  Don't forget to
   * create a Comparator to pass to the constructor for each Song object that
   * you create.  This will be used to store these songs in order within your
   * tree, and to retrieve them by speed range in the getRange method.
   * @param filename is the name of the csv file to load data from
   * @throws IOException when there is trouble finding/reading file
   */
  @Override
  public void readData(String filename) throws IOException {
    File file = new File(filename);

    if (!file.exists()) {
      throw new IOException("File not found: " + filename);
    }

    Scanner scnr = new Scanner(file);

    // skip the header line
    if (scnr.hasNextLine()) {
      scnr.nextLine();
    }

    while (scnr.hasNextLine()) {
      String line = scnr.nextLine();
      String[] data = parseCSVLine(line);

      if (data.length < 9) {
        continue;
      }

      try {
        // get  important data
        String title = data[0];
        String artist = data[1];
        String genre = data[2];
        int year = Integer.parseInt(data[3]);
        int bpm = Integer.parseInt(data[4]);
        int energy = Integer.parseInt(data[5]);
        int dance = Integer.parseInt(data[6]);
        int loud = Integer.parseInt(data[7]);
        int live = Integer.parseInt(data[8]);

        // create song and insert
        Song s = new Song(title, artist, genre, year, bpm, energy, dance, loud, live);
        tree.insert(s);

      } catch (NumberFormatException e) {
        System.err.println("Skip line " + line + " due to invalid number");
      }
    }

    scnr.close();
  }

  /**
   * Helper method to correctly sift through data and ensure all indexes are correct
   * @param line
   * @return list of data
   */
  private String[] parseCSVLine(String line) {
    List<String> data = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean insideQuotes = false;

    for (char c : line.toCharArray()) {
      if (c == '"') {
        // Toggle the quote state
        insideQuotes = !insideQuotes;
      } else if (c == ',' && !insideQuotes) {
        // Comma outside quotes means new data
        data.add(current.toString().trim());
        current.setLength(0); // reset for next data
      } else {
        // Regular character, append to current data
        current.append(c);
      }
    }

    // Add the last token
    data.add(current.toString().trim());

    return data.toArray(new String[0]);
  }

  /**
   * Retrieves a list of song titles from the tree passed to the contructor.
   * The songs should be ordered by the songs' speed, and fall within
   * the specified range of speed values.  This speed range will
   * also be used by future calls to filterSongs and getFiveMost.
   *
   * If a year filter has been set using the filterSongs method
   * below, then only songs that pass that filter should be included in the
   * list of titles returned by this method.
   *
   * When null is passed as either the low or high argument to this method,
   * that end of the range is understood to be unbounded.  For example, a
   * null argument for the hight parameter means that there is no maximum
   * speed to include in the returned list.
   *
   * @param low is the minimum speed of songs in the returned list
   * @param high is the maximum speed of songs in the returned list
   * @return List of titles for all songs from low to high that pass any
   *     set filter, or an empty list when no such songs can be found
   */
  @Override
  public List<String> getAndSetRange(Integer low, Integer high) {
    this.rangeLow = low;
    this.rangeHigh = high;

    List<String> titlesInRange = new ArrayList<>();

    // make the tree iterable within the range
    if (low != null) {
      Song minSong = new Song("temp", "temp", "temp", 0, low, 0, 0, 0, 0);
      tree.setIteratorMin(minSong);
    } else {
      tree.setIteratorMin(null);
    }

    if (high != null) {
      Song maxSong = new Song("temp", "temp", "temp", 0, high, 0, 0, 0, 0);
      tree.setIteratorMax(maxSong);
    } else {
      tree.setIteratorMax(null);
    }

    // iterate over the tree and collect song titles
    for (Song s : tree) {
      // check if song passes year filter
      if (this.yearFilter == null || s.getYear() == this.yearFilter) {
        titlesInRange.add(s.getTitle());
      }
    }

    return titlesInRange;
  }

  /**
   * Retrieves a list of song titles that have a year that is
   * larger than the specified threshold.  Similar to the getRange
   * method: this list of song titles should be ordered by the songs'
   * speed, and should only include songs that fall within the specified
   * range of speed values that was established by the most recent call
   * to getRange.  If getRange has not previously been called, then no low
   * or high speed bound should be used.  The filter set by this method
   * will be used by future calls to the getRange and fiveMost methods.
   *
   * When null is passed as the threshold to this method, then no
   * year threshold should be used.  This clears the filter.
   *
   * @param threshold filters returned song titles to only include songs that
   *     have a year that is larger than this threshold.
   * @return List of titles for songs that meet this filter requirement and
   *     are within any previously set speed range, or an empty list
   *     when no such songs can be found
   */
  @Override
  public List<String> applyAndSetFilter(Integer threshold) {
    this.yearFilter = threshold;

    List<String> newTitles = new ArrayList<>();

    // go through all songs in tree
    for (Song s : tree) {
      // check if song is within previous speed range
      boolean inRange = true;
      if (rangeLow != null && s.getBPM() < rangeLow) {
        inRange = false;
      }
      if (rangeHigh != null && s.getBPM() > rangeHigh) {
        inRange = false;
      }

      // check year filter
      boolean passesYear = (threshold == null || s.getYear() > threshold);

      if (inRange && passesYear) {
        newTitles.add(s.getTitle());
      }
    }

    return newTitles;
  }

  /**
   * This method returns a list of song titles representing the five
   * most energetic songs that both fall within any attribute range specified
   * by the most recent call to getRange, and conform to any filter set by
   * the most recent call to filteredSongs.  The order of the song titles
   * in this returned list is up to you.
   *
   * If fewer than five such songs exist, return all of them.  And return an
   * empty list when there are no such songs.
   *
   * @return List of five most energetic song titles
   */
  @Override
  public List<String> fiveMost() {
    List<Song> candidates = new ArrayList<>();

    // go through all songs in the tree
    for (Song s : tree) {
      // check if song passes the year filter
      if (yearFilter == null || s.getYear() > yearFilter) {
        candidates.add(s);
      }
    }

    // sort candidates by energy
    candidates.sort(new Comparator<Song>() {
      @Override
      public int compare(Song s1, Song s2) {
        return s2.getEnergy() - s1.getEnergy(); // descending order
      }
    });

    // collect up to 5 song titles
    List<String> topFive = new ArrayList<>();
    for (int i = 0; i < candidates.size() && i < 5; i++) {
      topFive.add(candidates.get(i).getTitle());
    }

    return topFive;
  }
}
