package com.zggis.howler.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogMatcher {

    public static boolean check(String matchingString, String logLine, boolean regex) {
        if (regex) {
            return matchRegEx(matchingString, logLine);
        } else {
            return matchString(matchingString, logLine);
        }
    }

    private static boolean matchString(String matchingString, String logLine) {
        return logLine.toLowerCase().contains(matchingString.toLowerCase());
    }

    private static boolean matchRegEx(String matchingString, String logLine) {
        Pattern pattern = Pattern.compile(matchingString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(logLine);
        return matcher.find();
    }
}
