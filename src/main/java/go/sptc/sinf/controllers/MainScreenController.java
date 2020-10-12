package go.sptc.sinf.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainScreenController {
    @FXML
    private Button btnChooseCaseFolder;
    @FXML
    private Button btnChooseDestFolder;
    @FXML
    private Button btnStart;
    @FXML
    private TextField texCaseFolder;
    @FXML
    private TextField texDestFolder;
    @FXML
    private TextArea txaQuery;
    @FXML
    private Spinner<Integer> spnLimit;
    @FXML
    private ProgressBar pgbProgress;
    private final StringProperty updateMessage = new SimpleStringProperty();

    public final String getUpdateMessage(){
        return updateMessage.getValue();
    }

    public final void setUpdateMessage(String value){
        updateMessage.setValue(value);
    }

    public final StringProperty updateMessageProperty() {
        return this.updateMessage;
    }

    @FXML
    private void initialize()
    {
//        spnLimit = new Spinner<Integer>();
//        spnLimit.getValueFactory().setValue(-1);
    }

    @FXML
    public void chooseCaseFolder(ActionEvent event)
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(texCaseFolder.getText()));
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if(selectedDirectory != null){
            texCaseFolder.setText(selectedDirectory.getAbsolutePath());
        }

    }

    @FXML
    public void chooseDestFolder(ActionEvent event)
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(texDestFolder.getText()));
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        if(selectedDirectory != null){
            texDestFolder.setText(selectedDirectory.getAbsolutePath());
        }

    }

    @FXML
    public void start(ActionEvent event){
        Task task = new Task<Void>() {
            @Override public Void call() {
                final int max = 100;
                for (int i = 1; i <= max; i++) {
                    updateProgress(i, max);
                    updateMessage(String.format("Valor corrente: %d", i));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        pgbProgress.progressProperty().bind(task.progressProperty());
        texCaseFolder.textProperty().bind(task.messageProperty());
//        updateMessageProperty().bind(task.messageProperty());
        new Thread(task).start();

    }
}