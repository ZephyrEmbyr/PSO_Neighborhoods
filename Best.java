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
        position = new double[50];
        fitness = 100;
        startTime = System.currentTimeMillis();
        endTime = startTime;
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
