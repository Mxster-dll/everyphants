package com.mxster.everyphants.plugin;

import com.mxster.everyphants.model.Result;

public class MorseEncryptionPlugin extends EncryptionPlugin {
    public static String morseCode(String s) {
        StringBuilder sb = new StringBuilder();
        String upper = s.toUpperCase();

        for (int i = 0; i < upper.length(); i++) {
            String code = switch (upper.charAt(i)) {
                case 'A' -> ".-";
                case 'B' -> "-...";
                case 'C' -> "-.-.";
                case 'D' -> "-..";
                case 'E' -> ".";
                case 'F' -> "..-.";
                case 'G' -> "--.";
                case 'H' -> "....";
                case 'I' -> "..";
                case 'J' -> ".---";
                case 'K' -> "-.-";
                case 'L' -> ".-..";
                case 'M' -> "--";
                case 'N' -> "-.";
                case 'O' -> "---";
                case 'P' -> ".--.";
                case 'Q' -> "--.-";
                case 'R' -> ".-.";
                case 'S' -> "...";
                case 'T' -> "-";
                case 'U' -> "..-";
                case 'V' -> "...-";
                case 'W' -> ".--";
                case 'X' -> "-..-";
                case 'Y' -> "-.--";
                case 'Z' -> "--..";
                case '0' -> "-----";
                case '1' -> ".----";
                case '2' -> "..---";
                case '3' -> "...--";
                case '4' -> "....-";
                case '5' -> ".....";
                case '6' -> "-....";
                case '7' -> "--...";
                case '8' -> "---..";
                case '9' -> "----.";
                case '.' -> ".-.-.-";
                case ':' -> "---...";
                case ',' -> "--..--";
                case ';' -> "-.-.-.";
                case '?' -> "..--..";
                case '=' -> "-...-";
                case '\'' -> "-..-.";
                case '/' -> "-..-.";
                case '!' -> "-.-.--";
                case '-' -> "-....-";
                case '_' -> "..--.-";
                case '"' -> ".-..-.";
                case '(' -> "-.--.";
                case ')' -> "-.--.-";
                case '$' -> "...-..-";
                case '&' -> "....";
                case '@' -> ".--.-.";
                default -> "";
            };
            if (!code.isEmpty()) {
                sb.append(code).append("  ");
            }
        }

        return sb.toString().trim();
    }

    @Override
    public Result build(String s) {
        return new Result(morseCode(s), "摩斯电码", 0.5);

    }
}
