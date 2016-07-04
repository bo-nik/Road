/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Road;

import java.applet.Applet;
import java.awt.*;
import java.awt.geom.Point2D;
//import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author bo-nik
 */
public class Road extends Applet {

    private Image backgroundImage;
    private Image imageBuffer;

    TrafficLight topLeftTrafficLight;
    TrafficLight topRightTrafficLight;
    TrafficLight bottomLeftTrafficLight;
    TrafficLight bottomRightTrafficLight;

    CopyOnWriteArrayList<Car> cars;

    private Timer timer;

    private final double TRAFFIC_INTENSITY = 3.0;
    private final int ROAD_UPDATE_INTERVAL;
    private final int MAX_CARS_AMOUNT;

    public Road() {
        ROAD_UPDATE_INTERVAL = (int) Math.round(500 / TRAFFIC_INTENSITY);
        MAX_CARS_AMOUNT = (int) Math.round(32 * TRAFFIC_INTENSITY / 2);
    }

    @Override
    public void init() {

        backgroundImage = getToolkit().getImage("resources/images/Road.jpg");
        imageBuffer = createImage(500, 500);

        // create traffic lights
        topLeftTrafficLight = new TrafficLight(this);
        topLeftTrafficLight.position = new Point(-70, -70);
        topLeftTrafficLight.color = TrafficLight.TrafficLightColor.RED;
        topLeftTrafficLight.start();

        topRightTrafficLight = new TrafficLight(this);
        topRightTrafficLight.position = new Point(70, -70);
        topRightTrafficLight.color = TrafficLight.TrafficLightColor.GREEN;
        topRightTrafficLight.start();

        bottomLeftTrafficLight = new TrafficLight(this);
        bottomLeftTrafficLight.position = new Point(-70, 70);
        bottomLeftTrafficLight.color = TrafficLight.TrafficLightColor.GREEN;
        bottomLeftTrafficLight.start();

        bottomRightTrafficLight = new TrafficLight(this);
        bottomRightTrafficLight.position = new Point(70, 70);
        bottomRightTrafficLight.color = TrafficLight.TrafficLightColor.RED;
        bottomRightTrafficLight.start();

        // create cars
        cars = new CopyOnWriteArrayList<>();

        // timer for adding cars
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                addRandomCar();
            }
        }, 0, ROAD_UPDATE_INTERVAL);

        // resize applet to background image size
        setSize(500, 500);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D graphics = (Graphics2D) imageBuffer.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.drawImage(backgroundImage, 0, 0, null);

        graphics.translate(250, 250);

        topLeftTrafficLight.paint(graphics);
        topRightTrafficLight.paint(graphics);
        bottomLeftTrafficLight.paint(graphics);
        bottomRightTrafficLight.paint(graphics);

        for (int i = 0; i < cars.size(); i++) {
            cars.get(i).paint(graphics);
        }

        g.drawImage(imageBuffer, 0, 0, null);
    }

    private void addRandomCar() {
        // remove unactive cars
        for (int i = 0; i < cars.size(); i++) {
            if (!cars.get(i).isAlive()) {
                cars.remove(i);
            }
        }

        if (cars.size() >= MAX_CARS_AMOUNT) {
            return;
        }

        Car car = null;
        double speed = 1.0;
//        double speed = new Random().nextDouble() + 0.5;

        String carIamgesFileNames[] = new String[]{"Car-red", "Car-blue", "Car-white"};
        String carImageFileName = carIamgesFileNames[new Random().nextInt(carIamgesFileNames.length)];

        int templateNumber = new Random().nextInt(16);
        switch (templateNumber) {
            case 0: // FORWARD from bottom
                car = new Car(this, new Route(Route.RouteDirection.FORWARD), carImageFileName);
                car.position = new Point2D.Double(13, 280);
                car.speed = new Point2D.Double(0, -speed);
                break;
            case 1: // FORWARD_AND_LEFT from bottom
                car = new Car(this, new Route(Route.RouteDirection.FORWARD_AND_LEFT), carImageFileName);
                car.position = new Point2D.Double(13, 280);
                car.speed = new Point2D.Double(0, -speed);
                break;
            case 2: // FORWARD_AND_RIGHT from bottom
                car = new Car(this, new Route(Route.RouteDirection.FORWARD_AND_RIGHT), carImageFileName);
                car.position = new Point2D.Double(39, 280);
                car.speed = new Point2D.Double(0, -speed);
                break;
            case 3: // FORWARD from top
                car = new Car(this, new Route(Route.RouteDirection.FORWARD), carImageFileName);
                car.position = new Point2D.Double(-13, -280);
                car.speed = new Point2D.Double(0, speed);
                break;
            case 4: // FORWARD_AND_LEFT from top
                car = new Car(this, new Route(Route.RouteDirection.FORWARD_AND_LEFT), carImageFileName);
                car.position = new Point2D.Double(-13, -280);
                car.speed = new Point2D.Double(0, speed);
                break;
            case 5: // FORWARD_AND_RIGHT from top
                car = new Car(this, new Route(Route.RouteDirection.FORWARD_AND_RIGHT), carImageFileName);
                car.position = new Point2D.Double(-39, -280);
                car.speed = new Point2D.Double(0, speed);
                break;
            case 6: // FORWARD from left
                car = new Car(this, new Route(Route.RouteDirection.FORWARD), carImageFileName);
                car.position = new Point2D.Double(-280, 13);
                car.speed = new Point2D.Double(speed, 0);
                break;
            case 7: // FORWARD_AND_LEFT from left
                car = new Car(this, new Route(Route.RouteDirection.FORWARD_AND_LEFT), carImageFileName);
                car.position = new Point2D.Double(-280, 13);
                car.speed = new Point2D.Double(speed, 0);
                break;
            case 8: // FORWARD_AND_RIGHT from left
                car = new Car(this, new Route(Route.RouteDirection.FORWARD_AND_RIGHT), carImageFileName);
                car.position = new Point2D.Double(-280, 39);
                car.speed = new Point2D.Double(speed, 0);
                break;
            case 9: // FORWARD from right
                car = new Car(this, new Route(Route.RouteDirection.FORWARD), carImageFileName);
                car.position = new Point2D.Double(280, -13);
                car.speed = new Point2D.Double(-speed, 0);
                break;
            case 10: // FORWARD_AND_LEFT from right
                car = new Car(this, new Route(Route.RouteDirection.FORWARD_AND_LEFT), carImageFileName);
                car.position = new Point2D.Double(280, -13);
                car.speed = new Point2D.Double(-speed, 0);
                break;
            case 11: // FORWARD_AND_RIGHT from right
                car = new Car(this, new Route(Route.RouteDirection.FORWARD_AND_RIGHT), carImageFileName);
                car.position = new Point2D.Double(280, -39);
                car.speed = new Point2D.Double(-speed, 0);
                break;

            case 12: // FORWARD from bottom
                car = new Car(this, new Route(Route.RouteDirection.FORWARD), carImageFileName);
                car.position = new Point2D.Double(39, 280);
                car.speed = new Point2D.Double(0, -speed);
                break;
            case 13: // FORWARD from top
                car = new Car(this, new Route(Route.RouteDirection.FORWARD), carImageFileName);
                car.position = new Point2D.Double(-39, -280);
                car.speed = new Point2D.Double(0, speed);
            case 14: // FORWARD from left
                car = new Car(this, new Route(Route.RouteDirection.FORWARD), carImageFileName);
                car.position = new Point2D.Double(-280, 39);
                car.speed = new Point2D.Double(speed, 0);
                break;
            case 15: // FORWARD from right
                car = new Car(this, new Route(Route.RouteDirection.FORWARD), carImageFileName);
                car.position = new Point2D.Double(280, -39);
                car.speed = new Point2D.Double(-speed, 0);
                break;
        }

        // check for free place on the road for new car
        for (int i = 0; i < cars.size(); i++) {
            Car otherCar = cars.get(i);
                if (Route.distance(car, otherCar) < 60
                        && Route.distance(car.speed, otherCar.route.startSpeed) < 0.5) {
                    return;
            }
        }

        cars.add(car);
        car.start();
    }
}
