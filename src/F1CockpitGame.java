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
    private double carX = WIDTH / 8;
    private double carY = HEIGHT / 8;
    private double accelerator = 0.015;
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

    private void drawDiagonalTrack(GraphicsContext gc, double baseX, double baseY, double length, double angle, double trackWidth) {
        // Calculate the end coordinates for the diagonal line segment
        double endX = baseX + length * Math.cos(Math.toRadians(angle));
        double endY = baseY + length * Math.sin(Math.toRadians(angle));
    
        // Calculate the offsets for the track width
        double offsetX = trackWidth * Math.sin(Math.toRadians(angle));
        double offsetY = trackWidth * Math.cos(Math.toRadians(angle));
    
        // Draw the track using the calculated coordinates
        gc.fillPolygon(
            new double[] {baseX, endX, endX + offsetX, baseX + offsetX},
            new double[] {baseY, endY, endY - offsetY, baseY - offsetY},
            4
        );
    }
    
    private void track(GraphicsContext gc){
        //fillArc(double x, double y, double w, double h, double startAngle, double arcExtent, ArcType closure)
        //fillRect(double x, double y, double width, double height)
        //background
        gc.setFill(Color.GREEN);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.BLACK);

        gc.fillRect(100, 100, 1200, 100);                              //first straightaway
        gc.fillArc(1200, 100, 200, 200, 0, 90, ArcType.ROUND);         //first curve

        gc.fillRect(1300,200, 100, 700);                               //second straight away
        gc.fillArc(1150, 800, 250, 200, 180, 180, ArcType.ROUND);      //second curve

        gc.fillRect(1150, 350, 100, 550);                              // third staraightaway
        gc.fillArc(1050, 250, 200, 200, 0, 135, ArcType.ROUND);         //third curve (120 degrees)

        drawDiagonalTrack(gc, 1080, 279, 700, 135, 100); //diagnol straightaway
        gc.fillArc(485, 673, 200, 200, 270, 45,ArcType.ROUND);          //diagnol curve

        gc.fillRect(100, 773, 485, 100);                                  //fourth straightaway
        gc.fillArc(0, 673, 200, 200, 180, 90, ArcType.ROUND);              //fourth arc

        gc.fillRect(0, 200, 100, 573);                                  //final straightaway
        gc.fillArc(0, 100, 200, 200, 90, 90, ArcType.ROUND);            //final arc

        gc.setStroke(Color.WHITE); // Set line color to black
        gc.setLineWidth(1);
        for(int i=0;i<1500;i+=100)
        {
            gc.strokeLine(0, i, 1500, i);
        }
        for(int i=0;i<1500;i+=100)
        {
            gc.strokeLine(i,0,i,1000);
        }

        
    
    }
    private void draw(GraphicsContext gc) {
        // Clear the canvas
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.save();
        gc.setFill(Color.GREEN);
        // You can draw the borders and other elements of your track here
        track(gc);
        gc.translate(carX + carImage.getWidth() / 2, carY + carImage.getHeight() / 2); // Move to the center of the image
        gc.rotate(carAngle);
        gc.drawImage(carImage, -carImage.getWidth() / 2, -carImage.getHeight() / 2); // Draw the image centered at its pivot
        gc.restore(); // Restore the transformation matrix to its previous state
 
    }


}
