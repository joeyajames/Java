// Java HashMap
// Joe James 2023

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.*;

class MapFunctions {
	public static void main (String[] args) {
		// 1. Create a HashMap, add kv pairs
		Map<String, Integer> ages = new HashMap<String, Integer>();
		// OR, to initialize capaacity,
		// Map<String, Integer> ages = new HashMap<String, Integer>(100);
		ages.put("Avni",11);
		ages.put("Bing", 12);
		ages.put("Cassie", 13);
		ages.put("Devarshi", 14);
		System.out.println(ages);

		// 2. Get an item from the map
		String person = "Cassie";
		System.out.println(person + " : " + ages.get(person));
		System.out.println("Frank" + " : " + ages.getOrDefault("Frank", 0));

		// 3. Check for membership
		System.out.println("Contains key Avni?: " + ages.containsKey("Avni"));
		System.out.println("Contains value 23?: " + ages.containsValue(23));

		// 4. Remove an item from the map
		ages.remove("Bing");
		System.out.println(ages);
		ages.remove("Devarshi", 41);
		System.out.println(ages);

		// 5. Get count of items in map
		System.out.println("Num Items = " + ages.size());

		// 6. Remove all items from map
		ages.clear();
		System.out.println("Empty map? " + ages.isEmpty());
		ages.put("Elena", 15);
		ages.put("Frank", 16);
		System.out.println(ages);

		// 7. Conditional add & replace
		ages.putIfAbsent("George", 23); 
		ages.putIfAbsent("Elena", 5);
		System.out.println(ages);
		ages.replace("George", 23, 17);
		ages.replace("George", 44, 55);
		System.out.println(ages);

		// 8. Get collection of Values
		System.out.println(ages.values());

		// 9. Iterate keys using enhanced for loop
		for (String key : ages.keySet()) {
			System.out.println(key +  " : " + ages.get(key));
		}

		// 10. Iterate KV pairs using enhanced for loop
		for (Map.Entry<String, Integer> pair : ages.entrySet()) {
			System.out.println(pair.getKey() + " : " + pair.getValue());
		}

		// 11. Iterate KV pairs using Java 8 Stream API
		ages.entrySet().stream().forEach(pair ->  System.out.println(pair.getKey() + " : " + pair.getValue()));

	}
}