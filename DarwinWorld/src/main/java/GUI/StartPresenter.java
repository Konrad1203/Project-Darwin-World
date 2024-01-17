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

    @FXML
    private ComboBox<String> presetOption;
    @FXML
    private Spinner<Integer> height;
    @FXML
    private Spinner<Integer> width;
    @FXML
    private Spinner<Integer> startPlantsCount;
    @FXML
    private Spinner<Integer> energyFromPlant;
    @FXML
    private Spinner<Integer> dailyPlantsGrowCount;
    @FXML
    private ComboBox<String> plantsGrowVariant;
    @FXML
    private Spinner<Integer> startAnimalsCount;
    @FXML
    private Spinner<Integer> startEnergyCount;
    @FXML
    private Spinner<Integer> fullEnergyCount;
    @FXML
    private Spinner<Integer> energyLossToCopulate;
    @FXML
    private Spinner<Integer> genomeLength;
    @FXML
    private ComboBox<String> genomeVariant;
    @FXML
    private Spinner<Integer> minMutationCount;
    @FXML
    private Spinner<Integer> maxMutationCount;
    @FXML
    private ComboBox<String> mutationVariant;
    @FXML
    private Spinner<Integer> frameTime;
    @FXML
    private CheckBox saveStats;
    @FXML
    private Button runSimulationButton;

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

        runSimulationButton.setOnAction(event -> {
            if (checkTextFieldsValues()) {
                try { simApp.buildSimulationStage(createSettings()); }
                catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    private void loadSettings() {
        Map<String, SimSettings> dataMap = PresetsSaver.deserializeMapFromFile(filename);
        SimSettings preset = dataMap.get(presetOption.getSelectionModel().getSelectedItem());
        if (preset != null) fillTextFieldWithPreset(preset);
        else createAndShowAlert(Alert.AlertType.ERROR, "Loading preset error", "You cannot load not existing preset");
    }

    @FXML
    private void refreshPresetsList() {
        if (!Files.exists(Path.of(filename))) putStandardSetting();
        this.dataMap = PresetsSaver.deserializeMapFromFile(filename);
        List<String> presetNames = new ArrayList<>(dataMap.keySet());
        Collections.sort(presetNames);
        presetOption.getItems().clear();
        for (String name : presetNames) presetOption.getItems().add(name);
        presetOption.getSelectionModel().select("Standard");
    }

    @FXML
    private void savePreset() {
        String presetName = presetOption.getSelectionModel().getSelectedItem();
        if (presetName.equals("Standard")) createAndShowAlert(Alert.AlertType.ERROR, "Saving preset error", "You cannot overwrite Standard preset");
        else if (presetName.isEmpty())  createAndShowAlert(Alert.AlertType.ERROR, "Saving preset error", "You cannot create preset without name");
        else if ( checkTextFieldsValues() && (!dataMap.containsKey(presetName) || (dataMap.containsKey(presetName) && createAndShowAlert(Alert.AlertType.CONFIRMATION, "Saving preset warning", "You are sure, you want to overwrite the %s preset?".formatted(presetName))))) {
            SimSettings preset = createSettings();
            dataMap.put(presetName, preset);
            PresetsSaver.serializeMapToFile(dataMap, filename);
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

    @FXML
    private void removePreset() {
        String presetName = presetOption.getSelectionModel().getSelectedItem();
        if (presetName.equals("Standard")) {
            createAndShowAlert(Alert.AlertType.ERROR, "Removing preset error", "You cannot remove Standard preset");
        } else {
            if (dataMap.containsKey(presetName) && createAndShowAlert(Alert.AlertType.CONFIRMATION, "Removing preset confirmation", "You are sure that you want to delete the preset: %s?".formatted(presetName))) {
                dataMap.remove(presetName);
                PresetsSaver.serializeMapToFile(dataMap, filename);
                refreshPresetsList();
            } else if (!dataMap.containsKey(presetName)) {
                createAndShowAlert(Alert.AlertType.ERROR, "Removing preset error", "You cannot remove not existing preset");
            }
        }
    }

    @FXML
    private void renamePreset() {
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
                    PresetsSaver.serializeMapToFile(dataMap, filename);
                    refreshPresetsList();
                    presetOption.getSelectionModel().select(newName);
                }
            });
        }
    }

    private void putStandardSetting() {
        Map<String, SimSettings> dataMap = new HashMap<>();
        dataMap.put("Standard", SimSettings.STANDARD_SETTINGS);
        PresetsSaver.serializeMapToFile(dataMap, filename);
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
}
