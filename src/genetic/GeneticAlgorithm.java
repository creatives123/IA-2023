package genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controllers.NeuralNetworkGameController;
import genetic.Mutation.ScrambleMutation;

import java.util.concurrent.CountDownLatch;

public class GeneticAlgorithm {
    private int populationSize;
    private List<NeuralNetworkGameController> population;
    private Random random;

    public GeneticAlgorithm(int populationSize) {
        this.populationSize = populationSize;
        this.random = new Random();
        this.population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            NeuralNetworkGameController controller = new NeuralNetworkGameController();
            population.add(controller);
        }
    }

    public void evolve() {
        playGame();

        //Print the Best Fitness of a give Population
        printBestFitness(population);

        // Generate a new population through crossover and mutation
        List<NeuralNetworkGameController> newPopulation = generatePopulation(population);
    
        // Replace the current population with the new population
        this.population = newPopulation;
    }

    public void playGame() {
        CountDownLatch latch = new CountDownLatch(populationSize);

        for (NeuralNetworkGameController controller : population) {
            EvaluationThread evaluationThread = new EvaluationThread(controller, latch);
            evaluationThread.start();
        }
    
        try {
            // Wait for all threads to finish
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void printBestFitness(List<NeuralNetworkGameController> population) {
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (NeuralNetworkGameController controller : population) {
            double fitness = controller.getFitness();
            if (fitness > bestFitness) {
                bestFitness = fitness;
            }
        }
        System.out.println("Best Fitness: " + bestFitness);
    }
    

    private List<NeuralNetworkGameController> generatePopulation(List<NeuralNetworkGameController> selectedIndividuals) {
        List<NeuralNetworkGameController> newPopulation = new ArrayList<>();
    
        // Perform crossover and mutation on the selected individuals
        for (int i = 0; i < populationSize; i++) {
            // Choose two parents using tournament selection
            NeuralNetworkGameController parent1 = tournamentSelection(selectedIndividuals);
            NeuralNetworkGameController parent2 = tournamentSelection(selectedIndividuals);
    
            // Perform crossover to create a new child individual
            NeuralNetworkGameController child = crossover(parent1, parent2);
    

            // Create an instance of ScrambleMutation with the desired mutation rate and scramble size
            //ScrambleMutation scrambleMutation = new ScrambleMutation(1, 2);

            // Apply mutation to the child individual
            //scrambleMutation.mutate(child);


            // Apply mutation to the child individual
            mutate(child);
    
            // Add the child individual to the new population
            newPopulation.add(child);
        }
    
        return newPopulation;
    }
    
    private NeuralNetworkGameController tournamentSelection(List<NeuralNetworkGameController> individuals) {
        int tournamentSize = 30; // Number of individuals participating in each tournament
        int numTournaments = 10; // Number of tournaments to be conducted
    
        NeuralNetworkGameController bestIndividual = null;
        double bestFitness = Double.NEGATIVE_INFINITY;
    
        for (int i = 0; i < numTournaments; i++) {
            // Randomly select individuals for the tournament
            List<NeuralNetworkGameController> tournamentParticipants = new ArrayList<>();
            for (int j = 0; j < tournamentSize; j++) {
                int randomIndex = random.nextInt(individuals.size());
                tournamentParticipants.add(individuals.get(randomIndex));
            }
    
            // Find the fittest individual in the tournament
            for (NeuralNetworkGameController participant : tournamentParticipants) {
                double fitness = participant.getFitness();
                if (fitness > bestFitness) {
                    bestFitness = fitness;
                    bestIndividual = participant;
                }
            }
        }
    
        return bestIndividual;
    }
    
    public void mutate(NeuralNetworkGameController child) {
        double mutationRate = 0.1; // Mutation rate
        double mutationRange = 0.05; // Range for the random value
    
        double[] chromosome = child.getChromossome();
        for (int i = 0; i < chromosome.length; i++) {
            if (random.nextDouble() < mutationRate) {
                double randomValue = random.nextGaussian() * mutationRange;
                chromosome[i] += randomValue;
            }
        }
        child.setChromossome(chromosome);
    }
    
    //Single-Point Crossover
    private NeuralNetworkGameController crossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2) {
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

    public double getBestFitness() {
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (NeuralNetworkGameController controller : population) {
            double fitness = controller.getFitness();
            if (fitness > bestFitness) {
                bestFitness = fitness;
            }
        }
        return bestFitness;
    }

    public NeuralNetworkGameController getBestIndividual() {
        NeuralNetworkGameController bestIndividual = null;
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (NeuralNetworkGameController controller : population) {
            double fitness = controller.getFitness();
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestIndividual = controller;
            }
        }
        return bestIndividual;
    }

}