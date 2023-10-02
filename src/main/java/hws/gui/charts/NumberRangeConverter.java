/*
 * Copyright (C) 2023 grimm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package hws.gui.charts;

import hws.gui.charts.skins.RangeControlSet;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.scene.chart.ValueAxis;

/**
 *
 * @author grimm
 */
public class NumberRangeConverter
extends RangeConverter<Number>
{
    private ValueAxis<Number> axis;
    private double minRange;
    private double maxRange;

    public NumberRangeConverter(ValueAxis<Number> axis)
    {
        super(axis);
        this.axis = axis;
        
        minRange = axis.getLowerBound();
        maxRange = axis.getUpperBound();

        if (axis.isAutoRanging()) {
            axis.setAutoRanging(false);
            axis.setUpperBound(maxRange);
            axis.setLowerBound(minRange);
        }
        
        axis.upperBoundProperty().addListener(axisUpperBoundlistener);
        axis.lowerBoundProperty().addListener(axisLowerBoundlistener);
    }

    @Override
    public void updateData(List<Number> list)
    {
        if (list.isEmpty()) {
            minRange = axis.getLowerBound();
            maxRange = axis.getUpperBound();

        } else  {
            minRange = Double.MAX_VALUE;
            maxRange = -Double.MAX_VALUE;

            for (Number value : list) {
                minRange = Math.min(minRange, value.doubleValue());
                maxRange = Math.max(maxRange, value.doubleValue());
            }
        }

        if (minRange > axis.getLowerBound())  minRange = axis.getLowerBound();
        if (maxRange < axis.getUpperBound())  maxRange = axis.getUpperBound();
    }
    
    /**
     * Establish the binding of the converter. It links the input to the output and vice versa.
     * 
     * @param rangeCTRL  RangeControlSet instance with the controls for one axis
     */
    @Override
    public void link(RangeControlSet rangeCTRL)
    {
        registerControlSet(rangeCTRL);
                
        // set initial values
        rangeCTRL.setUpperLimit(numberToPercent(axis.getUpperBound()));
        rangeCTRL.setLowerLimit(numberToPercent(axis.getLowerBound()));

        // set initial range length and position
        double rangeLength = calcRangeLength(axis);
        rangeCTRL.setRangeLengthAndPosition(rangeLength);
        
        ChangeListener<Number> upperRangeLimitlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                System.err.printf("%s%d UpperRangeLimit (%f / %f)\n", " ".repeat(level), level, oVal.doubleValue(), nVal.doubleValue());
                level += 1;

                Number data = percentToNumber(nVal.doubleValue());
                axis.setUpperBound(data.doubleValue());            // calls axisUpperBoundListener

                double rangeLen = calcRangeLength(axis);
                rangeCTRL.setRangeLengthAndPosition(rangeLen); // calls rangePositionListener

                level -= 1;
            };

        ChangeListener<Number> lowerRangeLimitlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                System.err.printf("%s%d LowerRangeLimit (%f / %f)\n", " ".repeat(level), level, oVal.doubleValue(), nVal.doubleValue());
                level += 1;

                Number data = percentToNumber(nVal.doubleValue());
                axis.setLowerBound(data.doubleValue());            // calls axisLowerBoundListener

                double rangeLen = calcRangeLength(axis);
                rangeCTRL.setRangeLengthAndPosition(rangeLen); // calls rangePositionListener

                level -= 1;
            };

        ChangeListener<Number> rangePositionlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                System.err.printf("%s%d RangePosition (%f / %f)\n", " ".repeat(level), level, oVal.doubleValue(), nVal.doubleValue());
                level += 1;

                rangeCTRL.moveLimits(nVal.doubleValue());   // calls lowerRangeLimitListener
                                                                   // calls upperRangeLimitListener
                level -= 1;
        };

        rangeCTRL.addUpperLimitListener(upperRangeLimitlistener);
        rangeCTRL.addLowerLimitListener(lowerRangeLimitlistener);
        rangeCTRL.addRangeListener(rangePositionlistener);
    }

    private double calcRangeLength(ValueAxis<? extends Number> axis)
    {
        double val = 100.0 * (axis.getUpperBound() - axis.getLowerBound()) /
                                 (maxRange - minRange);
        return val;
    }
    
    private double numberToPercent(Number value)
    {
        if (Math.abs(maxRange - minRange) <= 0.000001)
                return 0;

        double range = maxRange - minRange;
        return (value.doubleValue() - minRange) / range * 100;
    }

    private Number percentToNumber(double percent)
    {
        double range = maxRange - minRange;
        return percent/100 * range + minRange;
    }

    /****************************************************************************************/
    /*                              Listener definitions                                    */    
    /****************************************************************************************/
    
    int level = 0;
    

    private final ChangeListener<Number> axisUpperBoundlistener = (obs, oVal, nVal) -> {
            if (areEqual(oVal, nVal)) return;

            System.err.printf("%s%d UpperAxisBoundariy (%f / %f)\n", " ".repeat(level), level, oVal.doubleValue(), nVal.doubleValue());
            level += 1;

            double percent = numberToPercent(nVal);
            double value = nVal.doubleValue();

            if (axis.getUpperBound() < axis.getLowerBound()) {
                value = axis.getLowerBound();
                percent = numberToPercent(axis.getLowerBound());
            }

            if (nVal.doubleValue() < minRange) {
                value = minRange;
                percent = 0;

            } else if (nVal.doubleValue() > maxRange) {
                value = maxRange;
                percent = 100;
            }

            if (nVal.doubleValue() != value)
                axis.setUpperBound(value);                // calls axisUpperBoundListener

            for (RangeControlSet item : listControlSets)
                item.setUpperLimit(percent);              // calls upperRangeLimitListener twice

            level -= 1;
        };
            
    private final ChangeListener<Number> axisLowerBoundlistener = (obs, oVal, nVal) -> {
            if (areEqual(oVal, nVal)) return;

            System.err.printf("%s%d LowerAxisBoundariy (%f / %f)\n", " ".repeat(level), level, oVal.doubleValue(), nVal.doubleValue());
            level += 1;

            double percent = numberToPercent(axis.getLowerBound());
            double value = nVal.doubleValue();

            if (axis.getLowerBound() > axis.getUpperBound()) {
                value = axis.getUpperBound();
                percent = numberToPercent(value);
            }

            if (nVal.doubleValue() < minRange) {
                value = minRange;
                percent = 0;

            } else if (nVal.doubleValue() > maxRange) {
                value = maxRange;
                percent = 100;
            }

            if (nVal.doubleValue() != value)
                axis.setLowerBound(value);                // calls axisLowerBoundListener
            
            for (RangeControlSet item : listControlSets)
                item.setLowerLimit(percent);              // calls lowerRangeLimitListener twice

            level -= 1;
        };
}
