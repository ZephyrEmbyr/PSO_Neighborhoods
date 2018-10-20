import java.util.*;

// Swarm class
class Swarm {

	final static int RING_SIZE = 3;
	final static int VON_NEUMANN_SIZE = 5;

	static int updatedPBest = 1;
	static int usingRandomFlag = 0;
	static int usingGlobalSwarm = 0;

	static int numParticles = 20;
	static Particle[] allParticles;
	static int[][] neighborhoods;
	static int dimensions = 30;
	static double minPos = 16.0;
	static double maxPos = 32.0;
	static double maxSpeed = 4.0;
	static double minSpeed = -2.0;

	static boolean firstTimeRandom = true;
	private static Random rand = new Random();
	static int numRandNeighbors = 5;

	static double Chi = 0.7298;
	static double[] chiArray;
	static double phi_1 = 2.05;
	static double phi_2 = 2.05;


	// creates a neighborhood of particles depending on the command line arguments,
	// as well as sets the minimum and maximum positions and speeds. Finally, it
	// initializes all of the particles, as well as their personal and neighborhood
	// bests
	public Swarm(String topologyString, int numPart, String functionString, int dim)
	{

		numParticles = numPart;
		dimensions = dim;

		chiArray = new double[dimensions];
		Arrays.fill(chiArray, Chi);

		setNeighbors(topologyString);
		setTestFunction(functionString);

		allParticles = new Particle[numParticles];

		for (int i = 0; i < numParticles; i++)
		{
			Particle particle = new Particle(dimensions, minPos, maxPos, minSpeed, maxSpeed);
			allParticles[i] = particle;
		}

		for(int k = 0; k < numParticles; k++)
		{
			updatePBest(k);
		}

		if (usingGlobalSwarm == 1) 
		{
			updateNBest(0);
			double globalBest = allParticles[0].getNBest();
			for(int m = 0; m < numParticles; m++) {
				allParticles[m].setNBest(globalBest);
			}
		}
		else if (usingGlobalSwarm == 0) 
		{
			for(int k = 0; k < numParticles; k++)
			{
				updateNBest(k);
			}
		}

	}

	public static Particle getParticle(int i)
	{
		return allParticles[i];
	}

	public static int getNumParticles()
	{
		return numParticles;
	}

	public static int updatedPBest()
	{
		return updatedPBest;
	}

	public static int usingRandomFlag()
	{
		return usingRandomFlag;
	}

	public static int usingGlobalSwarm()
	{
		return usingGlobalSwarm;
	}

	public static void setNeighbors(String topologyString)
	{
		if (topologyString.equals("gl")) {
			// if global neighborhood
			neighborhoods = new int[numParticles][numParticles];
			findNeighborGlobal();
			usingGlobalSwarm = 1;
		}
		else if (topologyString.equals("ri")) {
			// if ring neighborhood
			neighborhoods = new int[numParticles][RING_SIZE];
			findNeighborRing();
		}
		else if (topologyString.equals("vn")) {
			// if von neumann neighborhood
			neighborhoods = new int[numParticles][VON_NEUMANN_SIZE];
			findNeighborVonNeumann();
		}
		else if (topologyString.equals("ra")) {
			// if random neighborhood
			neighborhoods = new int[numParticles][numRandNeighbors];
			usingRandomFlag = 1;
			findNeighborRandom();
		}
		else{
			System.out.println("Topology (first) argument needs to be one of gl, ri, vn, or ra");
			System.exit(1);
		}

	}

	public static void setTestFunction(String functionString)
	{
		if (functionString.equals("ack")) {
			TestFunctions.FUNCTION_TO_USE = 1;
			minPos = 16.0;
			maxPos = 32.0;
			minSpeed = -32.768;
			maxSpeed = 32.768;
		}
		else if (functionString.equals("rok")) {
			TestFunctions.FUNCTION_TO_USE = 2;
			System.out.println("In ROK");
			minPos = 15.0;
			maxPos = 30.0;
			minSpeed = -2.048;
			maxSpeed = 2.048;
		}
		else if (functionString.equals("ras")) {
			TestFunctions.FUNCTION_TO_USE = 3;
			minPos = 2.56;
			maxPos = 5.12;
			minSpeed = -5.12;
			maxSpeed = 5.12;
		}
		else{
			System.out.println("Function (fourth) argument needs to be one of rok, ack, or ras");
			System.exit(1);
		}

	}


	// returns array of neighborhood bests for each particle (each will be global best)
	public static void findNeighborGlobal()
	{
		for (int i = 0; i < numParticles; i++) {
			int[] neighbors = new int[numParticles];
			for (int j = 0; j < numParticles; j++) {
				neighbors[j] = j;
			}
			neighborhoods[i] = neighbors;
		}
	}

	// returns array of neighborhood bests for each particle where the neighborhood is a ring
	public static void findNeighborRing(){

		for (int i = 0; i < numParticles; i++)
		{
			int priorParticle, curParticle, nextParticle;
			if (i == 0)
			{
				priorParticle = numParticles - 1;
				curParticle   = i;
				nextParticle  = i + 1;
			}
			else if (i == numParticles - 1)
			{
				priorParticle = i - 1;
				curParticle   = i;
				nextParticle  = 0;
			}
			else
			{
				priorParticle = i - 1;
				curParticle   = i;
				nextParticle  = i + 1;
			}
			int[] neighbors   = {priorParticle, curParticle, nextParticle};
			neighborhoods[i]  = neighbors;
		}
	}


	// returns array of neighborhood bests for each particle where the neighborhood is the four
	// closest neighbors orthogonally
	public static void findNeighborVonNeumann()
	{
		int cols = (int) Math.ceil(Math.sqrt(numParticles));
		// int rows = (int) numParticles / cols

		for (int i = 0; i < numParticles; i++)
		{
			int curParticle   = i;
			int aboveParticle = (i - cols + numParticles) % numParticles;
			int belowParticle = (i + cols) % numParticles;
			int leftParticle;
			int rightParticle;
			// need last in col
			if (i % cols == 0) {
				leftParticle = i + cols - 1;
			}
			// everything else
			else {
				leftParticle = i - 1;
			}
			// need first in col
			if ((i + 1) % cols == 0 ) {
				rightParticle = i - cols + 1;
			}
			// everything else
			else {
				rightParticle = i + 1;
			}
			int[] neighbors   = {curParticle, aboveParticle, belowParticle, leftParticle, rightParticle};
			neighborhoods[i]  = neighbors;
		}
	}

	// randomly selects k number of neighbors from all the particles and sets
	// a particle's new set of neighbors to those
	public static void findNeighborRandom()
	{
		int numNeighbors = numRandNeighbors;
		if(firstTimeRandom || rand.nextDouble() < 0.2)
		{
			firstTimeRandom = false;
			ArrayList<Integer> solution = new ArrayList<Integer>();
			for(int i = 0; i < numParticles; i++)
			{
				solution.add(i);
			}

			for(int j = 0; j < numParticles; j++)
			{
				Collections.shuffle(solution);
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



	// updates a particle's neighborhood best value if there is some new value
	// in its neighborhood that is better than the previous neighborhood best
	public static void updateNBest(int indexParticle)
	{
		double curNBest = allParticles[indexParticle].getNBest();
		double[] curNBestPosition = allParticles[indexParticle].nBestPosition;

		for (int neighborIndex:neighborhoods[indexParticle])
		{
			double tempNeighborFitness = allParticles[neighborIndex].getPBest();
			if (tempNeighborFitness < curNBest)
			{
				curNBest = tempNeighborFitness;
				curNBestPosition = allParticles[neighborIndex].nBestPosition;
			}
		}
		allParticles[indexParticle].setNBest(curNBest);
		allParticles[indexParticle].setNBestPosition(curNBestPosition);
	}

	// updates a given particle's personal best if it's current position is
	// better than any position it had before
	public static void updatePBest(int index)
	{
		double curBest = allParticles[index].getPBest();
		double potentialBest = TestFunctions.testFunction(allParticles[index]);

		double[] testDummy = allParticles[index].position;
		double[] testDummyVel = allParticles[index].velocity;

		if (index == 0) {
			for (int i = 0; i < testDummy.length; i++ ) {
			}
			for (int i = 0; i < testDummyVel.length; i++ ) {
			}
		}
		if(potentialBest < curBest)
		{
			allParticles[index].setPBest(potentialBest);
			allParticles[index].setPBestPosition(allParticles[index].position);
			updatedPBest = 1;
		}
		else
		{
			updatedPBest = 0;
		}
	}

	// updates the velocity values for each dimension for a given particle
	// based on some random value added to the current velocity
	public static void updateVelocity(Particle particle)
	{
		double[] U_1 = new double[dimensions];
		double[] U_2 = new double[dimensions];
		for (int i = 0; i < dimensions; i++)
		{
			U_1[i] = phi_1 * rand.nextDouble();
			U_2[i] = phi_2 * rand.nextDouble();
		}

		double[] temp1 = product(U_1, sum(particle.pBestPosition, negate(particle.position)));
		double[] temp2 = product(U_2, sum(particle.nBestPosition, negate(particle.position)));
		double[] updatedVelocity = product(chiArray, sum(particle.velocity, sum(temp1, temp2)));

		particle.setVelocity(updatedVelocity);

		for(int k = 0; k < particle.velocity.length; k++)
		{
			if(particle.velocity[k] > maxSpeed)
			{
				particle.velocity[k] = maxSpeed;
			}
			else if(particle.velocity[k] < minSpeed)
			{
				particle.velocity[k] = minSpeed;
			}
		}
	}

	// updates the position of a particle based on it's current velocity
	public static void updatePosition(Particle particle)
	{
		particle.setPosition(sum(particle.position,particle.velocity));
	}

	// returns an array where each index contains the sum of two values of given arrays
	public static double[] sum(double[] a, double[] b)
	{
		double[] sum = new double[a.length];
		for(int k = 0; k < a.length; k++)
		{
			sum[k] = a[k]+b[k];
		}
		return sum;
	}

	// returns the negation of an array of doubles, i.e. negate({2, 6, -3}) => {-2, -6, 3}
	public static double[] negate(double[] a)
	{
		double[] negation;
		negation = new double[a.length];

		for(int k = 0; k < a.length; k++)
		{
			negation[k] = -1*a[k];
		}
		return negation;
	}

	// returns the dot product of two arrays of doubles
	public static double[] product(double[] a, double[] b)
	{
		double[] product = new double[a.length];
		for(int k = 0; k < a.length; k++)
		{
			product[k] = a[k]*b[k];
		}
		return product;
	}

}