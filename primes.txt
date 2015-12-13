// version 1
public class Primes {

    public static void main(String[] args) {
        int max = 10;
        for (int x = 2; x <= max; x++) {
            boolean isPrime = true;
            for (int y = 2; y < x; y++)
				if (x % y == 0)
                    isPrime = false;
            if (isPrime)
                System.out.println(x);
        }
    }
}
--------------------------------------------
// version 2: break
public class Primes {

    public static void main(String[] args) {
        int max = 10;
        for (int x = 2; x <= max; x++) {
            boolean isPrime = true;
            for (int y = 2; y < x; y++)
				if (x % y == 0) {
					isPrime = false;
					break;
				}
            if (isPrime)
                System.out.println(x);
        }
    }
}
--------------------------------------------
// version 3: break, add to list
import java.util.ArrayList;

public class Primes {

    public static void main(String[] args) {
        ArrayList<Integer> primeList  = new ArrayList<>();

        int max = 10000;
        for (int x = 2; x <= max; x++) {
            boolean isPrime = true;
            for (int y = 2; y < x; y++)
				if (x % y == 0) {
                    isPrime = false;
                    break;
                }
            if (isPrime)
                primeList.add(x);
        }
        System.out.println(primeList);
    }
}
--------------------------------------------
// version 4: break, add to list, square root
import java.util.ArrayList;

public class Primes {

    public static void main(String[] args) {
        ArrayList<Integer> primeList  = new ArrayList<>();

        int max = 200000;
        for (int x = 2; x <= max; x++) {
            boolean isPrime = true;
            for (int y = 2; y < Math.sqrt(x); y++)
				if (x % y == 0) {
                    isPrime = false;
                    break;
                }
            if (isPrime)
                primeList.add(x);
        }
        System.out.println(primeList);
    }
}
