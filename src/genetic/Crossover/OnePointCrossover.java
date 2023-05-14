package genetic.Crossover;

import java.util.Random;

import controllers.NeuralNetworkGameController;

public class OnePointCrossover {
    private NeuralNetworkGameController parent1;
    private NeuralNetworkGameController parent2;
    private Random random = new Random();

    public OnePointCrossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2){
        this.parent1 = parent1;
        this.parent2 = parent2;        
    }


    //Single-Point Crossover
    public NeuralNetworkGameController crossover() {
        // Perform single-point crossover to create a new child individual
        double[] chromossome1 = parent1.getChromossome();
        double[] chromossome2 = parent2.getChromossome();
        int crossoverPoint = random.nextInt(chromossome1.length);
        double[] childChromossome = new double[chromossome1.length];
        for (int i = 0; i < crossoverPoint; i++) {
            childChromossome[i] = chromossome1[i];
        }
        for (int i = crossoverPoint; i < chromossome1.length; i++) {
            childChromossome[i] = chromossome2[i];
        }
        NeuralNetworkGameController child = new NeuralNetworkGameController();
        child.setChromossome(childChromossome);
        return child;
    }  
    
}
