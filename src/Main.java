public class Main {
    public static void main(String[] args) {
        int left = 0;
        int right = 400;
        int top = 0;
        int bottom = 400;

        StdDraw.setXscale(left, right);
        StdDraw.setYscale(bottom, top);
        StdDraw.enableDoubleBuffering();

        boolean isDragging = false;
        boolean isFreefallActive = true;
        double gravity = 0.981;
        int circleRadius = 25;

        double airDrag = 0.99;
        double groundFriction = 0.98;
        double elasticity = 0.8;

        // Random initial coords
        double initialX = 360;
        double initialY = 255;
        double posX = initialX;
        double posY = initialY;

        // Random initial velocity
        double vx = -15.5;
        double vy = 0.0;

        double shootBallButtonSize = 30;
        double shootBallButtonPosX = shootBallButtonSize * 2;
        double shootBallButtonPosY = shootBallButtonSize / 2;

        while (true) {
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            boolean isBallPressed = StdDraw.isMousePressed() && mouseX >= posX - circleRadius && mouseX <= posX + circleRadius && mouseY >= posY - circleRadius && mouseY <= posY + circleRadius;
            boolean isShootBallButtonPressed = StdDraw.isMousePressed() && mouseX >= shootBallButtonPosX - circleRadius && mouseX <= shootBallButtonPosX + circleRadius && mouseY >= shootBallButtonPosY - circleRadius && mouseY <= shootBallButtonPosY + circleRadius;

            if (isShootBallButtonPressed) {
                posX = initialX;
                posY = initialY;
                vx = -15;
            }

            if (isBallPressed) {
                isDragging = true;
            } else if (!isShootBallButtonPressed) {
                isDragging = false;
                StdDraw.setPenColor(StdDraw.CYAN);
                if (isFreefallActive) {
                    posX += vx;
                    posY += vy;

                    // bounce Y
                    if (posY >= bottom - circleRadius) {
                        posY = bottom - circleRadius;
                        vy = -(vy * elasticity);
                    }

                    // bounce X
                    if ((posX >= right - circleRadius) || (posX <= left + circleRadius)) {
                        posX = (posX < (left + circleRadius) ? (left + circleRadius) : (right - circleRadius));
                        vx = -(vx * elasticity);
                    }

                    vy += gravity;
                    vx *= airDrag;
                    vy *= airDrag;
                    if (posY >= (bottom - circleRadius)) {
                        vx *= groundFriction;
                    }
                }
            }

            if (isDragging) {
                posX = mouseX;
                posY = mouseY;
                StdDraw.setPenColor(StdDraw.BOOK_RED);
            }
            StdDraw.clear();
            drawShootBallButton(shootBallButtonSize);
            drawBall(posX, posY, circleRadius);
            StdDraw.show();
            StdDraw.pause(10);
        }
    }

    private static void drawBall(double posX, double posY, int radius) {
        StdDraw.setPenColor(StdDraw.CYAN);
        StdDraw.filledCircle(posX, posY, radius);
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

    // Randomized shooting of the ball
    private static void shootBall() {

    }
}
