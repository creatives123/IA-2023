package genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controllers.NeuralNetworkGameController;
import genetic.Crossover.KPointCrossover;
import genetic.Crossover.OnePointCrossover;
import genetic.Crossover.TwoPointCrossover;
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
        
        normalizeFitness(population);

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

    private void normalizeFitness(List<NeuralNetworkGameController> population) {
        // Find the maximum fitness value in the population
        double maxFitness = Double.NEGATIVE_INFINITY;
        for (NeuralNetworkGameController controller : population) {
            double fitness = controller.getFitness();
            if (fitness > maxFitness) {
                maxFitness = fitness;
            }
        }
    
        // Normalize fitness values between 0 and 1
        for (NeuralNetworkGameController controller : population) {
            double fitness = controller.getFitness();
            double normalizedFitness = fitness / maxFitness;
            controller.setFitness(normalizedFitness);
        }
    }
    
    

    private List<NeuralNetworkGameController> generatePopulation(List<NeuralNetworkGameController> selectedIndividuals) {
        List<NeuralNetworkGameController> newPopulation = new ArrayList<>();
    
        // Perform crossover and mutation on the selected individuals
        for (int i = 0; i < populationSize; i++) {
            // Choose two parents using tournament selection
            NeuralNetworkGameController parent1 = tournamentSelection(selectedIndividuals);
            NeuralNetworkGameController parent2 = tournamentSelection(selectedIndividuals);

            //Scramble Mutation
            //ScrambleMutation scrambleMutation = new ScrambleMutation(1, 2);
            //scrambleMutation.mutate(child);

            NeuralNetworkGameController child = makeCrossover(parent1, parent2);

            // Apply mutation to the child individual
            mutate(child);
    
            // Add the child individual to the new population
            newPopulation.add(child);
        }
    
        return newPopulation;
    }

    //Choose the Crossover
    private NeuralNetworkGameController makeCrossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2){
        
        if(Commons.CROSSOVERTYPE.equals("TWOPOINT")){
            TwoPointCrossover twoPointCrossover = new TwoPointCrossover(parent1, parent2);
            return twoPointCrossover.crossover();
        }
        else if(Commons.CROSSOVERTYPE.equals("KPOINT")){
            KPointCrossover kPointCrossover = new KPointCrossover(parent1, parent2);
            return kPointCrossover.crossover();
        }
        else if(Commons.CROSSOVERTYPE.equals("ONEPOINT")){
            OnePointCrossover onePointCrossover = new OnePointCrossover(parent1, parent2);
            return onePointCrossover.crossover();
        }

        return null;

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