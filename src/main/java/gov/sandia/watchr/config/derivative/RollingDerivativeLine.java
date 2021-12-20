package gov.sandia.watchr.config.derivative;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.ArrayUtil;

public abstract class RollingDerivativeLine extends DerivativeLine {
    
    ////////////
    // FIELDS //
    ////////////

    protected int rollingRange;
    protected boolean ignoreFilteredData;
    protected String numberFormat = "#.####";

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    protected RollingDerivativeLine(String configPathPrefix, ILogger logger) {
        super(configPathPrefix, logger);
    }

    protected RollingDerivativeLine(RollingDerivativeLine copy) {
        super(copy);
        this.rollingRange = copy.getRollingRange();
        this.ignoreFilteredData = copy.shouldIgnoreFilteredData();
        this.numberFormat = copy.getNumberFormat();
    }

    /////////////
    // GETTERS //
    /////////////

    public int getRollingRange() {
        return rollingRange;
    }

    public boolean shouldIgnoreFilteredData() {
        return ignoreFilteredData;
    }

    public String getNumberFormat() {
        return numberFormat;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setRollingRange(int rollingRange) {
        this.rollingRange = rollingRange;
    }

    public void setIgnoreFilteredData(boolean ignoreFilteredData) {
        this.ignoreFilteredData = ignoreFilteredData;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

    /////////////
    // UTILITY //
    /////////////

    public List<PlotTracePoint> calculateRollingLine(List<PlotTracePoint> points) {
        logger.logDebug("RollingDerivativeLine:calculateRollingLine()");
        logger.logDebug("Number of points: " + points.size());
        List<PlotTracePoint> newRollingLine = new ArrayList<>();
        newRollingLine.add(points.get(0));

        for(int i = 1; i < points.size(); i++) {
            String lastXValue = points.get(i).x;

            int rangeSize = rollingRange;
            if(i < rollingRange) {
                rangeSize = i;
            }

            List<Double> listRangeToInspect = new ArrayList<>();
            for(int j = i; j >= i-rangeSize; j--) {
                if(NumberUtils.isCreatable(points.get(j).y)) {
                    listRangeToInspect.add(Double.parseDouble(points.get(j).y));
                }
            }

            double[] data = ArrayUtil.asDoubleArrFromDoubleList(listRangeToInspect);
            double newValue = calculateValue(data);
            String newValueDisplay = new DecimalFormat(numberFormat).format(newValue);
            newRollingLine.add(new PlotTracePoint(lastXValue, newValueDisplay));
        }

        return newRollingLine;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        super.validate();
        if(rollingRange < 1) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "The rolling range for a derivative line must use 1 or more points!"));
        }
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        RollingDerivativeLine otherDerivativeLine = (RollingDerivativeLine) other;
        List<WatchrDiff<?>> diffList = super.diff(other);

        if(rollingRange != otherDerivativeLine.rollingRange) {
            WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.ROLLING_RANGE);
            diff.setBeforeValue(rollingRange);
            diff.setNowValue(otherDerivativeLine.rollingRange);
            diffList.add(diff);
        }

        if(ignoreFilteredData != otherDerivativeLine.ignoreFilteredData) {
            WatchrDiff<Boolean> diff = new WatchrDiff<>(configPath, DiffCategory.IGNORE_FILTERED_DATA);
            diff.setBeforeValue(ignoreFilteredData);
            diff.setNowValue(otherDerivativeLine.ignoreFilteredData);
            diffList.add(diff);
        }

        if((numberFormat == null ^ otherDerivativeLine.numberFormat == null) ||
           (numberFormat != null && !numberFormat.equals(otherDerivativeLine.numberFormat))) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.NUMBER_FORMAT);
            diff.setBeforeValue(numberFormat);
            diff.setNowValue(otherDerivativeLine.numberFormat);
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
			RollingDerivativeLine otherDerivativeLine = (RollingDerivativeLine) other;
            equals = equals && rollingRange == otherDerivativeLine.rollingRange;
            equals = equals && ignoreFilteredData == otherDerivativeLine.ignoreFilteredData;
            equals = equals && 
                ((numberFormat == null && otherDerivativeLine.numberFormat == null) ||
                 (numberFormat != null && numberFormat.equals(otherDerivativeLine.numberFormat)));
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 31 * (hash + Integer.hashCode(rollingRange));
        hash = 31 * (hash + Boolean.hashCode(ignoreFilteredData));

        if(StringUtils.isNotBlank(numberFormat)) {
            hash = 31 * (hash + numberFormat.hashCode());
        }
        return hash;
    }

    //////////////
    // ABSTRACT //
    //////////////

    protected abstract double calculateValue(double[] data);
}
