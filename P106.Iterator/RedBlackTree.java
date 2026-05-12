import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class RedBlackTree<T extends Comparable<T>> extends BSTRotation<T> { //Class extends BSTRotation.java

    @Override
    public void insert(T data) { // Insert method
        if (data == null) throw new NullPointerException();

        RedBlackNode<T> z = new RedBlackNode<>(data); //instance red black node
        z.isBlackNode = false; //By default is a red node.

        if (root == null) { //If root is null, set instance node to root set isblack to true and return
            root = z;
            ((RedBlackNode<T>) root).isBlackNode = true;
            return;
        }

        insertHelper(z, root); //Else insert, ensure red property and set root.isBlack to true
        ensureRedProperty(z);
        ((RedBlackNode<T>) root).isBlackNode = true;
    }

protected void ensureRedProperty(RedBlackNode<T> n) {
    if (n == root || n.getParent() == null || n.getParent().isBlackNode) { // Base case: if n is root, or parent is black, no violation to fix.
        return;
    }

    RedBlackNode<T> p = n.getParent(); // parent
    RedBlackNode<T> g = p.getParent(); // grandparent
    if (g == null) return; // no grandparent → nothing to fix

    boolean leftCase = (p == g.getLeft()); // is parent left child of grandparent?
    RedBlackNode<T> u = leftCase ? g.getRight() : g.getLeft(); // uncle

    
    if (u != null && !u.isBlackNode) { // Case 1, Parent and Uncle are both red
        p.isBlackNode = true;   // parent becomes black
        u.isBlackNode = true;   // uncle becomes black
        g.isBlackNode = false;  // grandparent becomes red

        
        ensureRedProperty(g); // Recirse upward
        return;
    }

     
    if (leftCase && n == p.getRight()) { // Case 2: Parent is red, Uncle is black, and n is "inner child"
        rotate(n, p);  // left rotation about parent
        n = p;         // reset n to parent after rotation
        p = n.getParent();
    } else if (!leftCase && n == p.getLeft()) {
        rotate(n, p);  // right rotation about parent
        n = p;
        p = n.getParent();
    }

    
    RedBlackNode<T> childOfG = leftCase ? g.getLeft() : g.getRight(); //Case 3: Parent is red, Uncle is black, and n is "outer child"
    if (childOfG != null) {
        childOfG.isBlackNode = true; // parent becomes black
    }
    g.isBlackNode = false; // grandparent becomes red
    rotate(childOfG, g); // rotate parent about grandparent
}


/**
 * Tests Case 1, Recoloring.
 */
@Test
public void testInsertCase1_Recoloring() {
    RedBlackTree<String> rbt = new RedBlackTree<>();
    rbt.insert("C");
    rbt.insert("A");
    rbt.insert("E");
    rbt.insert("B"); // triggers recoloring
    assertTrue(rbt.contains("B"));
}

/**
 * Tests Case 2, Rotation (zig-zag).
 */
@Test
public void testInsertCase2_RotationZigZag() {
    RedBlackTree<Integer> rbt = new RedBlackTree<>();
    rbt.insert(10);
    rbt.insert(5);
    rbt.insert(8); // triggers zig-zag rotation
    assertTrue(rbt.contains(8));

}

/**
 * Tests Case 3, Rotation (zig-zig).
 */
@Test
public void testInsertCase3_RotationZigZig() {
    RedBlackTree<Integer> rbt = new RedBlackTree<>();
    rbt.insert(10);
    rbt.insert(5);
    rbt.insert(2); // triggers zig-zig rotation
    assertTrue(rbt.contains(2));
}

}

