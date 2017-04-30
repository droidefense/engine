package droidefense.entropy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntropyCalculator {

    private static EntropyCalculator ourInstance = new EntropyCalculator();

    private EntropyCalculator() {
    }

    public static EntropyCalculator getInstance() {
        return ourInstance;
    }

    public double getShannonEntropy(String s) {
        int n = 0;
        Map<Character, Integer> occ = new HashMap<>();

        for (int c_ = 0; c_ < s.length(); ++c_) {
            char cx = s.charAt(c_);
            if (occ.containsKey(cx)) {
                occ.put(cx, occ.get(cx) + 1);
            } else {
                occ.put(cx, 1);
            }
            ++n;
        }

        double e = 0.0;
        for (Map.Entry<Character, Integer> entry : occ.entrySet()) {
            char cx = entry.getKey();
            double p = (double) entry.getValue() / n;
            e += p * log2(p);
        }
        return -e;
    }

    private double getShannonEntropy(byte[] data) {
        int n = 0;
        Map<Byte, Integer> occ = new HashMap<>();

        for (int c_ = 0; c_ < data.length; ++c_) {
            byte cx = data[c_];
            if (occ.containsKey(cx)) {
                occ.put(cx, occ.get(cx) + 1);
            } else {
                occ.put(cx, 1);
            }
            ++n;
        }

        double e = 0.0;
        for (Map.Entry<Byte, Integer> entry : occ.entrySet()) {
            byte cx = entry.getKey();
            double p = (double) entry.getValue() / n;
            e += p * log2(p);
        }
        return -e;
    }

    public double getMethodEntropy(int[] data) {
        int n = 0;
        Map<Integer, Integer> occ = new HashMap<>();

        //remove nop instructions
        data = removeNOP(data);

        for (int c_ = 0; c_ < data.length; ++c_) {
            int cx = data[c_];
            if (occ.containsKey(cx)) {
                occ.put(cx, occ.get(cx) + 1);
            } else {
                occ.put(cx, 1);
            }
            ++n;
        }

        double e = 0.0;
        for (Map.Entry<Integer, Integer> entry : occ.entrySet()) {
            int cx = entry.getKey();
            double p = (double) entry.getValue() / n;
            e += p * log2(p);
        }
        return -e;
    }

    public double getMethodEntropy(byte[] opcodes) {
        //remove nop instruction before calculation
        ArrayList<Byte> list = new ArrayList<>();
        for (byte inst : opcodes) {
            if (inst != 0x00)
                list.add(inst);
        }

        //convert to array again
        opcodes = toByteArray(list);

        return getShannonEntropy(opcodes);
    }

    //PRIVATE METHODS

    private int[] removeNOP(int[] data) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i : data) {
            if (i != 0x00)
                list.add(i);
        }
        //convert arraylist to array
        return toIntArray(list);
    }


    private double log2(double a) {
        return Math.log(a) / Math.log(2);
    }

    private byte[] toByteArray(List<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }

    private int[] toIntArray(List<Integer> in) {
        final int n = in.size();
        int ret[] = new int[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }
}
