// Java - Convert String to an Int

class StringToInt {
	public static void main(String[] args) {
		String s = "222";
		int i;
		Integer integer;
		
		// returns an int
		i = Integer.parseInt(s); 
		System.out.println(i + 1);

		// returns an Integer
		integer = Integer.valueOf(s); 
		System.out.println(integer + 2);

		// Best Practice: safely returns an Integer
		try {
			integer = Integer.valueOf(s); 
		} catch (NumberFormatException e) {
			integer = 0;
		}
		System.out.println(integer + 3);
	}

}