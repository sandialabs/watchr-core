/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.generator.plotly;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import gov.sandia.watchr.util.OsUtil;

public class PlotlyHtmlFragmentGenerator {

    private PlotlyHtmlFragmentGenerator() {}
    
    public static String getPlotlyWindowBase(boolean includeHtml) {
        StringBuilder sb = new StringBuilder();

        if(includeHtml) {
            sb.append("<head>" + OsUtil.getOSLineBreak());
            sb.append("    <script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>" + OsUtil.getOSLineBreak());
            sb.append("</head>" + OsUtil.getOSLineBreak());
            sb.append("<body>" + OsUtil.getOSLineBreak());
            sb.append("    <div id=$$$WINDOW_DIV_NAME style=\"width:$$$WINDOW_VIEW_WIDTHpx;height:$$$WINDOW_VIEW_HEIGHTpx;\"></div>" + OsUtil.getOSLineBreak());
            sb.append("</body>" + OsUtil.getOSLineBreak());
        }
        sb.append("<script>" + OsUtil.getOSLineBreak());

        Map<String,String> modeBarSettingsProps = new LinkedHashMap<>();
        modeBarSettingsProps.put("modeBarButtonsToRemove", "['toImage', 'sendDataToCloud']");
        modeBarSettingsProps.put("displaylogo", "false");
        sb.append(generateJavascriptStruct("modeBarSettings", modeBarSettingsProps));

        sb.append(OsUtil.getOSLineBreak());
        sb.append(OsUtil.getOSLineBreak());
        sb.append("$$$WINDOW_CANVASES");
        sb.append(OsUtil.getOSLineBreak());
        sb.append(OsUtil.getOSLineBreak());
        sb.append("var data = [$$$WINDOW_TRACE_NAME_LIST];" + OsUtil.getOSLineBreak());

        Map<String,String> titleFontProps = new LinkedHashMap<>();
        titleFontProps.put("family", "$$$WINDOW_FONT");

        Map<String,String> layoutProps = new LinkedHashMap<>();
        layoutProps.put("title", "$$$WINDOW_LABEL");
        layoutProps.put("titlefont", generateJavascriptStruct(titleFontProps));
        layoutProps.put("showlegend", "$$$WINDOW_SHOW_LEGEND");
        layoutProps.put("height", "$$$WINDOW_VIEW_HEIGHT");
        layoutProps.put("width", "$$$WINDOW_VIEW_WIDTH");
        layoutProps.put("hovermode", "'closest'");
        layoutProps.put("plot_bgcolor", "$$$WINDOW_BACKGROUND");
        sb.append(generateJavascriptStruct("layout", layoutProps, "$$$WINDOW_CANVAS_LAYOUTS"));

        sb.append(";").append(OsUtil.getOSLineBreak()).append("    ").append(OsUtil.getOSLineBreak());
        sb.append("Plotly.newPlot($$$WINDOW_DIV_NAME, data, layout, modeBarSettings);" + OsUtil.getOSLineBreak());
        sb.append("</script>");

        return sb.toString();
    }

    public static String getPlotlyWindowTable(boolean includeHtml) {
        StringBuilder sb = new StringBuilder();

        if(includeHtml) {
            sb.append("<head>" + OsUtil.getOSLineBreak());
            sb.append("    <script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>" + OsUtil.getOSLineBreak());
            sb.append("</head>" + OsUtil.getOSLineBreak());
            sb.append("<body>" + OsUtil.getOSLineBreak());
            sb.append("    <div id=$$$WINDOW_DIV_NAME style=\"width:$$$WINDOW_VIEW_WIDTHpx;height:$$$WINDOW_VIEW_HEIGHTpx;\"></div>" + OsUtil.getOSLineBreak());
            sb.append("</body>" + OsUtil.getOSLineBreak());
        }

        sb.append("<script>" + OsUtil.getOSLineBreak());
        sb.append("var modeBarSettings = {" + OsUtil.getOSLineBreak());
        sb.append("modeBarButtonsToRemove: ['toImage', 'sendDataToCloud']," + OsUtil.getOSLineBreak());
        sb.append("displaylogo: false" + OsUtil.getOSLineBreak());
        sb.append("}" + OsUtil.getOSLineBreak());
        sb.append("" + OsUtil.getOSLineBreak());
        sb.append("$$$WINDOW_CANVASES" + OsUtil.getOSLineBreak());
        sb.append("" + OsUtil.getOSLineBreak());
        sb.append("var data = [$$$WINDOW_TRACE_NAME_LIST];" + OsUtil.getOSLineBreak());
        sb.append("var layout = {" + OsUtil.getOSLineBreak());
        sb.append("title: $$$WINDOW_LABEL," + OsUtil.getOSLineBreak());
        sb.append("titlefont: {" + OsUtil.getOSLineBreak());
        sb.append("family: $$$WINDOW_FONT" + OsUtil.getOSLineBreak());
        sb.append("}," + OsUtil.getOSLineBreak());
        sb.append("annotations: []," + OsUtil.getOSLineBreak());
        sb.append("xaxis: {" + OsUtil.getOSLineBreak());
        sb.append("ticks: ''," + OsUtil.getOSLineBreak());
        sb.append("side: 'bottom'," + OsUtil.getOSLineBreak());
        sb.append("showgrid: true," + OsUtil.getOSLineBreak());
        sb.append("automargin: true" + OsUtil.getOSLineBreak());
        sb.append("}," + OsUtil.getOSLineBreak());
        sb.append("yaxis: {" + OsUtil.getOSLineBreak());
        sb.append("ticks: ''," + OsUtil.getOSLineBreak());
        sb.append("ticksuffix: ' '," + OsUtil.getOSLineBreak());
        sb.append("height: $$$WINDOW_VIEW_HEIGHT," + OsUtil.getOSLineBreak());
        sb.append("width: $$$WINDOW_VIEW_WIDTH," + OsUtil.getOSLineBreak());
        sb.append("autosize: false," + OsUtil.getOSLineBreak());
        sb.append("showgrid: true," + OsUtil.getOSLineBreak());
        sb.append("automargin: true" + OsUtil.getOSLineBreak());
        sb.append("}" + OsUtil.getOSLineBreak());
        sb.append("};" + OsUtil.getOSLineBreak());
        sb.append("" + OsUtil.getOSLineBreak());
        sb.append("var maxZValue = Number.MIN_VALUE;" + OsUtil.getOSLineBreak());
        sb.append("var minZValue = Number.MAX_VALUE;" + OsUtil.getOSLineBreak());
        sb.append("for ( var i = 0; i < yValues.length; i++ ) {" + OsUtil.getOSLineBreak());
        sb.append("  for ( var j = 0; j < xValues.length; j++ ) {" + OsUtil.getOSLineBreak());
        sb.append("    var currentValue = $$$WINDOW_SHOW_NUMBER_LABELS;" + OsUtil.getOSLineBreak());
        sb.append("    if ( currentValue !== '' && currentValue !== \"NaN\" ) {" + OsUtil.getOSLineBreak());
        sb.append("      if ( currentValue > maxZValue ) {" + OsUtil.getOSLineBreak());
        sb.append("        maxZValue = currentValue;" + OsUtil.getOSLineBreak());
        sb.append("      }" + OsUtil.getOSLineBreak());
        sb.append("      if ( currentValue < minZValue ) {" + OsUtil.getOSLineBreak());
        sb.append("        minZValue = currentValue;" + OsUtil.getOSLineBreak());
        sb.append("      }" + OsUtil.getOSLineBreak());
        sb.append("    }" + OsUtil.getOSLineBreak());
        sb.append("  }" + OsUtil.getOSLineBreak());
        sb.append("}" + OsUtil.getOSLineBreak());
        sb.append("for ( var i = 0; i < yValues.length; i++ ) {" + OsUtil.getOSLineBreak());
        sb.append("  for ( var j = 0; j < xValues.length; j++ ) {" + OsUtil.getOSLineBreak());
        sb.append("    var currentValue = zValues[i][j];" + OsUtil.getOSLineBreak());
        sb.append("    // Calculate luminance for each number label." + OsUtil.getOSLineBreak());
        sb.append("    var luminance = 255;" + OsUtil.getOSLineBreak());
        sb.append("    if ( currentValue !== '' && currentValue !== \"NaN\" ) {" + OsUtil.getOSLineBreak());
        sb.append("        var nearestLowerIndex = 0;" + OsUtil.getOSLineBreak());
        sb.append("        var nearestUpperIndex = 1;" + OsUtil.getOSLineBreak());
        sb.append("        var nearestValue = Number.MAX_VALUE;" + OsUtil.getOSLineBreak());
        sb.append("        for ( var k = 0; k < trace1.colorscale.length; k++ ) {" + OsUtil.getOSLineBreak());
        sb.append("            var tuple = trace1.colorscale[k];" + OsUtil.getOSLineBreak());
        sb.append("            var value = tuple[0];" + OsUtil.getOSLineBreak());
        sb.append("            var color = tuple[1];" + OsUtil.getOSLineBreak());
        sb.append("            var scaledValue = (maxZValue - minZValue) * value;" + OsUtil.getOSLineBreak());
        sb.append("            if( Math.abs(currentValue - scaledValue) < nearestValue ) {" + OsUtil.getOSLineBreak());
        sb.append("                nearestLowerIndex = k;" + OsUtil.getOSLineBreak());
        sb.append("                if (k + 1 < trace1.colorscale.length) {" + OsUtil.getOSLineBreak());
        sb.append("                    nearestUpperIndex = k + 1;" + OsUtil.getOSLineBreak());
        sb.append("                }" + OsUtil.getOSLineBreak());
        sb.append("                nearestValue = Math.abs(currentValue - scaledValue);" + OsUtil.getOSLineBreak());
        sb.append("            }" + OsUtil.getOSLineBreak());
        sb.append("        }" + OsUtil.getOSLineBreak());
        sb.append("" + OsUtil.getOSLineBreak());
        sb.append("        var lowerScaledValue = trace1.colorscale[nearestLowerIndex][0] * (maxZValue - minZValue);" + OsUtil.getOSLineBreak());
        sb.append("        var upperScaledValue = trace1.colorscale[nearestUpperIndex][0] * (maxZValue - minZValue);" + OsUtil.getOSLineBreak());
        sb.append("" + OsUtil.getOSLineBreak());
        sb.append("        var valuePositionAsPercentage = 0.0;" + OsUtil.getOSLineBreak());
        sb.append("        if(upperScaledValue > 0.0) {" + OsUtil.getOSLineBreak());
        sb.append("            valuePositionAsPercentage = currentValue / upperScaledValue;" + OsUtil.getOSLineBreak());
        sb.append("        }" + OsUtil.getOSLineBreak());
        sb.append("" + OsUtil.getOSLineBreak());
        sb.append("        var lowerColor = trace1.colorscale[nearestLowerIndex][1];" + OsUtil.getOSLineBreak());
        sb.append("        var upperColor = trace1.colorscale[nearestUpperIndex][1];" + OsUtil.getOSLineBreak());
        sb.append("" + OsUtil.getOSLineBreak());
        sb.append("        var upperColor_r_str = upperColor.split(\"(\")[1].split(\",\")[0];" + OsUtil.getOSLineBreak());
        sb.append("        var upperColor_g_str = upperColor.split(\"(\")[1].split(\",\")[1];" + OsUtil.getOSLineBreak());
        sb.append("        var upperColor_b_str = upperColor.split(\"(\")[1].split(\",\")[2];" + OsUtil.getOSLineBreak());
        sb.append("        var lowerColor_r_str = lowerColor.split(\"(\")[1].split(\",\")[0];" + OsUtil.getOSLineBreak());
        sb.append("        var lowerColor_g_str = lowerColor.split(\"(\")[1].split(\",\")[1];" + OsUtil.getOSLineBreak());
        sb.append("        var lowerColor_b_str = lowerColor.split(\"(\")[1].split(\",\")[2];" + OsUtil.getOSLineBreak());
        sb.append("" + OsUtil.getOSLineBreak());
        sb.append("        var upperColor_r = parseFloat(upperColor_r_str);" + OsUtil.getOSLineBreak());
        sb.append("        var upperColor_g = parseFloat(upperColor_g_str);" + OsUtil.getOSLineBreak());
        sb.append("        var upperColor_b = parseFloat(upperColor_b_str);" + OsUtil.getOSLineBreak());
        sb.append("        var lowerColor_r = parseFloat(lowerColor_r_str);" + OsUtil.getOSLineBreak());
        sb.append("        var lowerColor_g = parseFloat(lowerColor_g_str);" + OsUtil.getOSLineBreak());
        sb.append("        var lowerColor_b = parseFloat(lowerColor_b_str);" + OsUtil.getOSLineBreak());
        sb.append("" + OsUtil.getOSLineBreak());
        sb.append("        var middleColor_r = lowerColor_r + ((upperColor_r - lowerColor_r) * valuePositionAsPercentage);" + OsUtil.getOSLineBreak());
        sb.append("        var middleColor_g = lowerColor_g + ((upperColor_g - lowerColor_g) * valuePositionAsPercentage);" + OsUtil.getOSLineBreak());
        sb.append("        var middleColor_b = lowerColor_b + ((upperColor_b - lowerColor_b) * valuePositionAsPercentage);" + OsUtil.getOSLineBreak());
        sb.append("" + OsUtil.getOSLineBreak());
        sb.append("        luminance = 0.299*(middleColor_r) + 0.587*(middleColor_g) + 0.114*(middleColor_b);" + OsUtil.getOSLineBreak());
        sb.append("    }" + OsUtil.getOSLineBreak());
        sb.append(    "" + OsUtil.getOSLineBreak());
        sb.append("    var textColor = (luminance >= 128) ? 'black' : 'white';" + OsUtil.getOSLineBreak());
        sb.append("    var result = {" + OsUtil.getOSLineBreak());
        sb.append("      xref: 'x1'," + OsUtil.getOSLineBreak());
        sb.append("      yref: 'y1'," + OsUtil.getOSLineBreak());
        sb.append("      x: xValues[j]," + OsUtil.getOSLineBreak());
        sb.append("      y: yValues[i]," + OsUtil.getOSLineBreak());
        sb.append("      text: $$$WINDOW_SHOW_NUMBER_LABELS," + OsUtil.getOSLineBreak());
        sb.append("      font: {" + OsUtil.getOSLineBreak());
        sb.append("        family: $$$WINDOW_FONT," + OsUtil.getOSLineBreak());
        sb.append("        size: 12" + OsUtil.getOSLineBreak());
        sb.append("      }," + OsUtil.getOSLineBreak());
        sb.append("      showarrow: false," + OsUtil.getOSLineBreak());
        sb.append("      font: {" + OsUtil.getOSLineBreak());
        sb.append("        color: textColor" + OsUtil.getOSLineBreak());
        sb.append("      }" + OsUtil.getOSLineBreak());
        sb.append("    };" + OsUtil.getOSLineBreak());
        sb.append("    layout.annotations.push(result);" + OsUtil.getOSLineBreak());
        sb.append("  }" + OsUtil.getOSLineBreak());
        sb.append("}" + OsUtil.getOSLineBreak());
        sb.append(    "" + OsUtil.getOSLineBreak());
        sb.append("Plotly.newPlot($$$WINDOW_DIV_NAME, data, layout, modeBarSettings);" + OsUtil.getOSLineBreak());

        sb.append("</script>");
        return sb.toString();
    }

    public static String getCanvasBase() {
        StringBuilder sb = new StringBuilder();
        sb.append("$$CANVAS_TRACES").append(OsUtil.getOSLineBreak());
        return sb.toString();
    }

    public static String getCanvasLayoutBase() {
        StringBuilder sb = new StringBuilder();
        sb.append("xaxis$$CANVAS_X_INDEX: {").append(OsUtil.getOSLineBreak());
        sb.append("title: $$CANVAS_X_AXIS_LABEL,").append(OsUtil.getOSLineBreak());
        sb.append("type: $$CANVAS_X_TYPE,").append(OsUtil.getOSLineBreak());
        sb.append("position: $$CANVAS_X_POSITION,").append(OsUtil.getOSLineBreak());
        sb.append("domain: [$$CANVAS_X_DOMAIN_START, $$CANVAS_X_DOMAIN_END],").append(OsUtil.getOSLineBreak());
        sb.append("autorange: $$CANVAS_X_AUTOSCALE,").append(OsUtil.getOSLineBreak());
        sb.append("range:[$$CANVAS_X_DISPLAYED_RANGE_START, $$CANVAS_X_DISPLAYED_RANGE_END],").append(OsUtil.getOSLineBreak());
        sb.append("titlefont: {color: $$CANVAS_X_AXIS_COLOR},").append(OsUtil.getOSLineBreak());
        sb.append("tickfont: {color: $$CANVAS_X_AXIS_COLOR},").append(OsUtil.getOSLineBreak());
        sb.append("showgrid: $$CANVAS_DRAW_AXIS_LINES,").append(OsUtil.getOSLineBreak());
        sb.append("zeroline: $$CANVAS_DRAW_GRID_LINES").append(OsUtil.getOSLineBreak());
        sb.append("},").append(OsUtil.getOSLineBreak());
        sb.append("yaxis$$CANVAS_Y_INDEX: {").append(OsUtil.getOSLineBreak());
        sb.append("title: $$CANVAS_Y_AXIS_LABEL,").append(OsUtil.getOSLineBreak());
        sb.append("type: $$CANVAS_Y_TYPE,").append(OsUtil.getOSLineBreak());
        sb.append("position: $$CANVAS_Y_POSITION,").append(OsUtil.getOSLineBreak());
        sb.append("domain: [$$CANVAS_Y_DOMAIN_START, $$CANVAS_Y_DOMAIN_END],").append(OsUtil.getOSLineBreak());
        sb.append("autorange: $$CANVAS_Y_AUTOSCALE,").append(OsUtil.getOSLineBreak());
        sb.append("range:[$$CANVAS_Y_DISPLAYED_RANGE_START, $$CANVAS_Y_DISPLAYED_RANGE_END],").append(OsUtil.getOSLineBreak());
        sb.append("titlefont: {color: $$CANVAS_Y_AXIS_COLOR},").append(OsUtil.getOSLineBreak());
        sb.append("tickfont: {color: $$CANVAS_Y_AXIS_COLOR},").append(OsUtil.getOSLineBreak());
        sb.append("showgrid: $$CANVAS_DRAW_AXIS_LINES,").append(OsUtil.getOSLineBreak());
        sb.append("zeroline: $$CANVAS_DRAW_GRID_LINES").append(OsUtil.getOSLineBreak());
        sb.append("}").append(OsUtil.getOSLineBreak());
        return sb.toString();
    }

    public static String getCanvasLayout3D() {
        StringBuilder sb = new StringBuilder();
        sb.append("scene: {").append(OsUtil.getOSLineBreak());
        sb.append("xaxis: {").append(OsUtil.getOSLineBreak());
        sb.append("title: $$CANVAS_X_AXIS_LABEL,").append(OsUtil.getOSLineBreak());
        sb.append("type: $$CANVAS_X_TYPE,").append(OsUtil.getOSLineBreak());
        sb.append("titlefont: {color: $$CANVAS_X_AXIS_COLOR},").append(OsUtil.getOSLineBreak());
        sb.append("tickfont: {color: $$CANVAS_X_AXIS_COLOR},").append(OsUtil.getOSLineBreak());
        sb.append("showgrid: $$CANVAS_DRAW_GRID_LINES,").append(OsUtil.getOSLineBreak());
        sb.append("zeroline: $$CANVAS_DRAW_AXIS_LINES").append(OsUtil.getOSLineBreak());
        sb.append("},").append(OsUtil.getOSLineBreak());
        sb.append("yaxis: {").append(OsUtil.getOSLineBreak());
        sb.append("title: $$CANVAS_Y_AXIS_LABEL,").append(OsUtil.getOSLineBreak());
        sb.append("type: $$CANVAS_Y_TYPE,").append(OsUtil.getOSLineBreak());
        sb.append("titlefont: {color: $$CANVAS_Y_AXIS_COLOR},").append(OsUtil.getOSLineBreak());
        sb.append("tickfont: {color: $$CANVAS_Y_AXIS_COLOR},").append(OsUtil.getOSLineBreak());
        sb.append("showgrid: $$CANVAS_DRAW_GRID_LINES,").append(OsUtil.getOSLineBreak());
        sb.append("zeroline: $$CANVAS_DRAW_AXIS_LINES").append(OsUtil.getOSLineBreak());
        sb.append("},").append(OsUtil.getOSLineBreak());
        sb.append("zaxis: {").append(OsUtil.getOSLineBreak());
        sb.append("title: $$CANVAS_Z_AXIS_LABEL,").append(OsUtil.getOSLineBreak());
        sb.append("type: $$CANVAS_Z_TYPE,").append(OsUtil.getOSLineBreak());
        sb.append("titlefont: {color: $$CANVAS_Z_AXIS_COLOR},").append(OsUtil.getOSLineBreak());
        sb.append("tickfont: {color: $$CANVAS_Z_AXIS_COLOR},").append(OsUtil.getOSLineBreak());
        sb.append("showgrid: $$CANVAS_DRAW_GRID_LINES,").append(OsUtil.getOSLineBreak());
        sb.append("zeroline: $$CANVAS_DRAW_AXIS_LINES").append(OsUtil.getOSLineBreak());
        sb.append("}").append(OsUtil.getOSLineBreak());
        sb.append("}").append(OsUtil.getOSLineBreak());
        return sb.toString();
    }

    public static String getTraceScatter2D() {
        StringBuilder sb = new StringBuilder();
        sb.append("var $TRACE_NAME = {").append(OsUtil.getOSLineBreak());
        sb.append("x: $TRACE_ARR_X,").append(OsUtil.getOSLineBreak());
        sb.append("y: $TRACE_ARR_Y,").append(OsUtil.getOSLineBreak());
        sb.append("error_y: {").append(OsUtil.getOSLineBreak());
        sb.append("  type: 'data',").append(OsUtil.getOSLineBreak());
        sb.append("  symmetric: false,").append(OsUtil.getOSLineBreak());
        sb.append("  visible: $TRACE_ERROR_ARR_Y_VISIBLE,").append(OsUtil.getOSLineBreak());
        sb.append("  array: $TRACE_ERROR_ARR_Y_UPPER,").append(OsUtil.getOSLineBreak());
        sb.append("  arrayminus: $TRACE_ERROR_ARR_Y_LOWER").append(OsUtil.getOSLineBreak());
        sb.append("},").append(OsUtil.getOSLineBreak());
        sb.append("mode: $TRACE_POINT_MODE,").append(OsUtil.getOSLineBreak());
        sb.append("hovertemplate: $TRACE_HOVER_TEXT,").append(OsUtil.getOSLineBreak());
        sb.append("text: $TRACE_ARR_METADATA,").append(OsUtil.getOSLineBreak());
        sb.append("type: $TRACE_POINT_TYPE,").append(OsUtil.getOSLineBreak());
        sb.append("name: $TRACE_LABEL,").append(OsUtil.getOSLineBreak());
        sb.append("xaxis: $TRACE_PARENT_CANVAS_X,").append(OsUtil.getOSLineBreak());
        sb.append("yaxis: $TRACE_PARENT_CANVAS_Y").append(OsUtil.getOSLineBreak());
        sb.append("};").append(OsUtil.getOSLineBreak());
        return sb.toString();        
    }

    public static String getTraceSurface3D() {
        StringBuilder sb = new StringBuilder();
        sb.append("var $TRACE_NAME = {").append(OsUtil.getOSLineBreak());
        sb.append("x: $TRACE_ARR_X,").append(OsUtil.getOSLineBreak());
        sb.append("y: $TRACE_ARR_Y,").append(OsUtil.getOSLineBreak());
        sb.append("z: $TRACE_ARR_Z,").append(OsUtil.getOSLineBreak());
        sb.append("colorscale: $TRACE_SCALE_COLOR,").append(OsUtil.getOSLineBreak());
        sb.append("showscale: $TRACE_DRAW_COLOR_SCALE,").append(OsUtil.getOSLineBreak());
        sb.append("type: $TRACE_POINT_TYPE,").append(OsUtil.getOSLineBreak());
        sb.append("name: $TRACE_LABEL").append(OsUtil.getOSLineBreak());
        sb.append("};").append(OsUtil.getOSLineBreak());
        return sb.toString();        
    }

    public static String getTraceColoredTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("var xValues = $TRACE_ARR_X;").append(OsUtil.getOSLineBreak());
        sb.append("var yValues = $TRACE_ARR_Y;").append(OsUtil.getOSLineBreak());
        sb.append("var zValues = $TRACE_ARR_Z;").append(OsUtil.getOSLineBreak());
        sb.append("").append(OsUtil.getOSLineBreak());
        sb.append("var $TRACE_NAME = {").append(OsUtil.getOSLineBreak());
        sb.append("    x: xValues,").append(OsUtil.getOSLineBreak());
        sb.append("    y: yValues,").append(OsUtil.getOSLineBreak());
        sb.append("    z: zValues,").append(OsUtil.getOSLineBreak());
        sb.append("    colorscale: $TRACE_SCALE_COLOR,").append(OsUtil.getOSLineBreak());
        sb.append("    showscale: $TRACE_DRAW_COLOR_SCALE,").append(OsUtil.getOSLineBreak());
        sb.append("    type: $TRACE_POINT_TYPE").append(OsUtil.getOSLineBreak());
        sb.append("};");
        return sb.toString();
    }

    public static String getTraceContour() {
        StringBuilder sb = new StringBuilder();
        sb.append("var xValues = $TRACE_ARR_X;").append(OsUtil.getOSLineBreak());
        sb.append("var yValues = $TRACE_ARR_Y;").append(OsUtil.getOSLineBreak());
        sb.append("var zValues = $TRACE_ARR_Z;").append(OsUtil.getOSLineBreak());
        sb.append("").append(OsUtil.getOSLineBreak());
        sb.append("var $TRACE_NAME = {").append(OsUtil.getOSLineBreak());
        sb.append("    x: xValues,").append(OsUtil.getOSLineBreak());
        sb.append("    y: yValues,").append(OsUtil.getOSLineBreak());
        sb.append("    z: zValues,").append(OsUtil.getOSLineBreak());
        sb.append("    colorscale: $TRACE_SCALE_COLOR,").append(OsUtil.getOSLineBreak());
        sb.append("    showscale: $TRACE_DRAW_COLOR_SCALE,").append(OsUtil.getOSLineBreak());
        sb.append("    type: $TRACE_POINT_TYPE,").append(OsUtil.getOSLineBreak());
        sb.append("    autocontour: false,").append(OsUtil.getOSLineBreak());
        sb.append("    contours: {").append(OsUtil.getOSLineBreak());
        sb.append("        start: $TRACE_BOUND_LOWER,").append(OsUtil.getOSLineBreak());
        sb.append("        end: $TRACE_BOUND_UPPER,").append(OsUtil.getOSLineBreak());
        sb.append("        size: $TRACE_PRECISION,").append(OsUtil.getOSLineBreak());
        sb.append("        coloring: $TRACE_POINT_MODE").append(OsUtil.getOSLineBreak());
        sb.append("    }").append(OsUtil.getOSLineBreak());
        sb.append("};").append(OsUtil.getOSLineBreak());
        return sb.toString();        
    }

    public static String getWatchrGraphCss() {
        StringBuilder sb = new StringBuilder();
        sb.append("<style type=\"text/css\">").append(OsUtil.getOSLineBreak());
        sb.append("    .Table").append(OsUtil.getOSLineBreak());
        sb.append("    {").append(OsUtil.getOSLineBreak());
        sb.append("        display: table;").append(OsUtil.getOSLineBreak());
        sb.append("    }").append(OsUtil.getOSLineBreak());
        sb.append("    .Title").append(OsUtil.getOSLineBreak());
        sb.append("    {").append(OsUtil.getOSLineBreak());
        sb.append("        display: table-caption;").append(OsUtil.getOSLineBreak());
        sb.append("        text-align: center;").append(OsUtil.getOSLineBreak());
        sb.append("        font-weight: bold;").append(OsUtil.getOSLineBreak());
        sb.append("        font-size: larger;").append(OsUtil.getOSLineBreak());
        sb.append("    }").append(OsUtil.getOSLineBreak());
        sb.append("    .Heading").append(OsUtil.getOSLineBreak());
        sb.append("    {").append(OsUtil.getOSLineBreak());
        sb.append("        display: table-row;").append(OsUtil.getOSLineBreak());
        sb.append("        font-weight: bold;").append(OsUtil.getOSLineBreak());
        sb.append("        text-align: center;").append(OsUtil.getOSLineBreak());
        sb.append("    }").append(OsUtil.getOSLineBreak());
        sb.append("    .Row").append(OsUtil.getOSLineBreak());
        sb.append("    {").append(OsUtil.getOSLineBreak());
        sb.append("        display: table-row;").append(OsUtil.getOSLineBreak());
        sb.append("    }").append(OsUtil.getOSLineBreak());
        sb.append("    .Cell").append(OsUtil.getOSLineBreak());
        sb.append("    {").append(OsUtil.getOSLineBreak());
        sb.append("        display: table-cell;").append(OsUtil.getOSLineBreak());
        sb.append("        border: none;").append(OsUtil.getOSLineBreak());
        sb.append("        border-width: thin;").append(OsUtil.getOSLineBreak());
        sb.append("        padding-left: 5px;").append(OsUtil.getOSLineBreak());
        sb.append("        padding-right: 5px;").append(OsUtil.getOSLineBreak());
        sb.append("    }").append(OsUtil.getOSLineBreak());
        sb.append("</style>").append(OsUtil.getOSLineBreak());
        return sb.toString();
        
    }

    private static String generateJavascriptStruct(Map<String, String> properties) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int elementCount = 0;
        for(Entry<String,String> entry : properties.entrySet()) {
            sb.append(OsUtil.getOSLineBreak());
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
            elementCount++;
            if(elementCount < properties.entrySet().size()) {
                sb.append(",");
            }
        }
        sb.append(OsUtil.getOSLineBreak());
        sb.append("}");
        return sb.toString();
    }    

    private static String generateJavascriptStruct(String varName, Map<String, String> properties) {
        StringBuilder sb = new StringBuilder();
        sb.append("var ").append(varName).append(" = {");
        int elementCount = 0;
        for(Entry<String,String> entry : properties.entrySet()) {
            sb.append(OsUtil.getOSLineBreak());
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
            elementCount++;
            if(elementCount < properties.entrySet().size()) {
                sb.append(",");
            }
        }
        sb.append(OsUtil.getOSLineBreak());
        sb.append("}");
        return sb.toString();
    }

    private static String generateJavascriptStruct(String varName, Map<String, String> properties, String suffixText) {
        StringBuilder sb = new StringBuilder();
        sb.append("var ").append(varName).append(" = {");
        int elementCount = 0;
        for(Entry<String,String> entry : properties.entrySet()) {
            sb.append(OsUtil.getOSLineBreak());
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
            elementCount++;
            if(elementCount < properties.entrySet().size()) {
                sb.append(",");
            }
        }
        sb.append(",").append(OsUtil.getOSLineBreak());
        sb.append(suffixText).append(OsUtil.getOSLineBreak());
        sb.append("}");
        return sb.toString();
    }
}
