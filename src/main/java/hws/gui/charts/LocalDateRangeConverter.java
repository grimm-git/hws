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
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.List;
import javafx.beans.value.ChangeListener;

/**
 *
 * @author grimm
 */
public class LocalDateRangeConverter
extends RangeConverter<LocalDate>
{
    private LocalDateAxis axis;
    private LocalDate minRange;
    private LocalDate maxRange;

    public LocalDateRangeConverter(LocalDateAxis axis)
    {
        super(axis);
        this.axis = axis;
        
        if (axis.getLowerBound() != null && axis.getUpperBound() != null) {
            minRange = axis.getLowerBound();
            maxRange = axis.getUpperBound();
        } else {
            LocalDate now = LocalDate.now();
            minRange = LocalDate.of(now.getYear(), 1, 1);   // Default:  1. 1.<current year>
            maxRange = LocalDate.of(now.getYear(), 12, 31); //          31.12.<current year>
        }
        
        if (axis.isAutoRanging()) {
            axis.setAutoRanging(false);
            axis.setLowerBound(minRange);
            axis.setUpperBound(maxRange);
        }
        
        axis.upperBoundProperty().addListener(axisUpperBoundlistener);
        axis.lowerBoundProperty().addListener(axisLowerBoundlistener);
    }
     
    @Override
    public void updateData(List<LocalDate> list)
    {
        if (list.isEmpty()) {
            minRange = axis.getLowerBound();   // Default range:  1. 1.<current year>
            maxRange = axis.getUpperBound();   //                31.12.<current year>
            
        } else {
            minRange = LocalDate.MAX;
            maxRange = LocalDate.MIN;

            for (LocalDate date : list) {
                if (date.isBefore(minRange))
                    minRange = date;
                if (date.isAfter(maxRange))
                    maxRange = date;
            }
        }
        
        if (axis.getUpperBound().isAfter(minRange) && axis.getLowerBound().isBefore(maxRange)) {
            if (axis.getLowerBound().isBefore(minRange))  minRange = axis.getLowerBound();
            if (axis.getUpperBound().isAfter(maxRange))   maxRange = axis.getUpperBound();
        }

        axis.setLowerBound(minRange);
        axis.setUpperBound(maxRange);
    }

    /**
     * Establish the binding of the converter.It links the input to the output and vice versa.
     * 
     * @param rangeCTRL  RangeControlSet instance with the controls for one axis
     */
    @Override
    public void link(RangeControlSet rangeCTRL)
    {
        registerControlSet(rangeCTRL);

        // set initial values
        if (axis.getUpperBound() != null)
            rangeCTRL.setUpperLimit(localDateToPercent(axis.getUpperBound()));
        
        if (axis.getLowerBound() != null)
            rangeCTRL.setLowerLimit(localDateToPercent(axis.getLowerBound()));
            
        // set initial range length and position
        double rangeLength = calcRangeLength(axis);
        rangeCTRL.setRangeLengthAndPosition(rangeLength);
        
        ChangeListener<Number> upperRangeLimitlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                System.err.printf("%s%d UpperRangeLimit (%f / %f)\n", " ".repeat(level), level, oVal.doubleValue(), nVal.doubleValue());
                level += 1;

                LocalDate date = percentToLocalDate(nVal.doubleValue());
                axis.setUpperBound(date);                          // calls axisUpperBoundListener

                double rangeLen = calcRangeLength(axis);
                rangeCTRL.setRangeLengthAndPosition(rangeLen);     // calls rangePositionListener

                level -= 1;
            };

        ChangeListener<Number> lowerRangeLimitlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                System.err.printf("%s%d LowerRangeLimit (%f / %f)\n", " ".repeat(level), level, oVal.doubleValue(), nVal.doubleValue());
                level += 1;

                LocalDate date = percentToLocalDate(nVal.doubleValue());
                axis.setLowerBound(date);                          // calls axisLowerBoundListener

                double rangeLen = calcRangeLength(axis);
                rangeCTRL.setRangeLengthAndPosition(rangeLen);     // calls rangePositionListener

                level -= 1;
            };

        ChangeListener<Number> rangePositionlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                System.err.printf("%s%d RangePosition (%f / %f)\n", " ".repeat(level), level, oVal.doubleValue(), nVal.doubleValue());
                level += 1;

                rangeCTRL.moveLimits(nVal.doubleValue());          // calls lowerRangeLimitListener
                                                                   // calls upperRangeLimitListener

                level -= 1;
            };

        rangeCTRL.addUpperLimitListener(upperRangeLimitlistener);
        rangeCTRL.addLowerLimitListener(lowerRangeLimitlistener);
        rangeCTRL.addRangeListener(rangePositionlistener);
    }

    private double calcRangeLength(LocalDateAxis axis)
    {
        return 100.0 * DAYS.between(axis.getUpperBound(),axis.getLowerBound()) /
                       DAYS.between(maxRange, minRange);
    }

    private double localDateToPercent(LocalDate date)
    {
        long range = maxRange.toEpochDay() - minRange.toEpochDay();
        return range == 0 ? 0 : (double)(date.toEpochDay() - minRange.toEpochDay()) / range  * 100;
    }

    private LocalDate percentToLocalDate(double percent)
    {
        long range = maxRange.toEpochDay() - minRange.toEpochDay();
        long epochDay = Math.round(percent/100 * range);
        return LocalDate.ofEpochDay(epochDay + minRange.toEpochDay());
    }

    /****************************************************************************************/
    /*                              Listener definitions                                    */    
    /****************************************************************************************/

    int level = 0;

    private final ChangeListener<LocalDate> axisUpperBoundlistener = (obs, oVal, nVal) -> {
            System.err.printf("%s%d UpperAxisBoundariy (%s / %s)\n", " ".repeat(level), level, oVal == null ? "null" : oVal.toString(), nVal.toString());
            level += 1;

            double percent = localDateToPercent(nVal);
            LocalDate value = nVal;

            if (axis.getUpperBound().isBefore(axis.getLowerBound())) {
                value = axis.getLowerBound();
                percent = localDateToPercent(axis.getLowerBound());
            }

            if (nVal.isBefore(minRange)) {
                value = minRange;
                percent = 0;
            } else if (nVal.isAfter(maxRange)) {
                value = maxRange;
                percent = 100;
            }

            if (!nVal.equals(value))
                axis.setUpperBound(value);                     // calls axisUpperBoundListener

            for (RangeControlSet item : listControlSets)
                item.setUpperLimit(percent);              // calls upperRangeLimitListener twice

            level -= 1;
        };
            
    private final ChangeListener<LocalDate> axisLowerBoundlistener = (obs, oVal, nVal) -> {
            System.err.printf("%s%d LowerAxisBoundariy (%s / %s)\n", " ".repeat(level), level, oVal == null ? "null" : oVal.toString(), nVal.toString());
            level += 1;

            double percent = localDateToPercent(nVal);
            LocalDate value = nVal;

            if (axis.getLowerBound().isAfter(axis.getUpperBound())) {
                value = axis.getUpperBound();
                percent = localDateToPercent(value);
            }

            if (value.isBefore(minRange)) {
                value = minRange;
                percent = 0;
            } else if (value.isAfter(maxRange)) {
                value = maxRange;
                percent = 100;
            }

            if (!nVal.equals(value))
                axis.setLowerBound(value);                     // calls axisLowerBoundListener

            for (RangeControlSet item : listControlSets)
                item.setLowerLimit(percent);              // calls lowerRangeLimitListener twice

            level -= 1;
        };
}
