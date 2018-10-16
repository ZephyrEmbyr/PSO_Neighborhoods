import java.util.*;

class Best
{
    // Best class stores the best solution and its various properties
    // Stores generation, position, fitness, time
    int generation = 0;
    double[] position;
    double fitness;
    long startTime;
    long endTime;
    double[] fitnessPerThousand;

    // constructs a Best object using specified values
    public Best(int gen, double[] pos, double fit, int iter)
    {
        generation = gen;
        position = pos;
        fitness = fit;
        startTime = System.currentTimeMillis();
        endTime = startTime;
        fitnessPerThousand = new double[(int)((iter+1)/1000)];
        System.out.println("initialized fitness Per Thousand");
    }

    // no-argument constructor
    public Best()
    {
        generation = 0;
        fitness = Double.MAX_VALUE;
        startTime = System.currentTimeMillis();
        endTime = startTime;
        fitnessPerThousand = new double[10];
        System.out.println("initialized fitness Per Thousand");
    }

    public void updatePerThousand(int index)
    {
        fitnessPerThousand[index] = fitness;
    }

    // getters and setters
    public double[] getPos()
    {
        return position;
    }

    public int getGen() {
        return generation;
    }

    public void setGen(int gen)
    {
        generation = gen;
        endTime = System.currentTimeMillis();
    }

    public void setPos(double[] pos)
    {
        position = pos.clone();
        endTime = System.currentTimeMillis();
    }

    public void setFit(double fit)
    {
        // System.out.println("set fit");
        // System.out.println(fit);
        fitness = fit;
        // System.out.println(fitness);
        endTime = System.currentTimeMillis();
    }

    public double getFit()
    {
        // System.out.println("get fit");
        // System.out.println(fitness);
        return fitness;
    }

    public long getTimeElapsed()
    {
        long diff = System.currentTimeMillis()-startTime;
        return diff;
    }

    public long getSolutionTime()
    {
        long diff = endTime-startTime;
        return diff;
    }

    public double[] getFitnessPerThousand()
    {
        return fitnessPerThousand;
    }
}
