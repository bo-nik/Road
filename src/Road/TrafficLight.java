/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Road;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bo-nik
 */
public class TrafficLight extends Thread {

    public enum TrafficLightColor {
        RED, GREEN, YELLOW_AFTER_RED, YELLOW_AFTER_GREEN
    }

    public final WeakReference<Road> road;

    public Point position;
    public int size;

    private final int redInterval;
    private final int yellowInterval;
    private final int greenInterval;

    public TrafficLightColor color;

    public TrafficLight(Road road) {

        this.road = new WeakReference<Road>(road);

        position = new Point(0, 0);
        size = 15;

        greenInterval = 20000;
        yellowInterval = 6000;
        redInterval = 20000;

        color = TrafficLightColor.GREEN;
    }

    @Override
    public void run() {
        while (true) {
            int sleepInterval = 0;

            switch (color) {
                case RED:
                    color = TrafficLightColor.YELLOW_AFTER_RED;
                    sleepInterval = yellowInterval;
                    break;
                case GREEN:
                    color = TrafficLightColor.YELLOW_AFTER_GREEN;
                    sleepInterval = yellowInterval;
                    break;
                case YELLOW_AFTER_RED:
                    color = TrafficLightColor.GREEN;
                    sleepInterval = greenInterval;
                    break;
                case YELLOW_AFTER_GREEN:
                    color = TrafficLightColor.RED;
                    sleepInterval = redInterval;
                    break;
            }

            road.get().repaint();

            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException ex) {
                Logger.getLogger(TrafficLight.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void paint(Graphics2D graphics) {
        switch (color) {
            case RED:
                graphics.setColor(Color.RED);
                break;
            case GREEN:
                graphics.setColor(Color.GREEN);
                break;
            case YELLOW_AFTER_RED:
                graphics.setColor(Color.YELLOW);
                break;
            case YELLOW_AFTER_GREEN:
                graphics.setColor(Color.YELLOW);
                break;
        }
        graphics.fillOval(position.x - size / 2, position.y - size / 2, size, size);
        
        // fill red arc if yellow appears after red
        if (color == TrafficLightColor.YELLOW_AFTER_RED) {
            graphics.setColor(Color.RED);
            graphics.fillArc(position.x - size / 2, position.y - size / 2, size, size, 0, 180);
        }
        
        // stroke
        graphics.setStroke(new BasicStroke(0.5f));
        graphics.setColor(Color.BLACK);
        graphics.drawOval(position.x - size / 2, position.y - size / 2, size, size);
    }
}
