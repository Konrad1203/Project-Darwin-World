package GUI;

import simulation.statistics.SimSettings;
import simulation.SimApp;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class StartPresenter {

    public ComboBox<String> presetOption;
    public Spinner<Integer> height;
    public Spinner<Integer> width;
    public Spinner<Integer> startPlantsCount;
    public Spinner<Integer> energyFromPlant;
    public Spinner<Integer> dailyPlantsGrowCount;
    public ComboBox<String> plantsGrowVariant;
    public Spinner<Integer> startAnimalsCount;
    public Spinner<Integer> startEnergyCount;
    public Spinner<Integer> fullEnergyCount;
    public Spinner<Integer> energyLossToCopulate;
    public Spinner<Integer> genomeLength;
    public ComboBox<String> genomeVariant;
    public Spinner<Integer> minMutationCount;
    public Spinner<Integer> maxMutationCount;
    public ComboBox<String> mutationVariant;
    public Spinner<Integer> frameTime;
    public CheckBox saveStats;
    public Button runSimulationButton;

    private SimApp simApp;
    private Map<String, SimSettings> dataMap;
    private final String filename = "preset.data";

    public void setSimApp(SimApp simApp) {
        this.simApp = simApp;
    }

    @FXML
    private void initialize() {
        setTextFieldsToNumbers();
        refreshPresetsList();
        loadSettings();
    }

    public void loadSettings() {
        Map<String, SimSettings> dataMap = MapToFile.deserializeMapFromFile(filename);
        SimSettings preset = dataMap.get(presetOption.getSelectionModel().getSelectedItem());
        if (preset != null) fillTextFieldWithPreset(preset);
        else createAndShowAlert(Alert.AlertType.ERROR, "Loading preset error", "You cannot load not existing preset");
    }

    public void refreshPresetsList() {
        if (!Files.exists(Path.of(filename))) putStandardSetting();
        this.dataMap = MapToFile.deserializeMapFromFile(filename);
        List<String> presetNames = new ArrayList<>(dataMap.keySet());
        Collections.sort(presetNames);
        presetOption.getItems().clear();
        for (String name : presetNames) presetOption.getItems().add(name);
        presetOption.getSelectionModel().select("Standard");
    }

    public void savePreset() {
        String presetName = presetOption.getSelectionModel().getSelectedItem();
        if (presetName.equals("Standard")) createAndShowAlert(Alert.AlertType.ERROR, "Saving preset error", "You cannot overwrite Standard preset");
        else if (presetName.isEmpty())  createAndShowAlert(Alert.AlertType.ERROR, "Saving preset error", "You cannot create preset without name");
        else if ( checkTextFieldsValues() && (!dataMap.containsKey(presetName) || (dataMap.containsKey(presetName) && createAndShowAlert(Alert.AlertType.CONFIRMATION, "Saving preset warning", "You are sure, you want to overwrite the %s preset?".formatted(presetName))))) {
            SimSettings preset = createSettings();
            dataMap.put(presetName, preset);
            MapToFile.serializeMapToFile(dataMap, filename);
            refreshPresetsList();
            presetOption.getSelectionModel().select(presetName);
            createAndShowAlert(Alert.AlertType.INFORMATION, "Saved preset information", "Your preset %s has been created successfully".formatted(presetName));
        }
    }

    private SimSettings createSettings() {
        return new SimSettings(
                height.getValue(), width.getValue(),
                startPlantsCount.getValue(), energyFromPlant.getValue(), dailyPlantsGrowCount.getValue(), plantsGrowVariant.getSelectionModel().getSelectedItem(),
                startAnimalsCount.getValue(), startEnergyCount.getValue(), fullEnergyCount.getValue(), energyLossToCopulate.getValue(),
                minMutationCount.getValue(), maxMutationCount.getValue(), mutationVariant.getSelectionModel().getSelectedItem(),
                genomeLength.getValue(), genomeVariant.getSelectionModel().getSelectedItem(), saveStats.isSelected(), frameTime.getValue()
        );
    }

    public void removePreset() {
        String presetName = presetOption.getSelectionModel().getSelectedItem();
        if (presetName.equals("Standard")) {
            createAndShowAlert(Alert.AlertType.ERROR, "Removing preset error", "You cannot remove Standard preset");
        } else {
            if (dataMap.containsKey(presetName) && createAndShowAlert(Alert.AlertType.CONFIRMATION, "Removing preset confirmation", "You are sure that you want to delete the preset: %s?".formatted(presetName))) {
                dataMap.remove(presetName);
                MapToFile.serializeMapToFile(dataMap, filename);
                refreshPresetsList();
            } else if (!dataMap.containsKey(presetName)) {
                createAndShowAlert(Alert.AlertType.ERROR, "Removing preset error", "You cannot remove not existing preset");
            }
        }
    }

    public void renamePreset() {
        String presetOldName = presetOption.getSelectionModel().getSelectedItem();
        if (presetOldName.equals("Standard")) {
            createAndShowAlert(Alert.AlertType.WARNING, "Renaming preset error", "You cannot rename Standard preset.");
        } else {
            TextInputDialog td = new TextInputDialog(presetOldName);
            td.setTitle("Renaming preset");
            td.setHeaderText("Enter new name for preset:  %s".formatted(presetOldName));
            td.showAndWait().ifPresent(newName -> {
                if (newName.isEmpty())
                    createAndShowAlert(Alert.AlertType.WARNING, "Empty preset name", "The preset must have a name");
                else if (dataMap.containsKey(newName))
                    createAndShowAlert(Alert.AlertType.WARNING, "Occupied preset name", "Name %s is already taken".formatted(newName));
                else {
                    dataMap.put(newName, dataMap.remove(presetOldName));
                    MapToFile.serializeMapToFile(dataMap, filename);
                    refreshPresetsList();
                    presetOption.getSelectionModel().select(newName);
                }
            });
        }
    }

    public void putStandardSetting() {
        SimSettings settings = new SimSettings(
                10,10,
                10,15,3,"Equator",
                10,20,15,10,
                0,3,"Standard",
                10,"Standard", false, 500);

        Map<String, SimSettings> dataMap = new HashMap<>();
        dataMap.put("Standard", settings);
        MapToFile.serializeMapToFile(dataMap, filename);
    }

    private boolean createAndShowAlert(Alert.AlertType alertType, String title, String headerText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        if (alertType == Alert.AlertType.CONFIRMATION) {
            ButtonType overwriteButton = new ButtonType("Process");
            ButtonType cancelButton = new ButtonType("Cancel");
            alert.getButtonTypes().setAll(overwriteButton, cancelButton);
            AtomicBoolean confirmation = new AtomicBoolean(false);
            alert.showAndWait().ifPresent(response -> { if (response == overwriteButton) confirmation.set(true); });
            return confirmation.get();
        }
        alert.show();
        return true;
    }

    private boolean checkTextFieldsValues() {
        if (height.getEditor().getText().isEmpty() || width.getEditor().getText().isEmpty() || startPlantsCount.getEditor().getText().isEmpty() ||
                energyFromPlant.getEditor().getText().isEmpty() || dailyPlantsGrowCount.getEditor().getText().isEmpty() || startAnimalsCount.getEditor().getText().isEmpty() ||
                startEnergyCount.getEditor().getText().isEmpty() || fullEnergyCount.getEditor().getText().isEmpty() || energyLossToCopulate.getEditor().getText().isEmpty() ||
                minMutationCount.getEditor().getText().isEmpty() || maxMutationCount.getEditor().getText().isEmpty() || genomeLength.getEditor().getText().isEmpty()) {
            createAndShowAlert(Alert.AlertType.WARNING, "Preset data inconsistency", "All fields must be filled in");
            return false;
        } else if (minMutationCount.getValue() > maxMutationCount.getValue()) {
            createAndShowAlert(Alert.AlertType.WARNING, "Preset data inconsistency",
                    "The maximum number of mutations must be equal or higher than the minimum number");
            return false;
        } else if (energyLossToCopulate.getValue() > fullEnergyCount.getValue()) {
            createAndShowAlert(Alert.AlertType.WARNING, "Preset data inconsistency",
                    "Required energy to be full must be higher or equal to energy copulation loss");
            return false;
        } else return true;
    }

    private void fillTextFieldWithPreset(SimSettings preset) {
        height.getValueFactory().setValue(preset.height());
        width.getValueFactory().setValue(preset.width());
        startPlantsCount.getValueFactory().setValue(preset.startPlantsCount());
        energyFromPlant.getValueFactory().setValue(preset.energyFromPlant());
        dailyPlantsGrowCount.getValueFactory().setValue(preset.dailyPlantsGrowCount());
        startAnimalsCount.getValueFactory().setValue(preset.startAnimalsCount());
        startEnergyCount.getValueFactory().setValue(preset.startEnergyCount());
        fullEnergyCount.getValueFactory().setValue(preset.fullEnergyCount());
        energyLossToCopulate.getValueFactory().setValue(preset.energyLossToCopulate());
        genomeLength.getValueFactory().setValue(preset.genomeLength());
        minMutationCount.getValueFactory().setValue(preset.minMutationCount());
        maxMutationCount.getValueFactory().setValue(preset.maxMutationCount());
        frameTime.getValueFactory().setValue(preset.frameTime());

        plantsGrowVariant.getSelectionModel().select(preset.plantsGrowVariant());
        genomeVariant.getSelectionModel().select(preset.genomeVariant());
        mutationVariant.getSelectionModel().select(preset.mutationVariant());

        saveStats.setSelected(preset.saveStats());
    }

    private void setTextFieldsToNumbers() {
        height.getEditor().setTextFormatter(numberFormatter());
        width.getEditor().setTextFormatter(numberFormatter());
        startPlantsCount.getEditor().setTextFormatter(numberFormatter());
        energyFromPlant.getEditor().setTextFormatter(numberFormatter());
        dailyPlantsGrowCount.getEditor().setTextFormatter(numberFormatter());
        startAnimalsCount.getEditor().setTextFormatter(numberFormatter());
        startEnergyCount.getEditor().setTextFormatter(numberFormatter());
        fullEnergyCount.getEditor().setTextFormatter(numberFormatter());
        energyLossToCopulate.getEditor().setTextFormatter(numberFormatter());
        genomeLength.getEditor().setTextFormatter(numberFormatter());
        minMutationCount.getEditor().setTextFormatter(numberFormatter());
        maxMutationCount.getEditor().setTextFormatter(numberFormatter());
    }
    private TextFormatter<Integer> numberFormatter() {
        return new TextFormatter<>(change -> {
            change.setText(change.getText().replaceAll("\\D", ""));
            return change;
        });
    }

    public void runSimulation() {
        if (checkTextFieldsValues()) {
            try { simApp.buildSimulationStage(createSettings()); }
            catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
