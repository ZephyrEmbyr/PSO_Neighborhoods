
public class PSO {

	public static void main(String[] args) {
		System.out.println("Hello World!");
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
	private Random rand = new Random();
	static int[][] neighborhoods;
	static int numRandNeighbors = (int)numParticles/2;

    public static void createNeighborHood() {

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

    // returns minimum value in an array of doubles
    public static double getMin(double[] values) {
    	double curMin = values[0];
    	for (int i = 1; i < values.length; i++) {
    		if (values[i] < curMin) {
    			curMin = values[i];
    		}
    	}
    	return curMin;
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
    public static double[] findNeighborRing(Particle[] particles) {
       	double[] neighborhoodBests = new double[particles.length];
    	for (int i = 0; i < particles.length; i++) {
			double priorParticle, curParticle, nextParticle;
    		if (i == 0) {
    			priorParticle = particles[particles.length-1].getPBest();
    			curParticle   = particles[i].getPBest();
    			nextParticle  = particles[i + 1].getPBest();
    		}
    		else if (i == particles.length - 1) {
    			priorParticle = particles[i - 1].getPBest();
    			curParticle   = particles[i].getPBest();
    			nextParticle  = particles[0].getPBest();
    		}
    		else {
    			priorParticle = particles[i - 1].getPBest();
    			curParticle   = particles[i].getPBest();
    			nextParticle  = particles[i + 1].getPBest();
    		}
    		double bestNeighbor = Math.min(Math.min(priorParticle, curParticle), nextParticle);
    		neighborhoodBests[i] = bestNeighbor;
    	}
    	return neighborhoodBests;
    }

    // returns array of neighborhood bests for each particle where the neighborhood is the four
    // closest neighbors orthogonally
    public static double[] findNeighborVonNeumann(Particle[] particles) {
    	// int numParticles = particles.length;
    	int cols = (int) Math.ceil(Math.sqrt(numParticles));
    	// int rows = (int) numParticles / cols
       	double[] neighborhoodBests = new double[particles.length];
    	for (int i = 0; i < numParticles; i++) {
    		double aboveParticle = particles[((i - cols + numParticles) % numParticles)].getPBest();
    		double belowParticle = particles[((i + cols) % numParticles)].getPBest();
    		double leftParticle  = particles[((i - 1 + numParticles) % numParticles)].getPBest();
    		double rightParticle = particles[((i + 1) % numParticles)].getPBest();
    		double curParticle   = particles[i].getPBest();
    		double[] neighbors   = {aboveParticle, belowParticle, leftParticle, rightParticle, curParticle};
    		neighborhoodBests[i] = getMin(neighbors);
    	}
    	return neighborhoodBests;
    }


    public static void findNeighborRandom()
	{
		int numNeighbors = numRandNeighbors;
		neighborhoods = new int[numParticles][numNeighbors]
		if(firstTimeRandom || rand.nextDouble() < 0.2)
		{
			firstTimeRandom = false;
			Particle[] particles = allParticles.clone();
			List<Integer> solution = new ArrayList<>();
			for(int i = 0; i < numParticles; i++)
			{
				solution.add(i);
			}
			Collections.shuffle(solution);

			for(int j = 0; j < numParticles.length; j++)
			{
				List<Integer> solutionCopy = solution.copy();
				solutionCopy.remove(new Integer(j));
				for(int k = 0; k < numNeighbors-1; k++)
				{
					neighborhoods[j][k] = solutionCopy.get(k);

				}
				neighborhoods[j][numNeighbors-1] = j;
			}
		}
    }


}
