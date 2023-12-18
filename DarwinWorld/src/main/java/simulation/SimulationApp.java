package simulation;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import simulation.GUI.SimPresenter;
import simulation.GUI.StartPresenter;
import simulation.model.SimSettings;
import simulation.model.SimulationMap;

import java.io.IOException;

public class SimulationApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("start.fxml"));
        BorderPane viewRoot = loader.load();
        configureStage(primaryStage, viewRoot, "Simulation start");
        primaryStage.show();
        StartPresenter presenter = loader.getController();
        presenter.setSimApp(this);
    }

    public void buildSimulation(SimSettings settings) throws IOException, InterruptedException {
        Stage simStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        Parent root = loader.load();
        configureStage(simStage, root, "Simulation app");
        SimPresenter presenter = loader.getController();
        presenter.setSimSettings(settings);
        simStage.show();

        SimulationMap map = new SimulationMap(settings, presenter);
        presenter.printMap(map);
    }

    private void configureStage(Stage stage, Parent root, String name) {
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image("icons/frog48.png"));
        stage.setTitle(name);
    }
}

