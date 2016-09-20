package atom.core.entropy;

public class Main {

    public static void main(String[] args) {
        byte[] byt = new byte[]{0x00, 0x00, 0x1A};
        String op = new String(byt);
        String[] sstr = {
                "1223334444",
                "1223334444555555555",
                "122333",
                "1227774444",
                "aaBBcccDDDD",
                "1234567890abcdefghijklmnopqrstuvwxyz",
                "Rosetta Code",
                "11111",
                "11",
                "0000141A",
                "1414",
                op
        };

        for (String ss : sstr) {
            double entropy = EntropyCalculator.getInstance().getShannonEntropy(ss);
            double e = EntropyCalculator.getInstance().getMethodEntropy(ss.getBytes());
            System.out.printf("Shannon entropy of %40s: %.12f%n", "\"" + ss + "\"", entropy);
            System.out.printf("Method entropy of %40s: %.12f%n", "\"" + ss + "\"", e);
        }
        return;
    }
}
