package fpt.edu.vn.gms.utils;

import java.text.Normalizer;

public class SkuUtils {

    public static String toCode(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase();
        return normalized.length() > 10 ? normalized.substring(0, 10) : normalized;
    }
}
