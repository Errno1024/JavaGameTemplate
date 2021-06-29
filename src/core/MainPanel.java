package core;

import component.GameScene;
import settings.Constants;
import settings.Variables;
import utility.GameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;

public class MainPanel extends JPanel implements Runnable, ActionListener {
    private static final long serialVersionUID = 1L;
    public static int defaultFrameRate = Variables.defaultFrameRate;
    public static Color defaultBackgroundColor = Variables.defaultBackgroundColor;

    Timer timer;
    GameScene mainScene;
    Thread thread;
    int frameRate;
    Image image;
    MainFrame frame;
    BigInteger frameCount = BigInteger.valueOf(0);

    private int frameRateCounterCount = 0;
    private int realFrameRate = defaultFrameRate;

    public void startGame(GameScene initialScene) {
        initialScene.setSize(getSize());
        sceneChange(initialScene);
    }

    MainPanel(MainFrame frame) {
        this(frame, defaultFrameRate, defaultBackgroundColor);
    }

    MainPanel(MainFrame frame, int framerate) {
        this(frame, framerate, defaultBackgroundColor);
    }

    MainPanel(MainFrame frame, int framerate, Color bgcolor) {
        super(new BorderLayout());
        this.frame = frame;
        if (framerate > 0)
            this.frameRate = framerate;
        else
            this.frameRate = defaultFrameRate;
        if (bgcolor == null)
            this.setBackground(defaultBackgroundColor);
        else
            this.setBackground(bgcolor);
        initialize();
    }

    protected void initialize() {
        this.setFocusable(true);
        timer = new Timer(1000, this);
        thread = new Thread(this);
        setSize(frame.getContentPane().getSize());
        thread.start();
        setListener();
        this.setVisible(true);
    }

    void setListener() {
        try {
            Thread.sleep(this.frameRate);
        } catch (Throwable e) {
        }
        frame.addMouseListener(GameUtils.listener);
        frame.addKeyListener(GameUtils.listener);
        frame.addMouseMotionListener(GameUtils.listener);
        frame.addMouseWheelListener(GameUtils.listener);
    }

    public void sceneChange(GameScene scene) {
        Image sceneImage = null;
        if (mainScene != null) {
            sceneImage = GameUtils.cloneImage(mainScene.getImage());
            mainScene.dispose();
            frame.removeKeyListener(mainScene);
            frame.removeMouseListener(mainScene);
            frame.removeMouseMotionListener(mainScene);
            frame.removeMouseWheelListener(mainScene);
            timer.removeActionListener(mainScene);
        }
        mainScene = scene;
        scene.setPanel(this);
        scene.setTransition(sceneImage);
        frame.addKeyListener(mainScene);
        frame.addMouseListener(mainScene);
        frame.addMouseMotionListener(mainScene);
        frame.addMouseWheelListener(mainScene);
        timer.addActionListener(mainScene);
    }

    public void refresh() {
        if (mainScene != null) {
            mainScene.refresh();
        }
    }

    protected void bufferedPaint(Graphics2D g) {
        if (mainScene != null) {
            g.drawImage(mainScene.getImage(), 0, 0, this);
        }
        g.setColor(new java.awt.Color(255, 255, 255, 255));
        GameUtils.graphicsDrawText(g, "FPS: " + realFrameRate, new java.awt.Rectangle(0, 0, getWidth(), getHeight()), 3);
        GameUtils.graphicsDrawText(g, "Frame Count: " + frameCount.toString(), new java.awt.Rectangle(0, 0, getWidth(), getHeight()), 1);
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (image == null) {
            image = this.createImage(this.getWidth(), this.getHeight());
        }
        bufferedPaint((Graphics2D) g);
    }


    protected void onResize() {
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void setSize(Dimension d) {
        this.setSize(d.width, d.height);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        if (mainScene != null) {
            mainScene.setSize(width, height);
            this.onResize();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            realFrameRate = frameRateCounterCount;
            frameRateCounterCount = 0;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 / frameRate);
            } catch (InterruptedException e) {
            }
            frameRateCounterCount += 1;
            refresh();
            GameUtils.inputClear();
            repaint();
            frameCount = frameCount.add(BigInteger.valueOf(1));
        }
    }

}

