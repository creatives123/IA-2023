package genetic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import controllers.NeuralNetworkGameController;
import genetic.Mutation.ScrambleMutation;
import space.Commons;

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
            EvaluationThread evaluationThread = new EvaluationThread(controller, latch, Commons.SEED);
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
            NeuralNetworkGameController child = twoPointCrossover(parent1, parent2);
    

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
        int tournamentSize = Commons.TOURNSIZE; // Number of individuals participating in each tournament
        int numTournaments = Commons.NUMTOURNSIZE; // Number of tournaments to be conducted
    
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

    private NeuralNetworkGameController kcrossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2) {
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
    

    private NeuralNetworkGameController twoPointCrossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2) {
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