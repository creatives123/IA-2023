package genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controllers.NeuralNetworkGameController;
import space.Board;

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
        // Evaluate fitness of each individual in population
        int i = 0;
        for (NeuralNetworkGameController controller : population) {
            double fitness = evaluateFitness(controller);
            controller.setFitness(fitness);
            System.out.println("Generation: " + (++i));
            System.out.println("Best Fitness: " + controller.getFitness());
            System.out.println("------------");
        }

        // Select the best-performing individuals
        List<NeuralNetworkGameController> selectedIndividuals = selectIndividuals();

        // Generate a new population through crossover and mutation
        List<NeuralNetworkGameController> newPopulation = generatePopulation(selectedIndividuals);

        // Replace the current population with the new population
        this.population = newPopulation;
             
    }

    

    public double evaluateFitness(NeuralNetworkGameController controller) {
        // Create a new instance of the Board class
        Board board = new Board(controller);
    
        // Run the board
        board.run();
    
        // Return the fitness score achieved by the controller
        return board.getFitness();
    }
    

    private List<NeuralNetworkGameController> selectIndividuals() {
        // Perform tournament selection to select the best individuals
        List<NeuralNetworkGameController> selectedIndividuals = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            // Choose two random individuals from the population
            NeuralNetworkGameController individual1 = population.get(random.nextInt(populationSize));
            NeuralNetworkGameController individual2 = population.get(random.nextInt(populationSize));
            individual1.getFitness();
            // Add the fitter of the two individuals to the selected individuals list
            if (individual1.getFitness() > individual2.getFitness()) {
                selectedIndividuals.add(individual1);
            } else {
                selectedIndividuals.add(individual2);
            }
        }
        return selectedIndividuals;
    }

    private List<NeuralNetworkGameController> generatePopulation(List<NeuralNetworkGameController> selectedIndividuals) {
        List<NeuralNetworkGameController> newPopulation = new ArrayList<>();

        // Perform crossover and mutation on the selected individuals
        for (int i = 0; i < populationSize; i++) {
            // Choose two random individuals from the selected individuals list
            NeuralNetworkGameController parent1 = selectedIndividuals.get(random.nextInt(selectedIndividuals.size()));
            NeuralNetworkGameController parent2 = selectedIndividuals.get(random.nextInt(selectedIndividuals.size()));
            // Perform crossover to create a new child individual
            NeuralNetworkGameController child = crossover(parent1, parent2);

            // Apply mutation to the child individual
            mutate(child);

            // Add the child individual to the new population
            newPopulation.add(child);
        }

        return newPopulation;
    }

    public void mutate(NeuralNetworkGameController child) {
        double mutationRate = 0.1; //TODO: Alterar
        double[] chromosome = child.getChromossome();
        for (int i = 0; i < chromosome.length; i++) {
            if (random.nextDouble() < mutationRate) {
                chromosome[i] += random.nextGaussian();
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

    //Two-Point Crossover
    private NeuralNetworkGameController twoPointCrossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2) {
        double[] chromossome1 = parent1.getChromossome();
        double[] chromossome2 = parent2.getChromossome();
        int chromossomeLength = chromossome1.length;
    
        int crossoverPoint1 = random.nextInt(chromossomeLength);
        int crossoverPoint2 = random.nextInt(chromossomeLength);
        int startPoint = Math.min(crossoverPoint1, crossoverPoint2);
        int endPoint = Math.max(crossoverPoint1, crossoverPoint2);
    
        double[] childChromossome = new double[chromossomeLength];
        for (int i = 0; i < chromossomeLength; i++) {
            if (i < startPoint || i > endPoint) {
                childChromossome[i] = chromossome1[i];
            } else {
                childChromossome[i] = chromossome2[i];
            }
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