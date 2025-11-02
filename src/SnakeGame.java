import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    private class Tile {
        int x;
        int y;
        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    // Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // Food
    Tile food;
    Random random;

    // Obstacles
    ArrayList<Tile> obstacles;

    // Game Logic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;

    // Levels & Scoring
    int level = 1;
    int score = 0;
    int pointsToNextLevel = 10;

    Color bgColor = Color.black;

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(bgColor);
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();

        food = new Tile(10, 10);
        random = new Random();

        obstacles = new ArrayList<>();
        createObstaclesForLevel(level);

        placeFood();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(200, this);
        gameLoop.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Background color per level
        setBackground(bgColor);

        // Obstacles
        g.setColor(Color.gray);
        for (Tile wall : obstacles) {
            g.fill3DRect(wall.x * tileSize, wall.y * tileSize, tileSize, tileSize, true);
        }

        // Food
        g.setColor(Color.red);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        // Snake Head
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        // Snake Body
        for (Tile part : snakeBody) {
            g.fill3DRect(part.x * tileSize, part.y * tileSize, tileSize, tileSize, true);
        }

        // Score
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.white);
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over! Final Score: " + score, tileSize, tileSize);
        } else {
            g.drawString("Score: " + score + "   Level: " + level, tileSize, tileSize);
        }
    }

    public void createObstaclesForLevel(int level) {
        obstacles.clear();

        if (level == 1) {
            // Simple obstacles in a horizontal line
            for (int i = 5; i < 10; i++) {
                obstacles.add(new Tile(i, 10));
            }
        } else if (level == 2) {
            // More complex pattern
            for (int i = 8; i < 13; i++) {
                obstacles.add(new Tile(15, i));
            }
            for (int i = 10; i < 15; i++) {
                obstacles.add(new Tile(i, 5));
            }
        }
    }

    private void updateBackgroundColor() {
        if (level == 1) {
            bgColor = Color.black;
        } else if (level == 2) {
            bgColor = new Color(30, 30, 30); // lighter black shade
        }
    }

    private void placeFood() {
        boolean valid = false;
        while (!valid) {
            food.x = random.nextInt(boardWidth / tileSize);
            food.y = random.nextInt(boardHeight / tileSize);

            valid = true;

            // Avoid spawning on obstacles
            for (Tile wall : obstacles) {
                if (collision(food, wall)) {
                    valid = false;
                    break;
                }
            }

            // Avoid spawning on snake body
            if (collision(food, snakeHead)) valid = false;
            for (Tile part : snakeBody) {
                if (collision(food, part)) {
                    valid = false;
                    break;
                }
            }
        }
    }

    public boolean collision(Tile t1, Tile t2) {
        return t1.x == t2.x && t1.y == t2.y;
    }

    public void move() {
        // Eat food
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            score++;
            placeFood();

            // Level up check
            if (score % pointsToNextLevel == 0) {
                level++;

                if (level > 2) {
                    gameOver = true;
                    JOptionPane.showMessageDialog(this,
                            "🎉 You completed all levels!\nFinal Score: " + score,
                            "Game Completed!", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this,
                        "Level " + level + " reached!",
                        "Level Up!", JOptionPane.INFORMATION_MESSAGE);

                createObstaclesForLevel(level);
                updateBackgroundColor();

                // Reset snake safely to center
                snakeHead = new Tile(boardWidth / (2 * tileSize), boardHeight / (2 * tileSize));
                snakeBody.clear();
                velocityX = 0;
                velocityY = 0;

                placeFood();
            }
        }

        // Move snake body
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile part = snakeBody.get(i);
            if (i == 0) {
                part.x = snakeHead.x;
                part.y = snakeHead.y;
            } else {
                Tile prev = snakeBody.get(i - 1);
                part.x = prev.x;
                part.y = prev.y;
            }
        }

        // Move snake head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Collisions
        // Snake body collision
        for (Tile part : snakeBody) {
            if (collision(snakeHead, part)) {
                gameOver = true;
            }
        }

        // Wall collision
        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize ||
                snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize) {
            gameOver = true;
        }

        // Obstacle collision
        for (Tile wall : obstacles) {
            if (collision(snakeHead, wall)) {
                gameOver = true;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        } else {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}
