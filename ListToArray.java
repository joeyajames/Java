// Java: convert ArrayList<String> to String[]
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.*;

class ListToArray {
	public static void main(String[] args) {
		// create Array
		String[] array = {"Bugatti", "Ferarri", "Lamborghini", "Rolls Royce"};
		
		// 3 ways to print Array
		System.out.println(Arrays.toString(array));

		for(String s : array) {
			System.out.print(s + ", ");
		}
		System.out.println();

		Stream.of(array) 
			.forEach(System.out::println);
		
		// 2 ways to convert Array to ArrayList
		List<String> arrayList = new ArrayList<>(Arrays.asList(array));
		System.out.println("arrayList: " + arrayList);
		
		List<String> arrayList2 = new ArrayList<>(List.of(array));
		System.out.println("arrayList2: " + arrayList2);
		
		// convert ArrayList to Array
		String[] array2 = arrayList.toArray(new String[0]);

		// print Array
		Stream.of(array2) 
			.forEach(System.out::println);

	}
}







