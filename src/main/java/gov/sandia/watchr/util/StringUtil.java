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

    private StringUtil() {}
    
    public static String[] splitFilePath(String path) {
        if(!StringUtils.isBlank(path)) {
            String[] pathComponents = path.split(Pattern.quote(File.separator));
            return pathComponents;
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
}
