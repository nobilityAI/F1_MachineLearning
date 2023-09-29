import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Arc;

public class F1CockpitGame extends Application {
    DecimalFormat df = new DecimalFormat("#.00");
    Set<KeyCode> activeKeys = new HashSet<>();
    private static final double WIDTH = 1500;
    private static final double HEIGHT = 1000;
    private Image carImage;
    private double carX = WIDTH / 8;
    private double carY = HEIGHT / 8;
    private double accelerator = 0.0175;
    private double turnFriction = 0.0175;
    private double friction = 0.005;
    private double brake = 0.03;
    private double speed = 0;
    private double carAngle = 90;
    private List<Shape> trackBorders = new ArrayList<>();

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
        initializeTrackBorders(gc);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        scene.setOnKeyPressed(e -> activeKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));
        AnimationTimer timer = new AnimationTimer() {
            // ... (keep the previous part of the code unchanged)
            private void handleCollision() {
                // Logic for what to do when the car hits the border
                // For example:
                System.out.println("Collision Detected"); // Stop the car
                // TODO: Add other logic/actions as required
            }

            @Override
            public void handle(long now) {
                if (activeKeys.contains(KeyCode.W)) {
                    if (speed * 48.421 < 242)
                        speed += accelerator;
                }
                if (activeKeys.contains(KeyCode.S)) {
                    speed -= brake;
                    if (speed < 0) {
                        speed = 0;
                    }
                }
                if (activeKeys.contains(KeyCode.A)) {
                    if (speed > 0) {
                        carAngle -= 1;
                        speed -= turnFriction;
                    }
                }
                if (activeKeys.contains(KeyCode.D)) {
                    if (speed > 0) {
                        carAngle += 1;
                        speed -= turnFriction;
                    }
                }
                if (isCarCollidingWithBorders()) {
                    handleCollision();
                }
                double radians = Math.toRadians(carAngle);
                if (speed > 0) {
                    speed -= friction;
                }
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

    private void drawDiagonalTrack(GraphicsContext gc, double baseX, double baseY, double length, double angle,
            double trackWidth, boolean isBorder) {
        // Calculate the end coordinates for the diagonal line segment
        double endX = baseX + length * Math.cos(Math.toRadians(angle));
        double endY = baseY + length * Math.sin(Math.toRadians(angle));

        // Calculate the offsets for the track width
        double offsetX = trackWidth * Math.sin(Math.toRadians(angle));
        double offsetY = trackWidth * Math.cos(Math.toRadians(angle));

        // Draw the track using the calculated coordinates
        if (!isBorder) {
            gc.fillPolygon(
                    new double[] { baseX, endX, endX + offsetX, baseX + offsetX },
                    new double[] { baseY, endY, endY - offsetY, baseY - offsetY },
                    4);
        } else {
            // Otherwise, add the border for collision detection
            Polygon diagonal = new Polygon(
                    baseX, baseY,
                    endX, endY,
                    endX + offsetX, endY - offsetY,
                    baseX + offsetX, baseY - offsetY);
            diagonal.setFill(Color.TRANSPARENT); // Border is invisible
            trackBorders.add(diagonal);
        }
    }

    private void initializeTrackBorders(GraphicsContext gc) {
        // Straightaways
        trackBorders.add(new Rectangle(100, 95, 1200, 5));
        trackBorders.add(new Rectangle(100, 200, 1200, 5));

        trackBorders.add(new Rectangle(1295, 200, 5, 700));
        trackBorders.add(new Rectangle(1400, 200, 5, 700));

        trackBorders.add(new Rectangle(1145, 350, 5, 550));
        trackBorders.add(new Rectangle(1250, 350, 5, 550));

        trackBorders.add(new Rectangle(100, 768, 485, 5));
        trackBorders.add(new Rectangle(100, 870, 485, 5));

        trackBorders.add(new Rectangle(0, 200, 5, 573));
        trackBorders.add(new Rectangle(100, 200, 5, 573));

        Arc arc1 = new Arc(1300, 200, 102.5, 102.5, 0, 90);
        arc1.setType(ArcType.OPEN);
        trackBorders.add(arc1);
//Arc(double centerX, double centerY, double radiusX, double radiusY, double startAngle, double length)

       Arc arc2 = new Arc(1150,800,250,200,180,180);
       arc2.setType(ArcType.OPEN);
       trackBorders.add(arc2);
// gc.fillArc(1150, 800, 250, 200, 180, 180, ArcType.ROUND);
        drawDiagonalTrack(gc, 1077, 276, 699, 135, 5, true);
        drawDiagonalTrack(gc, 1145, 350, 699, 135, 5, true);

        for (Shape border : trackBorders) {
            border.setFill(Color.WHITE); 
        }
    }

    private boolean isCarCollidingWithBorders() {
        double carCenterX = carX + carImage.getWidth() / 2;
        double carCenterY = carY + carImage.getHeight() / 2;

        Circle carCenter = new Circle(carCenterX, carCenterY, 5); // 5 is a small radius to represent the car's center

        for (Shape border : trackBorders) {
            if (Shape.intersect(carCenter, border).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
        }
        return false;
    }

    private void track(GraphicsContext gc) {
        // background
        gc.setFill(Color.GREEN);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.GRAY);

        gc.fillRect(100, 100, 1200, 100); // first straightaway
        gc.fillArc(1200, 100, 200, 200, 0, 90, ArcType.ROUND); // first curve

        gc.fillRect(1300, 200, 100, 700); // second straight away
        gc.fillArc(1150, 800, 250, 200, 180, 180, ArcType.ROUND); // second curve

        gc.fillRect(1150, 350, 100, 550); // third staraightaway
        gc.fillArc(1050, 250, 200, 200, 0, 135, ArcType.ROUND); // third curve (120 degrees)

        drawDiagonalTrack(gc, 1080, 279, 700, 135, 100, false); // diagnol straightaway

        gc.fillArc(485, 673, 200, 200, 270, 45, ArcType.ROUND); // diagnol curve

        gc.fillRect(100, 773, 485, 100); // fourth straightaway
        gc.fillArc(0, 673, 200, 200, 180, 90, ArcType.ROUND); // fourth arc

        gc.fillRect(0, 200, 100, 573); // final straightaway
        gc.fillArc(0, 100, 200, 200, 90, 90, ArcType.ROUND); // final arc

        gc.setStroke(Color.WHITE); // Set line color to black
        gc.setLineWidth(1);
        for (int i = 0; i < 1500; i += 100) {
        gc.strokeLine(0, i, 1500, i);
         }
        for (int i = 0; i < 1500; i += 100) {
        gc.strokeLine(i, 0, i, 1000);
         }

    }

    private void dataOnScreen(GraphicsContext gc) {
        // Speed Bar
        gc.setFill(Color.BLACK);
        gc.fillRect(198, 298, 204, 24);
        gc.setFill(Color.GREEN);
        gc.fillRect(200, 300, 200, 20);
        gc.setFill(Color.CYAN);
        gc.fillRect(200, 300, speed * 40, 20);
        // Speed
        gc.setFont(new Font("Arial", 24));
        gc.setFill(Color.WHITE);
        String speedText = "Speed - " + (int) (speed * speed * 10) + " MPH";
        gc.fillText(speedText, 205, 275);

    }

    private void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.save();
        gc.setFill(Color.GREEN);
        track(gc);
        dataOnScreen(gc);
        //For drawing the borders:::Source - ChatGPT
        for (Shape border : trackBorders) {
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(5);
            gc.setFill((Color) border.getFill());
            if (border instanceof Rectangle) {
                Rectangle rect = (Rectangle) border;
                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            } else if (border instanceof Polygon) {
                Polygon poly = (Polygon) border;
                gc.fillPolygon(
                        poly.getPoints().stream().filter(point -> poly.getPoints().indexOf(point) % 2 == 0)
                                .mapToDouble(Double::doubleValue).toArray(),
                        poly.getPoints().stream().filter(point -> poly.getPoints().indexOf(point) % 2 != 0)
                                .mapToDouble(Double::doubleValue).toArray(),
                        poly.getPoints().size() / 2);
            } else if (border instanceof Arc) {
                Arc arc = (Arc) border;
                gc.strokeArc(arc.getCenterX() - arc.getRadiusX(), arc.getCenterY() - arc.getRadiusY(),
                        arc.getRadiusX() * 2, arc.getRadiusY() * 2, arc.getStartAngle(), arc.getLength(), ArcType.OPEN);
            }

        }        //End of Source
        //For turning car image::::Source - ChatGPT
        gc.translate(carX + carImage.getWidth() / 2, carY + carImage.getHeight() / 2); 
        gc.rotate(carAngle);
        gc.drawImage(carImage, -carImage.getWidth() / 2, -carImage.getHeight() / 2); 
        gc.restore(); 
        //End of Source

    }

}
