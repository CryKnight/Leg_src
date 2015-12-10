package RainbowReef;

import static java.applet.Applet.newAudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import javax.swing.*;
import java.applet.AudioClip;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;

public class RainbowGame extends JApplet implements Runnable {

    private Thread thread;
    int PopSpeed = 1;
    public static int score1 = 0;
    public static int window_width = 640;
    public static int window_height = 480;
    public static int land_width = 1280;
    public static int land_height = 1280;

    public static ArrayList wallBlocks;
    public static ArrayList wallBlocks2; //for next level
    public static AudioClip snd_block, snd_bigLeg, snd_wall, snd_katch, snd_lost, backgroundMusic;
    Random generator = new Random(1234567);
    Wall w1, w2, w3, w4, w5, w6,w7;
    Player1 p1;
    Pop pp;
    Bigleg leg;
    SmallBigleg sLeg;
    GameEvents gameEvents;
    private BufferedImage bimg, bimg2;
    private BufferedImage Katch_strip;
    private BufferedImage Pop_strip;
    private BufferedImage Bigleg_strip;
    private BufferedImage sLeg_strip;
    Image screen1,screen2, background, background2, wall1, block2, block3, block4, block5, block6,block7, life, gameover;
    Image[] Katch, spr_Katch, spr_Pop, spr_Bigleg, spr_boss,spr_sLeg;
    boolean gameOver;
    public boolean nextlevel;
    ImageObserver observer;

    public void init() {

        //Background
        setBackground(Color.white);
        background = getSprite("Resources/Background1.png");
        gameover = getSprite("Resources/Title.png");
        background2 = getSprite("Resources/Background2.png");
        // Walls
        wall1 = getSprite("Resources/Wall.png");
        block2 = getSprite("Resources/Block2.png");
        block3 = getSprite("Resources/Block_solid.png");
        block4 = getSprite("Resources/Block1.png");
        block5 = getSprite("Resources/Block_life.png");
        block6 = getSprite("Resources/Block5.png");
        block7 = getSprite("Resources/Block_double.png");
        life = getSprite("Resources/Katch_small.png");
        //read txt
        wallBlocks = new ArrayList();
        wallBlocks2 = new ArrayList();
        int[][] matrixx = new int[40][40];
       int[][]matrixx2= new int[40][40];
       nextlevel=false;
        try {
            
            InputStream input1, input2;
            input1 = ClassLoader.getSystemResourceAsStream("Resources/Field2.txt");
            input2 = ClassLoader.getSystemResourceAsStream("Resources/Field2.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(input1));
            String s = br.readLine();
            int i = 0;
            while (s != null) {
                for (int j = 0; j < s.length(); j++) {
                    matrixx[i][j] = Integer.parseInt(String.valueOf(s.charAt(j)));
                }
                s = br.readLine();
                i++;
            }
            br = new BufferedReader(new InputStreamReader(input2));
            s = br.readLine();
            i = 0;
            while (s != null) {
                for (int j = 0; j < s.length(); j++) {
                    matrixx2[i][j] = Integer.parseInt(String.valueOf(s.charAt(j)));
                }
                s = br.readLine();
                i++;
            }
            

        } catch (Exception e) {
            System.out.println("error: " + e);
        }            
        for (int row = 0; row < 40; row++) {
            for (int col = 0; col < 40; col++) {
                if (matrixx[row][col] == 1) {
                    w1 = new Wall(wall1, 20 * col, 20 * row, 1);
                    wallBlocks.add(w1);
                } else if (matrixx[row][col] == 2) {
                    w2 = new Wall(block2, 40 * col, 20 * row, 2);
                    wallBlocks.add(w2);
                } else if (matrixx[row][col] == 3) {
                    w3 = new Wall(block3, 40 * col, 20 * row, 3);
                    wallBlocks.add(w3);
                } else if (matrixx[row][col] == 4) {
                    w4 = new Wall(block4, 40 * col, 20 * row, 4);
                    wallBlocks.add(w4);
                } else if (matrixx[row][col] == 5) {
                    w5 = new Wall(block5, 40 * col, 20 * row, 5);
                    wallBlocks.add(w5);
                } else if (matrixx[row][col] == 6) {
                    w6 = new Wall(block6, 40 * col, 20 * row, 6);
                    wallBlocks.add(w6);
                } else if (matrixx[row][col] == 7) {
                    w7 = new Wall(block7, 40 * col, 20 * row, 7);
                    wallBlocks.add(w7);
                }

            }
        }
        for (int row = 0; row < 40; row++) {
            for (int col = 0; col < 40; col++) {
                if (matrixx2[row][col] == 1) {
                    w1 = new Wall(wall1, 20 * col, 20 * row, 1);
                    wallBlocks2.add(w1);
                } else if (matrixx2[row][col] == 2) {
                    w2 = new Wall(block2, 40 * col, 20 * row, 2);
                    wallBlocks2.add(w2);
                } else if (matrixx2[row][col] == 3) {
                    w3 = new Wall(block3, 40 * col, 20 * row, 3);
                    wallBlocks2.add(w3);
                } else if (matrixx2[row][col] == 4) {
                    w4 = new Wall(block4, 40 * col, 20 * row, 4);
                    wallBlocks2.add(w4);
                } else if (matrixx2[row][col] == 5) {
                    w5 = new Wall(block5, 40 * col, 20 * row, 5);
                    wallBlocks2.add(w5);
                } else if (matrixx2[row][col] == 6) {
                    w6 = new Wall(block6, 40 * col, 20 * row, 6);
                    wallBlocks2.add(w6);
                } else if (matrixx2[row][col] == 7) {
                    w7 = new Wall(block7, 40 * col, 20 * row, 7);
                    wallBlocks2.add(w7);
                }

            }
        }
        
     

        //create katch image
        try {
            URL urlKatch = ClassLoader.getSystemResource("Resources/Katch_strip24.png");
            Katch_strip = ImageIO.read(urlKatch);

            Katch = new Image[24];
            for (int i = 0; i < 24; i++) {
                Katch[i] = Katch_strip.getSubimage(i * 80, 0, 80, 30);
            }

        } catch (Exception e) {
        }
        //create pop image
        try {
            URL urlPop = ClassLoader.getSystemResource("Resources/Pop_strip45.png");
            Pop_strip = ImageIO.read(urlPop);

            spr_Pop = new Image[45];
            for (int i = 0; i < 45; i++) {
                spr_Pop[i] = Pop_strip.getSubimage(i * 35, 0, 35, 35);
            }
        } catch (Exception e) {
        }
        try {
            URL urlBigleg = ClassLoader.getSystemResource("Resources/Bigleg_strip24.png");
            Bigleg_strip = ImageIO.read(urlBigleg);

            spr_Bigleg = new Image[24];
            for (int i = 0; i < 24; i++) {
                spr_Bigleg[i] = Bigleg_strip.getSubimage(i * 80, 0, 80, 80);
            }
        } catch (Exception e) {
        }
        try {
            URL urlSmallLeg = ClassLoader.getSystemResource("Resources/Bigleg_small_strip24.png");
            sLeg_strip = ImageIO.read(urlSmallLeg);

            spr_sLeg = new Image[24];
            for (int i = 0; i < 24; i++) {
                spr_sLeg[i] = sLeg_strip.getSubimage(i * 40, 0, 40, 40);
            }
        } catch (Exception e) {
        }
        gameOver = false;
        observer = this;
        
        KeyControl key = new KeyControl();
        setFocusable(true);
        addKeyListener(key);

        gameEvents = new GameEvents();
        p1 = new Player1(Katch[0], 250, 430, 10);
        pp = new Pop(290, 280, 5, 270, 1, 1);
        leg = new Bigleg(spr_Bigleg[0], 270, 20, 0);
        sLeg= new SmallBigleg(spr_sLeg[0], 200, 180, 180, 10);
        gameEvents.addObserver(p1);

        try {
            URL back = ClassLoader.getSystemResource("Resources/Music.mid");
              backgroundMusic = newAudioClip(back);
              //backgroundMusic.loop();
        } catch (Exception e) {
        }
        try {
            URL block = ClassLoader.getSystemResource("Resources/Sound_block.wav");
            snd_block = newAudioClip(block);
        } catch (Exception e) {
        }
        try {
            URL lost = ClassLoader.getSystemResource("Resources/Sound_lost.wav");
            snd_lost = newAudioClip(lost);
        } catch (Exception e) {
        }
        try {
            URL wall = ClassLoader.getSystemResource("Resources/Sound_wall.wav");
            snd_wall = newAudioClip(wall);
        } catch (Exception e) {
        }
    }

    public class KeyControl extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            gameEvents.setValue(e);
        }
    }

    public class Wall {

        Image img;
        int x, y, sizeX, sizeY;
        public int type;
        int counter,walltouch;
        boolean show;
        boolean collision;

        public Wall(Image img, int x, int y, int type) {
            this.type = type;
            this.img = img;
            this.x = x;
            this.y = y;
            sizeX = img.getWidth(null);
            sizeY = img.getHeight(null);
            show = true;
            collision = false;
            counter = 0;
        }

        public boolean getVisible() {
            return show;
        }

        public int popcollision(int x, int y, int w, int h) {
            if ((y < this.y + this.sizeY) && (x + w > this.x) && (x < this.x + this.sizeX) && (y + h > this.y)) {

                if (y >= this.y + this.sizeY - 5) {
                    System.out.println("For South");
                    return 1;
                }
                if (x >= this.x + this.sizeX - 5) {
                    //east
                    return 4;
                }
                if (this.y <= y + h + 5) {
                    //north
                    return 2;
                }
                if (this.x <= x + w) {
                    //west
                    return 3;
                }

            }
            return 0;
        }
                public int collision(int x, int y, int w, int h) {
            if (((y > (this.y + 20)) && (y < (this.y + sizeY))) && ((x + w) > (this.x + 12)) && (x < (this.x + sizeX - 12))) {
                return 1;//FOR NORTH
            }
            if ((((y + h) < (this.y + sizeY - 20)) && ((y + h) > this.y)) && ((x + w) > (this.x + 12)) && (x < (this.x + sizeX - 12))) {
                return 2;//FOR SOUTH
            }
            if ((y < (this.y + sizeY - 12)) && ((y + h) > (this.y + 12)) && (x > (this.x + 20)) && (x < (this.x + sizeX))) {
                return 3;//FOR WEST
            }
            if ((y < (this.y + sizeY - 12)) && ((y + h) > (this.y + 12)) && (((x + w) > this.x) && ((x + w) < (this.x + 12)))) {
                return 4;//FOR EAST
            }

            return 0;
        }

        public void update(int w, int h) {
       
        }

        public void draw(Graphics g, ImageObserver obs) {
            if (show) {
                g.drawImage(this.img, x, y, obs);
            } else if (show == false) {
             img=null;
            }
        }

    }

    public class Bigleg {

        Image img;
        int x, y, sizeX, sizeY, direction, i;
        boolean show;
        boolean  updateDirection;

        Bigleg(Image img, int x, int y, int direction) {
            this.direction = direction;
            this.x = x;
            this.y = y;
            this.img = img;
            sizeX = img.getWidth(null);
            sizeY = img.getHeight(null);
            show = true;
         
        }

        public boolean getVisible() {
            return show;
        }

        public int collision(int x, int y, int w, int h) {
            /*
            if (((y > (this.y + 20)) && (y < (this.y + sizeY))) && ((x + w) > (this.x + 12)) && (x < (this.x + sizeX - 12))) {
                return 1;//FOR NORTH
            }
            if ((((y + h) < (this.y + sizeY - 20)) && ((y + h) > this.y)) && ((x + w) > (this.x + 12)) && (x < (this.x + sizeX - 12))) {
                return 2;//FOR SOUTH
            }
            if ((y < (this.y + sizeY - 12)) && ((y + h) > (this.y + 12)) && (x > (this.x + 20)) && (x < (this.x + sizeX))) {
                return 3;//FOR WEST
            }
            if ((y < (this.y + sizeY - 12)) && ((y + h) > (this.y + 12)) && (((x + w) > this.x) && ((x + w) < (this.x + 12)))) {
                return 4;//FOR EAST
            }*/
            if ((y < this.y + this.sizeY) && (x + w > this.x) && (x < this.x + this.sizeX) && (y + h > this.y)) {
                return 1;
            }
            return 0;
        }

        public boolean updateDirection() {
            if (show) {
                int N;
                i += 8;
                if (i >= 144) {
                    N = 23;
                    i = 0;
                } else {
                    N = i / 6;
                }
                this.img = spr_Bigleg[N];
                return true;

            }
            return false;
        }

        public void update() {
          if(show) { 
            updateDirection();}
        else{
        this.img=null;
        }
        }

        public void draw(Graphics g, ImageObserver obs) {
            g.drawImage(img, x, y, obs);
        }
    }
   public class SmallBigleg {

        Image img;
        int x, y, sizeX, sizeY, direction, i,speed;
        boolean show;
        boolean  updateDirection;

        SmallBigleg(Image img, int x, int y, int direction,int speed) {
            this.direction = direction;
            this.x = x;
            this.y = y;
            this.img = img;
            this.sizeX = img.getWidth(null);
            this.sizeY = img.getHeight(null);
            this.show = true;
           this.speed=speed;
        }

        public boolean getVisible() {
            return show;
        }



        public boolean updateDirection() {
            if (show) {
                int N;
                i += 8;
                if (i >= 144) {
                    N = 23;
                    i = 0;
                } else {
                    N = i / 6;
                }
                this.img = spr_sLeg[N];
                return true;

            }
            return false;
        }

        public void update() {
          if(show) { 
            updateDirection();
            for (int k = 0; k < wallBlocks.size(); k++) {
                Wall wll = (Wall) wallBlocks.get(k);
                int collisionType = wll.popcollision(x, y, sizeX, sizeY);
                if(wll.show==true && collisionType != 0){
                    direction = 360 - (direction - 180);
                    System.out.println("nima");
                    break;
                }           
            } 
            x +=((float) Math.cos(-1 * Math.toRadians(direction))) * speed + 2;
            
          }
          
        else{
        this.img=null;
        }     
        }
        public void draw(Graphics g, ImageObserver obs) {
            g.drawImage(img, x, y, obs);
        }
    }
    public class Pop {

        Image img;
        int x, y, speed, sizeX, sizeY, direction, bulletType, Player, i, collisionType,walltouch;
        boolean show;

        Pop(int x, int y, int speed, int direction, int type, int Player) {
            this.direction = direction;
            this.bulletType = type;
            this.Player = Player;
            this.collisionType = 1;
            //普通子弹
            this.x = x;
            this.y = y;
            this.speed = speed;
            show = true;
         this.walltouch=0;
        }

        public int getY() {
            return y;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public int getSpeed() {
            return speed;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void updateDirection() {
            int N;
            i += 5;
            if (i >= 270) {
                N = 44;
                i = 0;
            } else {
                N = i / 6;
            }
            if (bulletType == 1) {
                this.img = spr_Pop[N];
            }
            sizeX = img.getWidth(null);
            sizeY = img.getHeight(null);

        }

        public void update(int w, int h) {
            updateDirection();
            int moveType = 1; // not collision between board and pop
            for (int k = 0; k < wallBlocks.size(); k++) {
                Wall wll = (Wall) wallBlocks.get(k);
                int collisionType = wll.popcollision(x, y, sizeX, sizeY);
                if (wll.show == true && (collisionType == 1 || collisionType == 2)) {

                    if (wll.type == 2 || wll.type == 4 || wll.type == 6) {
                        //this.collisionType = -this.collisionType;
                        wll.show = false;
                        direction = -direction;
                        score1++;
                        snd_block.play();
                      //  System.out.println("可打破（南北）");
                    }
                    else if ( wll.type == 7) {
                      
                          wll.type=4;
                         wll.img=w4.img;
                        direction =  - direction;                     
                   
                    } 
                    
                    else if ( wll.type == 5 ) {
                        //this.collisionType = -this.collisionType;
                        wll.show = false;
                        direction = -direction;
                       p1.lifes+=30;
                        snd_block.play();
                        //System.out.println("可打破shengming（南北）");
                    } 
                    else if (wll.type == 3) {
                        direction = -direction;
                      //  System.out.println("硬砖块（南北）");
                        snd_wall.play();
                    } else if (wll.type == 1) {

                        if (x > 585 || x < 15) {
                            direction = 360 - (direction - 180);
                        } else {
                            direction = -direction;
                        }
                        snd_wall.play();
                      //  System.out.println("墙（南北）");
                    }
         
                    moveType = 0;
                    y += ((float) Math.sin(-1 * Math.toRadians(direction))) * speed + 1;
                    x += ((float) Math.cos(-1 * Math.toRadians(direction))) * speed + 1;
                    break;
                   
                } else if (wll.show == true && (collisionType == 3 || collisionType == 4)) {
                    if (wll.type == 2 || wll.type == 4 || wll.type == 6) {
                        wll.show = false;
                        direction = 360 - (direction - 180);
                        score1++;
                        snd_block.play();
                       // System.out.println("可打破砖块");
                    }
                     else if (wll.type == 7) {
                     wll.type=4;
                        wll.img=w4.img;
                        direction =  - direction;                     
                                    
                    }
                        
                       // System.out.println("shengming");                  
                     else if (wll.type == 5) {
                        wll.show = false;
                        direction = 360 - (direction - 180);
                        p1.lifes+=30;
                        snd_block.play();
                       // System.out.println("shengming");
                    }
                    else if (wll.type == 3) {
                        direction = 360 - (direction - 180);
                       // System.out.println("硬砖块");
                        snd_wall.play();
                    } else if (wll.type == 1) {
                        direction = 360 - (direction - 180);
                       // System.out.println("墙");
                        snd_wall.play();
                    }
             
                    moveType = 0;
                    y += ((float) Math.sin(-1 * Math.toRadians(direction))) * speed + 1;
                    x += ((float) Math.cos(-1 * Math.toRadians(direction))) * speed + 1;
                    break;

                }
            }
            if (moveType == 1) {
                if (p1.popCollision(x, y, sizeX, sizeY) == 0) {
                    y += ((float) Math.sin(-1 * Math.toRadians(direction))) * speed;
                    x += ((float) Math.cos(-1 * Math.toRadians(direction))) * speed;
                } else if (p1.popCollision(x, y, sizeX, sizeY) == 2) {
                    if(p1.x<=pp.x&&pp.x<p1.x+20){
                   // System.out.println("zuozuozuo");
                    
                    direction = 120;
                    y += ((float) Math.sin(-1 * Math.toRadians(direction))) * speed;
                    x += ((float) Math.cos(-1 * Math.toRadians(direction))) * speed;
                    }
                    else if(p1.x+20<=pp.x&&pp.x<=p1.x+70){
                    direction = 60;
                  //  System.out.println("youyouyou");
                    y += ((float) Math.sin(-1 * Math.toRadians(direction))) * speed;
                    x += ((float) Math.cos(-1 * Math.toRadians(direction))) * speed;
                    }
                } //右边
                else if (p1.popCollision(x, y, sizeX, sizeY) == 3) {
                    direction = 30;
                  //  System.out.println("3");
                    //this.collisionType = -this.collisionType;
                    x += ((float) Math.cos(-1 * Math.toRadians(direction))) * speed;
                    y += ((float) Math.sin(-1 * Math.toRadians(direction))) * speed;
                    //  y -= this.speed;
                } //左边
                else if (p1.popCollision(x, y, sizeX, sizeY) == 4) {
                    direction = 150;
                    //System.out.println("4");
                    //this.collisionType = -this.collisionType;

                }
               
                
            }
            if(leg.show==true&&leg.collision(x, y, sizeX, sizeY)==1){
                nextlevel=true;
                restart();
                System.out.println("legg");
            }
            if (this.y > 450) {
                //System.out.println("mei");
                //direction = -direction;
                snd_lost.play();
                restart();
            }
        }

        public void draw(Graphics g, ImageObserver obs) {

            g.drawImage(this.img, x, y, obs);

        }

    }


    public abstract class Katch implements Observer {

        Image img;
        int x, y, sizeX, sizeY, kspeed;
        boolean show;
        int damage;
        int lifes;
        int health;
        int direction;
        int WeaponType;
        int count;

        Katch(Image img, int x, int y, int kspeed) {
            direction = 0;
            this.img = img;
            this.x = x;
            this.y = y;
            sizeX = img.getWidth(null);
            sizeY = img.getHeight(null);
            this.kspeed = kspeed;
            WeaponType = 0;
            count = 0;
            show = true;
            lifes = 140;

        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getSizeX() {
            return sizeX;
        }

        public int getSizeY() {
            return sizeY;
        }

        public int getLifes() {
            return lifes;
        }

        public int getHealth() {
            return health;
        }

        public void draw(Graphics g, ImageObserver obs) {
            if (show) {
                g.drawImage(img, x, y, obs);

            }

        }

        public int popCollision(int x, int y, int w, int h) {

            if (((y > (this.y + 20)) && (y < (this.y + sizeY))) && ((x + w) > (this.x + 12)) && (x < (this.x + sizeX - 12))) {
                return 1;//FOR NORTH
            }
            if ((((y + h) < (this.y + sizeY - 20)) && ((y + h) > this.y)) && ((x + w) > (this.x + 12)) && (x < (this.x + sizeX - 12))) {
                return 2;//FOR SOUTH
            }
            if ((y < (this.y + sizeY - 12)) && ((y + h) > (this.y + 12)) && (x > (this.x + 20)) && (x < (this.x + sizeX))) {
                return 3;//FOR WEST
            }
            if ((y < (this.y + sizeY - 12)) && ((y + h) > (this.y + 12)) && (((x + w) > this.x) && ((x + w) < (this.x + 12)))) {
                return 4;//FOR EAST
            }
            return 0;
        }
    }

    public class Player1 extends Katch {

        Player1(Image img, int x, int y, int speed) {

            super(img, x, y, speed);

        }

        public void update(Observable obj, Object arg) {
            GameEvents ge = (GameEvents) arg;

            if (ge.type == 1) {
                KeyEvent e = (KeyEvent) ge.event;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                        System.out.println("Left");

                        if (x > 30) {
                            x -= kspeed +5;
                        }

                        break;
                    case KeyEvent.VK_D:
                        System.out.println("Right");

                        if (x < 530) {
                            x += kspeed +5;
                        }

                        break;

                    default:
                        if (e.getKeyChar() == ' ') {
                            System.out.println("Fire");
                        }
                }
            }

        }
    }

    public void restart() {

        p1.x = 250;
        p1.y = 430;
        p1.direction = 0;
        p1.img = Katch[p1.direction / 6];
        p1.lifes -= 30;

        pp.x = 290;
        pp.y = 280;
        pp.direction = 280;
        
        
        if (p1.lifes == 20) {
            gameOver = true;
        }
        /////////////

    }

    public Image getSprite(String name) {
        URL url = ClassLoader.getSystemResource(name);
        Image img = getToolkit().getImage(url);

        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {
        }
        return img;
    }
//same with dong's code

    public void drawBackGroundWithTileImage(int w, int h, Graphics2D g2) {
        int TileWidth = background.getWidth(this);
        int TileHeight = background.getHeight(this);

        int NumberX = (int) (w / TileWidth);
        int NumberY = (int) (h / TileHeight);

        for (int i = -1; i <= NumberY; i++) {
            for (int j = 0; j <= NumberX; j++) {
                g2.drawImage(background, j * TileWidth, i * TileHeight, TileWidth, TileHeight, this);
            }
        }

    }

    public void drawDemo(int w, int h, Graphics2D g2) {

        if (!gameOver) {
            drawBackGroundWithTileImage(w, h, g2);
            if (this.nextlevel){
                wallBlocks=wallBlocks2;
                this.nextlevel=false;
            }
            for (int k = 0; k < wallBlocks.size(); k++) {
                Wall wll = (Wall) wallBlocks.get(k);
                wll.update(w, h);
                if (wll.getVisible() == true) {
                    wll.draw(g2, this);
                }
            }
            

            p1.draw(g2, this);

            pp.update(w, h);
            pp.draw(g2, this);

            leg.update();
            leg.draw(g2, this);
            sLeg.update();
            sLeg.draw(g2, this);
            for (int i = p1.lifes; i >= 50; i -= 30) {
                g2.drawImage(life, i, 400, this);

            }
            
        } else {
            g2.drawImage(background2, 0, 0, this);
            g2.drawImage(gameover, 0, 0, this);
        }

    }

    public void drawMap(int w, int h, Graphics2D g3) {

        Graphics2D g2 = createGraphics2D(land_width, land_height);
        drawDemo(land_width, land_height, g2);
        g2.dispose();
        g3.drawImage(bimg, 0, 0, this);

        int t1X = 0, t1Y = 0;
        screen1 = bimg.getSubimage(t1X, t1Y, window_width, window_height);
        g3.drawImage(screen1, 0, 0, null);

        //Scores
        if (!gameOver) {
            g3.setColor(Color.BLACK);
            g3.setFont(new Font(null, Font.BOLD, 25));
            g3.drawString("Score: " + Integer.toString(score1), 50, 350);

        } else{

            g3.setColor(Color.BLACK);
            g3.setFont(new Font(null, Font.BOLD, 25));
            g3.drawString("Your Total Score: " + Integer.toString(score1), 50, 50);
        } 
    }

    public Graphics2D createGraphics2DG3(int w, int h) {
        Graphics2D g3 = null;
        if (bimg2 == null || bimg2.getWidth() != w || bimg2.getHeight() != h) {
            bimg2 = (BufferedImage) createImage(w, h);
        }
        g3 = bimg2.createGraphics();
        g3.setBackground(getBackground());
        g3.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g3.clearRect(0, 0, w, h);
        return g3;
    }

    public Graphics2D createGraphics2D(int w, int h) {
        Graphics2D g2 = null;
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
            bimg = (BufferedImage) createImage(w, h);
        }
        g2 = bimg.createGraphics();
        g2.setBackground(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.clearRect(0, 0, w, h);
        return g2;
    }

    public void paint(Graphics g) {
        Graphics2D g3 = createGraphics2DG3(window_width, window_height);
        drawMap(window_width, window_height, g3);
        g3.dispose();
        g.drawImage(bimg2, 0, 0, this);
    }

    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    @SuppressWarnings("static-access")
    public void run() {

        Thread me = Thread.currentThread();
        while (thread == me) {
            repaint();

            try {
                thread.sleep(25);
            } catch (InterruptedException e) {
                break;
            }

        }
    }

    public static void main(String[] args) throws IOException {
        final RainbowGame demo = new RainbowGame();
        demo.init();
   
        JFrame f = new JFrame("Rainbow Reef");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.getContentPane().add("Center", demo);
        f.pack();
        f.setSize(new Dimension(window_width, window_height));
        f.setVisible(true);
        f.setResizable(false);
        demo.setFocusable(true);
        demo.start();
    }

}
