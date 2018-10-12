public class PSO {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		TestFunctions.tester();
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

    public static void findNeighborGlobal() {
    }

    public static void findNeighborRing() {
    }

    public static void findNeighborVonNeumann() {
    }

    public static void findNeighborRandom() {
    }

	
}
