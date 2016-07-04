/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Road;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bo-nik
 */
public class Car extends Thread {

    public final WeakReference<Road> road;
    public final Route route;

    public Point2D.Double position;
    public Point2D.Double speed;

    private Image carImage;

    // for turn signal
    private int currentTurnSignalTicksCount;
    private final int maxTurnSignalTicksCount;
    private boolean showTurnSignal;

    // for preloaded images
    private final Image carImageDefault;
    private final Image carImageTurningLeft;
    private final Image carImageTurningRight;

    public boolean moving;

    public Car(Road road, Route route, String carImageFileName) {

        // preload images
        carImageDefault = road.getToolkit().getImage("resources/images/" + carImageFileName + ".png");
        carImageTurningLeft = road.getToolkit().getImage("resources/images/" + carImageFileName + "-turning-left.png");
        carImageTurningRight = road.getToolkit().getImage("resources/images/" + carImageFileName + "-turning-right.png");

        carImage = carImageDefault;

        this.road = new WeakReference<>(road);
        this.route = route;

        position = new Point2D.Double(0, 0);
        speed = new Point2D.Double(0, 0);
        moving = true;

        // turn signal
        currentTurnSignalTicksCount = 0;
        maxTurnSignalTicksCount = 10 + new Random().nextInt(30);
        showTurnSignal = false;
    }

    @Override
    public void run() {
        while (true) {
            route.moveCar(this);
            blinkTurnSignal();

            if (Math.abs(position.x) >= 320 || Math.abs(position.y) >= 320) {
                return;
            }

            road.get().repaint();

            try {
                Thread.sleep(30);

            } catch (InterruptedException ex) {
                Logger.getLogger(Car.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void paint(Graphics2D graphics) {
        double carRotation = Math.atan2(speed.y, speed.x) + Math.PI / 2;

        AffineTransform transform = graphics.getTransform();
        graphics.rotate(carRotation, position.x, position.y);

        graphics.drawImage(carImage,
                (int) Math.round(position.x - carImage.getWidth(null) / 2.0),
                (int) Math.round(position.y - carImage.getHeight(null) / 2.0), null);

        graphics.setTransform(transform);
    }

    private void blinkTurnSignal() {
        if (route.direction == Route.RouteDirection.FORWARD_AND_LEFT
                || route.direction == Route.RouteDirection.FORWARD_AND_RIGHT) {
            // if car is far from crossroad, do not turn on turn signal
            if (Math.abs(position.x) > 150 || Math.abs(position.y) > 150) {
                return;
            }

            currentTurnSignalTicksCount++;

            // invert state of turn signal
            if (currentTurnSignalTicksCount >= maxTurnSignalTicksCount) {
                showTurnSignal = !showTurnSignal;
                currentTurnSignalTicksCount = 0;
            }

            // if car has finished turning, turn signal off
            if (Math.abs(route.theta - Math.PI / 2) < 1e-3
                    && (Math.abs(position.x) > 60 || Math.abs(position.y) > 60)) {
                showTurnSignal = false;
            } else {
                
            }

            // set car image according to showTurnSignal state
            if (showTurnSignal) {
                if (route.direction == Route.RouteDirection.FORWARD_AND_LEFT) {
                    carImage = carImageTurningLeft;
                } else {
                    carImage = carImageTurningRight;
                }
            } else {
                carImage = carImageDefault;
            }
        }
    }

}
