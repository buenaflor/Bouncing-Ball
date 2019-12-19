import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class Point {
    double x;
    double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double distance(Point otherPoint) {
        return Math.sqrt(Math.pow(otherPoint.x - x, 2) + Math.pow(otherPoint.y - y, 2));
    }
}

class Line {
    Point p1;
    Point p2;

    Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    void draw() {
        StdDraw.line(p1.x, p1.y, p2.x, p2.y);
    }
}

class WindowBoundaries {
    int left;
    int right;
    int top;
    int bottom;

    WindowBoundaries(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }
}

class Ball {
    boolean isActive = false;
    private Point point;
    private double vx;
    private double vy;
    private double airDrag = 0.99;
    private double groundFriction = 0.98;
    private double elasticity = 0.6;            // Elasticity determines how much the ball bounces during collisions
    private double gravity = 0.981;
    private WindowBoundaries boundaries;
    private Color color;
    int radius;
    double mass = 10;                            // Mass is an important factor when calculating the new velocity during ball to ball collisions

    Ball(double x, double y, double vx, double vy, int radius, WindowBoundaries boundaries, Color color) {
        this.point = new Point(x, y);
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.boundaries = boundaries;
        this.color = color;
    }

    void setXVelocity(double vx) { this.vx = vx; }
    void setYVelocity(double vy) { this.vy = vy; }
    void setMass(double value) { this.mass = value; }
    void setElasticity(double value) { this.elasticity = value; }
    void setGroundFriction(double value) { this.groundFriction = value; }
    void setAirDrag(double value) { this.airDrag = value; }

    double getX() { return point.x; }
    double getY() { return point.y; }
    double getVX() { return vx; }
    double getVY() { return vy; }
    Point getPoint() { return point; }

    // Make sure that StdDraw.clear() is called before drawing
    void draw() {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(point.x, point.y, radius);
        isActive = true;
    }

    private double oldX = 0;
    private double oldY = 0;

    double heightToLine(Line line) {
        double c = line.p1.distance(line.p2);
        double a = line.p2.distance(point);
        double b = line.p1.distance(point);
        return (0.5 * Math.sqrt((a + b + c) * (-a + b + c) * (a - b + c) * (a + b - c)) / c);
    }

    boolean isCollidingWithLine(Line line) {
        return heightToLine(line) - radius < 1;
    }

    void updateVelocity() {
        oldX = point.x;
        oldY = point.y;
        point.x += vx;
        point.y += vy;

        // bounce Y
        if (point.y >= boundaries.bottom - radius) {
            point.y = boundaries.bottom - radius;
            vy = -(vy * elasticity);
        }

        // bounce X
        if ((point.x >= boundaries.right - radius) || (point.x <= boundaries.left + radius)) {
            point.x = (point.x < (boundaries.left + radius) ? (boundaries.left + radius) : (boundaries.right - radius));
            vx = -(vx * elasticity);
        }

        vy += gravity;
        vx *= airDrag;
        vy *= airDrag;
        if (point.y >= (boundaries.bottom - radius)) {
            vx *= groundFriction;
        }

        // Balls are now standing still
        if (Math.abs(oldX - point.x) < 0.1) {
            vx = 0;
        }

        // Balls are not bouncing anymore
        if (Math.abs(oldY - point.y) < 0.1 && point.y == boundaries.bottom - radius) {
            vy = 0;
        }
    }

    void setPosition(double x, double y) {
        this.point.x = x;
        this.point.y = y;
    }

    boolean isPressed() {
        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();
        return StdDraw.isMousePressed() && mouseX >= point.x - radius && mouseX <= point.x + radius && mouseY >= point.y - radius && mouseY <= point.y + radius;
    }

    boolean collides(Ball ball) {
        if (!ball.isActive) {
            return false;
        }
        return distance(ball) <= radius + ball.radius;
    }

    private List<Point> getIntersectionPoints(Line line) {
        double x0 = line.p2.x - line.p1.x;
        double y0 = line.p2.y - line.p1.y;
        double x1 = line.p1.x - point.x;
        double y1 = line.p1.y - point.y;
        double b = (x0 * x1 + y0 * y1);
        double c = 2 * (x0 * x0 + y0 * y0);
        b *= -2;
        double d = Math.sqrt(b * b - 2 * c * (x1 * x1 + y1 * y1 - radius * radius));

        List<Point> points = new ArrayList<>();
        if(Double.isNaN(d)) {
            return points;
        }
        double u1 = (b - d) / c;
        double u2 = (b + d) / c;
        if (u1 <= 1 && u1 >= 0) {
            double resX = line.p1.x + x0 * u1;
            double resY = line.p1.y + y0 * u1;
            Point intersectionPoint = new Point(resX, resY);
            points.add(intersectionPoint);
        }
        if(u2 <= 1 && u2 >= 0) {
            double resX = line.p1.x + x0 * u2;
            double resY = line.p1.y + y0 * u2;
            Point intersectionPoint = new Point(resX, resY);
            points.add(intersectionPoint);
        }
        return points;
    }

    Point getAvgIntersectionPoint(Line line) {
        Point avgPoint = null;
        List<Point> intersectionPoints = getIntersectionPoints(line);
        if (getIntersectionPoints(line).size() == 2) {
            Point p1 = intersectionPoints.get(0);
            Point p2 = intersectionPoints.get(1);
            double averageX = (p1.x + p2.x) / 2;
            double averageY = (p1.y + p2.y) / 2;
            avgPoint = new Point(averageX, averageY);
        }
        return avgPoint;
    }

    void reverseTrajectory(Ball ball) {
        double newVX0 = (vx * (mass - ball.mass) + (2 * ball.mass * ball.vx)) / (mass + ball.mass) * elasticity;
        double newVY0 = (vy * (mass - ball.mass) + (2 * ball.mass * ball.vy)) / (mass + ball.mass) * elasticity;
        double newVX1 = (vx * (ball.mass - mass) + (2 * mass * vx)) / (mass + ball.mass) * elasticity;
        double newVY1 = (vy * (ball.mass - mass) + (2 * mass * vy)) / (mass + ball.mass) * elasticity;

        vx = newVX0;
        vy = newVY0;
        ball.setXVelocity(newVX1);
        ball.setYVelocity(newVY1);
    }

    double distance(Ball ball) {
        return Math.sqrt(Math.pow(point.x - ball.point.x, 2) + Math.pow(point.y - ball.point.y, 2));
    }

    void drawHelperLines(Line line) {
        StdDraw.setPenColor(Color.black);
        StdDraw.line(line.p1.x, line.p1.y, point.x, point.y);
        StdDraw.line(line.p2.x, line.p2.y, point.x, point.y);
    }
}

public class Main {
    public static void main(String[] args) {
        int left = 0;
        int right = 500;
        int top = 0;
        int bottom = 400;
        StdDraw.setCanvasSize(right, bottom);
        StdDraw.setXscale(left, right);
        StdDraw.setYscale(bottom, top);
        StdDraw.enableDoubleBuffering();
        WindowBoundaries boundaries = new WindowBoundaries(left, right, top, bottom);

        // De/activates free fall
        boolean isFreefallActive = true;

        int circleRadius = 25;

        // Random initial coords
        double initialX = 350;
        double initialY = bottom / 2;

        // Random initial velocity
        double vx = -1.5;
        double vy = -5.0;

        double shootBallButtonSize = 30;
        double shootBallButtonPosX = shootBallButtonSize * 2;
        double shootBallButtonPosY = shootBallButtonSize / 2;

        Ball ball1 = new Ball(initialX, initialY, vx, vy, circleRadius, boundaries, Color.CYAN);
        Ball ball2 = new Ball(initialX - 200, initialY, -vx, vy, circleRadius, boundaries, Color.darkGray);
        ball2.setMass(40);

        Point point1 = new Point(left, 200);
        Point point2 = new Point(250, bottom);
        Line line = new Line(point1, point2);

        while (true) {
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            boolean isShootBallButtonPressed = StdDraw.isMousePressed() && mouseX >= shootBallButtonPosX - circleRadius && mouseX <= shootBallButtonPosX + circleRadius && mouseY >= shootBallButtonPosY - circleRadius && mouseY <= shootBallButtonPosY + circleRadius;

            if (isShootBallButtonPressed) {
                ball1.setPosition(initialX, initialY);
                ball2.setPosition(initialX, initialY + 100);
                ball1.setXVelocity(-5.5);
                ball1.setYVelocity(-15.5);
            }

            if (ball1.isPressed()) {
                ball1.setPosition(mouseX, mouseY);
            } else if (ball2.isPressed()) {
                ball2.setPosition(mouseX, mouseY);
            } else if (!isShootBallButtonPressed) {
                if (isFreefallActive) {
                    ball1.updateVelocity();
                    ball2.updateVelocity();
                    if (ball1.collides(ball2)) {
                        ball1.reverseTrajectory(ball2);
                    }
                }
            }

            StdDraw.point(ball1.getX(), ball1.getY());

            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            line.draw();
            drawShootBallButton(shootBallButtonSize);
            ball1.draw();
            ball2.draw();

            // Draw helper lines for ball1
            ball1.drawHelperLines(line);

            if (ball1.isCollidingWithLine(line)) {
                double dx = line.p2.x - line.p1.x;
                double dy = line.p2.y - line.p1.y;
                Point normal = new Point(-dy, dx);
                double lengthOfNormal = Math.sqrt(normal.x * normal.x + normal.y * normal.y);
                normal.x /= lengthOfNormal;
                normal.y /= lengthOfNormal;
                double distanceAlongNormal = ball1.getVX() * normal.x + ball1.getVY() * normal.y;
                double newVX = ball1.getVX() - 2 * distanceAlongNormal * normal.x;
                double newVY = ball1.getVY() - 2 * distanceAlongNormal * normal.y;
                ball1.setYVelocity(newVY * 0.6);
                ball1.setXVelocity(newVX * 0.6 * 0.98);
            }

            StdDraw.show();
            StdDraw.pause(10);
        }
    }


    private static void drawShootBallButton(double size) {
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.filledRectangle(size * 2, size / 2, size, size);
        drawShootBallText(size * 2, size * 2, "Press to shoot");
    }

    private static void drawShootBallText(double posX, double posY, String text) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(posX, posY, text);
    }

    public static double dotProduct(Point p1, Point p2){
        return p1.x * p2.x + p1.y * p2.y;
    }
}
