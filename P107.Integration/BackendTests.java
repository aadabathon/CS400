import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BackendTests {

  @Test
  void backendTest1() {
    Tree_Placeholder tree = new Tree_Placeholder();
    Backend back = new Backend(tree);
    Compare comp = new Compare();
    Song last = new Song("Kills You Slowly", "The Chainsmokers", "electropop", 2019, 150, 44, 70,
        -9, 13, comp);

    // Test readData

    try {
      back.readData("/home/schnell/P103.RoleCode/songs.csv");

      Assertions.assertTrue(tree.contains(last));

    } catch (IOException e) {
      Assertions.fail("File Path did not work");
    }

  }

  @Test
  void backendTest2() {

    // Test min and max filters and year filters

    Tree_Placeholder tree = new Tree_Placeholder();
    Backend back = new Backend(tree);

    List<String> songs = back.getAndSetRange(null, null);

    Assertions.assertTrue(songs.get(0).equals("A L I E N S"));
    Assertions.assertTrue(songs.get(1).equals("BO$$"));
    Assertions.assertTrue(songs.get(2).equals("Cake By The Ocean"));

    songs = back.applyAndSetFilter(null);

    Assertions.assertTrue(songs.get(0).equals("A L I E N S"));
    Assertions.assertTrue(songs.get(1).equals("BO$$"));
    Assertions.assertTrue(songs.get(2).equals("Cake By The Ocean"));

    songs = back.applyAndSetFilter(2015);

    Assertions.assertTrue(songs.get(0).equals("A L I E N S"));
    Assertions.assertTrue(songs.get(1).equals("Cake By The Ocean"));

    back.applyAndSetFilter(null);

    songs = back.getAndSetRange(110, null);

    Assertions.assertTrue(songs.get(0).equals("A L I E N S"));
    Assertions.assertTrue(songs.get(1).equals("Cake By The Ocean"));

    songs = back.getAndSetRange(null, 119);

    Assertions.assertTrue(songs.get(0).equals("BO$$"), "Actual is " + songs.get(0));
    Assertions.assertTrue(songs.get(1).equals("Cake By The Ocean"));

    songs = back.getAndSetRange(110, 120);

    Assertions.assertTrue(songs.get(0).equals("Cake By The Ocean"));

    songs = back.applyAndSetFilter(5000);

    Assertions.assertTrue(songs.isEmpty());
  }

  @Test
  void backendTest3() {

    // Test five most

    Tree_Placeholder tree = new Tree_Placeholder();
    Backend back = new Backend(tree);
    String eff = "F";
    Song f = new Song(eff, "Christopher Larkin", "Soundtrack", 2000, 119, 1, 77, -5, 4);

    tree.insert(f);

    List<String> five = back.fiveMost();

    Assertions.assertTrue(five.size() == 5);
    Assertions.assertFalse(five.contains(eff));

  }

}

