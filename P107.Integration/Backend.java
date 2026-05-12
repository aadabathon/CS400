import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Backend implements BackendInterface {

  private IterableSortedCollection<Song> tree;
  private Integer yearThreshold = null;
  private Compare comp = new Compare();

  public Backend(IterableSortedCollection<Song> tree) {
    this.tree = tree;
  }

  /**
   * Loads data from the .csv file referenced by filename. You can rely on the exact headers found
   * in the provided songs.csv, but you should not rely on them always being presented in this order
   * or on there not being additional columns describing other song qualities. After reading songs
   * from the file, the songs are inserted into the tree passed to this backend' constructor. Don't
   * forget to create a Comparator to pass to the constructor for each Song object that you create.
   * This will be used to store these songs in order within your tree, and to retrieve them by speed
   * range in the getRange method.
   * 
   * @param filename is the name of the csv file to load data from
   * @throws IOException when there is trouble finding/reading file
   */
  @Override
  public void readData(String filename) throws IOException {
    BufferedReader read = null;
    String[] headings = null;
    String line = null;
    String[] parsedLine = null;

    try {

      read = new BufferedReader(new FileReader(filename));
      // read the line of headings
      line = read.readLine();
      if (line != null)
        // set aside the headings of the columns
        headings = line.split(",");
      // read the first line of data
      line = read.readLine();

      // repeat until all data has been read
      while (line != null) {
        parsedLine = parseCSVLine(line, headings);

        // parsedLine should return an array of Strings in the same order as defined in the Song
        // constructor
        tree.insert(new Song(parsedLine[0], parsedLine[1], parsedLine[2],
            Integer.parseInt(parsedLine[3]), Integer.parseInt(parsedLine[4]),
            Integer.parseInt(parsedLine[5]), Integer.parseInt(parsedLine[6]),
            Integer.parseInt(parsedLine[7]), Integer.parseInt(parsedLine[8]), comp));

        // read the next line of data
        line = read.readLine();
      }

    } catch (IOException excpt) {
      throw new IOException(excpt.getMessage());
    } finally {
      read.close();
    }
  }

  /**
   * Using the headings of a particular .csv file, splits up a line of data, and then reorganizes it
   * so that the String[] that is returned has the data in the same order as defined by the Song
   * constructor
   * 
   * @param line     a String representing a line of data from a .csv file that is to be parsed
   * @param headings a String[] representing the headings of each column in the .csv file
   * @return a String[] representing the data of the given line, with the data in the same order as
   *         defined by the Song constructor. The data returned only represents the categories in
   *         the Song constructor. Other extraneous categories are ignored
   */
  private String[] parseCSVLine(String line, String[] headings) {
    String[] parsedLine = new String[9];
    String[] splitLine = null;
    int[] indexOfColumns = new int[9];

    /*
     * Somewhat proud of this one :)
     * 
     * indexOfColumns stores which index of headings corresponds to which category in the order
     * determined by the Song constructor.
     * 
     * indexOfColumns[0] holds the index of the title category within headings, indexOfColumns[6]
     * holds the index of the danceability category within headings, etc.
     * 
     * So, once I have line split up into the same categories as headings, I can put the song title
     * into parsedLine[0] by looking at the value of splitLine[indexOfColumns[0]].
     * 
     * For example, if the 'title' category is in column 5, then indexOfColumns[0] == 5, and so
     * splitLine[indexOfColumns[0]] == the title of the song, which can then be placed into
     * parsedLine[0]. This can be repeated so that parsedLine ends up with all of the parameters
     * required for the Song constructor in same order that they are listed in the constructor.
     */

    indexOfColumns[0] = indexOf(headings, "title");
    indexOfColumns[1] = indexOf(headings, "artist");
    indexOfColumns[2] = indexOf(headings, "top genre");
    indexOfColumns[3] = indexOf(headings, "year");
    indexOfColumns[4] = indexOf(headings, "bpm");
    indexOfColumns[5] = indexOf(headings, "nrgy");
    indexOfColumns[6] = indexOf(headings, "dnce");
    indexOfColumns[7] = indexOf(headings, "dB");
    indexOfColumns[8] = indexOf(headings, "live");

    // splits up line into its categories (not sorted according to order as defined in the Song
    // constructor)
    splitLine = splitCSV(line);

    // slot in the 9 different category data in the order as defined in the Song constructor
    for (int i = 0; i < indexOfColumns.length; i++) {
      parsedLine[i] = splitLine[indexOfColumns[i]];
    }

    return parsedLine;
  }

  /**
   * Finds the index of the target within the given array
   * 
   * @param arr    a String[] that is being searched through
   * @param target a String that is what is being searched for in arr
   * @return an int representing the index of target in arr, or -1 if target is not found
   */
  private int indexOf(String[] arr, String target) {

    // iterate through the given array, returning the index of the target if found
    for (int i = 0; i < arr.length; i++) {
      if (arr[i].equals(target)) {
        return i;
      }
    }

    // return -1 if target is not found
    return -1;
  }

  /**
   * Splits a line of data from a .csv file by commas, ignoring commas that are in quotation marks.
   * Done by breaking the given line into a char[] and iterating through the array, adding chars
   * together to reconstruct the data while ignoring single quotation marks and starting a new word
   * at commas that are not in quotes
   * 
   * @param line a line of data from a .csv file
   * @return A String[] of the data that has been split up
   */
  private String[] splitCSV(String line) {
    int quotes = 0;
    ArrayList<String> data = new ArrayList<String>();
    String built = "";
    boolean lastWasQuote = false;

    for (char c : line.toCharArray()) {
      if (c == '"') {
        quotes++;
        // checks if we have double (or more) quotes
        if (lastWasQuote) {
          // if so, add the most recent quotes
          built += c;
        }
        lastWasQuote = true;
      } else if (quotes % 2 == 0 && c == ',') {
        // since quotes are even and we have reached a comma, this is the end of a category, and
        // so we add our word to data and clear our values
        lastWasQuote = false;
        data.add(built);
        built = "";
      } else {
        // since we either have an odd number of quotes or the character is not a quote or comma,
        // add the character to our built String
        lastWasQuote = false;
        built += c;
      }
    }

    String[] arr = new String[data.size()];

    // move the data from an ArrayList to an array
    for (int i = 0; i < data.size(); i++) {
      arr[i] = data.get(i);
    }

    return arr;
  }

  /**
   * Retrieves a list of song titles from the tree passed to the contructor. The songs should be
   * ordered by the songs' speed, and fall within the specified range of speed values. This speed
   * range will also be used by future calls to filterSongs and getFiveMost.
   *
   * If a year filter has been set using the filterSongs method below, then only songs that pass
   * that filter should be included in the list of titles returned by this method.
   *
   * When null is passed as either the low or high argument to this method, that end of the range is
   * understood to be unbounded. For example, a null argument for the hight parameter means that
   * there is no maximum speed to include in the returned list.
   *
   * @param low  is the minimum speed of songs in the returned list
   * @param high is the maximum speed of songs in the returned list
   * @return List of titles for all songs from low to high that pass any set filter, or an empty
   *         list when no such songs can be found
   */
  @Override
  public List<String> getAndSetRange(Integer low, Integer high) {
    ArrayList<String> songs = new ArrayList<String>();

    // set IteratorMin to a Song that just holds the low value
    // if low == null, 'clears' the low filter
    if (low == null) {
      tree.setIteratorMin(null);
    } else {
      Song min = new Song("a", "a", "a", 0, low - 1, 0, 0, 0, 0, comp);
      tree.setIteratorMin(min);
    }

    // set IteratorMax to a Song that just holds the high value
    // if high == null, 'clears' the high filter
    if (high == null) {
      tree.setIteratorMax(null);
    } else {
      Song max = new Song("a", "a", "a", 0, high + 1, 0, 0, 0, 0, comp);
      tree.setIteratorMax(max);
    }

    // Add all song titles that fit the min speed, max speed, and yearThreshold to songs
    // min and max are accounted for via IterableSortedCollection
    for (Song s : tree) {
      // check that there is a year filter
      if (yearThreshold != null) {
        if (s.getYear() > yearThreshold) {
          songs.add(s.getTitle());
        }
      } else {
        songs.add(s.getTitle());
      }
    }

    return songs;
  }

  /**
   * Retrieves a list of song titles that have a year that is larger than the specified threshold.
   * Similar to the getRange method: this list of song titles should be ordered by the songs' speed,
   * and should only include songs that fall within the specified range of speed values that was
   * established by the most recent call to getRange. If getRange has not previously been called,
   * then no low or high speed bound should be used. The filter set by this method will be used by
   * future calls to the getRange and fiveMost methods.
   *
   * When null is passed as the threshold to this method, then no year threshold should be used.
   * This clears the filter.
   *
   * @param threshold filters returned song titles to only include songs that have a year that is
   *                  larger than this threshold.
   * @return List of titles for songs that meet this filter requirement and are within any
   *         previously set speed range, or an empty list when no such songs can be found
   */
  @Override
  public List<String> applyAndSetFilter(Integer threshold) {
    ArrayList<String> songs = new ArrayList<String>();

    // if threshold == null, this 'clears' the filter
    yearThreshold = threshold;

    // Add all song titles that fit the min speed, max speed, and yearThreshold to songs
    // min and max are accounted for via IterableSortedCollection
    for (Song s : tree) {
      // check that there is a year filter
      if (yearThreshold != null) {
        if (s.getYear() > yearThreshold) {
          songs.add(s.getTitle());
        }
      } else {
        songs.add(s.getTitle());
      }
    }

    return songs;
  }

  /**
   * This method returns a list of song titles representing the five most energetic songs that both
   * fall within any attribute range specified by the most recent call to getRange, and conform to
   * any filter set by the most recent call to filteredSongs. The order of the song titles in this
   * returned list is up to you.
   *
   * If fewer than five such songs exist, return all of them. And return an empty list when there
   * are no such songs.
   *
   * @return List of five most energetic song titles
   */
  @Override
  public List<String> fiveMost() {
    ArrayList<Song> songs = new ArrayList<Song>();

    // Add all songs that fit the min speed, max speed, and yearThreshold to songs
    // min and max are accounted for via IterableSortedCollection
    for (Song s : tree) {
      // check that there is a year filter
      if (yearThreshold != null) {
        if (s.getYear() > yearThreshold) {
          songs.add(s);
        }
      } else {
        songs.add(s);
      }
    }

    Song minNRG = songs.get(0);

    // cull the songs to a list of 5 by removing the smallest until only 5 remain - skips if less
    // than 5 songs
    while (songs.size() > 5) {
      for (Song s : songs) {
        if (s.getEnergy() < minNRG.getEnergy()) {
          minNRG = s;
        }
      }
      songs.remove(minNRG);
    }

    ArrayList<String> songNames = new ArrayList<String>();

    // pull the names of the songs for returning
    for (Song s : songs) {
      songNames.add(s.getTitle());
    }

    return songNames;
  }

}

