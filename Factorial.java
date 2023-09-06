public class Factorial {
    public static void main(String[] args) {
        Factorial factorial = new Factorial();
        int n = 6; // You can change this to calculate factorial for a different number.
        System.out.println("Recursive factorial of " + n + ": " + factorial.getRecursiveFactorial(n));
        System.out.println("Iterative factorial of " + n + ": " + factorial.getIterativeFactorial(n));
    }

    public int getRecursiveFactorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers.");
        }
        return factorialRecursive(n);
    }

    private int factorialRecursive(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * factorialRecursive(n - 1);
    }

    public int getIterativeFactorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers.");
        }
        int fact = 1;
        for (int i = 2; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
}
