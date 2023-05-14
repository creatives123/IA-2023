package main;

import java.util.concurrent.TimeUnit;

import controllers.NeuralNetworkGameController;

import genetic.GeneticAlgorithm;

import space.Commons;
import space.SpaceInvaders;

public class PlayRandomController {
    public static void main(String[] args) {
        int populationSize = Commons.POPSIZE;

        // Capture the start timestamp
        long startTime = System.currentTimeMillis();

        // Create a GeneticAlgorithm instance with the specified population size
        GeneticAlgorithm ga = new GeneticAlgorithm(populationSize);

        // Evolve the population for a certain number of generations using multiple threads
        int numGenerations = Commons.GENSIZE;
        for (int i = 0; i < numGenerations; i++) {
            ga.evolve();
        }

        // Play the Game with the final Population
        ga.playGame();

        // Retrieve the best individual from the final population
        NeuralNetworkGameController bestIndividual = ga.getBestIndividual();

        // Capture the end timestamp
        long endTime = System.currentTimeMillis();

        System.out.println("Best Individual Fitness: " + bestIndividual.getFitness());

        // Calculate the total runtime in milliseconds
        long runtimeMillis = endTime - startTime;

        // Convert the runtime to minutes and seconds format
        long minutes = TimeUnit.MILLISECONDS.toMinutes(runtimeMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(runtimeMillis) % 60;

        System.out.printf("Total Runtime: %d:%02d\n", minutes, seconds);

        // Play the game with the best individual
        SpaceInvaders.showControllerPlaying(bestIndividual, Commons.SEED);
    }
}
