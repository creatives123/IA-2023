package main;

import controllers.NeuralNetworkGameController;
import genetic.GeneticAlgorithm;
import space.SpaceInvaders;

// public class PlayRandomController {
// 	public static void main(String[] args) {
// 		GameController c = new NeuralNetworkGameController();
// 		SpaceInvaders.hiddenControllerPlaying(c,5);
// 	}
// }


public class PlayRandomController {
    public static void main(String[] args) {
        int populationSize = 50;
  

        // Create a GeneticAlgorithm instance with the specified population size
        GeneticAlgorithm ga = new GeneticAlgorithm(populationSize);

        // Create a thread pool with the specified number of threads


        // Evolve the population for a certain number of generations using multiple threads
        int numGenerations = 100;
        for (int i = 0; i < numGenerations; i++) {
                ga.evolve();
        }


        // Retrieve the best individual from the final population
        NeuralNetworkGameController bestIndividual = ga.getBestIndividual();

        System.out.println("Best Individual Fitness: " + bestIndividual.getFitness());

        // Play the game with the best individual
        SpaceInvaders.showControllerPlaying(bestIndividual, 5);
    }
}





