/**
 * This is a simple application that demonstrates the use of MyList.
 */
public class Main {

    public static void main(String[] args) {
        // populate zoo with antelope, bear, cheetah, deer, and eagle
        ListADT<String> miniZoo = new MyList<>();
        miniZoo.add("antelope");
        miniZoo.add("bear");
        miniZoo.add("cheetah");
        miniZoo.add("deer");
        miniZoo.add("eagle");
        System.out.println("Miniature Zoo started with: "+miniZoo);
        
        // TODO: add code here to trade the cheetah for a fox
        // 1) remove the cheetah element from miniZoo
        // 2) add a new element with the value "fox" to miniZoo
	miniZoo.remove(2);
	miniZoo.add("fox");		        
        System.out.println("Miniature Zoo ended with: "+miniZoo);
    }

}
