import java.util.*;
import java.io.*;
import static java.lang.System.out;
// import TestFunctions.*;
public class PSO
{
	
	static int numIterations = 100;

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
		int numParticles = 0;
		String functionString = "";
		int dimensions = 0;
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
		Swarm swarm = new Swarm(topologyString, numParticles, functionString, dimensions);
		// createNeighborhood(topologyString, functionString);
		System.out.println("Done Initializing");

		for (int generation = 0; generation < numIterations; generation++)
		{
			for (int k = 0; k < swarm.getNumParticles(); k++)
			{
				//update velocity
				swarm.updateVelocity(swarm.getParticle(k));
			}
			for (int k = 0; k < swarm.getNumParticles(); k++)
			{
				//update position
				swarm.updatePosition(swarm.getParticle(k));
			}
			for (int k = 0; k < swarm.getNumParticles(); k++)
			{
				//evaluate function at new position
				//update personal best
				swarm.updatePBest(k);

				if(swarm.updatedPBest() == 1 && swarm.getParticle(k).getPBest() < bestFound.getFit())
				{

					bestFound.setFit(swarm.getParticle(k).getPBest());
					bestFound.setGen(generation+1);
					bestFound.setPos(swarm.getParticle(k).getPBestPosition());
				}
			}

			//If random topology used then update neighbors
			if (swarm.usingRandomFlag() == 1) {
				swarm.findNeighborRandom();
			}

			if (swarm.usingGlobalSwarm() == 1) 
			{
				swarm.updateNBest(0);
				double globalBest = swarm.getParticle(0).getNBest();
				for(int m = 0; m < swarm.getNumParticles(); m++) {
					swarm.getParticle(m).setNBest(globalBest);
				}
			}

			else if (swarm.usingGlobalSwarm() == 0) 
			{
				for (int k = 0; k < swarm.getNumParticles(); k++)
				{
					//update neighborhood bests
					swarm.updateNBest(k);
				}
			}

			if((generation+1)%1000 == 0)
			{
				bestFound.updatePerThousand((generation-999)/1000);
			}

		}
		
		// This provides simple outputs to command line
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


		// This lets us preprocess and output our data to a .csv file
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

		appendStringToFile("output.csv",finalOutput);
	}

    public static void appendStringToFile(String fileName, String str) 
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
