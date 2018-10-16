


// A class containing the three test functions we'll use to verify our PSO code
public class TestFunctions
{

	public static int FUNCTION_TO_USE;

	final static int ACKLEY_NUM = 1;
	final static int ROSENBROCK_NUM = 2;
	final static int RASTRIGIN_NUM = 3;

	// runs the test function specified by the command line argument and returns
	// the calculated fitness
	public static double testFunction(Particle particle)
	{
		double fitness;
		// use Ackley
		if (FUNCTION_TO_USE == TestFunctions.ACKLEY_NUM)
		{
			fitness = Ackley(particle.position);
		}
		// use Rosenbrock
		else if (FUNCTION_TO_USE == TestFunctions.ROSENBROCK_NUM)
		{
			fitness = Rosenbrock(particle.position);
		}
		// use Rastrigin
		else if (FUNCTION_TO_USE == TestFunctions.RASTRIGIN_NUM)
		{
			fitness = Rastrigin(particle.position);
		}
		// should never reach this
		else
		{
			System.out.println("Error: Didn't calculate the fitness");
			System.out.println(FUNCTION_TO_USE);
			fitness = 100;
		}
		return fitness;

	}


	// Ackley Test Function. Int value = 1
	public static double Ackley(double[] values)
	{
		double a = 20.0;
		double b = 0.2;
		double c = 2.0 * Math.PI;

		double firstSum = 0.0;
		double secondSum = 0.0;
		for (int i = 0; i < values.length; i++)
		{
			firstSum += values[i] * values[i];
			secondSum += Math.cos(c * values[i]);
		}

		double total = -a * Math.exp(-b * Math.sqrt(firstSum/values.length)) - Math.exp(secondSum/values.length) + a + Math.E;
		return total;

	}


	// Rosenbrock Test Function. Int value = 2
	public static double Rosenbrock(double[] values)
	{
		double total = 0.0;
		for (int i = 0; i < values.length - 1; i++) {
			total += 100.0 * Math.pow(values[i+1] - values[i] * values[i], 2.0) + Math.pow(values[i] - 1, 2.0);
		}
		return total;
	}

	// Rastrigin Test Function. Int value = 3
	public static double Rastrigin(double[] values)
	{
		double total = 10.0 * values.length;

		for (int i = 0; i < values.length; i++) {
			total += values[i] * values[i] - 10.0 * Math.cos(2.0 * Math.PI * values[i]);
		}

		return total;
	}
}
