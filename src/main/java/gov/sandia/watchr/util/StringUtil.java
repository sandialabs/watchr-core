/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * Convenience methods for string-related operations.
 *
 * @author Derek Trumbo, Elliott Ridgway, Lawrence Allen
 */

public class StringUtil {

    public static final String REGEX_START = "R$";

    private StringUtil() {}
    
    public static String[] splitFilePath(String path) {
        if(!StringUtils.isBlank(path)) {
            return path.split(Pattern.quote(File.separator));
        } else {
            return new String[0];
        }
    }
    
    public static String encode(String url) throws UnsupportedEncodingException {
        if(!StringUtils.isBlank(url)) {
            return URLEncoder.encode(url, StandardCharsets.UTF_8.name());
        }
        return url;
    }

    public static boolean hasIllegalCharacters(String str) {
        return str.matches(".*[^a-zA-Z0-9\\s\\-_]+.*");
    }

    public static String convertToRegex(String original) {
        if(StringUtils.isNotBlank(original)) {
            boolean fullRegex = original.startsWith(REGEX_START);
            if(fullRegex) {
                return original.replace(REGEX_START, "");
            } else {
                if(original.contains("*") && !original.contains(".*")) {
                    return original.replace("*", ".*");
                }
            }
        }
        return original;
    }

    public static String escapeRegexCharacters(String original) {
        String finalRegex = original;
        finalRegex = finalRegex.replace("|", "\\|");
        finalRegex = finalRegex.replace("&", "\\&");
        finalRegex = finalRegex.replace(":", "\\:");
        finalRegex = finalRegex.replace(";", "\\;");
        finalRegex = finalRegex.replace("<", "\\<");
        finalRegex = finalRegex.replace(">", "\\>");
        finalRegex = finalRegex.replace("(", "\\(");
        finalRegex = finalRegex.replace(")", "\\)");
        finalRegex = finalRegex.replace("$", "\\$");
        finalRegex = finalRegex.replace("`", "\\`");
        finalRegex = finalRegex.replace("-", "\\-");
        finalRegex = finalRegex.replace("_", "\\_");
        finalRegex = finalRegex.replace("=", "\\=");
        finalRegex = finalRegex.replace("\"", "\\\"");
        finalRegex = finalRegex.replace("\'", "\\\'");
        finalRegex = finalRegex.replace(".", "\\.");
        finalRegex = finalRegex.replace("*", "\\*");
        finalRegex = finalRegex.replace(" ", "\\s");
        return finalRegex;
    }
}
