package genetic;

import controllers.NeuralNetworkGameController;
import space.Board;

import java.util.concurrent.CountDownLatch;

public class EvaluationThread extends Thread {
    private NeuralNetworkGameController controller;
    private CountDownLatch latch;

    public EvaluationThread(NeuralNetworkGameController controller, CountDownLatch latch) {
        this.controller = controller;
        this.latch = latch;
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
        board.setSeed(5);
        board.run();

        // Return the fitness score achieved by the controller
        return board.getFitness();
    }
}

