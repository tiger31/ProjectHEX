package data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileModel {
    private File file;
    private List<ByteAddress> addresses;
    private List<Byte> data;
    private List<String> hexData;

    public void open(String filename) {
        File file = new File(filename);
        if (file.exists() && file.canRead() && file.canWrite()) {
            try {
                this.file = file;
                data = new ArrayList<>();
                hexData = new ArrayList<>();
                byte[] bytes = Files.readAllBytes(file.toPath());
                for (byte b: bytes) {
                    data.add(b);
                    hexData.add(String.format("%02X", b));
                }
                createAddressTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createAddressTable() {
        addresses = new ArrayList<>();
        for (long i = 0; i < (long)Math.ceil((double) file.length() / 16); i++)
            addresses.add(new ByteAddress(i));
    }

    public List<ByteAddress> getAddressTable() {
        return new ArrayList<>(addresses);
    }

    public List<String> getStringAddressTable() {
        return addresses.stream().map(ByteAddress::toString).collect(Collectors.toList());
    }

    public List<String> getSplittedHexData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < addresses.size(); i++) {
            int lowerBound = i * 16;
            int higherBound = ((i + 1) * 16 >= hexData.size()) ? hexData.size() : (i + 1) * 16;
            list.add(String.join(" ", hexData.subList(lowerBound, higherBound)));
        }
        return list;
    }
    public List<String> getSplittedStringData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < addresses.size(); i++) {
            int lowerBound = i * 16;
            int higherBound = ((i + 1) * 16 >= hexData.size()) ? hexData.size() : (i + 1) * 16;
            List<Byte> subList  = data.subList(lowerBound, higherBound);
            byte[] unboxed = new byte[subList.size()];
            for(int j = 0; j < subList.size(); j++) {
                unboxed[j] = subList.get(j);
            }
            list.add(new String(unboxed).replaceAll("\\s", "."));
        }
        return list;
    }
}
