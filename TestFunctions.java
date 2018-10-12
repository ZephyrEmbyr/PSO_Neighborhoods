

// A class containing the three test functions we'll use to verify our PSO code
class TestFunctions {

	// Ackley Test Function
	public static double Ackley(double[] values) {

		double a = 20.0;
		double b = 0.2;
		double c = 2.0 * Math.PI;

		double firstSum = 0.0;
		double secondSum = 0.0;
		for (int i = 0; i < values.length; i++) {
			firstSum += values[i] * values[i];
			secondSum += Math.cos(c * values[i]);
		}

		double total = -a * Math.exp(-b * Math.sqrt(firstSum/values.length)) - Math.exp(secondSum/values.length) + a + Math.E;
		return total;

	}


	// Rosenbrock Test Function
	public static double Rosenbrock(double[] values) {

		double total = 0.0;
		for (int i = 0; i < values.length - 1; i++) {
			total += 100.0 * Math.pow(values[i+1] - values[i] * values[i], 2.0) + Math.pow(values[i] - 1, 2.0);
		}
		return total;
	}


	// Rastrigin Test Function
	public static double Rastrigin(double[] values) {

		double total = 10.0 * values.length;

		for (int i = 0; i < values.length; i++) {
			total += values[i] * values[i] - 10.0 * Math.cos(2.0 * Math.PI * values[i]);
		}

		return total;
	}


	// DELETE THIS
	public static void tester() {
		System.out.println("Hello Part 2");
	}

}

