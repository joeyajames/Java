// Java - How to Break out of Nested For Loops

class EscapeNestedLoops {
	public static void main(String[] args) {

		for(int x=0; x<8; x++) {
			for(int y=1; y<4; y++) {
				System.out.println(x + " " + y + " " + x*y);
				if(x * y > 5)
			}
		}

		System.out.println(" ");
		
		xLoop:
		for(int x=0; x<8; x++) {
			for(int y=1; y<4; y++) {
				System.out.println(x + " " + y + " " + x*y);
				if(x * y > 5)
					break xLoop;
			}
		}

	}
}
