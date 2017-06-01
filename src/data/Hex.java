package data;

public class Hex {

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String normalizeHexString(String string) {
        String hex = string.replaceAll("\\s", "");
        int length = hex.length();
        if (length % 2 == 1) hex = hex.substring(0, length - 2) + "0" + hex.charAt(length - 1);
        return hex;
    }
}
