import java.awt.*;

class Boundaries {
    int left;
    int right;
    int top;
    int bottom;

    Boundaries(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }
}

class Ball {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double airDrag = 0.99;
    private double groundFriction = 0.98;
    private double elasticity = 0.8;
    private double gravity = 0.981;
    private Boundaries boundaries;
    private Color color;
    int radius;

    Ball(double x, double y, double vx, double vy, int radius, Boundaries boundaries, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.boundaries = boundaries;
        this.color = color;
    }

    void setXVelocity(double vx) {
        this.vx = vx;
    }

    void setYVelocity(double vy) {
        this.vy = vy;
    }

    void setElasticity(double value) {
        this.elasticity = value;
    }

    void setGroundFriction(double value) {
        this.groundFriction = value;
    }

    void setAirDrag(double value) {
        this.airDrag = value;
    }

    // Make sure that StdDraw.clear() is called before drawing
    void draw() {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(x, y, radius);
    }

    void updateVelocity() {
        x += vx;
        y += vy;

        // bounce Y
        if (y >= boundaries.bottom - radius) {
            y = boundaries.bottom - radius;
            vy = -(vy * elasticity);
        }

        // bounce X
        if ((x >= boundaries.right - radius) || (x <= boundaries.left + radius)) {
            x = (x < (boundaries.left + radius) ? (boundaries.left + radius) : (boundaries.right - radius));
            vx = -(vx * elasticity);
        }

        vy += gravity;
        vx *= airDrag;
        vy *= airDrag;
        if (y >= (boundaries.bottom - radius)) {
            vx *= groundFriction;
        }
    }

    void updateBounce() {
        x = (x < (boundaries.left + radius) ? (boundaries.left + radius) : (boundaries.right - radius));
        vx = -(vx * elasticity);
    }

    void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    boolean isPressed() {
        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();
        return StdDraw.isMousePressed() && mouseX >= x - radius && mouseX <= x + radius && mouseY >= y - radius && mouseY <= y + radius;
    }

    boolean collides(Ball ball) {
        return  distance(ball) <= radius + ball.radius;
    }

    void reverse() {
        vx = -vx;
        vy = -vy;
    }

    double distance(Ball ball) {
        return Math.sqrt(Math.pow(x - ball.x, 2) + Math.pow(y - ball.y, 2));
    }
}
public class Main {
    public static void main(String[] args) {
        int left = 0;
        int right = 400;
        int top = 0;
        int bottom = 400;

        StdDraw.setXscale(left, right);
        StdDraw.setYscale(bottom, top);
        StdDraw.enableDoubleBuffering();

        Boundaries boundaries = new Boundaries(left, right, top, bottom);

        // De/activates free fall
        boolean isFreefallActive = true;

        int circleRadius = 25;

        // Random initial coords
        double initialX = 350;
        double initialY = top + circleRadius;
        double posX = initialX;
        double posY = initialY;

        // Random initial velocity
        double vx = -15.5;
        double vy = 0.0;

        double shootBallButtonSize = 30;
        double shootBallButtonPosX = shootBallButtonSize * 2;
        double shootBallButtonPosY = shootBallButtonSize / 2;

        Ball ball1 = new Ball(initialX, initialY, vx, vy, circleRadius, boundaries, Color.CYAN);
        Ball ball2 = new Ball(initialX - 200, initialY, vx, vy, circleRadius, boundaries, Color.darkGray);

        while (true) {
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();

            boolean isShootBallButtonPressed = StdDraw.isMousePressed() && mouseX >= shootBallButtonPosX - circleRadius && mouseX <= shootBallButtonPosX + circleRadius && mouseY >= shootBallButtonPosY - circleRadius && mouseY <= shootBallButtonPosY + circleRadius;

            if (isShootBallButtonPressed) {
                ball1.setPosition(initialX, initialY);
                ball2.setPosition(initialX - 200, initialY + 50);
                ball1.setXVelocity(-15);
                ball2.setXVelocity(-15);
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
                        ball1.reverse();
                        ball2.reverse();
                    }
                }
            }

            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            drawShootBallButton(shootBallButtonSize);
            ball1.draw();
            ball2.draw();
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
}
