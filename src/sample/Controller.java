package sample;

import data.ByteAddress;
import data.DataModel;
import data.ScrollData;
import data.Selection;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;

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
                IndexRange selection = hexDump.getSelection();
                if(selection.getStart() >= selection.getEnd()) return;
                Selection range = new Selection(selection, true);
                IndexRange forHex = range.getRangeForHex();
                IndexRange forStr = range.getRangeForString();
                //Calc byteAddress of first selected Byte
                ByteAddress begin = new ByteAddress(scrollData.getBegin() + range.getStrBegin(), range.getIndexBegin());
                ByteAddress end = new ByteAddress(scrollData.getBegin() + range.getStrEnd(), range.getIndexEnd() - 1);
                main.editData(hexDump.getText(forHex.getStart(), forHex.getEnd()),
                        dataString.getText(forStr.getStart(), forStr.getEnd()), begin, end);
                if (dataModel.isModified()) {
                    scrollData.onChange((int)dataModel.getFileHexLength(), dataModel.getLastAddress());
                    updateForms();
                }
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
        buttonSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               dataModel.save();
               updateWindowTitle();
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

        main.primary.getScene().heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double height = hexDump.getHeight();
                int lines = (int)(height + 5) / 19;
                if (scrollData != null && lines != scrollData.getLines()) {
                    scrollData.setLinesAmount(lines);
                    scroll.setMax(scrollData.getValue());
                    scroll.setVisible(scrollData.getValue() > 0);
                    scroll.setVisibleAmount(scrollData.getVisibleAmount());
                    scroll.setValue(scrollData.getBegin());
                    updateForms();
                }
            }
        });
        main.primary.getScene().setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (!scroll.isFocused()) scroll.requestFocus();
            }
        });
    }
    private void fileOpen(File file) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (dataModel.open(file)) {
                    updateWindowTitle();
                    updateScrollData();
                    updateForms();
                    enableContextMenu();
                }
                hideProgressIndicator();
            }
        });
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
        scroll.setVisibleAmount(scrollData.getVisibleAmount());
    }
    private void updateScrollData() {
        int length = (int) dataModel.getFileHexLength();
        ByteAddress last = dataModel.getLastAddress();
        scrollData = new ScrollData(length, last, ((int)hexDump.getHeight() + 5) / 19);
        scroll.setVisible(scrollData.getValue() > 0);
        bindScrollBars();
    }
    private void updateForms() {
        binaryAddressTable.setText(String.join("\r\n",
                dataModel.getStringAddressTable(scrollData.getBegin(), scrollData.getEnd())));
        hexDump.setText(String.join("",
                dataModel.getHEXValueGrouped(scrollData.getBeginAddr(), scrollData.getEndAddr())));
        dataString.setText(dataModel.getStringValueGrouped(scrollData.getBeginAddr(), scrollData.getEndAddr()));
        if (dataModel.isModified())
            updateWindowTitle();
    }
    private void updateWindowTitle() {
        main.primary.setTitle("ProjectHEX - " + ((dataModel.isModified()) ? dataModel.getFilePath() + "*" : dataModel.getFilePath()));
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
    private void unlockSelection() {
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
