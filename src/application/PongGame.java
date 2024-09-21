package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class PongGame extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 15;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_SIZE = 20;
    private static final int WINNING_SCORE = 1; // Score to end the game

    private double ballX = WIDTH / 2;
    private double ballY = HEIGHT / 2;
    private double ballSpeedX = 5;
    private double ballSpeedY = 3;

    private double paddle1Y = HEIGHT / 2;
    private double paddle2Y = HEIGHT / 2;

    private int score1 = 0;
    private int score2 = 0;

    private Random random = new Random();
    private Timeline timeline;
    private boolean gameOver = false;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: white;");

        timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> run(gc, resultLabel)));
        timeline.setCycleCount(Timeline.INDEFINITE);

        canvas.setOnMouseMoved(e -> {
            if (!gameOver) {
                paddle1Y = e.getY();
            }
        });

        StackPane root = new StackPane(canvas, resultLabel);
        Scene scene = new Scene(root);

        // Add key listener to restart the game on space press
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE && gameOver) {
                restartGame(resultLabel); // Reset everything and restart the game
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Pong Game");
        primaryStage.show();
        timeline.play();
    }

    private void run(GraphicsContext gc, Label resultLabel) {
        // Check if the game is over
        if (score1 >= WINNING_SCORE || score2 >= WINNING_SCORE) {
            timeline.stop();
            gameOver = true;
            String winner = score1 >= WINNING_SCORE ? "Player" : "Computer";
            resultLabel.setText("    "+winner + " Wins! \nPress SPACE to Restart");
            return;
        }

        // Clear the canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Set text color
        gc.setFill(Color.WHITE);

        // Draw scores
        gc.fillText("Player: " + score1, 100, 50);
        gc.fillText("Computer: " + score2, WIDTH - 150, 50);

        // Draw paddles
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, paddle1Y - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT);
        gc.fillRect(WIDTH - PADDLE_WIDTH, paddle2Y - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball
        gc.setFill(Color.YELLOW);
        gc.fillOval(ballX - BALL_SIZE / 2, ballY - BALL_SIZE / 2, BALL_SIZE, BALL_SIZE);

        // Move the ball
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        // Ball collision with top and bottom
        if (ballY <= 0 || ballY >= HEIGHT) {
            ballSpeedY = -ballSpeedY;
        }

        // Ball collision with paddles
        if (ballX <= PADDLE_WIDTH && ballY > paddle1Y - PADDLE_HEIGHT / 2 && ballY < paddle1Y + PADDLE_HEIGHT / 2) {
            ballSpeedX = -ballSpeedX;
            ballSpeedY += (ballY - paddle1Y) * 0.1;
            randomizeBallSpeed();
        }

        if (ballX >= WIDTH - PADDLE_WIDTH && ballY > paddle2Y - PADDLE_HEIGHT / 2 && ballY < paddle2Y + PADDLE_HEIGHT / 2) {
            ballSpeedX = -ballSpeedX;
            ballSpeedY += (ballY - paddle2Y) * 0.1;
            randomizeBallSpeed();
        }

        // Score points
        if (ballX < 0) {
            score2++;
            resetBall();
        }
        if (ballX > WIDTH) {
            score1++;
            resetBall();
        }

        // Move computer paddle
        paddle2Y += (ballY - paddle2Y) * 0.1;
    }

    private void resetBall() {
        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;
        ballSpeedX = (random.nextBoolean() ? 1 : -1) * (4 + random.nextDouble() * 2);
        ballSpeedY = (random.nextBoolean() ? 1 : -1) * (2 + random.nextDouble() * 2);
    }

    private void randomizeBallSpeed() {
        double speed = Math.sqrt(ballSpeedX * ballSpeedX + ballSpeedY * ballSpeedY);
        double angle = Math.atan2(ballSpeedY, ballSpeedX) + (random.nextDouble() - 0.5) * Math.PI / 6;
        ballSpeedX = speed * Math.cos(angle);
        ballSpeedY = speed * Math.sin(angle);
    }

    private void restartGame(Label resultLabel) {
        // Reset game variables
        score1 = 0;
        score2 = 0;
        paddle1Y = HEIGHT / 2;
        paddle2Y = HEIGHT / 2;
        resetBall();
        
        // Clear the label text
        resultLabel.setText("");

        // Start the game again
        gameOver = false;
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
