import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Arrays;

public class FrontendTests {

  private Frontend makeFrontend() {
    Graph_Placeholder g = new Graph_Placeholder();
    Backend_Placeholder b = new Backend_Placeholder(g);
    return new Frontend(b);
  }

  @Test
  public void roleTest1() {
    Frontend fe = makeFrontend();
    String html = fe.generateShortestPathPromptHTML();
    assertTrue(html.contains("id=\"start\""));
    assertTrue(html.contains("id=\"end\""));
    assertTrue(html.toLowerCase().contains("find shortest path"));
  }

  @Test
  public void roleTest2() {
    Frontend fe = makeFrontend();

    String html = fe.generateShortestPathResponseHTML(
        "Union South", "Weeks Hall for Geological Sciences");

    assertTrue(html.contains("<ol>"));
    assertTrue(html.contains("Union South"));
    assertTrue(html.contains("Weeks Hall for Geological Sciences"));

    assertTrue(html.contains("6 seconds"));
  }

  @Test
  public void roleTest3() {
    Frontend fe = makeFrontend();

    String html = fe.generateClosestDestinationsFromAllResponseHTML(
        "Union South, Computer Sciences and Statistics");

    assertTrue(html.contains("<ul>"));
    assertTrue(html.contains("Union South"));
    assertTrue(html.contains("Computer Sciences and Statistics"));
    assertTrue(html.contains("Closest common destination"));
    assertTrue(html.contains("Weeks Hall for Geological Sciences"));

    assertTrue(html.contains("9 seconds"));
  }
}
