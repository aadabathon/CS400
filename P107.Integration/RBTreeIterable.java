import java.beans.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * This class extends RedBlackTree into a tree that supports iterating over the values it
 * stores in sorted, ascending order.
 */
public class RBTreeIterable<T extends Comparable<T>>
        extends RedBlackTree<T> implements IterableSortedCollection<T> {
            private Comparable<T> Min = null;
            private Comparable<T> Max = null;

    /**
     * Allows setting the start (minimum) value of the iterator. When this method is called,
     * every iterator created after it will use the minimum set by this method until this method
     * is called again to set a new minimum value.
     *
     * @param min the minimum for iterators created for this tree, or null for no minimum
     */
    @Override
    public void setIteratorMin(Comparable<T> min) {
        this.Min = min;
    }

    /**
     * Allows setting the stop (maximum) value of the iterator. When this method is called,
     * every iterator created after it will use the maximum set by this method until this method
     * is called again to set a new maximum value.
     *
     * @param max the maximum for iterators created for this tree, or null for no maximum
     */
    @Override
    public void setIteratorMax(Comparable<T> max) {
        this.Max = max;
    }

    /**
     * Returns an iterator over the values stored in this tree. The iterator uses the
     * start (minimum) value set by a previous call to setIteratorMin, and the stop (maximum)
     * value set by a previous call to setIteratorMax. If setIteratorMin has not been called
     * before, or if it was called with a null argument, the iterator uses no minimum value
     * and starts with the lowest value that exists in the tree. If setIteratorMax has not been
     * called before, or if it was called with a null argument, the iterator uses no maximum
     * value and finishes with the highest value that exists in the tree.
     */
    @Override
    public Iterator<T> iterator() {
        TreeIterator tree = new TreeIterator<>(this.root, this.Min, this.Max);
        return tree;
    }

    /**
     * Nested class for Iterator objects created for this tree and returned by the iterator method.
     * This iterator follows an in-order traversal of the tree and returns the values in sorted,
     * ascending order.
     */
    protected static class TreeIterator<R extends Comparable<R>> implements Iterator<R> {

        // stores the start point (minimum) for the iterator
        Comparable<R> min = null;
        // stores the stop point (maximum) for the iterator
        Comparable<R> max = null;
        // stores the stack that keeps track of the inorder traversal
        Stack<BinaryNode<R>> stack = null;

        /**
         * Constructor for a new iterator if the tree with root as its root node, and
         * min as the start (minimum) value (or null if no start value) and max as the
         * stop (maximum) value (or null if no stop value) of the new iterator.<br/>
         * Time complexity should be <b>O(log n)</b>
         *
         * @param root root node of the tree to traverse
         * @param min  the minimum value that the iterator will return
         * @param max  the maximum value that the iterator will return
         */
        public TreeIterator(BinaryNode<R> root, Comparable<R> min, Comparable<R> max) { //Constructor as per the instructions.
            this.min = min;
            this.max = max;
            this.stack = new Stack<>();
            updateStack(root); 
        }

        /**
         * Helper method for initializing and updating the stack. This method both<br/>
         * - finds the next data value stored in the tree (or subtree) that is between
         * start(minimum) and stop(maximum) point (including start and stop points
         * themselves), and<br/>
         * - builds up the stack of ancestor nodes that contain values between
         * start(minimum) and stop(maximum) values (including start and stop values
         * themselves) so that those nodes can be visited in the future.
         *
         * @param node the root node of the subtree to process
         */
        private void updateStack(BinaryNode<R> node) {
            if (node == null) return;

            if (this.min != null && this.min.compareTo(node.data) > 0) { //If our min is greater than our data at node, we recurse right.
                updateStack(node.right);
                return;
            }
            stack.push(node); //Otherwise we push our node to the stack and then recurse left.
            updateStack(node.left);
        }

        /**
         * Returns true if the iterator has another value to return, and false otherwise.
         */
        @Override
        public boolean hasNext() {
            if (stack.isEmpty()) return false; //if the stack is empty return false immediately
            if (max == null) return true; //If there is no upper bound, return true.
            R nextVal = stack.peek().data;
            return max.compareTo(nextVal) >= 0; // if the data in the next node on the stack is less than or equal to max, return true.

        }

        /**
         * Returns the next value of the iterator.<br/>
         * Amortized time complexity should be <b>O(1)</b><br/>
         * Worst case time complexity <b>O(log n)</b><br/>
         * <p><b>Do not</b> implement this method by linearly walking through the
         * entire tree from the smallest element until the start bound is reached.
         * That process should occur <b>only once</b> during construction of the
         * iterator object.</p>
         *
         * @throws NoSuchElementException if the iterator has no more values to return
         */
        @Override
        public R next() {
            if (!hasNext()) throw new NoSuchElementException(); // Has next enforces the requirements needs to effectively run this method.
            BinaryNode<R> curr = stack.pop(); //pop the next node on stack
            R val = curr.data; // gather data
            updateStack(curr.right); //updateStack on popped node.right
            return val; //return that nodes value, subsequent calls of next will return values in ascending order and will implement an in-order traversal of a tree within a given range.
        }
    }

    /**
     * TEST 1
     * Verifies full ascending traversal and that a tree with no duplicates
     * returns each elements only once.
     */



    @Test
    public void Test1(){
        RBTreeIterable<Integer> tree = new RBTreeIterable<>();
        int[] test_list = {5, 3, 7, 1, 4, 6, 8};
        for (int v : test_list) {
             tree.insert(v);
        }
        tree.setIteratorMin(null);
        tree.setIteratorMax(null);
        List<Integer> seen = new ArrayList<>();
        for (Integer x : tree) {
            seen.add(x);
        }
        assertIterableEquals(
            Arrays.asList(1, 3, 4, 5, 6, 7, 8),
            seen,
            "Iterator should return all values in ascending order with no bounds."
        );
    }
    /**
     * TEST 2
     * Verifies the iteration starts at the min and
     * duplicates within range are not skipped.
     */

    @Test
    public void Test2(){
       RBTreeIterable<String> tree = new RBTreeIterable<>();
        String[] test_list = {"mango", "apple", "banana", "banana", "kiwi", "pear", "peach"};
        for (String s : test_list) {
            tree.insert(s);
        }
        tree.setIteratorMin("banana");
        tree.setIteratorMax(null);
        List<String> seen = new ArrayList<>();
        for (String s : tree) {
            seen.add(s);
        }
        assertIterableEquals(
            Arrays.asList("banana", "banana", "kiwi", "mango", "peach", "pear"),
            seen,
            "Iterator with only min should include all elements >= min (inclusive) and keep duplicates."
        );
    }

    /**
     * TEST 3
     * Verifies that iteration respects both bounds set (inclusively),
     * duplicates that are equal to the max are included,
     * and out of range values are excluded.
     */

    @Test
    public void Test3(){
        RBTreeIterable<Integer> tree = new RBTreeIterable<>();
        int[] test_list = {2, 2, 2, 3, 4, 4, 5, 7};
        for (int v : test_list){
            tree.insert(v);
        }
        tree.setIteratorMin(3);
        tree.setIteratorMax(4);
        List<Integer> seen = new ArrayList<>();
        for (Integer x : tree) {
            seen.add(x);
        }
        assertIterableEquals(
            Arrays.asList(3, 4, 4),
            seen,
            "Iterator with [min,max] should include endpoints and keep duplicates."
        );
    }
}
