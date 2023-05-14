package genetic.Crossover;

import java.util.Random;

import controllers.NeuralNetworkGameController;

public class TwoPointCrossover {
    private Random random = new Random();
    private NeuralNetworkGameController parent1;
    private NeuralNetworkGameController parent2;
    
    public TwoPointCrossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2){
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public NeuralNetworkGameController crossover() {
        // Perform two-point crossover to create a new child individual
        double[] chromosome1 = parent1.getChromossome();
        double[] chromosome2 = parent2.getChromossome();
        int length = chromosome1.length;
    
        // Select two random crossover points
        int crossoverPoint1 = random.nextInt(length);
        int crossoverPoint2 = random.nextInt(length);
    
        // Ensure crossover points are distinct
        while (crossoverPoint1 == crossoverPoint2) {
            crossoverPoint2 = random.nextInt(length);
        }
    
        // Swap genetic material between parents within the crossover points
        int startPoint = Math.min(crossoverPoint1, crossoverPoint2);
        int endPoint = Math.max(crossoverPoint1, crossoverPoint2);
        double[] childChromosome = new double[length];
        System.arraycopy(chromosome1, 0, childChromosome, 0, startPoint);
        System.arraycopy(chromosome2, startPoint, childChromosome, startPoint, endPoint - startPoint);
        System.arraycopy(chromosome1, endPoint, childChromosome, endPoint, length - endPoint);
    
        NeuralNetworkGameController child = new NeuralNetworkGameController();
        child.setChromossome(childChromosome);
        return child;
    }
}
