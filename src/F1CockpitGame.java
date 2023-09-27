import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

public class F1CockpitGame extends Application {
    DecimalFormat df = new DecimalFormat("#.00");
    Set<KeyCode> activeKeys = new HashSet<>();
    private static final double WIDTH = 1500;
    private static final double HEIGHT = 1000;
    private Image carImage;
    private double carX = WIDTH / 7;
    private double carY = HEIGHT / 7;
    private double accelerator = 0.02;
    private double turnFriction = 0.01;
    private double brake = 0.03;
    private double speed = 0;
    private double carAngle = 90;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        carImage = new Image("file:resources/f1Car.png");
        primaryStage.setTitle("F1 Cockpit Game");
        Pane root = new Pane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        scene.setOnKeyPressed(e -> activeKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));
        AnimationTimer timer = new AnimationTimer() {
           // ... (keep the previous part of the code unchanged)

@Override
public void handle(long now) {
    if (activeKeys.contains(KeyCode.W)) {
        speed += accelerator;
    }
    if (activeKeys.contains(KeyCode.S)) {
        speed -= brake;
        if(speed < 0) {
            speed = 0;
        }
    }
    if (activeKeys.contains(KeyCode.A)){
        if(speed>0){
        carAngle -= 2;
        speed -= turnFriction;
        }
    }
    if (activeKeys.contains(KeyCode.D)){
        if(speed>0){
        carAngle += 2;
        speed -= turnFriction;
        }
    }

    double radians = Math.toRadians(carAngle);

    carX += speed * Math.sin(radians); 
    carY -= speed * Math.cos(radians);

    draw(gc);
}

        };
        
        timer.start(); // Start the animation timer

        primaryStage.show();
        draw(gc);
        primaryStage.show();
    }

    private void draw(GraphicsContext gc) {
        // Clear the canvas
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.save();
        gc.setFill(Color.GREEN);
        // You can draw the borders and other elements of your track here
         gc.setFill(Color.GREEN);
    gc.fillRect(0, 0, WIDTH, HEIGHT);

    // Draw the straight track (For instance)
    gc.setFill(Color.BLACK);
    gc.fillRect(200, 200, 200, 400);

    // Draw a simple curve (for instance)
    gc.fillArc(400, 400, 200, 200, 90, 90, ArcType.ROUND);

    // Draw red and white striped borders (just for the straight track as an example)
    for (int i = 0; i < 400; i += 20) {
        gc.setFill(i % 40 == 0 ? Color.RED : Color.WHITE);
        gc.fillRect(190, 200 + i, 10, 20); // left border
        gc.fillRect(400, 200 + i, 10, 20); // right border
    }
        gc.translate(carX + carImage.getWidth() / 2, carY + carImage.getHeight() / 2); // Move to the center of the image
        gc.rotate(carAngle);
        gc.drawImage(carImage, -carImage.getWidth() / 2, -carImage.getHeight() / 2); // Draw the image centered at its pivot
    
        gc.restore(); // Restore the transformation matrix to its previous state
        for (int i = 0; i < 400; i += 20) {
            gc.setFill(i % 40 == 0 ? Color.RED : Color.WHITE);
            gc.fillRect(190, 200 + i, 10, 20); // left border
            gc.fillRect(400, 200 + i, 10, 20); // right border
        }
    
        // Reset logic (basic example for the straight track)
        if (carX > 190 && carX < 410 && carY > 200 && carY < 600) {
            // Car is on the straight track
            if (carX < 200 || carX > 400) {
                resetGame(); // If it's on the border area, reset
            }
        }
        // You will need to add similar checks for curves and other parts of the track.
    
        // ... Rest of the car drawing code
    }
    
    private void resetGame() {
        carX = WIDTH / 7;
        carY = HEIGHT / 7;
        carAngle = 90;
        speed = 0;
    }
    }
    


