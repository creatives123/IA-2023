package genetic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import controllers.NeuralNetworkGameController;
import genetic.Crossover.KPointCrossover;
import genetic.Crossover.OnePointCrossover;
import genetic.Crossover.TwoPointCrossover;
import genetic.Mutation.GaussianMutation;
import genetic.Mutation.ScrambleMutation;
import genetic.Selection.StochasticUniversalSampling;
import genetic.Selection.TournamentSelection;
import space.Commons;

import java.util.concurrent.CountDownLatch;

public class GeneticAlgorithm {
    private int populationSize;
    private List<NeuralNetworkGameController> population;
    private Random random;
    private int generation = 1;

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
        printBestFitness(population, "teste", generation);
        generation ++;
        
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
    
    public void printBestFitness(List<NeuralNetworkGameController> population, String filename, int generation) {
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (NeuralNetworkGameController controller : population) {
            double fitness = controller.getFitness();
            if (fitness > bestFitness) {
                bestFitness = fitness;
            }
        }
        
        // Get the current timestamp
        LocalDateTime timestamp = LocalDateTime.now();
        // Format the timestamp to include milliseconds
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedTimestamp = timestamp.format(formatter);
        
        String data = (generation) + "," + formattedTimestamp + "," + bestFitness;
        
        File file = new File(filename);
        boolean fileExists = file.exists();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (!fileExists) {
                // If the file doesn't exist, write the headings
                writer.write("Generation,Time,Fitness");
                writer.newLine();
            }
            
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            List<NeuralNetworkGameController> parentlist = makeSelection(selectedIndividuals);

            // Where the Magic Happens
            NeuralNetworkGameController child = makeCrossover(parentlist.get(0), parentlist.get(1));
    
            // Add the child individual to the new population
            newPopulation.add(makeMutation(child));
        }
        return newPopulation;
    }

    //Choose the Individuals
    private List<NeuralNetworkGameController> makeSelection(List<NeuralNetworkGameController> selectedIndividual){

        if(Commons.SELECTIONTYPE.equals("TOURNAMENT")){
            TournamentSelection tournamentSelection = new TournamentSelection(selectedIndividual);
            return tournamentSelection.selection(2);
        }
        else if(Commons.SELECTIONTYPE.equals("SUS")){
            StochasticUniversalSampling stochasticUniversalSampling = new StochasticUniversalSampling(selectedIndividual);
            return stochasticUniversalSampling.selection(2);
        }
        return Collections.emptyList();
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

    //Choose the Mutation
    private NeuralNetworkGameController makeMutation(NeuralNetworkGameController child){

        if(Commons.MUTATIONTYPE.equals("GAUSSIAN")){
            GaussianMutation gaussianMutation = new GaussianMutation(child);
            return gaussianMutation.mutate();
        }
        else if (Commons.MUTATIONTYPE.equals("SCRAMBLE")){
            ScrambleMutation scrambleMutation = new ScrambleMutation(1, 2, child);
            return scrambleMutation.mutate();
        }
        return null;
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