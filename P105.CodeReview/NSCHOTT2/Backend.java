import javax.swing.*;
import java.util.*;
import java.io.IOException;
import java.util.List;
import java.io.FileReader;
import java.io.BufferedReader;


/**
 * This Backend class reading song data from a .csv file
 *
 * These song objects are stored in an iterable tree and are filtered through
 */
public class Backend implements BackendInterface {
protected IterableSortedCollection<Song> tree;
 private   int  titleIndex;
   private int artistIndex;
  private  int genresIndex;
  private  int yearIndex;
  private  int bpmIndex;
  private  int energyIndex;
  private  int danceabilityIndex;
  private  int loudnessIndex;
  private  int livenessIndex;
  private Integer lowRange = null;
  private Integer highRange = null;
private Integer yearThreshold = null;

    /**
     * Constructor takes in a tree data for songs to be stored
     * @param tree the IterableSortedCollection where songs will be inserted.
     */
    public Backend(IterableSortedCollection<Song> tree)
    {
        this.tree = tree;
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
    public void readData(String filename) throws IOException
    {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            // Get the Indexes of each of the fields
            String firstLine = reader.readLine();
            String[] theFields = firstLine.split(",");
            determineIndex(theFields);


                    String line;


                    while((line = reader.readLine()) != null)
                    {
int quotes = 0;
int start = 0;
ArrayList<String> songList = new ArrayList<>();

// Adds all of the song elements to an arrayList
              for(int i = 0;i < line.length(); i++) {
                  if (line.charAt(i) == '\"') {
                      quotes++;
                  }
                  if (line.charAt(i) == ',') {
                      if (quotes % 2 == 0) {
                          songList.add(line.substring(start, i));
                          start = i + 1;
                      }

                  }


              }
              line = line.substring(start);




                        // fix the quotes
              ArrayList<String> fixed = fixQuotes(songList);





              // Create Song
                        Song newSong = createSong(fixed);

                        //insert song into tree
                        tree.insert(newSong);

                    }
      reader.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }


    }


    /**
     * Helper method to creates a Song object fields
     *
     * @param order song fields
     * @return Song object
     */
    public Song createSong(ArrayList<String> order) {
        Comparator<Song> speedComparator =  new Comparator<Song>() {

            @Override
            public int compare(Song song1, Song song2) {

return Integer.compare(song1.getBPM(),song2.getBPM());
            }
        };


             return new Song(
                     order.get(artistIndex),
                     order.get(titleIndex),
                     order.get(genresIndex),
                     Integer.parseInt(order.get(yearIndex)),
                     Integer.parseInt(order.get(bpmIndex)),
                     Integer.parseInt(order.get(energyIndex)),
                     Integer.parseInt(order.get(danceabilityIndex)),
                     Integer.parseInt(order.get(loudnessIndex)),
                     Integer.parseInt(order.get(livenessIndex)),
                        speedComparator
             );

    }



    /**
     * Helper method to fix the single and double quotes in the class
     *
     * @param list list of lines in the file
     * @return arraylist of the fixed lines
     */
    public ArrayList<String> fixQuotes(ArrayList<String> list)
    {
        ArrayList<String> fixed = new ArrayList<>();

        for(int i = 0; i < list.size();i++)
        {
String field = list.get(i);
if(list.get(i).startsWith("\"") && list.get(i).endsWith("\""))
{
field = list.get(i).substring(1,list.get(i).length()-1);
}

field = field.replace("\"\"", "\"");
fixed.add(field);
        }
        return fixed;
    }



    /**
     * Helper method to determine the column indexes
     *
     * @param fields the column headers
     */
    public void determineIndex(String [] fields)
    {
        for (int i = 0 ; i < fields.length ; i ++)
        {
            String field = fields[i].trim();

            if(fields[i].equals("title"))
            {
                titleIndex = i;
            }
            else if(fields[i].equals("top genre"))
            {
                genresIndex = i;
            }
           else if(fields[i].equals("artist"))
            {
                artistIndex = i;
            }
           else if(fields[i].equals("year"))
            {
                yearIndex = i;
            }
           else if(fields[i].equals("bpm"))
            {
                bpmIndex = i;
            }
                  else if(fields[i].equals("nrgy"))
            {
                energyIndex = i;
            }
           else if(fields[i].equals("dnce"))
            {
                danceabilityIndex = i;
            }
           else if(fields[i].equals("dB"))
            {
                loudnessIndex = i;
            }
           else if(fields[i].equals("live"))
            {
                livenessIndex = i;
            }





        }


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
    public List<String> getAndSetRange(Integer low, Integer high)
    {
this.lowRange = low;
this.highRange = high;
return getFiltered();
    }

    /**
     * Helper method to filter the tree based on range and threshold
     *
     * @return filtered list
     */
    public List<String> getFiltered()
    {
        Iterator<Song> songList = tree.iterator();
        List<String> songTitles = new ArrayList<>();
        while(songList.hasNext()) {

Song current = songList.next();
            boolean rangeTest = (lowRange == null || current.getBPM() >= lowRange) && (highRange == null || current.getBPM() <= highRange);

            boolean threshTest = yearThreshold == null || current.getYear() > yearThreshold;

            if(rangeTest && threshTest)
            {
                songTitles.add(current.getTitle());
            }
        }

        return songTitles;
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
    public List<String> applyAndSetFilter(Integer threshold) {

        this.yearThreshold = threshold;
        return getFiltered();
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
    public List<String> fiveMost() {


        Iterator<Song> songList = tree.iterator();
        List<Song> songCands = new ArrayList<>();
        while(songList.hasNext()) {

            Song current = songList.next();
            boolean rangeTest = (lowRange == null || current.getBPM() >= lowRange) && (highRange == null || current.getBPM() <= highRange);

            boolean threshTest = yearThreshold == null || current.getYear() > yearThreshold;

            if(rangeTest && threshTest)
            {
                songCands.add(current);
            }
        }



        songCands.sort((s1, s2) -> Integer.compare(s2.getBPM(), s1.getBPM()));

List<String> fiveMost = new ArrayList<>();

int j = Math.min(5, songCands.size());
for(int i = 0 ; i < j ; i ++)
{
fiveMost.add(songCands.get(i).getTitle());
}

return fiveMost;

    }

}

