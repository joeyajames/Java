public class Factorial {
	public static void main(String[] args) {
		System.out.println(getRecursiveFactorial(6));
		System.out.println(getIterativeFactorial(6));
	}

	public static int getRecursiveFactorial(int num){
		if(num<0) return -1;
		else if(num==0) return 1;
		else return num*getRecursiveFactorial(num-1);
	}

	public static int getIterativeFactorial(int num){
		if (num < 0) return -1;

		int fact = 1;
		for (int i = 1; i <= num; i++)
			fact *= i;
		return fact;
	}
}