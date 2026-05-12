public class BSTRotation<T extends Comparable<T>> extends BinarySearchTree<T> {

    protected void rotate(BinaryNode<T> child, BinaryNode<T> parent)
        throws NullPointerException, IllegalArgumentException {
            if (parent == null|| child == null) { // Throw NPE if either arguement is null.
                throw new NullPointerException();
            }
            
            if (parent != child.parent) { 
                throw new IllegalArgumentException(); 
            }

            BinaryNode<T> G = parent.parent; // "grandparent" Node. 

            if (parent.left == child) { // In this case we do a right rotation about the parent node.
                BinaryNode<T> dummy = child.right;
                parent.left = dummy; // set parent's left node to child's right node (subtree).
                if (dummy != null) { // if dummy is null, then there's not need to set its parent.
                    dummy.parent = parent;
                } 

                child.parent = G;
                if (G == null) { //If G is null, that means we should make child the root node.
                    this.root = child;
                } else if (G.left == parent) { // if G.left is parent, replace G.left with child.
                    G.left = child;
                } else {  // else we replace G.right with child.
                    G.right = child;
                } 
                
                parent.parent = child; // Finish this off by putting parent on childs RIGHT SIDE.
                child.right = parent;
            
            } else if (parent.right == child) { // In this case we do a left rotation about the parent node.
                BinaryNode<T> dummy = child.left;
                parent.right = dummy; // same thing but flipped, parent's right node is child's left node.
                if (dummy != null) { // If dummy is null, there's no need to set its parent.
                    dummy.parent = parent;
                }

                child.parent = G;
                if (G == null) { // Again, in this case parent is the root so we set child to be the root.
                    this.root = child;
                } else if (G.left == parent) { //make G.left child if its left is parent.
                    G.left = child;
                } else { //else make G.right = child.
                    G.right = child;
                }

                parent.parent = child;  // Finish off by putting parent on childs LEFT SIDE.
                child.left = parent;
            } else {
                throw new IllegalArgumentException(); // If neither case above hits, throw IAE.
            } 
        }

    public boolean test1() { //Right rotation about the root.
    BSTRotation<Integer> t = new BSTRotation<>();
    t.root = new BinaryNode<>(2);
    t.root.left = new BinaryNode<>(1);
    t.root.left.parent = t.root;

    try { // After rotation, 1 should be new root, and 2 should be its right child.
        t.rotate(t.root.left, t.root); 
        return t.root.data == 1 && t.root.right.data == 2 && t.root.right.left == null;
    } catch (Exception e) {
        return false;
    }
}

    public boolean test2() { //Left rotation about the root.
    BSTRotation<Integer> t = new BSTRotation<>();
    t.root = new BinaryNode<>(1);
    t.root.right = new BinaryNode<>(3);
    t.root.right.parent = t.root;
    t.root.right.left = new BinaryNode<>(2);
    t.root.right.left.parent = t.root.right;
    try {  // after rotation, 3 should be new root, 1 should be left child, and 2 should be 1’s right child.
        t.rotate(t.root.right, t.root);
        return t.root.data == 3 && t.root.left.data == 1 && t.root.left.right.data == 2;
    } catch (Exception e) {
        return false;
    }
}

    public boolean test3() { // Right rotation deeper in tree, with 2 shared children.
    BSTRotation<Integer> t = new BSTRotation<>();
    t.root = new BinaryNode<>(10);
    t.root.right = new BinaryNode<>(20);
    t.root.right.parent = t.root;
    t.root.right.left = new BinaryNode<>(15);
    t.root.right.left.parent = t.root.right;
    t.root.right.right = new BinaryNode<>(30);
    t.root.right.right.parent = t.root.right;
    try { // After rotation, 15 should replace 20 as child of root, 20 should be right child of 15, and 30 should still be under 20.
        t.rotate(t.root.right.left, t.root.right); 
        return t.root.right.data == 15 && t.root.right.right.data == 20 && t.root.right.right.right.data == 30;
    } catch (Exception e) {
        return false;
    }
}

    public boolean test4() { // Right rotation with 3 shared children
    BSTRotation<Integer> t = new BSTRotation<>();
    t.root = new BinaryNode<>(80);
    t.root.left = new BinaryNode<>(40);
    t.root.left.parent = t.root;
    t.root.left.left = new BinaryNode<>(20);
    t.root.left.left.parent = t.root.left;
    t.root.left.right = new BinaryNode<>(60);
    t.root.left.right.parent = t.root.left;
    t.root.left.right.left = new BinaryNode<>(48);
    t.root.left.right.left.parent = t.root.left.right;
    try { // After rotation 40 becomes new root, 80 becomes right child of 40, 20 stays left of 40, 60 stays right of 40, 48 stays left of 60.
        t.rotate(t.root.left, t.root);
        return t.root.data == 40 && t.root.left.data == 20 && t.root.right.data == 80 && t.root.right.left.data == 60 && t.root.right.left.left.data == 48;
    } catch (Exception e) {
        return false;
    }
}

    public static void main(String[] args){ // main method tests 
                BSTRotation<Integer> Testing = new BSTRotation<>();
                boolean w = Testing.test1();
                boolean x = Testing.test2();
                boolean y = Testing.test3();
                boolean z = Testing.test4();

                if (w && x && y && z){
                    System.out.println("All test pasts yay");
                } else {
                    System.out.println("Go debug");
                }
            }
}
