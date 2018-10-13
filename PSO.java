import java.util.*;
import TestFunctions;
public class PSO {

	final static int RING_SIZE = 3;
	final static int VON_NEUMANN_SIZE = 5;

	public static void main(String[] args) {
		System.out.println("Hello World!");
		// Test function is set through the command line arguments
		TestFunction.FUNCTION_TO_USE = 0;
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

    public static void createNeighborhood() {

        // int dimensions = 30;
        // double minPos = 16.0;
        // double maxPos = 32.0;
        // double maxSpeed = 4.0;
        // double minSpeed = -2.0;
        // int numParticles = 20;
        // Particle[] allParticles;

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

		// use Ackley
		if (testFunction == TestFunctions.ACKLEY_NUM) {
			for (int i = 0; i < neighborhoods[indexParticle].length; i++) {
				double tempNeighborFitness = Ackley(allParticles[neighborhoods[indexParticle][i]].position);
				if (tempNeighborFitness < curNBest) {
					curNBest = tempNeighborFitness;
				}
			}

			allParticles[indexParticle].setNBest(curNBest);
		}

		// use Ackley
		if (testFunction == TestFunctions.ACKLEY_NUM) {
			for (int i = 0; i < neighborhoods[indexParticle].length; i++) {
				double tempNeighborFitness = (allParticles[neighborhoods[indexParticle][i]].position);
				if (tempNeighborFitness < curNBest) {
					curNBest = tempNeighborFitness;
				}
			}

			allParticles[indexParticle].setNBest(curNBest);
		}

		// use Ackley
		if (testFunction == TestFunctions.ACKLEY_NUM) {
			for (int i = 0; i < neighborhoods[indexParticle].length; i++) {
				double tempNeighborFitness = Ackley(allParticles[neighborhoods[indexParticle][i]].position);
				if (tempNeighborFitness < curNBest) {
					curNBest = tempNeighborFitness;
				}
			}

			allParticles[indexParticle].setNBest(curNBest);
		}

	}


	public static void updatePBest(int index) {
		double curBest = particle.getPBest();
		double potentialBest = testFunction(allParticles[index]);
		// // use Ackley
		// if (testFunction == TestFunctions.ACKLEY_NUM) {
		// 	potentialBest = Ackley(allParticles[index].position);
		// }
		// // use Rosenbrock
		// else if (testFunction == TestFunctions.ROSENBROCK_NUM) {
		// 	potentialBest = Rosenbrock(allParticles[index].position);
		// }
		// // use Rastrigin
		// else if (testFunction == TestFunctions.RASTRIGIN_NUM) {
		// 	potentialBest = Rastrigin(allParticles[index].position);
		// }



		// should never reach this
		// else {
		// 	System.out.println("Error: Didn't calculate the potential best");
		// }


	}



}
