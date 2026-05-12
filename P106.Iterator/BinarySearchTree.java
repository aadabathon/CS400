public class BinarySearchTree<T extends Comparable<T>> implements SortedCollection<T> { // This class implements the interface SortedCollection in a generic manner, all generic types must satisfy the bound "extends Comparable<T>".
    protected BinaryNode<T> root = null; // root automatically set to null upon instantiated, an implicitly defined no-arguement constructor does exist and so I have not included an explicitly defined constructor.

    protected void insertHelper(BinaryNode<T> newNode, BinaryNode<T> subtree){ //This recursive helper method takes in two nodes, a newNode to append and a subtree to append to.
        if (subtree == null || newNode == null) { //If either arguement is null, there is nothing to be done and we simply return.
            return;
        }

        int i = newNode.data.compareTo(subtree.data); //Comparable method, helps us traverse the list.

        if (i <= 0) { // If the newNode data is less than that of our subtree node, then go left and if it points to null, we append our new node to subtree and appoint it to be the parent of new node. Otherwise we recurrsively call this method on subtree.left. As per direction, duplicate values will be stored to the left.
            if (subtree.left == null) { 
                subtree.left = newNode;
                newNode.parent = subtree;
            } else {
                insertHelper(newNode, subtree.left);
            } 
        }
        else { //otherwise, the data is larger and we go right, implementing the same logic as before.
                if (subtree.right == null) { 
                subtree.right = newNode;
                newNode.parent = subtree;
            } else {
                insertHelper(newNode, subtree.right);
            }
            }
        }
    
    @Override
    public void insert(T data) {
        if (data == null) throw new NullPointerException(); //Throws NPE if data == null
        BinaryNode<T> node = new BinaryNode<>(data); // Create a new node with our data arguement to pass into our helper method with root.
        if (root == null) { //If root is null, the BST is empty and we can simply insert the node as the root.
            root = node;
            return;
        }
        insertHelper(node, root); //Call our helper method on our new node and root
    }

    @Override
    public boolean contains(Comparable<T> data) {
        if (data == null) throw new NullPointerException(); // Throw NPE if data == null
        
        BinaryNode<T> dummy = root; //Start at root
        while(dummy != null) { //Loop while dummy is not null
            int i = data.compareTo(dummy.data);
            if (i < 0){ // if dummy.data is less than our arguement, go left
                dummy = dummy.left;
            } else if (i > 0) { //if greater, go right
                dummy = dummy.right;
            } else return true; //if neither are true, dummy.data == data is true and we can safely return true
        }
        return false; //If the loop breaks it means that we traversed the list and did not find our value. Return False.
    }

    private int sizeOf(BinaryNode<T> dummy) { // This helper method lets us recursively find the size of any tree or subtree in a BST
        if (dummy == null){ // If arguement is null, return 0
            return 0;
        } else { // If it is not null, return 1 (size of itself; important for this recursive call) + the sizeOf its left and right child. This ensures all nodes are hit no matter where they are at in the tree.
            return 1 + sizeOf(dummy.left) + sizeOf(dummy.right);
        }
    }

    @Override
    public int size(){ // size is called with no arguements, so we assume that we should return the size of the whole BST. So we start at the root.
        return sizeOf(root);
    }

    @Override
    public boolean isEmpty(){
        return root == null; //If the root is null, then the BST is necessarily empty.
    }

    @Override
    public void clear(){ //Setting the root node to null renders the BST cleared.
        root = null;
    }

    public boolean test1() { //Int Type BST, tests insert(), size(), and contains() on both interior and leaf type nodes.
        BinarySearchTree<Integer> test1 = new BinarySearchTree<>();
        int[] vals = {2, 4, 6, 8, 10, 1, 3, 5, 7, 9};
        for (int v : vals) test1.insert(v);
        boolean leg1 = test1.contains(1) && test1.contains(2) && test1.contains(3) && test1.contains(4) && test1.contains(5) && test1.contains(6) && test1.contains(7) && test1.contains(8) && test1.contains(9) && test1.contains(10); 
        boolean leg2 = (test1.size() == 10);
        return leg1 && leg2;
    }
    
    public boolean test2() { //String Type BST, again testing insert(), size(), and contains() with a different shape as well
        BinarySearchTree<String> test2 = new BinarySearchTree<>();
        String[] letters = {"J", "a", "v", "a", "I", "s", "C", "o", "o", "l"};
        for (String s : letters) test2.insert(s);
        boolean leg1 = test2.contains("J") && test2.contains("a") && test2.contains("v") && test2.contains("I") && test2.contains("s") && test2.contains("C") &&  test2.contains("o") && test2.contains("l"); 
        boolean leg2 = !test2.contains("Z");
        boolean leg3 = test2.size() == 10;
        return leg1 && leg2 && leg3; 
    }

    public boolean test3() { //Testing clear() and IsEmpty()
        BinarySearchTree<Integer> test3 = new BinarySearchTree<>();
        test3.insert(100); test3.insert(300); test3.insert(200);
        boolean leg1 = test3.size() == 3;
        test3.clear();
        boolean leg2 = test3.size() == 0 && test3.isEmpty();
        return leg1 && leg2;
    }

    public boolean test4() { //Testing NullPointerException
        BinarySearchTree<Integer> test4 = new BinarySearchTree<>();
        try {
            test4.insert(null);
            return false; // If no exception is caught, return false immediately
        }
        catch (NullPointerException e) { //Correct exception is caught, return True
            return true;
        }
        catch (Exception e){ //An erroneous exception is caught, return false
            return false;
        }
    }

    public boolean test5() { //Making sure parent-child connections remain intact
        BinarySearchTree<Integer> dummy = new BinarySearchTree<>();
        int[] vals = {2, 4, 6, 8, 10, 1, 14, 21, 16, 47, 12};
        for (int i : vals) dummy.insert(i); 

        BinaryNode<Integer> leaf = dummy.root.right.right.right.right.right.left;
        while (leaf.parent != null) { //Start at the leaf node and traverse up the list while we are not at the root, in essence while this.parent != null.
            leaf = leaf.parent;
        }
        return leaf == dummy.root;
        }

    public static void main(String[] args){ //main method to confirm our tests
        BinarySearchTree<Integer> testingInt = new BinarySearchTree<>();
        BinarySearchTree<String> testingString = new BinarySearchTree<>();
        boolean l1 = testingInt.test1();
        boolean l2 = testingString.test2();
        boolean l3 = testingInt.test3();
        boolean l4 = testingInt.test4();
        boolean l5 = testingInt.test5();
        if (l1 && l2 && l3 && l4 && l5){
            System.out.println("ALL TESTS PASSED");
        } else{
            System.out.println("Get Back To Work.");
        }
    }
}

