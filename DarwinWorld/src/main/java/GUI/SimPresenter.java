package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import java.util.List;

public class SimPresenter {

    @FXML
    private GridPane mapGrid;
    @FXML
    private GridPane plantGrid;
    @FXML
    private Button stopStartButton;
    @FXML
    private Button plantGridButton;
    @FXML
    private Button animalWithPopularGenome;
    @FXML
    private Label day;
    @FXML
    private Label animalCount;
    @FXML
    private Label deadAnimalCount;
    @FXML
    private Label plantCount;
    @FXML
    private Label freeFieldCount;
    @FXML
    private Label popularGenotype;
    @FXML
    private Label averageEnergyCount;
    @FXML
    private Label averageLifeSpan;
    @FXML
    private Label averageChildrenCount;
    @FXML
    private VBox animalStats;
    @FXML
    private Label genome;
    @FXML
    private Label activeGenome;
    @FXML
    private Label energyCount;
    @FXML
    private Label plantsEaten;
    @FXML
    private Label childrenCount;
    @FXML
    private Label descendantsCount;
    @FXML
    private Label daysSurvived;
    @FXML
    private Label deathDay;
    @FXML
    private Button startTrackButton;
    @FXML
    private Button stopTrackButton;
    @FXML
    private LineChart<Number, Number> chart;

    private final String plantColor = "#85de92";
    private Simulation simulation;
    private SimMap map;
    private Planter planter;
    private SimStats stats;
    private Font font;
    private int columns;
    private int rows;
    private double scale;

    private boolean showingAnimalsWithPopularGenome = false;
    private Animal showingStats;

    private XYChart.Series<Number, Number> animals;
    private XYChart.Series<Number, Number> plants;
    private NumberAxis chartXAxis;


    public void setupPresenter(Simulation sim) {
        simulation = sim;
        columns = sim.settings.width();
        rows = sim.settings.height();
        int cellSize = Math.min(600/rows, 600/columns);
        scale = ((double)cellSize) / 60;
        font = new Font(Math.round(22*scale));
        setupButtons();
        createGrids(cellSize);
        createChart();
    }

    private void createGrids(int cellSize) {
        mapGrid.setMaxSize(cellSize*columns,cellSize*rows);
        plantGrid.setMaxSize(cellSize*columns,cellSize*rows);
        for (int col = 0; col < columns; col++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
            plantGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        }
        for (int row = 0; row < rows; row++) {
            mapGrid.getRowConstraints().add(new RowConstraints(cellSize));
            plantGrid.getRowConstraints().add(new RowConstraints(cellSize));
        }
    }

    private void setupButtons() {
        startTrackButton.setOnAction(event -> {
            simulation.setTrackedAnimal(showingStats);
            startTrackButton.setDisable(true);
            stopTrackButton.setDisable(false);
            if (!showingAnimalsWithPopularGenome) refreshMapAtPosition(showingStats.getPosition(), simulation.getMostEnergy());
        });
        stopTrackButton.setDisable(true);
        stopTrackButton.setOnAction(event -> {
            Animal wasTracked = showingStats;
            if (simulation.getTrackedAnimal().isPresent()) {
                wasTracked = simulation.getTrackedAnimal().get();
            }
            simulation.setTrackedAnimal(null);
            startTrackButton.setDisable(false);
            stopTrackButton.setDisable(true);
            if (!showingAnimalsWithPopularGenome) refreshMapAtPosition(wasTracked.getPosition(), simulation.getMostEnergy());

        });

        stopStartButton.setOnAction(event -> {
            if (simulation.isPaused()) {
                stopStartButton.setText("STOP");
                stopStartButton.setStyle("-fx-graphic: url('icons/pause24.png');");
                if (simulation.getTrackedAnimal().isEmpty()) animalStats.setVisible(false);
                else {
                    startTrackButton.setVisible(false);
                    stopTrackButton.setVisible(false);
                }

                if (plantGrid.isVisible()) printPlantGrid(); // wyłączenie wyświetlania plantGrid
                animalWithPopularGenome.setText("Show animals with popular genome");
                showingAnimalsWithPopularGenome = false;
                plantGridButton.setDisable(true);
                animalWithPopularGenome.setDisable(true);
            }
            else {
                stopStartButton.setText("START");
                stopStartButton.setStyle("-fx-graphic: url('icons/start24.png');");

                startTrackButton.setVisible(true);
                stopTrackButton.setVisible(true);
                plantGridButton.setDisable(false);
                animalWithPopularGenome.setDisable(false);
            }
            simulation.pauseAnimation();
        });
    }

    public void update(SimMap map, Planter planter, SimStats stats, AnimalStats animalInfo) {
        this.map = map;
        this.planter = planter;
        this.stats = stats;
        Platform.runLater(() -> {
            printMap();
            updateSimStats();
            if (animalInfo != null) updateAnimalInfo(animalInfo);
            updateGraphData();
            if (simulation.settings.saveStats()) stats.printSimStatsToCSV(simulation.getID());
        });
    }

    private void printMap() {
        double maxEnergy = simulation.getMostEnergy();
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Position pos = new Position(col, row);
                refreshMapAtPosition(pos, maxEnergy);
            }
        }
    }

    private void refreshMapAtPosition(Position pos, double maxEnergy) {
        StackPane stack = new StackPane();
        GridPane.setHalignment(stack, HPos.CENTER);
        if (planter.isInJungle(pos)) {
            stack.getChildren().add(new Rectangle(58 * scale, 58 * scale, Color.web("#daf5de")));
        }
        if (map.isAnimal(pos)) {
            addAnimalIcon(map, maxEnergy, pos, stack);

        } else if (map.isPlant(pos)) {
            Circle grassCircle = new Circle(25*scale, Color.web(plantColor));
            GridPane.setHalignment(grassCircle, HPos.CENTER);
            stack.getChildren().addAll(grassCircle);
        }

        mapGrid.add(stack, pos.x(), pos.y());
    }

    private void addAnimalIcon(SimMap map, double maxEnergy, Position pos, StackPane stack) {
        Animal animal;
        Circle animalCircle;
        if (simulation.getTrackedAnimal().isPresent() && pos.equals(simulation.getTrackedAnimal().get().getPosition())) {
            animal = simulation.getTrackedAnimal().get();
            animalCircle = new Circle(27*scale, Color.hsb(16, Math.min(1,animal.getEnergy()/maxEnergy), 0.96));
        }
        else {
            animal = map.animalAt(pos);
            animalCircle = new Circle(27*scale, Color.hsb(165, Math.min(1,animal.getEnergy()/maxEnergy), 0.87));
        }
        Text text = new Text(animal.toString());
        text.setFont(font);
        text.setBoundsType(TextBoundsType.LOGICAL);
        stack.getChildren().addAll(animalCircle, text);
        if (simulation.isPaused()) stack.setOnMouseClicked(event -> handleMouseClick(event, pos));
    }

    @FXML
    private void printPlantGrid() {
        if (plantGrid.isVisible()) {
            mapGrid.setVisible(true);
            plantGrid.setVisible(false);
            plantGridButton.setText("Show plant grid");
        } else {
            mapGrid.setVisible(false);
            plantGrid.setVisible(true);
            plantGridButton.setText("Hide plant grid");

            plantGrid.getChildren().retainAll(plantGrid.getChildren().get(0));
            double maxCount = map.getMaxPlantsCounter();
            int[][] plantCounter = map.getPlantsCounter();
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    StackPane stack = new StackPane();
                    GridPane.setHalignment(stack, HPos.CENTER);
                    Text text = new Text(String.valueOf(plantCounter[row][col]));
                    text.setFont(font);
                    text.setBoundsType(TextBoundsType.LOGICAL);
                    double colorIntensivity = plantCounter[row][col]/maxCount;
                    Rectangle rectangle = new Rectangle(59 * scale, 59 * scale, Color.hsb(120, colorIntensivity, 1-0.5*colorIntensivity));
                    stack.getChildren().addAll(rectangle, text);
                    plantGrid.add(stack, col, row);
                }
            }
        }
    }


    @FXML
    private void printAnimalsWithPopularGenome() {
        if (showingAnimalsWithPopularGenome) { // hide
            animalWithPopularGenome.setText("Show animals with popular genome");
            printMap();
        } else { // show
            if (plantGrid.isVisible()) printPlantGrid(); // hide plantGrid if visible
            animalWithPopularGenome.setText("Hide animals with popular genome");
            mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0));
            List<Integer> popularGenome = stats.popularGenotype();
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    Position pos = new Position(col, row);
                    StackPane stack = new StackPane();
                    GridPane.setHalignment(stack, HPos.CENTER);
                    if (planter.isInJungle(pos)) {
                        stack.getChildren().add(new Rectangle(58 * scale, 58 * scale, Color.web("#daf5de")));
                    }
                    if (map.isAnimal(pos)) {
                        Animal animal = map.animalAt(pos);
                        int counter = 0;
                        for (int gen : animal.getGenome().getGenomeList()) if (popularGenome.contains(gen)) counter++;
                        Text text = new Text(String.valueOf(counter));
                        text.setFont(font);
                        text.setBoundsType(TextBoundsType.LOGICAL);
                        Circle animalCircle;
                        if (counter == 0) animalCircle = new Circle(27*scale, Color.hsb(165, 0.5, 0.87));
                        else animalCircle = new Circle(27*scale, Color.hsb(16, Math.min(1,0.2 + 1.2 * counter/simulation.settings.genomeLength()), 0.96));
                        stack.getChildren().addAll(animalCircle, text);
                        stack.setOnMouseClicked(event -> handleMouseClick(event, pos));
                    } else if (map.isPlant(pos)) {
                        Circle grassCircle = new Circle(25*scale, Color.web(plantColor));
                        GridPane.setHalignment(grassCircle, HPos.CENTER);
                        stack.getChildren().addAll(grassCircle);
                    }

                    mapGrid.add(stack, pos.x(), pos.y());
                }
            }
        }
        showingAnimalsWithPopularGenome = !showingAnimalsWithPopularGenome;
    }

    private void handleMouseClick(MouseEvent event, Position position) {
        ContextMenu menu = new ContextMenu();
        for (Animal animal : simulation.getAnimalsFromCell(position)) {
            MenuItem menuItem = new MenuItem("Animal: energy: " + animal.getEnergy() + " days: " + animal.getDaysSurvived());
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

    private void showAnimalStats(Animal animal) {
        updateAnimalInfo(AnimalStats.getAnimalStats(animal));
        animalStats.setVisible(true);
        showingStats = animal;
    }

    private void updateSimStats() {
        day.setText(String.valueOf(stats.day()));
        animalCount.setText(String.valueOf(stats.animalCount()));
        deadAnimalCount.setText(String.valueOf(stats.deadAnimalCount()));
        plantCount.setText(String.valueOf(stats.plantCount()));
        freeFieldCount.setText(String.valueOf(stats.freeFieldCount()));
        popularGenotype.setText(stats.popularGenotype().toString());
        averageEnergyCount.setText( "%.2f".formatted(stats.averageEnergyCount()) );
        averageLifeSpan.setText( "%.2f".formatted(stats.averageLifeSpan()) );
        averageChildrenCount.setText( "%.2f".formatted(stats.averageChildrenCount()) );
    }

    private void updateAnimalInfo(AnimalStats i) {
        genome.setText(Arrays.toString(i.genome()));
        activeGenome.setText(String.valueOf(i.activeGenome()));
        energyCount.setText(String.valueOf(i.energyCount()));
        plantsEaten.setText(String.valueOf(i.plantsEaten()));
        childrenCount.setText(String.valueOf(i.childrenCount()));
        descendantsCount.setText(String.valueOf(i.descendantsCount()));
        daysSurvived.setText(String.valueOf(i.daysSurvived()));
        deathDay.setText(String.valueOf(i.isDead()));
    }

    private void createChart() {

        animals = new XYChart.Series<>();
        animals.setName("Animals");
        chart.getData().add(animals);
        animals.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #00c493");

        plants = new XYChart.Series<>();
        plants.setName("Plants");
        chart.getData().add(plants);
        plants.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #4fc460");

        chartXAxis = (NumberAxis) chart.getXAxis();
    }

    private void updateGraphData() {
        int day = stats.day();

        animals.getData().add(new XYChart.Data<>(day, stats.animalCount()));
        plants.getData().add(new XYChart.Data<>(day, stats.plantCount()));

        if (animals.getData().size() > 30) {
            animals.getData().remove(0);
            plants.getData().remove(0);
            chartXAxis.setLowerBound( day - 29 );
            chartXAxis.setUpperBound( day - 1 );
        } else if (animals.getData().size() > 10) {
            chartXAxis.setUpperBound( day - 1 );
        }
    }
}
