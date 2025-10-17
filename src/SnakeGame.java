import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
public class SnakeGame extends JPanel implements ActionListener,KeyListener{

    private class Tile{
        int x;
        int y;
        Tile(int x,int y){
            this.x=x;
            this.y=y;
        }
    }
    int boardwidth;
    int boardheight;
    int tilesize=25;
    //snake
    Tile snakehead;
    ArrayList<Tile> snakebody;
    //food
    Tile food;
    Random random;
    //GAME LOGIC
    Timer gameloop;
    int velocityX;
    int velocityY;
    boolean gameover=false;
    SnakeGame(int boardwidth,int boardheight){
        this.boardwidth=boardwidth;
        this.boardheight=boardheight;
        setPreferredSize(new Dimension(this.boardwidth,this.boardheight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        snakehead=new Tile(5,5);
        snakebody=new ArrayList<Tile>();

        food=new Tile(10,10);
        random=new Random();
        placefood();

        velocityX=0;
        velocityY=0;
        gameloop=new Timer(200,this);
        gameloop.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        //Grid
//        for(int i=0;i<boardwidth/tilesize;i++){
//            g.drawLine(i*tilesize,0,i*tilesize,boardheight);
//            g.drawLine(0,i*tilesize,boardwidth,i*tilesize);
//        }
        //FOOD
        g.setColor(Color.red);
       // g.fillRect(food.x*tilesize,food.y*tilesize,tilesize,tilesize);
        g.fill3DRect(food.x*tilesize,food.y*tilesize,tilesize,tilesize,true);
        //SNAKE HEAD
        g.setColor(Color.green);
        //g.fillRect(snakehead.x*tilesize,snakehead.y*tilesize,tilesize,tilesize);
        g.fill3DRect(snakehead.x*tilesize,snakehead.y*tilesize,tilesize,tilesize,true);
        //SNAKE BODY
        for(int i=0;i<snakebody.size();i++){
            Tile snakepart=snakebody.get(i);
            //g.fillRect(snakepart.x*tilesize,snakepart.y*tilesize,tilesize,tilesize);
            g.fill3DRect(snakepart.x*tilesize,snakepart.y*tilesize,tilesize,tilesize,true);
        }

        //Score
        g.setFont(new Font("Arial",Font.PLAIN,16));
        if(gameover){
            g.setColor(Color.red);
            g.drawString("Game Over: "+ String.valueOf(snakebody.size()),tilesize-16,tilesize);
        }else{
            g.drawString("Score: "+ String.valueOf(snakebody.size()),tilesize-16,tilesize);
        }
    }
    public void placefood(){
        food.x=random.nextInt(boardwidth/tilesize); //0 to 24
        food.y=random.nextInt(boardheight/tilesize); // 0 to 24
    }
    public boolean collision(Tile tile1,Tile tile2){
        return tile1.x==tile2.x && tile1.y==tile2.y;
    }

    public void move(){
        //eat food
        if(collision(snakehead,food)){
            snakebody.add(new Tile(food.x,food.y));
            placefood();
        }
        //SNAKE BODY
        for(int i=snakebody.size()-1;i>=0;i--){
            Tile snakepart=snakebody.get(i);
            if(i==0){
                snakepart.x=snakehead.x;
                snakepart.y=snakehead.y;
            }else{
                Tile prevsnakepart=snakebody.get(i-1);
                snakepart.x=prevsnakepart.x;
                snakepart.y=prevsnakepart.y;

            }
        }
        //Snakehead
        snakehead.x+=velocityX;
        snakehead.y+=velocityY;

        //GAME OVER CONDITIONS
        for(int i=0;i<snakebody.size();i++){
            Tile snakepart=snakebody.get(i);
            // collide with snake head
            if(collision(snakehead,snakepart)){
                gameover=true;
            }
        }
        //collision with wall
        if(snakehead.x*tilesize<0 || snakehead.x*tilesize>boardwidth || snakehead.y*tilesize<0 || snakehead.y*tilesize >boardheight){
            gameover=true;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameover){
            gameloop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP && velocityY !=1){
            velocityX=0;
            velocityY=-1;
        }else if(e.getKeyCode()==KeyEvent.VK_DOWN && velocityY!=-1){
            velocityX=0;
            velocityY=1;
        }else if(e.getKeyCode()==KeyEvent.VK_LEFT && velocityX !=1){
            velocityX=-1;
            velocityY=0;
        }else if(e.getKeyCode()==KeyEvent.VK_RIGHT && velocityX !=-1){
            velocityX=1;
            velocityY=0;
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }

}
