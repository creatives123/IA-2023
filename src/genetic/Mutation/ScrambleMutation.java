package genetic.Mutation;

import java.util.Random;

import controllers.NeuralNetworkGameController;

public class ScrambleMutation {
    private double mutationRate;
    private int scrambleSize;
    private Random random;

    public ScrambleMutation(double mutationRate, int scrambleSize) {
        this.mutationRate = mutationRate;
        this.scrambleSize = scrambleSize;
        this.random = new Random();
    }

    public void mutate(NeuralNetworkGameController child) {
        double[] chromosome = child.getChromossome();
        for (int i = 0; i < chromosome.length; i++) {
            if (random.nextDouble() < mutationRate) {
                int startIndex = random.nextInt(chromosome.length - scrambleSize + 1);
                int endIndex = startIndex + scrambleSize;
                scrambleGenes(chromosome, startIndex, endIndex);
            }
        }
        child.setChromossome(chromosome);
    }

    private void scrambleGenes(double[] chromosome, int startIndex, int endIndex) {
        int length = endIndex - startIndex;
        double[] genes = new double[length];
        for (int i = startIndex; i < endIndex; i++) {
            genes[i - startIndex] = chromosome[i];
        }
        shuffleArray(genes);
        for (int i = startIndex; i < endIndex; i++) {
            chromosome[i] = genes[i - startIndex];
        }
    }

    private void shuffleArray(double[] array) {
        int n = array.length;
        for (int i = 0; i < n; i++) {
            int j = i + random.nextInt(n - i);
            swap(array, i, j);
        }
    }

    private void swap(double[] array, int i, int j) {
        double temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
