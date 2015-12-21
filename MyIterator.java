import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class MyIterator {
		
	public static void main(String[] args) {
		ArrayList<String> cars = new ArrayList<>();
		cars.add("Chevy");
		cars.add("Ford");
		cars.add("Honda");
		cars.add("Mercedes");
		cars.add("Toyota");
		
		// for loop
		System.out.println("For Loop:");
		for (int i = 0; i < cars.size(); i++) {
			System.out.print(cars.get(i) + " ");
		}
		
		// advanced for loop
		System.out.println("\n\nAdvanced For Loop:");
		for (String car : cars) {
			System.out.print(car + " ");
		}
		
		// while loop
		System.out.println("\n\nWhile Loop:");
		int i = 0;
		while (i < cars.size()) {
			System.out.print(cars.get(i++) + " ");
		}
		
		// Iterator (supports hasNext, next, remove)
		System.out.println("\n\nIterator:");
		Iterator<String> iterator = cars.iterator();
		while (iterator.hasNext()) {
			System.out.print(iterator.next() + " ");
		}
		
		// ListIterator (supports hasNext, next, remove, hasPrevious, previous, add)
		System.out.println("\n\nListIterator:");
		ListIterator<String> li = cars.listIterator();
		while (li.hasNext()) {
			System.out.print(li.next() + " ");
		}
		
		// Java 8 Stream
		System.out.println("\n\nJava 8 Stream:");
		cars.forEach((car) -> {
			System.out.print(car + " ");
		});
	}
}






