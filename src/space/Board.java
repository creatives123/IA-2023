package space;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import controllers.GameController;
import space.sprite.Alien;
import space.sprite.Player;
import space.sprite.Shot;

public class Board extends JPanel {

    private Dimension d;
    private List<Alien> aliens;
    private Player player;
    private Shot shot;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String explImg = "src/images/explosion.png";
    private String message = "Game Over";

    private Timer timer;
    private int time;

    double[] state;

    private GameController controller;
    private boolean headLess = false;
    Random generator = new Random();

    // Variaveis para contabilizar o numero de tiros e acertos
    private int totalShots = 0;
    private int successfulShots = 0;

    // variaveis para calcular o movimento do jogador
    private int oldPlayerX = 0;
    private int newPlayerX = 0;
    private int totalMovement = 0;

    public void setSeed(long seed) {
        generator.setSeed(seed);
    }

    public Board() {
        initBoard();
        gameInit();
    }

    public Board(GameController controller) {
        this.headLess = true;
        this.controller = controller;
        gameInit();
    }

    private void initBoard() {

//		addKeyListener(new TAdapter());
        setFocusable(true);
        d = new Dimension(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
        setBackground(Color.black);

        timer = new Timer(Commons.DELAY, new GameCycle());
        timer.start();

        gameInit();
    }

    private void gameInit() {

        aliens = new ArrayList<>();

        for (int i = 0; i < Commons.NUMBER_OF_LINES; i++) {
            for (int j = 0; j < Commons.NUMBER_OF_ALIENS_TO_DESTROY / Commons.NUMBER_OF_LINES; j++) {

                Alien alien = new Alien(Commons.ALIEN_INIT_X + 18 * j, Commons.ALIEN_INIT_Y + 18 * i);
                aliens.add(alien);
            }
        }

        player = new Player();
        shot = new Shot();
    }

    private void drawAliens(Graphics g) {

        for (Alien alien : aliens) {

            if (alien.isVisible()) {

                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
            }

            if (alien.isDying()) {

                alien.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {

        if (player.isVisible()) {

            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {

            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {

        if (shot.isVisible()) {

            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
        }
    }

    private void drawBombing(Graphics g) {

        for (Alien a : aliens) {

            Alien.Bomb b = a.getBomb();

            if (!b.isDestroyed()) {

                g.drawImage(b.getImage(), b.getX(), b.getY(), this);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.green);

        if (inGame) {

            g.drawLine(0, Commons.GROUND, Commons.BOARD_WIDTH, Commons.GROUND);

            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);

        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);

        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message + "-->" + getFitness(), (Commons.BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                Commons.BOARD_WIDTH / 2);
    }

    public BufferedImage createImage(JPanel panel) {

        int w = panel.getWidth();
        int h = panel.getHeight();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        panel.paint(g);
        g.dispose();
        return bi;
    }

    private double[] createState() {
        double[] state = new double[Commons.STATE_SIZE];
        int index = 0;
        for (Alien a : aliens) {
            state[index++] = (a.getX() * 1.0) / Commons.BOARD_WIDTH;
            state[index++] = (a.getY() * 1.0) / Commons.BOARD_HEIGHT;
            state[index++] = a.isDying() ? -1 : 1;
        }
        for (Alien a : aliens) {
            // state[index++] = a.getBomb().isDestroyed()?-1:1;
            if (!a.getBomb().isDestroyed()) {
                state[index++] = (a.getBomb().getX() * 1.0) / Commons.BOARD_WIDTH;
                state[index++] = (a.getBomb().getY() * 1.0) / Commons.BOARD_HEIGHT;
            } else {
                state[index++] = 0;
                state[index++] = 0;

            }
        }
        state[index++] = (player.getX() * 1.0) / Commons.BOARD_WIDTH;
        if (!shot.isDying()) {
            state[index++] = (shot.getX() * 1.0) / Commons.BOARD_WIDTH;
            state[index++] = (shot.getY() * 1.0) / Commons.BOARD_HEIGHT;
            //state[index++] = shot.isDying() ? -1 : 1;
        }

        return state;
    }

    private void update() {
        oldPlayerX = player.getX();
        time++;
        if (deaths == Commons.NUMBER_OF_ALIENS_TO_DESTROY) {

            inGame = false;
            if (!headLess)
                timer.stop();
            message = "Game won!";
        }

        // player

        double[] d = createState();
        double[] output = controller.nextMove(d);

        player.act(output);
        if (output[3] > 0.5) {
            if (inGame) {
                if (!shot.isVisible()) {
                    shot = new Shot(player.getX(), player.getY());
                    totalShots++; // Incrementar o numero de tiros
                }
            }
        }

        // shot
        if (shot.isVisible()) {

            int shotX = shot.getX();
            int shotY = shot.getY();

            for (Alien alien : aliens) {

                int alienX = alien.getX();
                int alienY = alien.getY();

                if (alien.isVisible() && shot.isVisible()) {
                    if (shotX >= (alienX) && shotX <= (alienX + Commons.ALIEN_WIDTH) && shotY >= (alienY)
                            && shotY <= (alienY + Commons.ALIEN_HEIGHT)) {

                        var ii = new ImageIcon(explImg);
                        alien.setImage(ii.getImage());
                        alien.setDying(true);
                        deaths++;
                        shot.die();

                        successfulShots++; // Incrementar o numero de acertos corretos
                    }
                }
            }

            int y = shot.getY();
            y -= 4;

            if (y < 0) {
                shot.die();
            } else {
                shot.setY(y);
            }
        }

        // aliens

        for (Alien alien : aliens) {
            if (alien.isVisible()) {

                int x = alien.getX();

                if (x >= Commons.BOARD_WIDTH - Commons.BORDER_RIGHT && direction != -1) {

                    direction = -1;

                    Iterator<Alien> i1 = aliens.iterator();

                    while (i1.hasNext()) {

                        Alien a2 = i1.next();
                        a2.setY(a2.getY() + Commons.GO_DOWN);
                    }
                }

                if (x <= Commons.BORDER_LEFT && direction != 1) {

                    direction = 1;

                    Iterator<Alien> i2 = aliens.iterator();

                    while (i2.hasNext()) {

                        Alien a = i2.next();
                        a.setY(a.getY() + Commons.GO_DOWN);
                    }
                }
                if (alien.isDying()) {

                    alien.die();
                }
            }

        }

        Iterator<Alien> it = aliens.iterator();

        while (it.hasNext()) {

            Alien alien = it.next();

            if (alien.isVisible()) {

                int y = alien.getY();

                if (y > Commons.GROUND - Commons.ALIEN_HEIGHT) {
                    inGame = false;
                    message = "Invasion!";
                }

                alien.act(direction);
            }
        }

        // bombs

        for (Alien alien : aliens) {

            int shot = generator.nextInt(400);
            Alien.Bomb bomb = alien.getBomb();

            if ((shot == Commons.CHANCE || alien.getX() == player.getX()) && alien.isVisible() && bomb.isDestroyed()) {

                bomb.setDestroyed(false);
                bomb.setX(alien.getX());
                bomb.setY(alien.getY());
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed()) {

                if (bombX >= (playerX) && bombX <= (playerX + Commons.PLAYER_WIDTH) && bombY >= (playerY)
                        && bombY <= (playerY + Commons.PLAYER_HEIGHT)) {

                    ImageIcon ii = new ImageIcon(explImg);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                }
            }

            if (!bomb.isDestroyed()) {

                bomb.setY(bomb.getY() + 1);

                if (bomb.getY() >= Commons.GROUND - Commons.BOMB_HEIGHT) {

                    bomb.setDestroyed(true);
                }
            }
        }
        if (player.isDying()) {

            player.die();
            inGame = false;
        }
        newPlayerX = player.getX();
        totalMovement += calculateMovement();
    }


    public int getDeaths() {
        return deaths;
    }

    public int getTime() {
        return time;
    }

    private void doGameCycle() {

        update();
        repaint();
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            doGameCycle();
        }
    }

    public void run() {
        while (inGame) {
            update();
        }
    }

    public Double getFitness() {
        double hitScore = calculateHitScore();
        double movementScore = calculateMovementScore(true);
        double winBonus = 0;
        double missedShotPenalty = (totalShots - successfulShots) * 50000;
        double alienBonus = calculateAlienProximityPenalty();
        double timeToKillBonus = calculateTimeToKillBonus();

        // Check if the game was won
        if (deaths == Commons.NUMBER_OF_ALIENS_TO_DESTROY) {
            // Add a bonus proportional to the speed of the win
            winBonus = 50000.0 / getTime();
        }

        // Increase the weight of deaths (hitting targets) and decrease the weight of time
        double fitness = (getDeaths() * 10000 + getTime());
        return fitness;
    }



    public void setController(GameController controller) {
        this.controller = controller;
    }

    public double calculateHitScore() {
        if (totalShots == 0) {
            return 0;
        } else {
            return 10000.0 * successfulShots;
        }
    }

    public int calculateMovement() {
        return Math.abs(newPlayerX - oldPlayerX);
    }

    private double calculateMovementScore(boolean punishMovement) {
        double movementScore = totalMovement * 1;
        // If punishMovement is true, return negative score. Otherwise, return positive score.
        return punishMovement ? -movementScore : movementScore;
    }

    private double calculateAlienProximityPenalty() {
        double penalty = 0.0;
        for (Alien alien : aliens) {
            // Calculate how close the alien is to the ground
            int proximityToGround = Commons.GROUND - alien.getY();
            // Add this to the penalty. You could also square this value to give a higher penalty to aliens that are closer to the ground.
            penalty += proximityToGround * proximityToGround;
        }
        // You might want to adjust this multiplication factor depending on how harsh you want the penalty to be
        return penalty * -0.5;
    }
    private double calculateTimeToKillBonus() {
        if (deaths == 0) {
            return 0;
        }

        // Calculate the average time to kill an alien
        double avgTimeToKill = (double) getTime() / deaths;
        // Provide a bonus based on how fast the AI kills each alien
        double timeToKillBonus = 1000.0 / avgTimeToKill;

        return timeToKillBonus;
    }


}
