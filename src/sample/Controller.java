package sample;

import data.ByteAddress;
import data.DataModel;
import data.ScrollData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.File;

public class Controller {
    private Stage main;
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
    private final FileChooser fileChooser;


    public Controller() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File...");

    }

    @FXML
    private void initialize() {
        hexDump.setTextFormatter(new TextFormatter<>(formatter));
        buttonOpen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(main);
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
                IndexRange range = hexDump.getSelection();
                int strBegin = range.getStart() / 48;
                int strEnd = range.getEnd() / 48;
                int passedBeforeBegin = (1 + range.getStart() - 48 * strBegin) / 3;
                int passedBeforeEnd = (2 + range.getEnd() - 48 * strEnd) / 3;
                System.out.println(strBegin + "/" + strEnd + "|" + passedBeforeBegin + "/" + passedBeforeEnd);
                dataString.selectRange((strBegin * 17 + passedBeforeBegin), (strEnd * 17 + passedBeforeEnd));
            }
        });
    }

    public void linkToModel(Stage main,DataModel dataModel) {
        this.main = main;
        this.dataModel = dataModel;
    }
    private void fileOpen(File file) {
        if (dataModel.open(file)) {
            int length = (int) dataModel.getFileHexLength();
            ByteAddress last = dataModel.getLastAddress();
            scrollData = new ScrollData(length, last);
            scroll.setVisible(scrollData.getValue() > 0);
            bindScrollBars();
            updateForms();
            main.setTitle("ProjectHEX - "+ file.getAbsolutePath());
        }
    }
    public void bindScrollBars() {
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

    StringConverter<String> formatter = new StringConverter<String>()
    {
        @Override
        public String fromString(String string)
        {
            return string.replaceAll("\\s", "");
        }
        @Override
        public String toString(String object)
        {
            if (object == null) return "";
            String str = object.replaceAll("[A-F0-9]{32}", "$0\r\n");
            return str.replaceAll("[A-F0-9]{2}(?=[A-F0-9])", "$0 ");
        }
    };
}
