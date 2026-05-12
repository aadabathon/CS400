import java.util.Comparator;

public class Compare implements Comparator<Song> {

  @Override
  public int compare(Song o1, Song o2) {
    return o1.getBPM() - o2.getBPM();
  }

}

