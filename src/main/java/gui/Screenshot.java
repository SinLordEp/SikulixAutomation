package gui;

import interfaces.Callback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Screenshot extends JFrame {
    private Point startPoint;
    private Rectangle captureRect;
    private BufferedImage fullScreenImage;
    private final Callback<BufferedImage> callback;

    public Screenshot(Callback<BufferedImage> callback) {
        this.callback = callback;
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle screenRect = ge.getMaximumWindowBounds();
            fullScreenImage = new Robot().createScreenCapture(screenRect);
            setUndecorated(true);
            setAlwaysOnTop(true);
            setOpacity(1f);
            setBackground(Color.BLACK);
            setBounds(screenRect);
        } catch (Exception ex) {
            ex.printStackTrace();
            dispose();
            return;
        }

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
        if (fullScreenImage != null) {
            g.drawImage(fullScreenImage, 0, 0, getWidth(), getHeight(), this);
        }
        if (captureRect != null) {
            g.setColor(new Color(255, 0, 0, 128));
            g.drawRect(captureRect.x, captureRect.y, captureRect.width, captureRect.height);
            g.setColor(new Color(255, 255, 255, 60));
            g.fillRect(captureRect.x, captureRect.y, captureRect.width, captureRect.height);
        }
    }


    private void captureAndSave(Rectangle rect) {
        if (fullScreenImage == null || rect == null || rect.width == 0 || rect.height == 0) {
            return;
        }
        BufferedImage cropped = fullScreenImage.getSubimage(
                rect.x,
                rect.y,
                rect.width,
                rect.height
        );
        if (callback != null) {
            callback.onSubmit(cropped);
        }
    }

}