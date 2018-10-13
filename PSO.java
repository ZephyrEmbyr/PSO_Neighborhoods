
public class PSO {

	public static void main(String[] args) {
		System.out.println("Hello World!");
		// TestFunctions.tester();
	}


    public static void createNeighborHood() {
        
        int dimensions = 30;
        double minPos = 16.0;
        double maxPos = 32.0;
        double maxSpeed = 4.0;
        double minSpeed = -2.0;
        int numParticles = 20;
        Particle[] allParticles;

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
    		if (i == 0) {
    			double priorParticle = particles[particles.length-1].getPBest();
    			double curParticle   = particles[i].getPBest();
    			double nextParticle  = particles[i + 1].getPBest();
    		}
    		else if (i == particles.length - 1) {
    			double priorParticle = particles[i - 1].getPBest();
    			double curParticle   = particles[i].getPBest();
    			double nextParticle  = particles[0].getPBest();
    		}
    		else {
    			double priorParticle = particles[i - 1].getPBest();
    			double curParticle   = particles[i].getPBest();
    			double nextParticle  = particles[i + 1].getPBest();
    		}
    		double bestNeighbor = Math.min(Math.min(priorParticle, curParticle), nextParticle);
    		neighborhoodBests[i] = bestNeighbor;
    	}
    	return neighborhoodBests;
    }

    // returns array of neighborhood bests for each particle where the neighborhood is the four
    // closest neighbors orthogonally
    public static double[] findNeighborVonNeumann(Particle[] particles) {
    	numParticles = particles.length;
    	int cols = (int) Math.ceil(Math.sqrt(numParticles));
    	// int rows = (int) numParticles / cols
       	double[] neighborhoodBests = new double[particles.length];
    	for (int i = 0; i < numParticles; i++) {
    		double aboveParticle = particles[((i - cols + numParticles) % numParticles)].getPBest();
    		double belowParticle = particles[((i + cols) % numParticles)].getPBest();
    		double leftParticle  = particles[((i - 1 + numParticles) % numParticles)].getPBest();
    		double rightParticle = particles[((i + 1) % numParticles)].getPBest();
    		double curParticle   = particles[i].getPBest();
    		double[] neighbors   = {aboveParticle, belowParticle, leftParticle, rightParticle, curParticle}
    		neighborhoodBests[i] = getMin(neighbors);
    	}
    	return neighborhoodBests;
    }
    

    public static void findNeighborRandom() {
    }

	
}
