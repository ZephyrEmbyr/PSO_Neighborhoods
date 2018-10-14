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

    // constructs a Best object using specified values
    public Best(int gen, double[] pos, double fit)
    {
        generation = gen;
        position = pos;
        fitness = fit;
        startTime = System.currentTimeMillis();
        endTime = startTime;
    }

    // no-argument constructor
    public Best()
    {
        generation = 0;
        fitness = Double.MAX_VALUE;
        startTime = System.currentTimeMillis();
        endTime = startTime;
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
        position = pos;
        endTime = System.currentTimeMillis();
    }

    public void setFit(double fit)
    {
        fitness = fit;
        endTime = System.currentTimeMillis();
    }

    public double getFit()
    {
        // return fitness;
        return Double.MAX_VALUE;
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
}
