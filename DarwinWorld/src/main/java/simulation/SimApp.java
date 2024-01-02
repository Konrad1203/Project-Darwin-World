package simulation;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import GUI.StartPresenter;
import simulation.statistics.SimSettings;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class SimApp extends Application {

    ExecutorService executor = newFixedThreadPool(4);

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("start.fxml"));
        BorderPane viewRoot = loader.load();
        configureStage(primaryStage, viewRoot, "Simulation start");
        primaryStage.show();
        StartPresenter presenter = loader.getController();
        presenter.setSimApp(this);
        primaryStage.setOnCloseRequest(event -> executor.shutdown());
    }

    public void buildSimulationStage(SimSettings settings) throws IOException, InterruptedException {
        Stage simStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        Parent root = loader.load();
        configureStage(simStage, root, "Simulation app");
        simStage.show();

        Simulation simulation = new Simulation(settings, loader.getController());
        executor.submit(simulation);

        simStage.setOnCloseRequest(event -> {
            if (simulation.isPaused()) simulation.pauseAnimation();
            simulation.stop();
        });
    }

    private void configureStage(Stage stage, Parent root, String name) {
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image("icons/frog48.png"));
        stage.setTitle(name);
    }
}

