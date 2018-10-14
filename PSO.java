import java.util.*;
import static java.lang.System.out;
// import TestFunctions.*;
public class PSO
{

	final static int RING_SIZE = 3;
	final static int VON_NEUMANN_SIZE = 5;

	static int dimensions = 30;
	static double minPos = 16.0;
	static double maxPos = 32.0;
	static double maxSpeed = 4.0;
	static double minSpeed = -2.0;
	static int numParticles = 20;
	static int numIterations = 100;
	static Particle[] allParticles;
	static boolean firstTimeRandom = true;
	private static Random rand = new Random();
	static int[][] neighborhoods;
	static int numRandNeighbors = 5;

	static double Chi = 0.7298;
	static double[] chiArray;
	static double phi_1 = 2.05;
	static double phi_2 = 2.05;


	public static void main(String[] args)
	{
		/* command line arguments:
			 which neighborhood topology to test (gl, ri, vn, ra)
			 the size of the swarm
			 the number of iterations
			 the function to optimize (rok, ack, ras)
			 the dimensionality of the function
		*/
		String topologyString = "";
		String functionString = "";
		try
		{
			topologyString = args[0];
			numParticles = Integer.parseInt(args[1]);
			numIterations = Integer.parseInt(args[2]);
			functionString = args[3];
			dimensions = Integer.parseInt(args[4]);
		}
		catch(Exception e)
		{
			System.out.println("incorrect arguments.");
			System.exit(1);
		}

		// Test function is set through the command line arguments

		Best bestFound = new Best();

		// RUN PSO CODE
		// initialize data
		createNeighborhood(topologyString, functionString);

		for (int generation = 0; generation < numIterations; generation++)
		{
			for (int k = 0; k < numParticles; k++)
			{
				//update velocity
				updateVelocity(allParticles[k]);
			}
			for (int k = 0; k < numParticles; k++)
			{
				//update position
				updatePosition(allParticles[k]);
			}
			for (int k = 0; k < numParticles; k++)
			{
				//evaluate function at new position
				//update personal best
				if(updatePBest(k) && allParticles[k].getPBest() < bestFound.getFit())
				{
					bestFound.setFit(allParticles[k].getPBest());
					bestFound.setGen(generation);
					bestFound.setPos(allParticles[k].getPBestPosition());
				}
			}

			for (int k = 0; k < numParticles; k++)
			{
				//update neighborhood bests
				updateNBest(k);
			}
		}
		out.print("Time elapsed: ");
		out.println(bestFound.getTimeElapsed());
		out.print("Best found solution value: ");
		out.println(bestFound.getFit());
		out.print("Best found solution position: ");
		for(double k:bestFound.getPos())
		{
			out.printf("%.2f , ", k);
		}
		out.print("\nSolution found at generation: ");
		out.println(bestFound.getGen());
		out.print("Solution found in (milliseconds): ");
		out.println(bestFound.getSolutionTime());
	}

    public static void createNeighborhood(String topologyString, String functionString)
	{
		chiArray = new double[dimensions];
		Arrays.fill(chiArray, Chi);

		if (topologyString.equals("gl")) {
			// if global neighborhood
			neighborhoods = new int[numParticles][numParticles];
		}
		else if (topologyString.equals("ri")) {
			// if ring neighborhood
			neighborhoods = new int[numParticles][RING_SIZE];
		}
		else if (topologyString.equals("vn")) {
			// if von neumann neighborhood
			neighborhoods = new int[numParticles][VON_NEUMANN_SIZE];
		}
		else if (topologyString.equals("ra")) {
			// if random neighborhood
			neighborhoods = new int[numParticles][numRandNeighbors];
		}
		else{
			System.out.println("Topology (first) argument needs to be one of gl, ri, vn, or ra");
		}
		if (functionString.equals("ack")) {
			TestFunctions.FUNCTION_TO_USE = 1;
			minPos = 16.0;
			maxPos = 32.0;
			minSpeed = -32.768;
			maxSpeed = 32.768;
		}
		else if (functionString.equals("rok")) {
			TestFunctions.FUNCTION_TO_USE = 2;
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
		}

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
		for(int k = 0; k < numParticles; k++)
		{
			updateNBest(k);
		}


    }


    // returns array of neighborhood bests for each particle (each will be global best)
    public static double[] findNeighborGlobal()
	{
    	double gBest = allParticles[0].getNBest();
    	for (int i = 0; i < numParticles; i++)
		{
    		double curPBest = allParticles[i].getPBest();
    		if (curPBest < gBest)
			{
    			gBest = curPBest;
    		}
    	}

       	double[] globalBests = new double[numParticles];
    	for (int i = 0; i < numParticles; i++)
		{
			globalBests[i] = gBest;
    	}
    	return globalBests;
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
    		int leftParticle  = (i - 1 + numParticles) % numParticles;
    		int rightParticle = (i + 1) % numParticles;
    		int[] neighbors   = {curParticle, aboveParticle, belowParticle, leftParticle, rightParticle};
    		neighborhoods[i] = neighbors;
    	}
    }

	public static void findNeighborRandom()
	{
		int numNeighbors = numRandNeighbors;
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


	public static void updateNBest(int indexParticle)
	{
		double curNBest = allParticles[indexParticle].getNBest();

		for (int neighborIndex:neighborhoods[indexParticle])
		{
			double tempNeighborFitness = TestFunctions.testFunction(allParticles[neighborIndex]);
			if (tempNeighborFitness < curNBest)
			{
				curNBest = tempNeighborFitness;
			}
		}
		allParticles[indexParticle].setNBest(curNBest);
		allParticles[indexParticle].setNBestPosition(allParticles[indexParticle].position);
	}


	public static boolean updatePBest(int index)
	{
		double curBest = allParticles[index].getPBest();
		double potentialBest = TestFunctions.testFunction(allParticles[index]);
		if (potentialBest < curBest)
		{
			allParticles[index].setPBest(potentialBest);
			allParticles[index].setPBestPosition(allParticles[index].position);
			return true;
		}
		return false;
	}

	public static void updateVelocity(Particle particle)
	{
		double[] U_1 = new double[dimensions];
		double[] U_2 = new double[dimensions];
		for (int i = 0; i < dimensions; i++)
		{
			U_1[i] = phi_1 * rand.nextDouble();
			U_2[i] = phi_2 * rand.nextDouble();
		}

		double[] temp1 = product(U_1, sum(particle.position, negate(particle.pBestPosition)));
		double[] temp2 = product(U_2, sum(particle.position, negate(particle.nBestPosition)));

		double[] updatedVelocity = product(chiArray, sum(particle.velocity, sum(temp1, temp2)));

		particle.velocity = updatedVelocity;
		for(int k = 0; k < particle.velocity.length; k++)
		{
			if(particle.velocity[k] > maxPos)
			{
				particle.velocity[k] = maxPos;
			}
			else if(particle.velocity[k] < minPos)
			{
				particle.velocity[k] = minPos;
			}
		}
	}

	public static void updatePosition(Particle particle)
	{
		particle.position = sum(particle.position,particle.velocity);
		for(int k = 0; k < particle.position.length; k++)
		{
			if(particle.position[k] > maxPos)
			{
				particle.position[k] = maxPos;
			}
			else if(particle.position[k] < minPos)
			{
				particle.position[k] = minPos;
			}
		}
	}

	public static double[] sum(double[] a, double[] b)
	{
		double[] sum = new double[a.length];
		for(int k = 0; k < a.length; k++)
		{
			sum[k] = a[k]+b[k];
		}
		return sum;
	}

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
