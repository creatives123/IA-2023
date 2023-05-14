package genetic.Crossover;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import controllers.NeuralNetworkGameController;

public class KPointCrossover {
    private NeuralNetworkGameController parent1;
    private NeuralNetworkGameController parent2;
    private Random random;

    public KPointCrossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2){
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public NeuralNetworkGameController crossover() {
        int k = 4; // Number of crossover points
    
        double[] chromosome1 = parent1.getChromossome();
        double[] chromosome2 = parent2.getChromossome();
        int chromosomeLength = chromosome1.length;
    
        // Generate k random crossover points
        List<Integer> crossoverPoints = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            int crossoverPoint = random.nextInt(chromosomeLength + 1);
            crossoverPoints.add(crossoverPoint);
        }
        crossoverPoints.sort(Comparator.naturalOrder());
    
        // Perform k-point crossover
        double[] childChromosome = new double[chromosomeLength];
        int currentIndex = 0;
        int parentIndex = 0;
        for (int crossoverPoint : crossoverPoints) {
            int segmentLength = crossoverPoint - currentIndex;
            if (parentIndex % 2 == 0) {
                System.arraycopy(chromosome1, currentIndex, childChromosome, currentIndex, segmentLength);
            } else {
                System.arraycopy(chromosome2, currentIndex, childChromosome, currentIndex, segmentLength);
            }
            currentIndex = crossoverPoint;
            parentIndex++;
        }
    
        NeuralNetworkGameController child = new NeuralNetworkGameController();
        child.setChromossome(childChromosome);
        return child;
    } 

}
