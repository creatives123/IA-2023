package genetic.Mutation;

import java.util.Random;

import controllers.NeuralNetworkGameController;
import space.Commons;

public class GaussianMutation {
    private Random random = new Random();
    private NeuralNetworkGameController child;

    public GaussianMutation(NeuralNetworkGameController child){
        this.child = child;
    }

    public NeuralNetworkGameController mutate() {
        double mutationRate = Commons.MUTRATE; // Mutation rate
        double mutationRange = Commons.MUTRANGE; // Range for the random value
    
        double[] chromosome = child.getChromossome();
        for (int i = 0; i < chromosome.length; i++) {
            if (random.nextDouble() < mutationRate) {
                double randomValue = random.nextGaussian() * mutationRange;
                chromosome[i] += randomValue;
            }
        }
        child.setChromossome(chromosome);
        return child;
    }
    
}
