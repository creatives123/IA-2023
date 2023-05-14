package genetic.Selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controllers.NeuralNetworkGameController;

public class StochasticUniversalSampling {
    private Random random = new Random();
    private List<NeuralNetworkGameController> individuals;

    public StochasticUniversalSampling(List<NeuralNetworkGameController> individuals) {
        this.individuals = individuals;
    }

    public List<NeuralNetworkGameController> selection(int numIndividuals) {
        List<NeuralNetworkGameController> selectedIndividuals = new ArrayList<>();

        // Calculate the total fitness of the population
        double totalFitness = 0;
        for (NeuralNetworkGameController individual : individuals) {
            totalFitness += individual.getFitness();
        }

        // Calculate the spacing between pointers
        double spacing = totalFitness / numIndividuals;

        // Generate random starting point
        double start = random.nextDouble() * spacing;

        // Perform Stochastic Universal Sampling
        double sum = 0;
        int currentIndex = 0;
        for (int i = 0; i < numIndividuals; i++) {
            double pointer = start + i * spacing;

            while (sum < pointer) {
                sum += individuals.get(currentIndex).getFitness();
                currentIndex = (currentIndex + 1) % individuals.size();
            }

            selectedIndividuals.add(individuals.get(currentIndex));
        }

        return selectedIndividuals;
    }
}
