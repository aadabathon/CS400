import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {//implemented by: Adam Shebani
    protected class Pair { // Class pair that is the key value pair for our hashtable
        public KeyType key;
        public ValueType value;

        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }
    }

    protected LinkedList<Pair>[] table = null;
    private int size;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    public HashtableMap() { //Default Constructor
        this(8); //Call HashtableMap(8)
    }

    @SuppressWarnings("unchecked") //This annotation is used to acknowledge to the compiler that we understand the type cast could be dangerous
    public HashtableMap(int capacity) { //Capacity Constructor
        if (capacity <= 0) {
            capacity = 8;
        }
        table = (LinkedList<Pair>[]) new LinkedList[capacity];
        size = 0;
    }

    /**
     * Helper Methods Below:
     */

    private int indexForKey(KeyType key) { //Hash the key, take the abs of the hash, return hash modulo length
        int hash = key.hashCode();
        hash = hash & 0x7fffffff; // ensure non-negative
        return hash % table.length;
    }

    private void resizeIfNeeded() { //Resize given the nextLoadFactor exceeds our final threshold factor of 0.75
        double nextLoadFactor = ((double) (size + 1)) / table.length;
        if (nextLoadFactor >= LOAD_FACTOR_THRESHOLD) {
            resize();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void resize() {
        LinkedList<Pair>[] oldTable = table;
        int newCapacity = oldTable.length * 2;

        table = (LinkedList<Pair>[]) new LinkedList[newCapacity]; // create new table (type casting in this manner can be spooky, hence the annotation).
        
        int oldSize = size;
        size = 0;

        for (int i = 0; i < oldTable.length; i++) { // rehash all key-value pairs from old table into new table 
            if (oldTable[i] != null) {
                for (Pair p : oldTable[i]) { //Iterate through potential linked list
                    put(p.key, p.value); // reinsert with existing keys and values
                }
            }
        }
        size = oldSize; // size after rehash should already equal oldSize
    }

    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if (key == null) {
            throw new NullPointerException("key cannot be null");
        }

        resizeIfNeeded();// Check if we need to grow before inserting

        int index = indexForKey(key);

        if (table[index] == null) { //If nothing there instantiate Linkedlist<pair>
            table[index] = new LinkedList<Pair>();
        }

        
        for (Pair p : table[index]) { // check for duplicate key
            if (p.key.equals(key)) {
                throw new IllegalArgumentException("key already exists");
            }
        }

        table[index].add(new Pair(key, value)); //add pair and increment size
        size++;
    }

    @Override
    public boolean containsKey(KeyType key) {
        if (key == null) { //Throw error if null, return false if key is not in linkedlist of corresponding index of table.
            throw new NullPointerException("key cannot be null");
        }

        int index = indexForKey(key);
        LinkedList<Pair> bucket = table[index];

        if (bucket == null) {
            return false;
        }

        
        for (Pair p : bucket) {
            if (p.key.equals(key)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        if (key == null) {
            throw new NullPointerException("key cannot be null");
        }

        int index = indexForKey(key);
        LinkedList<Pair> bucket = table[index];

        if (bucket != null) { // Search table[idx]'s linked list for key, return corresponding value
            for (Pair p : bucket) {
                if (p.key.equals(key)) {
                    return p.value;
                }
            }
        }
        throw new NoSuchElementException("key not found"); //Throw if key not in table
    }

    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        if (key == null) {
            throw new NullPointerException("key cannot be null");
        }

        int index = indexForKey(key);
        LinkedList<Pair> bucket = table[index];

        if (bucket == null) {
            throw new NoSuchElementException("key not found");
        }

        for (int i = 0; i < bucket.size(); i++) { //For i in bucket, if key remove and decrement size
            Pair p = bucket.get(i);
            if (p.key.equals(key)) {
                bucket.remove(i);
                size--;
                return p.value; //return key's corresponding value
            }
        }

        throw new NoSuchElementException("key not found");
    }

    @Override
    public void clear() {// set all buckets to null without changing capacity
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    @Override
    public int getSize() { //return size
        return size;
    }

    @Override
    public int getCapacity() { // return table length, 8 unless set explicity
        return table.length;
    }

    /* 
     * TEST METHODs:
     */


    /**
     * Tests basic put, get, and containsKey behavior on a small HashtableMap.
     * Verifies that inserted key-value pairs can be retrieved correctly and
     * that size and containsKey are consistent.
     */

    @Test
    public void testBasicPutGetContains() {
        HashtableMap<String, Integer> map = new HashtableMap<>(4);
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);


        assertEquals(3, map.getSize()); // verify size after insertions

        assertTrue(map.containsKey("a")); // verify containsKey works
        assertTrue(map.containsKey("b"));
        assertTrue(map.containsKey("c"));
        assertFalse(map.containsKey("d"));

        assertEquals(1, map.get("a")); // verify get returns correct values
        assertEquals(2, map.get("b"));
        assertEquals(3, map.get("c"));
    }

    /**
     * Tests that the hashtable resizes when the load factor threshold is reached
     * and that the capacity doubles while all key-value pairs remain accessible.
     */

    @Test
    public void testResizeTriggeredAtThreshold() {

        HashtableMap<Integer, String> map = new HashtableMap<>(4);
        int initialCapacity = map.getCapacity();

        map.put(1, "one"); 
        map.put(2, "two"); 

        map.put(3, "three"); 

        int newCapacity = map.getCapacity();

        assertEquals(initialCapacity * 2, newCapacity); //verify that newcapacity is double the intial and the Values match the keys post rehash

        assertEquals("one", map.get(1));
        assertEquals("two", map.get(2));
        assertEquals("three", map.get(3));
    }

    /**
     * Tests that resize correctly rehashes colliding keys so that all mappings
     * remain correct
     */
    
    @Test
    public void testResizeRehashWithCollisions() {
        class BadHashKey { //Nice nested class BadHashKey that has a heinous hash function and allows us to test edge cases of my class
            private final String value;

            BadHashKey(String value) {
                this.value = value;
            }

            @Override
            public int hashCode() { // All hashed keys will collide
                return 1; 
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                BadHashKey other = (BadHashKey) o;
                return this.value.equals(other.value);
            }
        }

        HashtableMap<BadHashKey, Integer> map = new HashtableMap<>(2);

        BadHashKey k1 = new BadHashKey("k1");
        BadHashKey k2 = new BadHashKey("k2");
        BadHashKey k3 = new BadHashKey("k3");

        map.put(k1, 10);
        map.put(k2, 20);
        map.put(k3, 30); 

        assertEquals(3, map.getSize()); // verify size after resize and rehash

        assertEquals(10, map.get(k1)); // verify that all keys still map to the correct values
        assertEquals(20, map.get(k2));
        assertEquals(30, map.get(k3));
    }

    /**
     * Tests remove and clear operations. Verifies that removed keys are no longer
     * present, that size is updated correctly, and that clear empties the table
     * without changing its capacity.
     */

    @Test
    public void testRemoveAndClear() {
        HashtableMap<String, String> map = new HashtableMap<>(4);
        map.put("x", "X");
        map.put("y", "Y");
        map.put("z", "Z");

        int capacityBeforeClear = map.getCapacity();

        String removed = map.remove("y"); // remove a single key and check returned value and size
        assertEquals("Y", removed);
        assertFalse(map.containsKey("y"));
        assertEquals(2, map.getSize());

        
        map.clear(); // clear the map and check size resets to zero
        assertEquals(0, map.getSize());

        assertEquals(capacityBeforeClear, map.getCapacity()); // capacity should remain unchanged after clear
    }

    /**
     * Tests that the hashtable throws the correct exceptions for invalid
     * operations (null keys, duplicate keys, and missing keys essentially).
     */

    @Test
    public void testExceptionBehavior() {
        HashtableMap<String, Integer> map = new HashtableMap<>(4);

        assertThrows(NullPointerException.class, () -> {
            map.put(null, 1);
        });

        assertThrows(NullPointerException.class, () -> {
            map.containsKey(null);
        });

        assertThrows(NullPointerException.class, () -> {
            map.get(null);
        });

        assertThrows(NullPointerException.class, () -> {
            map.remove(null);
        });

        map.put("key", 42);

        assertThrows(IllegalArgumentException.class, () -> {
            map.put("key", 99);
        });

        assertThrows(NoSuchElementException.class, () -> {
            map.get("missing");
        });

        assertThrows(NoSuchElementException.class, () -> {
            map.remove("missing");
        });
    }
}
