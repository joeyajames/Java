public class Temperature {
	public static void main(String[] args) {
		Temperature temperature = new Temperature();
		
		double fTemp = 77.5;
		double cTemp = temperature.toCelsius(fTemp);
		System.out.printf("%.2f Fahrenheit = %.2f Celsius.\n", fTemp, cTemp);
		
		cTemp = 37.2;
		fTemp = temperature.toFahrenheit(cTemp);
		System.out.printf("%.2f Celsius = %.2f Fahrenheit.\n", cTemp, fTemp);
	}
	
	public double toCelsius(double f) {
		return (f - 32.0) / 1.8;
	}

	public double toFahrenheit(double c) {
		return (c * 1.8) + 32.0;
	}
}