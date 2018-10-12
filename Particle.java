

class Particle {

	public double[] position;
	public double[] velocity;
	public double pBest;
	private Random rand = new Random();

	double MIN_INIT_SPEED = -3.0;
	double MAX_INIT_SPEED = 3.0;

	Particle(int dimensions, double minPos, double maxPos, double minSpeed, double maxSpeed) {

		position = new double(dimensions);
		velocity = new double(dimensions);
		pBest = Double.MAX_VALUE;

		for (int i = 0; i < dimensions; i++) {
			position[i] = minPos * rand.nextDouble() + (maxPos - minPos);
			velocity[i] = minSpeed * rand.nextDouble() + (maxSpeed - minSpeed);
		}

	}


}

