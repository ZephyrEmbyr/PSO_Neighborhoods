package PSO_Neighborhoods;


public class TestFunctions {
	public static void main(String[] args) {

	}

	public static double Ackley(double[] values) {

		double a = 20.0;
		double b = 0.2;
		double c = 2.0 * Math.PI;

		double firstSum = 0;
		double secondSum = 0;
		for (int i = 0; i < values.length; i++) {
			firstSum += values[i] * values[i];
			secondSum += Math.cos(c * values[i]);
		}

		double total = -a * Math.exp(-b * Math.sqrt(firstSum/values.length)) - Math.exp(secondSum/values.length) + a + Math.E;
		return total;

	}

}

