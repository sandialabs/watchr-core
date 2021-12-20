package gov.sandia.watchr.parse.extractors;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import gov.sandia.watchr.util.NumUtil;

public class ExtractionResultIndexParser {

    public boolean isIndexSyntax(String str) {
        if(str.length() > 1) {
            boolean isIndexSyntax = str.charAt(0) == '{' && str.charAt(str.length()-1) == '}';
            String insideStr = str.substring(1, str.length()-1);
            isIndexSyntax = isIndexSyntax && !insideStr.contains("{") && !insideStr.contains("}");
            isIndexSyntax = isIndexSyntax && !insideStr.contains("."); // No decimal points allowed
            isIndexSyntax = isIndexSyntax && NumberUtils.isCreatable(insideStr);
            return isIndexSyntax;
        }
        return false;
    }

    public Integer getIndexFromIndexSyntax(String str) {
        String insideStr = str.substring(1, str.length()-1);
        if(NumUtil.isInteger(insideStr)) {
            return Integer.parseInt(insideStr);
        } else {
            return null;
        }
    }

    public boolean isIndexRangeSyntax(String str) {
        if(str.length() > 1) {
            boolean isIndexRangeSyntax = str.charAt(0) == '{' && str.charAt(str.length()-1) == '}';
            String insideStr = str.substring(1, str.length()-1);
            isIndexRangeSyntax = isIndexRangeSyntax && !insideStr.contains("{") && !insideStr.contains("}");
            isIndexRangeSyntax = isIndexRangeSyntax && insideStr.contains("-");
            isIndexRangeSyntax = isIndexRangeSyntax && insideStr.length() >= 3;
            return isIndexRangeSyntax;
        }
        return false;
    }

    public Pair<Integer, Integer> getRangeFromIndexRangeSyntax(String str, int arraySize) {
        final String endToken = "N";
        String insideStr = str.substring(1, str.length()-1);
        String[] elements = insideStr.split("-");
        if(elements.length == 2) {
            String startRange = elements[0];
            String endRange = elements[1];

            if(NumberUtils.isCreatable(startRange)) {
                Integer startRangeIndex = Integer.parseInt(startRange);
                Integer endRangeIndex = null;
                if(NumberUtils.isCreatable(endRange)) {
                    endRangeIndex = Integer.parseInt(endRange);
                } else if(endRange.equals(endToken)) {
                    endRangeIndex = arraySize;
                }

                if(endRangeIndex != null && endRangeIndex > startRangeIndex) {
                    return new ImmutablePair<>(startRangeIndex, endRangeIndex);
                }
            }
        }
        return null;
    }  
}
