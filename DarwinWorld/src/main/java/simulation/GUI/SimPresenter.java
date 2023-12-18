package simulation.GUI;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import simulation.model.SimSettings;
import simulation.model.SimulationMap;
import simulation.model.util.Position;
import simulation.model.animal.Animal;

import java.util.List;

public class SimPresenter {

    public GridPane mapGrid;
    private int columns;
    private int rows;
    private final Font font = new Font(18);

    public void setSimSettings(SimSettings settings) {
        columns = settings.width();
        rows = settings.height();
        int cellSize = Math.min(600/rows, 800/columns);
        for (int col = 0; col < columns; col++) mapGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        for (int row = 0; row < rows; row++) mapGrid.getRowConstraints().add(new RowConstraints(cellSize));
    }

    public void printMap(SimulationMap map) {
        Platform.runLater(() -> {
            mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0));
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    Position position = new Position(j, i);
                    if ( map.isAnimal(position) ) {
                        /*
                        Label label = new Label(String.valueOf(map.animalAt(position)));
                        label.setFont(font);
                        label.setOnMouseClicked(event -> map.getAnimalsFromCell(position));
                        GridPane.setHalignment(label, HPos.CENTER);
                        mapGrid.add(label, j, i);
                        */
                        Circle circle = new Circle(20, Color.LIGHTSKYBLUE);
                        Text text = new Text(String.valueOf(map.animalAt(position)));
                        text.setFont(font);
                        text.setBoundsType(TextBoundsType.VISUAL);
                        StackPane stack = new StackPane();
                        stack.getChildren().addAll(circle, text);
                        mapGrid.add(stack, j, i);
                    } else if ( map.isPlant(position) ) {
                        ImageView iw = new ImageView(new Image("icons/leaves64.png",36,36,true,true));
                        Circle circle = new Circle(20, Color.LIGHTGREEN);
                        StackPane stack = new StackPane();
                        stack.getChildren().addAll(circle, iw);
                        mapGrid.add(stack, j, i);
                    }
                }
            }
        });
    }

    public void showList(List<Animal> animals) {

    }
}
