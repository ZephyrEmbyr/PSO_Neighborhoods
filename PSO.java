import java.util.*;
import java.io.*;
import static java.lang.System.out;
// import TestFunctions.*;
public class PSO
{

	final static int RING_SIZE = 3;
	final static int VON_NEUMANN_SIZE = 5;

	static int flag = 1;
	static int usingRandomFlag = 0;
	static int usingGlobalSwarm = 0;

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


	// main function that gets the command line arguments and runs the PSO
	// algorithm for the number of iterations specified. Then, it prints out
	// all of the final information of the program's results
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
		System.out.println("Done Initializing");

		for (int generation = 0; generation < numIterations; generation++)
		{
			//out.println("new generation");
			for (int k = 0; k < numParticles; k++)
			{
				//update velocity
				updateVelocity(allParticles[k]);
			}
			//System.out.println("Velocity updated");
			for (int k = 0; k < numParticles; k++)
			{
				//update position
				updatePosition(allParticles[k]);
			}
			//System.out.println("Position updated");
			for (int k = 0; k < numParticles; k++)
			{
				//evaluate function at new position
				//update personal best
				updatePBest(k);


				//System.out.println("flag: " + flag);
				if(flag == 1 && allParticles[k].getPBest() < bestFound.getFit())
				{

					//out.println("set fit in PSO");
					bestFound.setFit(allParticles[k].getPBest());
					bestFound.setGen(generation+1);
					bestFound.setPos(allParticles[k].getPBestPosition());
					out.println(Arrays.toString(bestFound.getPos()));
				}
			}

			//If random topology used then update neighbors
			if (usingRandomFlag == 1) {
				findNeighborRandom();
				// System.out.println("NEIGHBORHOOD: " + Arrays.deepToString(neighborhoods));
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
				for (int k = 0; k < numParticles; k++)
				{
					//update neighborhood bests
					updateNBest(k);
				}
			}

			if((generation+1)%1000 == 0)
			{
				bestFound.updatePerThousand((generation-999)/1000);
			}

		}
		
		// System.exit(0);

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

		// System.exit(0);

		ArrayList<String> output = new ArrayList<String>();
		output.addAll(Arrays.asList(args));
		output.add(Double.toString(bestFound.getFit()));
		double[] perThousand = bestFound.getFitnessPerThousand();
		for(int k = 0; k < perThousand.length; k++)
			output.add(Double.toString(perThousand[k]));
		output.add(Integer.toString(bestFound.getGen()));
		output.add(Long.toString(bestFound.getSolutionTime()));
		output.add(Long.toString(bestFound.getTimeElapsed()));
		String finalOutput = String.join(",", output);
        finalOutput += "\n";
		out.println(finalOutput);

		System.exit(0);
		appendStringToFile("output.csv",finalOutput);
	}

	// creates a neighborhood of particles depending on the command line arguments,
	// as well as sets the minimum and maximum positions and speeds. Finally, it
	// initializes all of the particles, as well as their personal and neighborhood
	// bests
	public static void createNeighborhood(String topologyString, String functionString)
	{
		chiArray = new double[dimensions];
		Arrays.fill(chiArray, Chi);

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

		//TAKE OUT LATER
		//System.exit(1);  

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


		// System.out.println("NEIGHBORHOOD: " + Arrays.deepToString(neighborhoods));
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

  //   	double gBest = allParticles[0].getNBest();
  //   	for (int i = 0; i < numParticles; i++)
		// {
  //   		double curPBest = allParticles[i].getPBest();
  //   		if (curPBest < gBest)
		// 	{
  //   			gBest = curPBest;
  //   		}
  //   	}

  //      	double[] globalBests = new double[numParticles];
  //   	for (int i = 0; i < numParticles; i++)
		// {
		// 	globalBests[i] = gBest;
  //   	}
  //   	return globalBests;
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
			// need last val
			// if (i - 1 < 0) {
			// 	leftParticle = numParticles - 1;
			// }
			// need last in col
			if (i % cols == 0) {
				leftParticle = i + cols - 1;
			}
			// everything else
			else {
				leftParticle = i - 1;
			}
			// need first val
			// if (i + 1 == numParticles) {
			// 	rightParticle = 0;
			// }
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
			//Particle[] particles = (Particle[]) allParticles.clone();
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
		// System.out.println("curNBest: " + curNBest);
		double[] curNBestPosition = allParticles[indexParticle].nBestPosition;

		for (int neighborIndex:neighborhoods[indexParticle])
		{
			// double tempNeighborFitness = TestFunctions.testFunction(allParticles[neighborIndex]);
			double tempNeighborFitness = allParticles[neighborIndex].getPBest();
			// System.out.println("tempNeighborFitness: " + tempNeighborFitness);
			if (tempNeighborFitness < curNBest)
			{
				// out.println("updated neighbor fitness at index " + neighborIndex);
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
			//System.out.println("pbest position array");
			for (int i = 0; i < testDummy.length; i++ ) {
				//System.out.print(testDummy[i] + " ");
			}
			//System.out.println("end");

			//System.out.println("pbest velocity array");
			for (int i = 0; i < testDummyVel.length; i++ ) {
				//System.out.print(testDummyVel[i] + " ");
			}
			//System.out.println("end");

			//System.out.println("curBest: " + curBest);
			//System.out.println("potentialBest: " + potentialBest);
		}
		if(potentialBest < curBest)
		{
			// out.println("a pbest was updated");
			// out.println(potentialBest);
			// out.println(curBest);
			allParticles[index].setPBest(potentialBest);
			// out.println(allParticles[index].getPBest());
			allParticles[index].setPBestPosition(allParticles[index].position);

			// if (index == 0) {
			// 	// System.out.println("UPDATED!");
			// 	// System.out.println("new pbest: " + allParticles[index].getPBest());
			// 	// System.out.println("new pbest position: " + allParticles[index].position[0] + allParticles[index].position[1]);
			// 	// System.out.println();
			// }
			flag = 1;
		}
		else
		{
			// if (index == 0) {
			// 	//System.out.println("NOT UPDATED!");
			// 	//System.out.println();
			// }
			flag = 0;
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

		//System.out.println("U_1: " + U_1[0] + " " + U_1[1]);
		//System.out.println("U_2: " + U_2[0] + " " + U_2[1]);
		//System.out.println("pBestPosition: " + particle.pBestPosition[0] + " " + particle.pBestPosition[1]);
		//System.out.println("position: " + particle.position[0] + " " + particle.position[1]);

		double[] temp1 = product(U_1, sum(particle.pBestPosition, negate(particle.position)));
		// System.out.println("temp1: " + temp1[0] + " " + temp1[1]);
		//out.println(Arrays.toString(particle.pBestPosition) + " and " + Arrays.toString(particle.position));
		//out.println("additional printouts above");
		double[] temp2 = product(U_2, sum(particle.nBestPosition, negate(particle.position)));
		// System.out.println("temp2: " + temp2[0] + " " + temp2[1]);

		double[] updatedVelocity = product(chiArray, sum(particle.velocity, sum(temp1, temp2)));
		//System.out.println("updatedVelocity: " + updatedVelocity[0] + " " + updatedVelocity[1]);

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

    public static void appendStringToFile(String fileName, 
                                       String str) 
    { 
        try { 
            // Open given file in append mode. 
            BufferedWriter out = new BufferedWriter( 
                   new FileWriter(fileName, true)); 
            out.write(str); 
            out.close(); 
        } 
        catch (IOException e) { 
            System.out.println("exception occurred" + e); 
        } 
    } 


}
