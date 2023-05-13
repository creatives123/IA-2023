package genetic;

import controllers.NeuralNetworkGameController;
import space.Board;

import java.util.Random;
import java.util.concurrent.CountDownLatch;


public class EvaluationThread extends Thread {
    private NeuralNetworkGameController controller;
    private CountDownLatch latch;
    private final int seed;

    public EvaluationThread(NeuralNetworkGameController controller, CountDownLatch latch, int seed) {
        this.controller = controller;
        this.latch = latch;
        this.seed = seed;
    }

    @Override
    public void run() {
        // Evaluate fitness of the controller
        double fitness = evaluateFitness(controller);
        controller.setFitness(fitness);

        // Signal completion to the latch
        latch.countDown();
    }

    private double evaluateFitness(NeuralNetworkGameController controller) {
        // Create a new instance of the Board class
        Board board = new Board(controller);

        // Run the board
        board.setSeed(seed);
        board.run();

        // Return the fitness score achieved by the controller
        return board.getFitness();
    }
}

