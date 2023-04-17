// Java: Split a String at char
import java.util.Arrays;

class SplitString {
	
	public static void main(String[] args) {
		String s = "My dog ate my homework; Can I turn it in tomorrow?";

		String[] ss = s.split(" ");
		System.out.println(Arrays.toString(ss));

		ss = s.split(";");
		System.out.println(Arrays.toString(ss));

		// you must escape special chars because the split parameter is a regex
		// special chars include \ . + ^ $ | ? * ( ) [ {
		String t = "54.25-128.17";
		String[] tt = t.split("\\.");
		System.out.println(Arrays.toString(tt));

		// include multiple split chars inside brackets
		tt = t.split("[.-]");
		System.out.println(Arrays.toString(tt));

	}
}









