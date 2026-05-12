import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
* This class extends the RBTreeIterable class to run submission checks on it.
*/
public class P106SubmissionChecker extends RBTreeIterable<Integer> {

       @Test
       public void testNonNullIterator() {
           RBTreeIterable<Integer> tree = new RBTreeIterable<>();
           Assertions.assertFalse(tree.iterator() == null, "iterator method should not return null");
       }

       @Test
       public void simpleIterator() {
           RBTreeIterable<Integer> tree = new RBTreeIterable<>();
           tree.insert(5);
           Assertions.assertEquals(5, tree.iterator().next());
       }

}
