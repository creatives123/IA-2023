package main;

import java.util.Random;

import controllers.GameController;
import controllers.NeuralNetworkGameController;
import controllers.RandomController;
import space.SpaceInvaders;

public class PlayRandomController {
	public static void main(String[] args) {
		GameController c = new NeuralNetworkGameController();
		SpaceInvaders.showControllerPlaying(c,5);
	}
}
