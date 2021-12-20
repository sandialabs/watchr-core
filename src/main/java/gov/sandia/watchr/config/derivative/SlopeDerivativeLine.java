package gov.sandia.watchr.config.derivative;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.log.ILogger;

/**
 * 
 * @author Elliott Ridgway
 */
public class SlopeDerivativeLine extends DerivativeLine {

    ////////////
    // FIELDS //
    ////////////

    private String xExp = "";
    private String yExp = "";

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SlopeDerivativeLine(String configPathPrefix, ILogger logger) {
        super(configPathPrefix + "/derivativeLine/slope", logger);
    }

    public SlopeDerivativeLine(SlopeDerivativeLine copy) {
        super(copy);
        this.xExp = copy.getXExpression();
        this.yExp = copy.getYExpression();
    }

    /////////////
    // GETTERS //
    /////////////

    public String getXExpression() {
        return xExp;
    }
    
    public String getYExpression() {
        return yExp;
    }

    public double getYForX(double xValue) {
        if(NumberUtils.isCreatable(yExp)) {
            return Double.parseDouble(yExp);
        } else {
            return dijkstraTwoStack(yExp, "x", xValue);
        }
    }

    public double getXForY(double yValue) {
        if(NumberUtils.isCreatable(xExp)) {
            return Double.parseDouble(xExp);
        } else {
            return dijkstraTwoStack(xExp, "y", yValue);
        }
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setXExpression(String xExp) {
        this.xExp = xExp;
    }

    public void setYExpression(String yExp) {
        this.yExp = yExp;
    }

    /////////////
    // UTILITY //
    /////////////

    public List<PlotTracePoint> calculateSlopeLine(List<PlotTracePoint> points) {
        List<PlotTracePoint> newSlopeLine = new ArrayList<>();
        if(StringUtils.isNotBlank(yExp)) {
            for(PlotTracePoint point : points) {
                if(NumberUtils.isCreatable(point.x)) {
                    double newYPoint = getYForX(Double.parseDouble(point.x));
                    newSlopeLine.add(new PlotTracePoint(point.x, Double.toString(newYPoint)));
                } else {
                    logger.logWarning(point.x + " is not formattable for calculating a slope line.");
                }
            }
        } else if(StringUtils.isNotBlank(xExp)) {
            for(PlotTracePoint point : points) {
                if(NumberUtils.isCreatable(point.y)) {
                    double newXPoint = getXForY(Double.parseDouble(point.y));
                    newSlopeLine.add(new PlotTracePoint(Double.toString(newXPoint), point.y));
                } else {
                    logger.logWarning(point.y + " is not formattable for calculating a slope line.");
                }
            }
        } else {
            logger.logWarning("Tried to plot a slope-based line, but no expressions currently define the line.");
        }
        return newSlopeLine;
    }

    /////////////
    // PRIVATE //
    /////////////

    private double dijkstraTwoStack(String expression, String variable, double value) {
        String[] exp = expression.split("\\s+");
        Deque<String> ops = new ArrayDeque<>();
        Deque<Double> vals = new ArrayDeque<>();

        for (int i = 0; i < exp.length; i++) {
            String s = exp[i];
            if (!s.equals("(")) {
                if (s.equals("+") || s.equals("*")) {
                    ops.push(s);
                } else if (s.equals(")")) {
                    getComp(ops, vals);
                } else {
                    if(s.equalsIgnoreCase(variable)) {
                        vals.push(value);
                    } else {
                        vals.push(Double.parseDouble(s));
                    }
                }
            }
        }
        getComp(ops, vals);
        return vals.pop();
    }

    private void getComp(Deque<String> ops, Deque<Double> vals) {
        String op = ops.pop();
        if (op.equals("+")) {
            vals.push(vals.pop() + vals.pop());
        } else if (op.equals("*")) {
            vals.push(vals.pop() * vals.pop());
        }
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        SlopeDerivativeLine otherDerivativeLine = (SlopeDerivativeLine) other;
        List<WatchrDiff<?>> diffList = super.diff(other);

        if(!xExp.equals(otherDerivativeLine.xExp)) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.EXPRESSION_X);
            diff.setBeforeValue(xExp);
            diff.setNowValue(otherDerivativeLine.xExp);
            diffList.add(diff);
        }

        if(!yExp.equals(otherDerivativeLine.yExp)) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.EXPRESSION_Y);
            diff.setBeforeValue(yExp);
            diff.setNowValue(otherDerivativeLine.yExp);
            diffList.add(diff);
        }

        return diffList;
    }

    @Override
    public boolean equals(Object other) {
        boolean equals = super.equals(other);
		if(other == null) {
            return false;
        } else if(other == this) {
            return true;
        } else if(getClass() != other.getClass()) {
            return false;
        } else {
			SlopeDerivativeLine otherDerivativeLine = (SlopeDerivativeLine) other;
            equals = equals && xExp.equals(otherDerivativeLine.xExp);
            equals = equals && yExp.equals(otherDerivativeLine.yExp);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 31 * (hash + xExp.hashCode());
        hash = 31 * (hash + yExp.hashCode());
        return hash;
    }

    @Override
    public DerivativeLine applyOverTemplate(DerivativeLine template) {
        if(template instanceof AverageDerivativeLine) {
            SlopeDerivativeLine newDerivativeLine = new SlopeDerivativeLine((SlopeDerivativeLine)template);
            newDerivativeLine.setColor(getColor());
            newDerivativeLine.setName(getName());
            newDerivativeLine.setXExpression(getXExpression());
            newDerivativeLine.setYExpression(getYExpression());
        }
        return null;
    }
}
