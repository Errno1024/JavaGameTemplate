package core;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import component.GameScene;
import utility.GameUtils;

public class MainFrame extends JFrame implements WindowListener, ComponentListener {
    private static final long serialVersionUID = 1L;
    private MainPanel panel;

    public MainFrame(String title, int width, int height, String iconPath, boolean borderRegarded) {
        super(title);
        if (iconPath != null) {
            this.setIconImage(GameUtils.getImage(iconPath));
        }
        this.setBackground(MainPanel.defaultBackgroundColor);
        frameResize(width, height, borderRegarded);
        initialize();
    }

    public MainFrame(String title, int width, int height) {
        this(title, width, height, null, true);
    }

    public MainFrame(String title, int width, int height, String iconPath) {
        this(title, width, height, iconPath, true);
    }

    protected void initialize() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.addComponentListener(this);
        this.addWindowListener(this);
        this.panel = new MainPanel(this);
        this.add(panel);
    }

    public void frameResize(int width, int height, boolean borderRegarded) {
        this.setSize(width, height);
        this.setVisible(true);
        if (borderRegarded) {
            this.setVisible(false);
            Container cp = this.getContentPane();
            int w = cp.getWidth(), h = cp.getHeight();
            this.setSize(width * 2 - w, height * 2 - h);
            this.setVisible(true);
        }
    }

    public void startGame(GameScene scene) {
        this.panel.startGame(scene);
    }

    public void frameResize(int width, int height) {
        frameResize(width, height, true);
    }

    public void update(java.awt.Graphics g) {
        paint(g);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (panel != null) {
            Dimension d = this.getContentPane().getSize();
            panel.setSize(d);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

}
