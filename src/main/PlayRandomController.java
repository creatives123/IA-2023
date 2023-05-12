package main;

import java.util.List;
import java.util.Random;

import controllers.GameController;
import controllers.NeuralNetworkGameController;
import controllers.RandomController;
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
        int populationSize = 10;

        // Create a GeneticAlgorithm instance with the specified population size
        GeneticAlgorithm ga = new GeneticAlgorithm(populationSize);

        // Evolve the population for a certain number of generations
        int numGenerations = 100;
        for (int i = 0; i < numGenerations; i++) {
            ga.evolve();
        }

        // Retrieve the best individual from the final population
        NeuralNetworkGameController bestIndividual = ga.getBestIndividual();

        // Evaluate the fitness of the best individual
        double bestFitness = ga.evaluateFitness(bestIndividual);
        System.out.println("Best Individual Fitness: " + bestFitness);

        // Play the game with the best individual
        SpaceInvaders.showControllerPlaying(bestIndividual, 5);
    }
}




