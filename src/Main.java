import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        int broadwidth=600;
        int broadheight=broadwidth;

        JFrame frame=new JFrame("Snake");
        frame.setVisible(true);
        frame.setSize(broadwidth,broadheight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SnakeGame snakegame=new SnakeGame(broadwidth,broadheight);
        frame.add(snakegame);
        frame.pack();
        snakegame.requestFocusInWindow();
    }
}
