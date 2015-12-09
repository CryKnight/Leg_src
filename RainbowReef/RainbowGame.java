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
    public static ArrayList Blocks;
    public static AudioClip snd_block, snd_bigLeg, snd_katch, snd_lost, backgroundMusic, snd_expSmall, snd_expLarge;
    Random generator = new Random(1234567);
    Wall w1, w2, w3;
    Player1 p1;
    Pop pp;
    Bigleg leg;
    GameEvents gameEvents;
    Explosion explosion;
    private BufferedImage bimg, bimg2;
    private BufferedImage Katch_strip;
    private BufferedImage Pop_strip;
    private BufferedImage Bigleg_strip;
    private BufferedImage Lifes_strip;
    private BufferedImage explosion_small_strip;
    private BufferedImage explosion_large_strip;
    Image screen1, background, wall1, block2, block3, lifes,gameover;
    Image spr_explosion_small[];
    Image spr_explosion_large[];
//wall1 solid
    Image[] Katch, spr_Katch, spr_Pop, spr_Bigleg, spr_Lifes;
    boolean gameOver;
    ImageObserver observer;

    public void init() {

        //Background
        setBackground(Color.white);
        background = getSprite("Resources/Background1.png");
    gameover=getSprite("Resources/Title.png");
        // Walls
        wall1 = getSprite("Resources/Wall.png");
        block2 = getSprite("Resources/Block2.png");
        block3 = getSprite("Resources/Block_solid.png");
        //read txt
        wallBlocks = new ArrayList();
        int[][] matrixx = new int[41][41];
        try {
            InputStream input;
            input = ClassLoader.getSystemResourceAsStream("Resources/Field.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String s = br.readLine();
            int i = 0;
            while (s != null) {
                for (int j = 0; j < s.length(); j++) {
                    matrixx[i][j] = Integer.parseInt(String.valueOf(s.charAt(j)));
                }
                s = br.readLine();
                i++;
            }
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
        for (int row = 0; row < 41; row++) {
            for (int col = 0; col < 41; col++) {
                if (matrixx[row][col] == 1) {
                    w1 = new Wall(wall1, 20 * col, 20 * row, 1);
                    wallBlocks.add(w1);
                } else if (matrixx[row][col] == 2) {
                    w2 = new Wall(block2, 40 * col, 20 * row, 2);
                    wallBlocks.add(w2);
                } else if (matrixx[row][col] == 3) {
                    w3 = new Wall(block3, 40 * col, 20 * row, 3);
                    wallBlocks.add(w3);
                }

            }
        }

        //create Tank image
        try {
            URL urlKatch = ClassLoader.getSystemResource("Resources/Katch_strip24.png");
            Katch_strip = ImageIO.read(urlKatch);

            Katch = new Image[24];
            for (int i = 0; i < 24; i++) {
                Katch[i] = Katch_strip.getSubimage(i * 80, 0, 80, 30);
            }

        } catch (Exception e) {
        }
        //create shell image
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
            URL urlLifes = ClassLoader.getSystemResource("Resources/Katch_small.png");
            Lifes_strip = ImageIO.read(urlLifes);
            spr_Lifes = new Image[1];
            for (int i = 0; i < 1; i++) {
                spr_Lifes[i] = Lifes_strip.getSubimage(0, 0, 30, 10);
            }
        } catch (Exception e) {
        }
        //Create explosion image
        try {
            URL urlExplosionSmall = ClassLoader.getSystemResource("Resources/Explosion_small_strip6.png");
            explosion_small_strip = ImageIO.read(urlExplosionSmall);

            spr_explosion_small = new Image[6];
            for (int i = 0; i < 6; i++) {
                spr_explosion_small[i] = explosion_small_strip.getSubimage(i * 32, 0, 32, 32);
            }

            URL urlExplosionLarge = ClassLoader.getSystemResource("Resources/Explosion_large_strip7.png");
            explosion_large_strip = ImageIO.read(urlExplosionLarge);

            spr_explosion_large = new Image[7];
            for (int i = 0; i < 7; i++) {
                spr_explosion_large[i] = explosion_large_strip.getSubimage(i * 64, 0, 64, 64);
            }

        } catch (Exception e) {
            System.out.println("YES");
        }

        gameOver = false;
        observer = this;

        KeyControl key = new KeyControl();
        setFocusable(true);
        addKeyListener(key);

        gameEvents = new GameEvents();
        p1 = new Player1(Katch[0], 270, 430, 10);
        pp = new Pop(280, 280, 5, 270, 1, 1);
        leg = new Bigleg(spr_Bigleg[0], 270, 30, 0);

        gameEvents.addObserver(p1);

        explosion = new Explosion();

        try {
            URL back = ClassLoader.getSystemResource("Resources/Music.mid");
            //  backgroundMusic = newAudioClip(back);
            // backgroundMusic.loop();
        } catch (Exception e) {
        }
        try {
            URL snd1 = ClassLoader.getSystemResource("Resources/Explosion_small.wav");
            snd_expSmall = newAudioClip(snd1);
            URL snd2 = ClassLoader.getSystemResource("Resources/Explosion_large.wav");
            snd_expLarge = newAudioClip(snd2);

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
        int counter;
        public boolean show;
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

        public int katchCollision(int x, int y, int w, int h) {

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

        public int popcollision(int x, int y, int w, int h) {
            if (((y > (this.y +1)) && (y < (this.y + sizeY))) && ((x + w) > (this.x + 1)) && (x < (this.x + sizeX - 1))) {
                return 1;//FOR NORTH
            }
            if ((((y + h) < (this.y + sizeY - 1)) && ((y + h) > this.y)) && ((x + w) > (this.x + 1)) && (x < (this.x + sizeX - 1))) {
                return 2;//FOR SOUTH
            }
            if  ((y < (this.y + sizeY - 6)) && ((y + h) > (this.y + 6)) && (x > (this.x + 10)) && (x < (this.x + sizeX))) {
                return 3;//FOR WEST
            }
            if ((y < (this.y + sizeY - 6)) && ((y + h) > (this.y + 6)) && (((x + w) > this.x) && ((x + w) < (this.x + 6)))) {
                return 4;//FOR EAST
            }

            return 0;
        }

        public void update(int w, int h) {

            if (show == false) {
                counter++;
                if (counter > 500) {
                    // show = true;
                    counter = 0;
                }
            }
        }

        public void draw(Graphics g, ImageObserver obs) {
            g.drawImage(img, x, y, obs);
        }

    }

    public class Bigleg {

        Image img;
        int x, y, sizeX, sizeY, direction, i;
        public boolean show;
        boolean collision, updateDirection;

        Bigleg(Image img, int x, int y, int direction) {
            this.direction = direction;
            this.x = x;
            this.y = y;
            this.img = img;
            sizeX = img.getWidth(null);
            sizeY = img.getHeight(null);
            show = true;
            collision = false;
        }

        public boolean getVisible() {
            return show;
        }

        public boolean collision(int x, int y, int w, int h) {
            if (((y + h > this.y) && (y < this.y + sizeY)) && ((x + w > this.x) && (x < this.x + sizeX))) {
                return true;
            }
            return false;
        }

        public boolean updateDirection() {
            int N;
            i += 5;
            if (i >= 144) {
                N = 23;
                i = 0;
            } else {
                N = i / 6;
            }
            this.img = spr_Bigleg[N];
            return true;

        }

        public void update() {
            updateDirection();
            if (show == false) {
                updateDirection = false;
                System.out.println("gg");
            }
        }

        public void draw(Graphics g, ImageObserver obs) {
            g.drawImage(img, x, y, obs);
        }
    }

    public class Pop {

        Image img;
        int x, y, speed, sizeX, sizeY, direction, bulletType, Player, collisionType, i;
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
            i += 3;
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

            if (p1.popCollision(x, y, sizeX, sizeY,speed) == 0) {
                if (this.collisionType == 1) {
                  //  System.out.println("dd");
                    x += ((float) Math.cos(-1 * Math.toRadians(direction))) * speed;
                    y += ((float) Math.sin(-1 * Math.toRadians(direction))) * speed;
                } else {
                     y += ((float) Math.sin(-1 * Math.toRadians(direction))) * speed;
                     x += ((float) Math.cos(-1 * Math.toRadians(direction))) * speed;
                }
            } else if (p1.popCollision(x, y, sizeX, sizeY,speed) == 2) {
                System.out.println("2");
                this.collisionType = -this.collisionType;
                direction=-direction;
                y += ((float) Math.sin(-1 * Math.toRadians(direction))) * speed;
                x += ((float) Math.cos(-1 * Math.toRadians(direction))) * speed;          
            } //右边
            else if (p1.popCollision(x, y, sizeX, sizeY,speed) == 3) {
                direction = 60;
                System.out.println("3");
                this.collisionType = -this.collisionType;
                x += ((float) Math.cos(-1 * Math.toRadians(direction))) * speed;
                y += ((float) Math.sin(-1 * Math.toRadians(direction))) * speed;
                //  y -= this.speed;
            } //左边
            else if (p1.popCollision(x, y, sizeX, sizeY,speed) == 4) {
                direction = 120;
                System.out.println("4");
                this.collisionType = -this.collisionType;

            }

            if (bulletType == 1) {
               
                for (int k = 0; k < wallBlocks.size(); k++) {
                    Wall wll = (Wall) wallBlocks.get(k);
                    
                    if ((wll.popcollision(x, y, sizeX, sizeY)==2)||(wll.popcollision(x, y, sizeX, sizeY)==1)&&
                            
                                        wll.show == true) {
                        
                        if (wll.type == 2) {
                            //this.collisionType = -this.collisionType;
                            wll.show = false;
                             direction =  - direction;
                              score1++;
                            System.out.println("可打破（南北）");
                        } else if (wll.type == 3) {
                            direction = - direction;
                            this.collisionType = -this.collisionType;                           
                            System.out.println("硬砖块（南北）");
                        } else if (wll.type == 1) {
                            this.collisionType = -this.collisionType;
                            direction =- direction;                         
                            System.out.println("墙（南北）");
                        }
                    }else if((wll.popcollision(x, y, sizeX, sizeY)==3)||(wll.popcollision(x, y, sizeX, sizeY)==4)&&
                            
                                     wll.show == true){
                         if (wll.type == 2) {
                            this.collisionType = -this.collisionType;
                            wll.show = false;
                             direction = 360 - (direction - 180);
                              score1++;
                            System.out.println("可打破砖块");
                        } else if (wll.type == 3) {
                            direction = 360 - (direction - 180);
                            this.collisionType = -this.collisionType;                           
                            System.out.println("硬砖块");
                        } else if (wll.type == 1) {
                            this.collisionType = -this.collisionType;
                            direction = 360 - (direction - 180);                        
                            System.out.println("墙");
                        }
                    
                    }

                }
                x += ((float) Math.cos(-1 * Math.toRadians(direction))) * speed;
                y += ((float) Math.sin(-1 * Math.toRadians(direction))) * speed;
                
               /* if (leg.collision(x, y, sizeX, sizeY) && leg.show == true) {

                    this.collisionType = -this.collisionType;
                    leg.updateDirection = false;
                    leg.show = false;

                    score1++;
                    System.out.println("gameover1");

                }*/
            }

            if (this.y > 450) {
                System.out.println("mei");
                restart();
            }
            }

        public void draw(Graphics g, ImageObserver obs) {

            g.drawImage(this.img, x, y, obs);

        }

    }

    public abstract class Katch implements Observer {

        Image img;
        int x, y, sizeX, sizeY, speed;
        boolean show;
        int damage;
        int lifes;
        int health;
        int direction;
        int WeaponType;
        int count;

        Katch(Image img, int x, int y, int speed) {
            direction = 0;
            this.img = img;
            this.x = x;
            this.y = y;
            sizeX = img.getWidth(null);
            sizeY = img.getHeight(null);
            this.speed = speed;
            WeaponType = 0;
            count = 0;
            show = true;
            damage = 0;
            lifes = 3;

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

        public void fire(int weaponType, int player) {

            if (weaponType == 1) {
                pp = new Pop(x + (sizeX / 4), y + (sizeY / 4), PopSpeed, direction, 1, player);

            }
        }

        public void draw(Graphics g, ImageObserver obs) {
            if (show) {
                g.drawImage(img, x, y, obs);

            }

        }

        public int popCollision(int x, int y, int w, int h,int kspeed) {

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
            //call tankparent CODE REUSE
            super(img, x, y, speed);

        }

        public void update(Observable obj, Object arg) {
            GameEvents ge = (GameEvents) arg;

            if (ge.type == 1) {
                KeyEvent e = (KeyEvent) ge.event;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                        System.out.println("Left");
                        //转回来
                        if (x > 0) {
                            x -= speed*2;
                        }

                        break;
                    case KeyEvent.VK_D:
                        System.out.println("Right");

                        if (x < 570) {
                            x += speed*2;
                        }

                        break;
                    case KeyEvent.VK_W:
                        System.out.println("Up");

                        break;
                    case KeyEvent.VK_S:
                        System.out.println("Down");

                        break;
                    case KeyEvent.VK_SPACE:
                        //  fire(1, 1);
                        break;

                    case KeyEvent.VK_CONTROL:
                        if (count > 0) {
                            fire(2, 1);
                        }
                        break;

                    default:
                        if (e.getKeyChar() == ' ') {
                            System.out.println("Fire");
                        }
                }
            } else if (ge.type == 2) {
                String msg = (String) ge.event;
                if (msg.equals("Explosion1")) {
                    health--;

                    if (health == 0) {
                        score1++;
                        explosion.lx = x;
                        explosion.ly = y;
                        explosion.activeLarge = true;
                        snd_expLarge.play();
                        //show=false;
                    }
                }
                if (msg.equals("Rocket1")) {
                    WeaponType = 2;
                    count = 10;
                }
            }

            for (int k = 0; k < wallBlocks.size(); k++) {
                Wall wll = (Wall) wallBlocks.get(k);

                if (wll.katchCollision(x, y, sizeX, sizeY) == 1 && wll.show == true) {
                    y += 5;
                    System.out.println("1");
                }
                if (wll.katchCollision(x, y, sizeX, sizeY) == 2 && wll.show == true) {
                    y -= 5;
                    System.out.println("2");
                }
                if (wll.katchCollision(x, y, sizeX, sizeY) == 3 && wll.show == true) {
                    x += 5;
                    System.out.println("3");
                }
                if (wll.katchCollision(x, y, sizeX, sizeY) == 4 && wll.show == true) {
                    x -= 5;
                    System.out.println("4");
                }

            }

        }
    }

    public class Explosion {

        int sx = 0;
        int sy = 0;
        private int lx = 0;
        private int ly = 0;
        public boolean activeSmall = false;
        public boolean activeLarge = false;
        private int numFramesSmall = 6;
        private int numFramesLarge = 7;
        private int currentFrameSmall = 0;
        private int currentFrameLarge = 0;

        public void drawExplosionSmall(Graphics g, ImageObserver obs) {
            g.drawImage(spr_explosion_small[currentFrameSmall], sx, sy, null);
            currentFrameSmall++;
            if (currentFrameSmall >= numFramesSmall) {
                currentFrameSmall = 0;
                activeSmall = false;
            }
        }

        public void drawExplosionLarge(Graphics g, ImageObserver obs) {
            g.drawImage(spr_explosion_large[currentFrameLarge], lx, ly, null);
            currentFrameLarge++;
            if (currentFrameLarge >= numFramesLarge) {
                currentFrameLarge = 0;
                activeLarge = false;
                restart();
            }
        }
    }

    public void restart() {

        p1.x = 270;
        p1.y = 430;
        p1.direction = 0;
        p1.img = Katch[p1.direction / 6];
        p1.lifes -= 1;
        pp.x = 270;
        pp.y = 200;
        pp.direction=270;
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
        g3.setColor(Color.BLUE);
        g3.setFont(new Font(null, Font.BOLD, 25));
        g3.drawString("Score: " + Integer.toString(score1), 50, 350);
        g3.setColor(Color.BLUE);
        g3.setFont(new Font(null, Font.BOLD, 25));
        g3.drawString("Life: " + Integer.toString(p1.lifes), 50, 450);
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
        JFrame f = new JFrame("Tank War");
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
