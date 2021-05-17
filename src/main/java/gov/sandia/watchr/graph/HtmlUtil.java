/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.util.CommonConstants;
import gov.sandia.watchr.util.StringUtil;

public class HtmlUtil {

    private HtmlUtil() {}

    public static String createHead(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<head>");
        sb.append(content);
        sb.append("</head>");
        return sb.toString();        
    }
    
    public static String createBody(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<body>");
        sb.append(content);
        sb.append("</body>");
        return sb.toString();        
    }    

    public static final String createBreak() {
        return HtmlConstants.BR;
    }

    public static final String createButton(String buttonType, String buttonContents) {
        StringBuilder sb = new StringBuilder();
        sb.append("<button type=\"");
        sb.append(buttonType).append("\">");
        sb.append(buttonContents).append("</button>");
        return sb.toString(); 
    }

    public static final String createButton(String buttonType, String onClick, String buttonContents) {
        StringBuilder sb = new StringBuilder();
        sb.append("<button ");
        sb.append("type=\"").append(buttonType).append("\" ");
        sb.append("onclick=\"").append(onClick).append("\" ");
        sb.append(">");
        sb.append(buttonContents);
        sb.append("</button>");
        return sb.toString(); 
    }

    public static final String createDiv(String divContents, String id, String divClass, String align, String style) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div");
        if(StringUtils.isNotBlank(id)) sb.append(" id='").append(id).append("'");
        if(StringUtils.isNotBlank(divClass)) sb.append(" class='").append(divClass).append("'");
        if(StringUtils.isNotBlank(align)) sb.append(" align='").append(align).append("'");
        if(StringUtils.isNotBlank(style)) sb.append(" style='").append(style).append("'");
        sb.append(">");
        sb.append(divContents);
        sb.append("</div>");
        return sb.toString();
    }

    public static final String createDiv(String divContents, String align, String style) {
        return createDiv(divContents, "", "", align, style);
    }

    public static final String createDiv(String divContents, String style) {
        return createDiv(divContents, "", "", "", style);
    }

    public static final String createDiv(String divContents) {
        return createDiv(divContents, "", "", "", "");
    }

    public static String createForm(String formContents, String name, String method, String action, String autocomplete) {
        StringBuilder sb = new StringBuilder();
        sb.append("<form");
        if(StringUtils.isNotBlank(name)) sb.append(" name='").append(name).append("'");
        if(StringUtils.isNotBlank(method)) sb.append(" method='").append(method).append("'");
        if(StringUtils.isNotBlank(action)) sb.append(" action='").append(action).append("'");
        if(StringUtils.isNotBlank(autocomplete)) sb.append(" autocomplete='").append(autocomplete).append("'");
        sb.append(">");

        sb.append(formContents);

        sb.append("</form>");
        return sb.toString();
    } 

    public static String createH2(String content, String style) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2");
        if(StringUtils.isNotBlank(style)) sb.append(" style='").append(style).append("'");
        sb.append(">");
        sb.append(content);
        sb.append("</h2>");
        return sb.toString();
    }     

    public static final String createImage(String imgsrc) {
        StringBuilder sb = new StringBuilder();
        sb.append("<img src='").append(imgsrc).append("'/>");
        return sb.toString();
    }

    public static final String createImage(String imgsrc, int width, int height) {
        StringBuilder sb = new StringBuilder();
        sb.append("<img width='").append(width);
        sb.append("' height='").append(height);
        sb.append("' src='").append(imgsrc).append("'/>");
        return sb.toString();
    }    

    public static String createInput(String inputContents, String id, String inputClass, String name, String type, String value) {
        return createInput(inputContents, id, inputClass, name, type, value, "");
    }

    public static String createInput(String inputContents, String id, String inputClass, String name, String type, String value, String style) {
        StringBuilder sb = new StringBuilder();
        sb.append("<input");
        if(StringUtils.isNotBlank(id)) sb.append(" id='").append(name).append("'");
        if(StringUtils.isNotBlank(inputClass)) sb.append(" class='").append(inputClass).append("'");
        if(StringUtils.isNotBlank(name)) sb.append(" name='").append(name).append("'");
        if(StringUtils.isNotBlank(type)) sb.append(" type='").append(type).append("'");
        if(StringUtils.isNotBlank(value)) sb.append(" value='").append(value).append("'");
        if(StringUtils.isNotBlank(style)) sb.append(" style='").append(style).append("'");
        sb.append(">");

        sb.append(inputContents);

        sb.append("</input>");
        return sb.toString();
    }

    public static String createCheckboxInput(
            String inputContents, String id, String inputClass, String name,
            String type, String value, String style, boolean checked) {
        StringBuilder sb = new StringBuilder();
        sb.append("<input");
        if(StringUtils.isNotBlank(id)) sb.append(" id='").append(name).append("'");
        if(StringUtils.isNotBlank(inputClass)) sb.append(" class='").append(inputClass).append("'");
        if(StringUtils.isNotBlank(name)) sb.append(" name='").append(name).append("'");
        if(StringUtils.isNotBlank(type)) sb.append(" type='").append(type).append("'");
        if(StringUtils.isNotBlank(value)) sb.append(" value='").append(value).append("'");
        if(StringUtils.isNotBlank(style)) sb.append(" style='").append(style).append("'");
        sb.append(checked ? " checked" : "");
        sb.append(">");

        sb.append(inputContents);

        sb.append("</input>");
        return sb.toString();
    }
    

    public static final String createLink(String link, String contents) {
        StringBuilder sb = new StringBuilder();
        sb.append("<a href='").append(link).append("'>");
        sb.append(contents).append("</a>");
        return sb.toString();
    }

    public static final String createLink(String link, String style, String contents) {
        StringBuilder sb = new StringBuilder();
        sb.append("<a href='").append(link).append("'");
        if(StringUtils.isNotBlank(style)) sb.append(" style='").append(style).append("'");
        sb.append(">");
        sb.append(contents);
        sb.append("</a>");
        return sb.toString();
    }

    public static String createP(String pContents, String pStyle) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p ");
        if(StringUtils.isNotBlank(pStyle)) sb.append("style='").append(pStyle).append("'>");
        sb.append(pContents);
        sb.append("</p>");
        return sb.toString();
    }    

    public static final String createParameterList(Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for(Entry<String, String> parameter : parameters.entrySet()) {
            sb.append(counter == 0 ? "?" : "&");
            sb.append(parameter.getKey()).append("=").append(parameter.getValue());
            counter++;
        }
        return sb.toString();
    }

    public static String createScriptSrc(String src) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script src=\"");
        sb.append(src);
        sb.append("\" ></script>");
        return sb.toString();
    }

    public static String createScript(String contents) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script>");
        sb.append(contents);
        sb.append("</script>");
        return sb.toString();
    }

    public static String createSelection(List<String> options, String selectClass, String name, String style) {
        StringBuilder sb = new StringBuilder();
        sb.append("<select");
        if(StringUtils.isNotBlank(selectClass)) sb.append(" class='").append(selectClass).append("'");
        if(StringUtils.isNotBlank(name)) sb.append(" name='").append(name).append("'");
        if(StringUtils.isNotBlank(style)) sb.append(" style='").append(style).append("'");
        sb.append(">");

        for(String option : options) {
            sb.append(option);
        }

        sb.append("</select>");
        return sb.toString();
    }

    public static String createSelectionOption(String option, String value, boolean selected) {
        StringBuilder sb = new StringBuilder();
        sb.append("<option");
        if(StringUtils.isNotBlank(value)) sb.append(" value='").append(value).append("'");
        if(selected) sb.append(" selected='selected'");
        sb.append(">");
        sb.append(option);
        sb.append("</option>");
        return sb.toString();
    }

    public static final String createTableCell(String tdContents, String cellClass, String style) {
        return createTableCell(tdContents, cellClass, style, "");
    }

    public static final String createTableCell(String tdContents, String cellClass, String style, String colSpan) {
        StringBuilder sb = new StringBuilder();
        sb.append("<td");
        if(StringUtils.isNotBlank(cellClass)) sb.append(" class='").append(cellClass).append("'");
        if(StringUtils.isNotBlank(style)) sb.append(" style='").append(style).append("'");
        if(StringUtils.isNotBlank(colSpan)) sb.append(" colspan='").append(colSpan).append("'");
        sb.append(">");
        sb.append(tdContents);
        sb.append("</td>");
        return sb.toString();
    }

    public static String createTable(List<String> rows, String id, String tableClass, String style) {
        return createTable(rows, id, tableClass, style, new HashMap<>());
    }

    public static String createTable(List<String> rows, String id, String tableClass, String style, Map<String,String> additionalProps) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table");
        if(StringUtils.isNotBlank(id)) sb.append(" id='").append(id).append("'");
        if(StringUtils.isNotBlank(tableClass)) sb.append(" class='").append(tableClass).append("'");
        if(StringUtils.isNotBlank(style)) sb.append(" style='").append(style).append("'");

        for(Entry<String,String> additionalProp : additionalProps.entrySet()) {
            sb.append(" ").append(additionalProp.getKey()).append("='").append(additionalProp.getValue()).append("'");
        }
        sb.append(">");
        
        for(String row : rows) {
            sb.append(row);
        }

        sb.append("</table>");
        return sb.toString();
    }

    public static String createTableRow(List<String> cells, String style) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr");
        if(StringUtils.isNotBlank(style)) sb.append(" style='").append(style).append("'");
        sb.append(">");

        for(String cell : cells) {
            sb.append(cell);
        }

        sb.append("</tr>");
        return sb.toString();
    }

    /////////////
    // SPECIAL //
    /////////////

    public static final String createGraphLinkParameterList(String path, int page) throws UnsupportedEncodingException {
        if(StringUtils.isBlank(path)){
            path = CommonConstants.ROOT_PATH_ALIAS;
        }
        
        LinkedHashMap<String, String> parameterMap = new LinkedHashMap<>();
        parameterMap.put(HtmlConstants.PARAM_PATH, StringUtil.encode(path));
        parameterMap.put(HtmlConstants.PARAM_PAGE, Integer.toString(page));
        
        return createParameterList(parameterMap);
    } 
}
