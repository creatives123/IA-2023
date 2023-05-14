package space;

public interface Commons {

    int BOARD_WIDTH = 358;
    int BOARD_HEIGHT = 350;
    int IMAGE_SIZE = 358*322;
    int BORDER_RIGHT = 30;
    int BORDER_LEFT = 5;

    int GROUND = 290;
    int BOMB_HEIGHT = 5;

    int ALIEN_HEIGHT = 12;
    int ALIEN_WIDTH = 12;
    int ALIEN_INIT_X = 150;
    int ALIEN_INIT_Y = 5;

    int GO_DOWN = 15;
    int CHANCE = 1;
    int DELAY = 10;
    int PLAYER_WIDTH = 15;
    int PLAYER_HEIGHT = 10;
    
    int STATE_SIZE = Commons.NUMBER_OF_ALIENS_TO_DESTROY * 3 * 2 + 1 + 3;
    int NUM_ACTIONS = 4; // 0 - left, 1 - right, 2 - stop, 3 - fire.

    //FIT = 183842
    //JOGO GANHO

    //Variáveis
    int POPSIZE = 100; //100
    int GENSIZE = 100; //100
    int TOURNSIZE = 20; //20
    int NUMTOURNSIZE = 10; //10
    double MUTRANGE = 0.06; //0.06
    double MUTRATE = 0.9; // 0.9
    int HIDDENDIM = 20; //20
    int SEED = 5; //5
    int NUMBER_OF_LINES = 3;//24;
    int NUMBER_OF_ALIENS_TO_DESTROY = 18;//24;

    String CROSSOVERTYPE = "TWOPOINT"; //TWOPOINT //KPOINT //ONEPOINT
    String MUTATIONTYPE = "GAUSSIAN"; //GAUSSIAN //SCRAMBLE
    String SELECTIONTYPE = "TOURNAMENT"; //TOURNAMENT //SUS   
    int SCRAMBLERATE = 1;
    int SCRAMBLESIZE = 10;
    int CROSSOVERNUMBER = 4;
}
