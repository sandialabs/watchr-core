package gov.sandia.watchr.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class HtmlUtilTest {
    
    @Test
    public void test_CreateBreak() {
        assertEquals(HtmlConstants.BR, HtmlUtil.createBreak());
    }

    @Test
    public void test_CreateButton() {
        String expected = "<button type=\"button\">Click Me!</button>";
        String actual = HtmlUtil.createButton("button", "Click Me!");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateDiv_1() {
        String expected = "<div id='myId' class='myClass' align='myAlign' style='myStyle'>Test</div>";
        String actual = HtmlUtil.createDiv("Test", "myId", "myClass", "myAlign", "myStyle");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateDiv_2() {
        String expected = "<div style='myStyle'>Test</div>";
        String actual = HtmlUtil.createDiv("Test", "myStyle");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateDiv_3() {
        String expected = "<div>Test</div>";
        String actual = HtmlUtil.createDiv("Test");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateDiv_4() {
        String expected = "<div align='myAlign' style='myStyle'>Test</div>";
        String actual = HtmlUtil.createDiv("Test", "myAlign", "myStyle");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateForm() {
        String expected = "<form name='myName' method='myMethod' action='myAction' autocomplete='off'>Test</form>";
        String actual = HtmlUtil.createForm("Test", "myName", "myMethod", "myAction", "off");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateH2() {
        String expected = "<h2 style='myStyle'>Test</h2>";
        String actual = HtmlUtil.createH2("Test", "myStyle");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateImage_1() {
        String expected = "<img src='imagePath'/>";
        String actual = HtmlUtil.createImage("imagePath");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateImage_2() {
        String expected = "<img width='640' height='480' src='imagePath'/>";
        String actual = HtmlUtil.createImage("imagePath", 640, 480);
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateInput_1() {
        String expected = "<input id='myName' class='myClass' name='myName' type='myType' value='myValue' style='myStyle'>Test</input>";
        String actual = HtmlUtil.createInput("Test", "myId", "myClass", "myName", "myType", "myValue", "myStyle");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateInput_2() {
        String expected = "<input id='myName' class='myClass' name='myName' type='myType' value='myValue'>Test</input>";
        String actual = HtmlUtil.createInput("Test", "myId", "myClass", "myName", "myType", "myValue");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateCheckboxInput() {
        String expected = "<input id='myName' class='myClass' name='myName' type='myType' value='myValue' style='myStyle' checked>Test</input>";
        String actual = HtmlUtil.createCheckboxInput("Test", "myId", "myClass", "myName", "myType", "myValue", "myStyle", true);
        assertEquals(expected, actual);
    }
    
    @Test
    public void test_CreateLink_1() {
        String expected = "<a href='google.com'>MyLink</a>";
        String actual = HtmlUtil.createLink("google.com", "MyLink");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateLink_2() {
        String expected = "<a href='google.com' style='MyStyle'>MyLink</a>";
        String actual = HtmlUtil.createLink("google.com", "MyStyle", "MyLink");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateP() {
        String expected = "<p style='MyStyle'>Test</p>";
        String actual = HtmlUtil.createP("Test", "MyStyle");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateParameterList() {
        Map<String,String> parameters = new HashMap<>();
        parameters.put("A","1");
        parameters.put("B","2");

        String expected = "?A=1&B=2";
        String actual = HtmlUtil.createParameterList(parameters);
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateSelection() {
        List<String> options = new ArrayList<>();
        options.add("<p>1</p>");
        options.add("<p>2</p>");
        options.add("<p>3</p>");

        String expected = "<select class='myClass' name='myName' style='myStyle'><p>1</p><p>2</p><p>3</p></select>";
        String actual = HtmlUtil.createSelection(options, "myClass", "myName", "myStyle");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateSelectionOption() {
        String expected = "<option value='myValue' selected='selected'>myOption</option>";
        String actual = HtmlUtil.createSelectionOption("myOption", "myValue", true);
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateTableCell_1() {
        String expected = "<td class='myClass' style='myStyle' colspan='1'>Test</td>";
        String actual = HtmlUtil.createTableCell("Test", "myClass", "myStyle", "1");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateTableCell_2() {
        String expected = "<td class='myClass' style='myStyle'>Test</td>";
        String actual = HtmlUtil.createTableCell("Test", "myClass", "myStyle");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateTableRow() {
        List<String> tds = new ArrayList<>();
        tds.add("<td>1</td>");
        tds.add("<td>2</td>");
        tds.add("<td>3</td>");

        String expected = "<tr style='MyStyle'><td>1</td><td>2</td><td>3</td></tr>";
        String actual = HtmlUtil.createTableRow(tds, "MyStyle");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateTable_1() {
        List<String> trs = new ArrayList<>();
        trs.add("<tr>1</tr>");
        trs.add("<tr>2</tr>");
        trs.add("<tr>3</tr>");

        Map<String, String> additionalProps = new HashMap<>();
        additionalProps.put("A", "1");
        additionalProps.put("B", "2");

        String expected = "<table id='myId' class='myClass' style='myStyle' A='1' B='2'><tr>1</tr><tr>2</tr><tr>3</tr></table>";
        String actual = HtmlUtil.createTable(trs, "myId", "myClass", "myStyle", additionalProps);
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateTable_2() {
        List<String> trs = new ArrayList<>();
        trs.add("<tr>1</tr>");
        trs.add("<tr>2</tr>");
        trs.add("<tr>3</tr>");

        String expected = "<table id='myId' class='myClass' style='myStyle'><tr>1</tr><tr>2</tr><tr>3</tr></table>";
        String actual = HtmlUtil.createTable(trs, "myId", "myClass", "myStyle");
        assertEquals(expected, actual);
    }

    @Test
    public void test_CreateGraphLinkParameterList() {
        try {
            String expected = "?path=path&page=1";
            String actual = HtmlUtil.createGraphLinkParameterList("path", 1);
            assertEquals(expected, actual); 
        } catch(UnsupportedEncodingException e) {
            fail(e.getMessage());
        }
    }
}
