package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import model.planter.Planter;
import simulation.Simulation;
import simulation.SimMap;
import model.utilities.Position;
import model.animal.Animal;
import simulation.statistics.AnimalStats;
import simulation.statistics.SimStats;

import java.util.Arrays;

public class SimPresenter {

    public GridPane mapGrid;
    public Button stopStartButton;
    public Label day;
    public Label animalCount;
    public Label deadAnimalCount;
    public Label plantCount;
    public Label freeFieldCount;
    public Label popularGenotype;
    public Label averageEnergyCount;
    public Label averageLifeSpan;
    public Label averageChildrenCount;
    public VBox animalStats;
    public Label genome;
    public Label activeGenome;
    public Label energyCount;
    public Label plantsEaten;
    public Label childrenCount;
    public Label descendantsCount;
    public Label daysSurvived;
    public Label deathDay;
    public Button startTrackButton;
    public Button stopTrackButton;
    public LineChart<Number, Number> chart;
    private int columns;
    private int rows;
    private Font font;
    private Simulation simulation;
    private double scale;

    public void setSimulation(Simulation sim) {
        simulation = sim;
        columns = sim.settings.width();
        rows = sim.settings.height();
        int cellSize = Math.min(600/rows, 800/columns);
        mapGrid.setMaxSize(cellSize*columns,cellSize*rows);
        this.scale = ((double)cellSize) / 60;
        font = new Font(Math.round(22*scale));
        for (int col = 0; col < columns; col++) mapGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        for (int row = 0; row < rows; row++) mapGrid.getRowConstraints().add(new RowConstraints(cellSize));

        startTrackButton.setOnAction(event -> {
            simulation.setTrackedAnimal(showingStats);
            startTrackButton.setDisable(true);
            stopTrackButton.setDisable(false);
        });
        stopTrackButton.setDisable(true);
        stopTrackButton.setOnAction(event -> {
            simulation.setTrackedAnimal(null);
            startTrackButton.setDisable(false);
            stopTrackButton.setDisable(true);
        });
        createChart();

        stopStartButton.setOnAction(event -> {
            if (simulation.isPaused()) {
                stopStartButton.setText("STOP");
                stopStartButton.setStyle("-fx-graphic: url('icons/pause24.png');");
                if (simulation.getTrackedAnimal().isEmpty()) animalStats.setVisible(false);
                else {
                    startTrackButton.setVisible(false);
                    stopTrackButton.setVisible(false);
                }
            }
            else {
                stopStartButton.setText("START");
                stopStartButton.setStyle("-fx-graphic: url('icons/start24.png');");
                startTrackButton.setVisible(true);
                stopTrackButton.setVisible(true);
            }
            simulation.pauseAnimation();
        });
    }

    public void update(SimMap map, Planter planter, SimStats stats, AnimalStats animalInfo) {
        Platform.runLater(() -> {
            printMap(map, planter);
            updateSimStats(stats);
            if (animalInfo != null) updateAnimalInfo(animalInfo);
            updateGraphData(stats);
            if (simulation.settings.saveStats()) stats.printSimStatsToCSV(simulation.getID());
        });
    }

    public void printMap(SimMap map, Planter planter) {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0));
        String plantColor = "#85de92";
        double maxEnergy = simulation.getMostEnergy();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Position pos = new Position(col, row);
                StackPane stack = new StackPane();
                GridPane.setHalignment(stack, HPos.CENTER);

                if (planter.isInJungle(pos)) {
                    stack.getChildren().add(new Rectangle(60 * scale, 60 * scale, Color.web(plantColor, 0.3)));
                }
                if (map.isAnimal(pos)) {
                    addAnimalIcon(map, maxEnergy, pos, stack);
                }
                else if (map.isPlant(pos)) {
                    Circle grassCircle = new Circle(28*scale, Color.web(plantColor));
                    GridPane.setHalignment(grassCircle, HPos.CENTER);
                    stack.getChildren().addAll(grassCircle);
                }

                mapGrid.add(stack, col, row);
            }
        }
    }

    public void updateSimStats(SimStats s) {
        day.setText(String.valueOf(s.day()));
        animalCount.setText(String.valueOf(s.animalCount()));
        deadAnimalCount.setText(String.valueOf(s.deadAnimalCount()));
        plantCount.setText(String.valueOf(s.plantCount()));
        freeFieldCount.setText(String.valueOf(s.freeFieldCount()));
        popularGenotype.setText(s.popularGenotype());
        averageEnergyCount.setText( "%.2f".formatted(s.averageEnergyCount()) );
        averageLifeSpan.setText( "%.2f".formatted(s.averageLifeSpan()) );
        averageChildrenCount.setText( "%.2f".formatted(s.averageChildrenCount()) );
    }

    public void updateAnimalInfo(AnimalStats i) {
        genome.setText(Arrays.toString(i.genome()));
        activeGenome.setText(String.valueOf(i.activeGenome()));
        energyCount.setText(String.valueOf(i.energyCount()));
        plantsEaten.setText(String.valueOf(i.plantsEaten()));
        childrenCount.setText(String.valueOf(i.childrenCount()));
        descendantsCount.setText(String.valueOf(i.descendantsCount()));
        daysSurvived.setText(String.valueOf(i.daysSurvived()));
        deathDay.setText(String.valueOf(i.deathDay()));
    }

    private void addAnimalIcon(SimMap map, double maxEnergy, Position pos, StackPane stack) {
        Animal animal;
        Circle animalCircle;
        if (simulation.getTrackedAnimal().isPresent() && pos.equals(simulation.getTrackedAnimal().get().getPosition())) {
            animal = simulation.getTrackedAnimal().get();
            animalCircle = new Circle(28*scale, Color.hsb(16, animal.getEnergy()/maxEnergy, 0.96));
        }
        else {
            animal = map.animalAt(pos);
            animalCircle = new Circle(28*scale, Color.hsb(165, animal.getEnergy()/maxEnergy, 0.87));
        }
        Text text = new Text(animal.toString());
        text.setFont(font);
        text.setBoundsType(TextBoundsType.VISUAL);
        stack.getChildren().addAll(animalCircle, text);
        if (simulation.isPaused()) stack.setOnMouseClicked(event -> handleMouseClick(event, pos));

    }

    private void handleMouseClick(MouseEvent event, Position position) {
        ContextMenu menu = new ContextMenu();
        for (Animal animal : simulation.getAnimalsFromCell(position)) {
            MenuItem menuItem = new MenuItem("Animal " + animal.toString());
            menuItem.setOnAction(new MenuMouseClickHandler(animal, this));
            menu.getItems().add(menuItem);
        }
        menu.show(mapGrid.getScene().getWindow(), event.getScreenX(), event.getScreenY());
    }

    private record MenuMouseClickHandler(Animal animal, SimPresenter presenter) implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            presenter.showAnimalStats(animal);
        }
    }

    private Animal showingStats;

    private void showAnimalStats(Animal animal) {
        updateAnimalInfo(simulation.createTrackedAnimalStats(animal));
        animalStats.setVisible(true);
        showingStats = animal;
    }

    private XYChart.Series<Number, Number> animals;
    private XYChart.Series<Number, Number> plants;

    public void createChart() {

        animals = new XYChart.Series<>();
        animals.setName("Animals");
        chart.getData().add(animals);
        animals.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #00c493");

        plants = new XYChart.Series<>();
        plants.setName("Plants");
        chart.getData().add(plants);
        plants.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #4fc460");
    }

    private void updateGraphData(SimStats stats) {

        animals.getData().add(new XYChart.Data<>(stats.day(), stats.animalCount()));
        plants.getData().add(new XYChart.Data<>(stats.day(), stats.plantCount()));

        if (animals.getData().size() > 30) {
            animals.getData().remove(0);
            plants.getData().remove(0);
            ((NumberAxis)chart.getXAxis()).setLowerBound( stats.day() - 29 );
            ((NumberAxis)chart.getXAxis()).setUpperBound( stats.day() - 1 );
        } else if (animals.getData().size() > 10) {
            ((NumberAxis)chart.getXAxis()).setUpperBound( stats.day() - 1 );
        }
    }
}
