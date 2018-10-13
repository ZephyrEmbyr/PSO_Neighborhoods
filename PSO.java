import java.util.*;
// import TestFunctions.*;
public class PSO {

	final static int RING_SIZE = 3;
	final static int VON_NEUMANN_SIZE = 5;

	public static void main(String[] args) {
		System.out.println(args[0]);
		System.out.println("Hello World!");
		// Test function is set through the command line arguments
		TestFunctions.FUNCTION_TO_USE = 0;
		TestFunctions.tester();
	}

	static int dimensions = 30;
	static double minPos = 16.0;
	static double maxPos = 32.0;
	static double maxSpeed = 4.0;
	static double minSpeed = -2.0;
	static int numParticles = 20;
	static Particle[] allParticles;
	static boolean firstTimeRandom = true;
	private static Random rand = new Random();
	static int[][] neighborhoods;
	static int numRandNeighbors = (int)numParticles/2;

	static double Chi = 0.7298;
	static double[] chiArray;
	static double phi_1 = 2.05;
	static double phi_2 = 2.05;

    public static void createNeighborhood() {

        // int dimensions = 30;
        // double minPos = 16.0;
        // double maxPos = 32.0;
        // double maxSpeed = 4.0;
        // double minSpeed = -2.0;
        // int numParticles = 20;
        // Particle[] allParticles;

		chiArray = new double[dimensions];
		Arrays.fill(chiArray, Chi);

		allParticles = new Particle[numParticles];

        for (int i = 0; i < numParticles; i++) {
            Particle particle = new Particle(dimensions, minPos, maxPos, minSpeed, maxSpeed);
            allParticles[i] = particle;
        }


    }


    // returns array of neighborhood bests for each particle (each will be global best)
    public static double[] findNeighborGlobal(Particle[] particles) {
    	double gBest = Double.MAX_VALUE;
    	for (int i = 0; i < particles.length; i++) {
    		double curPBest = particles[i].getPBest();
    		if (curPBest < gBest) {
    			gBest = curPBest;
    		}
    	}

       	double[] globalBests = new double[particles.length];
    	for (int i = 0; i < particles.length; i++) {
			globalBests[i] = gBest;
    	}
    	return globalBests;
    }


    // returns array of neighborhood bests for each particle where the neighborhood is a ring
    public static void findNeighborRing(Particle[] particles) {

	neighborhoods = new int[numParticles][RING_SIZE];
	for (int i = 0; i < particles.length; i++) {
			int priorParticle, curParticle, nextParticle;
    		if (i == 0) {
    			priorParticle = particles.length - 1;
    			curParticle   = i;
    			nextParticle  = i + 1;
    		}
    		else if (i == particles.length - 1) {
    			priorParticle = i - 1;
    			curParticle   = i;
    			nextParticle  = 0;
    		}
    		else {
    			priorParticle = i - 1;
    			curParticle   = i;
    			nextParticle  = i + 1;
    		}
    		int[] neighbors   = {priorParticle, curParticle, nextParticle};
    		neighborhoods[i] = neighbors;
    	}
    }


    // returns array of neighborhood bests for each particle where the neighborhood is the four
    // closest neighbors orthogonally
    public static void findNeighborVonNeumann(Particle[] particles) {

    	neighborhoods = new int[numParticles][VON_NEUMANN_SIZE];
    	int cols = (int) Math.ceil(Math.sqrt(numParticles));
    	// int rows = (int) numParticles / cols

    	for (int i = 0; i < numParticles; i++) {
    		int curParticle   = i;
    		int aboveParticle = (i - cols + numParticles) % numParticles;
    		int belowParticle = (i + cols) % numParticles;
    		int leftParticle  = (i - 1 + numParticles) % numParticles;
    		int rightParticle = (i + 1) % numParticles;
    		int[] neighbors   = {curParticle, aboveParticle, belowParticle, leftParticle, rightParticle};
    		neighborhoods[i] = neighbors;
    	}
    }

	public static void findNeighborRandom()
{
	int numNeighbors = numRandNeighbors;
	neighborhoods = new int[numParticles][numNeighbors];
	if(firstTimeRandom || rand.nextDouble() < 0.2)
	{
		firstTimeRandom = false;
		Particle[] particles = allParticles.clone();
		ArrayList<Integer> solution = new ArrayList<Integer>();
		for(int i = 0; i < numParticles; i++)
		{
			solution.add(i);
		}
		Collections.shuffle(solution);

		for(int j = 0; j < numParticles; j++)
		{
			List<Integer> solutionCopy = (ArrayList<Integer>) solution.clone();
			solutionCopy.remove(new Integer(j));
			for(int k = 0; k < numNeighbors-1; k++)
			{
				neighborhoods[j][k] = solutionCopy.get(k);

			}
			neighborhoods[j][numNeighbors-1] = j;
		}
	}
}


	public static void updateNBest(int indexParticle) {
		double curNBest = allParticles[indexParticle].getNBest();

		for (int neighborIndex:neighborhoods[indexParticle]) {
			double tempNeighborFitness = TestFunctions.testFunction(allParticles[neighborIndex]);
			if (tempNeighborFitness < curNBest) {
				curNBest = tempNeighborFitness;
			}
		}

		allParticles[indexParticle].setNBest(curNBest);
	}


	public static void updatePBest(int index) {
		double curBest = allParticles[index].getPBest();
		double potentialBest = TestFunctions.testFunction(allParticles[index]);
		if (potentialBest < curBest) {
			allParticles[index].setPBest(potentialBest);
			allParticles[index].setPBestPosition(allParticles[index].position);
		}

	}

	public static void updateVelocity(Particle particle)
	{
		double[] U_1 = new double[dimensions];
		double[] U_2 = new double[dimensions];
		for (int i = 0; i < dimensions; i++) {
			U_1[i] = phi_1 * rand.nextDouble();
			U_2[i] = phi_2 * rand.nextDouble();
		}

		double[] temp1 = product(U_1, sum(particle.position, negate(particle.getPBest)));
		double[] temp2 = product(U_2, sum(particle.position, negate(particle.getNBest)));

		double[] updatedVelocity = product(chiArray, sum(particle.velocity, sum(temp1, temp2)));

		particle.velocity = updatedVelocity;
	}

	public static void sum(double[] a, double[] b)
	{
		double[] sum = new double[a.length];
		for(int k = 0; k < a.length; k++)
		{
			sum[k] = a[k]+b[k];
		}
		return product;
	}

	public static void negate(double[] a)
	{
		double[] negation = new double[a.length];
		for(int k = 0; k < a.length; k++)
		{
			negation[k] = -1*a[k];
		}
		return negation;
	}

	public static void product(double[] a, double[] b)
	{
		double[] product = new double[a.length];
		for(int k = 0; k < a.length; k++)
		{
			product[k] = a[k]*b[k];
		}
		return product;
	}

	public static void run()
	{
		//update velocity
		//update position
		//evaluate function at new position
		//update personal best
		//update neighborhood bests
	}

}
