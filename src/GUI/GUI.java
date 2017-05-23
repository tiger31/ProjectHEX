package GUI;


import java.io.*;

public class GUI {
    public static void main(String[] args) {
        try {
            FileInputStream s = new FileInputStream("ProjectHEX.iml");
            byte[] chars = new byte[4096];
            int lenght = s.read(chars);
            StringBuilder HEX = new StringBuilder();
            for (int i = 0; i < lenght; i++) {
                if (i % 16 == 0)  {
                    HEX.append("\r\n");
                }
                HEX.append(String.format("%02X", chars[i])).append(" ");
            }
            System.out.print(HEX);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
