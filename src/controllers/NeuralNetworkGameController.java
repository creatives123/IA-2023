package controllers;

import java.util.Random;

import nn.FeedForwardNeuralNetwork;
import space.Commons;

public class NeuralNetworkGameController implements GameController {

    private FeedForwardNeuralNetwork neuralNetwork;
    private double fitness;

    public NeuralNetworkGameController(int inputDim, int hiddenDim, int outputDim) {
        neuralNetwork = new FeedForwardNeuralNetwork(inputDim, hiddenDim, outputDim);
        neuralNetwork.initializeWeights();
    }

    public NeuralNetworkGameController(int inputDim, int hiddenDim, int outputDim, double[] values) {
        neuralNetwork = new FeedForwardNeuralNetwork(inputDim, hiddenDim, outputDim, values);
    }

    public NeuralNetworkGameController() {
        int inputDim = Commons.STATE_SIZE;
        int hiddenDim = Commons.HIDDENDIM; // You can choose the hidden layer size according to your needs.
        int outputDim = Commons.NUM_ACTIONS;

        this.neuralNetwork = new FeedForwardNeuralNetwork(inputDim, hiddenDim, outputDim);
        this.neuralNetwork.initializeWeights();
    }


    @Override
    public double[] nextMove(double[] currentState) {
        return neuralNetwork.forward(currentState);
    }

    public int getChromossomeSize() {
        return neuralNetwork.getChromossomeSize();
    }

    public double[] getChromossome() {
        return neuralNetwork.getChromossome();
    }

    public void setChromossome(double[] chromossome) {
        neuralNetwork = new FeedForwardNeuralNetwork(neuralNetwork.inputDim, neuralNetwork.hiddenDim, neuralNetwork.outputDim, chromossome);
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

}
