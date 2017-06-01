package sample;

import data.ByteAddress;
import data.DataModel;
import data.ScrollData;
import data.Selection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Controller {
    private Main main;
    private DataModel dataModel;
    private ScrollData scrollData;
    //Text areas
    @FXML
    private TextArea binaryAddressTable;
    @FXML
    private TextArea hexDump;
    @FXML
    private TextArea dataString;
    @FXML
    private ScrollBar scroll;
    //Menu buttons
    @FXML
    private MenuItem buttonOpen;
    @FXML
    private MenuItem buttonSave;
    @FXML
    private MenuItem buttonSaveAs;
    @FXML
    private ProgressIndicator fileLoadIndicator;

    private final FileChooser fileChooser;

    private boolean biSelectionLock;
    ContextMenu textfieldContextMenu = new ContextMenu();


    public Controller() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File...");
        biSelectionLock = false;

        MenuItem edit = new MenuItem("Edit");
        edit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Selection range = new Selection(hexDump.getSelection(), true);
                IndexRange forHex = range.getRangeForHex();
                IndexRange forStr = range.getRangeForString();
                main.editData(hexDump.getText(forHex.getStart(), forHex.getEnd()),
                        dataString.getText(forStr.getStart(), forStr.getEnd()));
            }
        });
        edit.setDisable(true);
        textfieldContextMenu.getItems().add(edit);
    }

    @FXML
    private void initialize() {
        hexDump.setTextFormatter(new TextFormatter<>(formatter));
        hexDump.setContextMenu(textfieldContextMenu);
        buttonOpen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(main.primary);
                if (file != null) {
                    fileOpen(file);
                }
            }
        });
        scroll.setVisible(false);
        scroll.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue <? extends Number> ov, Number old_val, Number new_val) {
                int min = scrollData.getBegin();
                int max = scrollData.getEnd();
                int current = scrollData.getCurrent();
                scrollData.collect((new_val.doubleValue() - old_val.doubleValue()));
                if (current != scrollData.getCurrent()) {
                    if (min != scrollData.getBegin() || max != scrollData.getEnd()) {
                        updateForms();
                    }
                }
            }
        });
        hexDump.selectedTextProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                IndexRange selection = new Selection(hexDump.getSelection(), true).getRangeForString();
                lockSelection();
                dataString.selectRange(selection.getStart(), selection.getEnd());
                unlockSelection();
            }
        });
        dataString.selectedTextProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                IndexRange selection = new Selection(dataString.getSelection(), false).getRangeForHex();
                if (!isSelectionLocked())
                    hexDump.selectRange(selection.getStart(), selection.getEnd());
            }
        });
    }

    public void linkToModel(Main main,DataModel dataModel) {
        this.main = main;
        this.dataModel = dataModel;
    }
    private void fileOpen(File file) {
        //Loading file in thread to prevent freezes
        fileLoadIndicator.setVisible(true);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws InterruptedException {
                try {
                    byte[] data = Files.readAllBytes(file.toPath());
                    dataModel.setData(file, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setScrollData();
                updateForms();
                hideProgressIndicator();
                enableContextMenu();
                return null;
            }
        };
        new Thread(task).start();
    }
    private void setScrollData() {
        int length = (int) dataModel.getFileHexLength();
        ByteAddress last = dataModel.getLastAddress();
        scrollData = new ScrollData(length, last);
        scroll.setVisible(scrollData.getValue() > 0);
        bindScrollBars();
    }
    private void bindScrollBars() {
        ScrollPane first = (ScrollPane) binaryAddressTable.getChildrenUnmodifiable().get(0);
        ScrollPane second = (ScrollPane) hexDump.getChildrenUnmodifiable().get(0);
        ScrollPane third = (ScrollPane) dataString.getChildrenUnmodifiable().get(0);
        first.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        second.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        third.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setMin(0);
        scroll.setMax(scrollData.getValue());
    }
    private void updateForms() {
        binaryAddressTable.setText(String.join("\r\n",
                dataModel.getStringAddressTable(scrollData.getBegin(), scrollData.getEnd())));
        hexDump.setText(String.join("",
                dataModel.getHEXValueGrouped(scrollData.getBeginAddr(), scrollData.getEndAddr())));
        dataString.setText(dataModel.getStringValueGrouped(scrollData.getBeginAddr(), scrollData.getEndAddr()));
    }
    private void enableContextMenu() {
        textfieldContextMenu.getItems().get(0).setDisable(false);
    }
    private void hideProgressIndicator() {
        fileLoadIndicator.setVisible(false);
    }
    private void lockSelection() {
        biSelectionLock = true;
    }
    private void  unlockSelection() {
        biSelectionLock = false;
    }
    private boolean isSelectionLocked() {
        return biSelectionLock;
    }

    private StringConverter<String> formatter = new StringConverter<String>()
    {
        @Override
        public String fromString(String string) {
            return string.replaceAll("\\s", "");
        }
        @Override
        public String toString(String object) {
            if (object == null) return "";
            String str = object.replaceAll("[A-F0-9]{32}(?=[A-F0-9])", "$0\r\n");
            return str.replaceAll("[A-F0-9]{2}(?=[A-F0-9])", "$0 ");
        }
    };
}
