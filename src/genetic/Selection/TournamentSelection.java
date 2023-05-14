package genetic.Selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controllers.NeuralNetworkGameController;
import space.Commons;

public class TournamentSelection {
    private Random random = new Random();
    private List<NeuralNetworkGameController> individuals;

    public TournamentSelection(List<NeuralNetworkGameController> individuals) {
        this.individuals = individuals;
    }

    public List<NeuralNetworkGameController> selection(int numParents) {
        int tournamentSize = Commons.TOURNSIZE; // Number of individuals participating in each tournament
        int numTournaments = Commons.NUMTOURNSIZE; // Number of tournaments to be conducted

        List<NeuralNetworkGameController> selectedParents = new ArrayList<>();

        for (int i = 0; i < numParents; i++) {
            NeuralNetworkGameController bestIndividual = null;
            double bestFitness = Double.NEGATIVE_INFINITY;

            for (int j = 0; j < numTournaments; j++) {
                // Randomly select individuals for the tournament
                List<NeuralNetworkGameController> tournamentParticipants = new ArrayList<>();
                for (int k = 0; k < tournamentSize; k++) {
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

            selectedParents.add(bestIndividual);
        }

        return selectedParents;
    }
}
