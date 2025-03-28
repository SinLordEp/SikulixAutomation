package utils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Screenshot extends JFrame{
    private Point startPoint;
    private Rectangle captureRect;
    private BufferedImage imageCaptured;

    public Screenshot() {
        setUndecorated(true);
        setAlwaysOnTop(true);
        setOpacity(0.3f);
        setBackground(Color.BLACK);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle screenRect = ge.getMaximumWindowBounds();
        setBounds(screenRect);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                captureRect = createRectangle(startPoint, e.getPoint());
                dispose();
                try {
                    captureAndSave(captureRect);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                captureRect = createRectangle(startPoint, e.getPoint());
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });

        setFocusable(true);
        setVisible(true);
    }

    private Rectangle createRectangle(Point p1, Point p2) {
        return new Rectangle(
                Math.min(p1.x, p2.x),
                Math.min(p1.y, p2.y),
                Math.abs(p1.x - p2.x),
                Math.abs(p1.y - p2.y)
        );
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (captureRect != null) {
            g.setColor(Color.RED);
            g.drawRect(captureRect.x, captureRect.y, captureRect.width, captureRect.height);
        }
    }

    private void captureAndSave(Rectangle rect) throws Exception {
        Robot robot = new Robot();
        imageCaptured = robot.createScreenCapture(rect);
    }

    public void saveImage(String fileName, String filePath) throws IOException {
        if(imageCaptured != null){
            ImageIO.write(imageCaptured, "png", new File(filePath +  "/" + fileName));
        }
    }
}
