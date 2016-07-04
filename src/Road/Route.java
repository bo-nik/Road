/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Road;

import java.awt.geom.Point2D;
import java.util.Random;

/**
 *
 * @author bo-nik
 */
public class Route {

    public enum RouteDirection {

        FORWARD, FORWARD_AND_LEFT, FORWARD_AND_RIGHT
    }

    public final RouteDirection direction;

    public double theta;
    public Double thetaAtTheBeginingOfRoute;
    public Point2D.Double startSpeed;
    public Point2D.Double startPosition;

    private final double safeDistanceInMove;
    private final double safeDistanceOnStop;

    public Route(RouteDirection direction) {
        this.direction = direction;
        theta = 0;

        thetaAtTheBeginingOfRoute = null;
        startSpeed = null;
        startPosition = null;

        safeDistanceInMove = 70 + new Random().nextInt(20);
        safeDistanceOnStop = 40 + new Random().nextInt(10);
    }

    public synchronized void moveCar(Car car) {

        if (thetaAtTheBeginingOfRoute == null) {
            thetaAtTheBeginingOfRoute = Math.atan2(-car.speed.y, car.speed.x);
        }
        if (startSpeed == null) {
            startSpeed = new Point2D.Double(car.speed.x, car.speed.y);
        }
        if (startPosition == null) {
            startPosition = new Point2D.Double(car.position.x, car.position.y);
        }

        car.moving = false;

        // check traffic lights
        if (Math.abs(car.speed.x) < 1e-6 && car.speed.y < 0) { // moving to North, check bottom right traffic light
            if (car.road.get().bottomRightTrafficLight.color != TrafficLight.TrafficLightColor.GREEN) {
                if (70 < car.position.y && car.position.y < 90) {
                    return;
                }
            }
        } else if (Math.abs(car.speed.x) < 1e-6 && car.speed.y > 0) { // moving to South, check top left traffic light
            if (car.road.get().topLeftTrafficLight.color != TrafficLight.TrafficLightColor.GREEN) {
                if (-70 > car.position.y && car.position.y > -90) {
                    return;
                }
            }
        } else if (car.speed.x < 0 && Math.abs(car.speed.y) < 1e-6) { // moving to West, check top right traffic light
            if (car.road.get().topRightTrafficLight.color != TrafficLight.TrafficLightColor.GREEN) {
                if (70 < car.position.x && car.position.x < 90) {
                    return;
                }
            }
        } else if (car.speed.x > 0 && Math.abs(car.speed.y) < 1e-6) { // moving to East, check bottom left traffic light
            if (car.road.get().bottomLeftTrafficLight.color != TrafficLight.TrafficLightColor.GREEN) {
                if (-70 > car.position.x && car.position.x > -90) {
                    return;
                }
            }
        }

        // check if are there cars staying forward
        double carTheta = Math.atan2(-car.speed.y, car.speed.x);
        for (int i = 0; i < car.road.get().cars.size(); i++) {
            Car otherCar = car.road.get().cars.get(i);
            if (otherCar != null && otherCar != car
                    && Math.abs(otherCar.position.x) <= 250 && Math.abs(otherCar.position.x) <= 250
                    && theta <= 0.41) {
                double otherCarTheta = Math.atan2(-otherCar.speed.y, otherCar.speed.x);
                // if car and other car are moving with the same theta (angle)
                if (Math.abs(otherCarTheta - carTheta) < Math.PI / 4) {
                    // if car and other car are moving in the same direction
                    if (otherCar.route.startPosition != null
                            && Route.distance(startPosition, otherCar.route.startPosition) < 1) {
                        // if they are near
                        double safeDistance = safeDistanceInMove;
                        if (!otherCar.moving) {
                            safeDistance = safeDistanceOnStop;
                        }
                        if (Route.distance(car, otherCar) < safeDistance) {
                            // check what car is forward
                            Point2D.Double carPostitionAfter60 = new Point2D.Double(
                                    car.position.x + car.speed.x * safeDistance,
                                    car.position.y + car.speed.y * safeDistance);
                            Point2D.Double otherCarPositionAfter60 = new Point2D.Double(
                                    otherCar.position.x + otherCar.speed.x * safeDistance,
                                    otherCar.position.y + otherCar.speed.y * safeDistance);
                            double distanceFromCarAfter60ToOtherCar = Route.distance(otherCar.position, carPostitionAfter60);
                            double distanceFromOtherCarAfter60ToCar = Route.distance(car.position, otherCarPositionAfter60);
                            if (distanceFromCarAfter60ToOtherCar < distanceFromOtherCarAfter60ToCar) {
                                return;
                            }
                        }
                    }
                }
            }
        }

        // turning
        double speedLen = Math.sqrt(Math.pow(car.speed.x, 2) + Math.pow(car.speed.y, 2));
        switch (direction) {
            case FORWARD:
                break;
            case FORWARD_AND_LEFT: {
                // if there is oncoming car -> stop and give way
                // if car is on crossroad
                if (Math.abs(car.position.x) < 30 && Math.abs(car.position.y) < 30 && theta < 0.4) {
                    for (int i = 0; i < car.road.get().cars.size(); i++) {
                        Car otherCar = car.road.get().cars.get(i);
                        if (otherCar != car
                                && Math.abs(otherCar.position.x) <= 250 && Math.abs(otherCar.position.x) <= 250
                                && otherCar.moving) {
                            // give way only cars, wich are moving forward
                            if (otherCar.route.direction == Route.RouteDirection.FORWARD) {
                                int safeDistance = 120;
                                // if car in on the right lane
                                if ((Math.abs(otherCar.speed.x) < 1e-3 && Math.abs(otherCar.position.x) > 20)
                                        || (Math.abs(otherCar.speed.y) < 1e-3 && Math.abs(otherCar.position.y) > 20)) {
                                    safeDistance = 170;
                                }
                                // check distance between cars
                                double summarySpeed = speedLen + Math.sqrt(Math.pow(otherCar.speed.x, 2) + Math.pow(otherCar.speed.y, 2));
                                if (Route.distance(car, otherCar) <= safeDistance * summarySpeed / 2.0) {
                                    // check if cars are moving towards each other
                                    // and if other car is forward (it caт pass car and way is free)
                                    if (((car.route.startSpeed.x < 1e-3)
                                            && areDifferentSigns(car.route.startSpeed.y, otherCar.route.startSpeed.y)
                                            && !(areSameSigns(car.position.y, otherCar.position.y) && Math.abs(otherCar.position.y) > Math.abs(car.position.y)))
                                            || ((car.route.startSpeed.y < 1e-3)
                                            && areDifferentSigns(car.route.startSpeed.x, otherCar.route.startSpeed.x)
                                            && !(areSameSigns(car.position.x, otherCar.position.x) && Math.abs(otherCar.position.x) > Math.abs(car.position.x)))) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }

                // turn
                double dTheta = Math.PI / 2 / 99 * speedLen;
                if (theta < Math.PI / 2
                        && Math.abs(car.position.x) <= 50 && Math.abs(car.position.y) <= 50) { // turning left
                    theta += dTheta;
                } else if (Math.abs(theta - Math.PI / 2) < dTheta / speedLen) { // after left turn
                    theta = Math.PI / 2;
                }
                car.speed = new Point2D.Double(speedLen * Math.cos(thetaAtTheBeginingOfRoute + theta),
                        -speedLen * Math.sin(thetaAtTheBeginingOfRoute + theta));
                break;
            }
            case FORWARD_AND_RIGHT: {
                double dTheta = Math.PI / 2 / 18 * speedLen;
                if (theta < Math.PI / 2
                        && Math.abs(car.position.x) <= 50 && Math.abs(car.position.y) <= 50) { // turning right
                    theta += dTheta;
                } else if (Math.abs(theta - Math.PI / 2) < dTheta / speedLen) { // after right turn
                    theta = Math.PI / 2;
                }
                car.speed = new Point2D.Double(speedLen * Math.cos(thetaAtTheBeginingOfRoute - theta),
                        -speedLen * Math.sin(thetaAtTheBeginingOfRoute - theta));
                break;
            }
        }

        car.moving = true;
        car.position.x += car.speed.x;
        car.position.y += car.speed.y;
    }

    public static double distance(Car car1, Car car2) {
        return Route.distance(car1.position, car2.position);
    }

    public static double distance(Point2D.Double p1, Point2D.Double p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    private boolean areSameSigns(double a, double b) {
        return Math.abs(a + b) == Math.abs(a) + Math.abs(b);
    }

    private boolean areDifferentSigns(double a, double b) {
        return !areSameSigns(a, b);
    }
}
