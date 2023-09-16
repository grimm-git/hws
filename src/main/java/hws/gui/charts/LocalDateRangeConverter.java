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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author grimm
 */
public class LocalDateRangeConverter
extends RangeConverter
{
    private LocalDateAxis axis;
    private LocalDate minRange;
    private LocalDate maxRange;

    public LocalDateRangeConverter(LocalDateAxis axis, ObservableList<? extends LocalDate> data)
    {
        super(axis, data);
        this.axis = axis;
        
        extractRange(axis, data);
        if (axis.isAutoRanging()) {
            axis.setAutoRanging(false);
            axis.setUpperBound(maxRange);
            axis.setLowerBound(minRange);
        }

        data.addListener((ListChangeListener.Change<? extends LocalDate> c) -> {
                c.next();
                for (LocalDate date : c.getAddedSubList()) {
                    if (date.isBefore(minRange))
                        minRange = date;
                    if (date.isAfter(maxRange))
                        maxRange = date;
                }
                for (LocalDate date : c.getRemoved()) {
                    if (date.equals(minRange) || date.equals(maxRange)) {
                        extractRange(axis, data);
                        break;
                    }
                }
            });

        axis.upperBoundProperty().addListener(axisUpperBoundlistener);
        axis.lowerBoundProperty().addListener(axisLowerBoundlistener);
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

                LocalDate date = percentToLocalDate(nVal.doubleValue());
                axis.setUpperBound(date);                          // calls axisUpperBoundListener

                double rangeLen = calcRangeLength(axis);
                rangeCTRL.setRangeLengthAndPosition(rangeLen);     // calls rangePositionListener
            };

        ChangeListener<Number> lowerRangeLimitlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                LocalDate date = percentToLocalDate(nVal.doubleValue());
                axis.setLowerBound(date);                          // calls axisLowerBoundListener

                double rangeLen = calcRangeLength(axis);
                rangeCTRL.setRangeLengthAndPosition(rangeLen);     // calls rangePositionListener
            };

        ChangeListener<Number> rangePositionlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                rangeCTRL.moveLimits(nVal.doubleValue());          // calls lowerRangeLimitListener
                                                                   // calls upperRangeLimitListener
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

    private void extractRange(LocalDateAxis axis, List<? extends LocalDate> list)
    {
        if (axis.getLowerBound() == null || axis.getUpperBound() == null) {
            LocalDate now = LocalDate.now();
            axis.setLowerBound(LocalDate.of(now.getYear(), 1, 1));
            axis.setUpperBound(LocalDate.of(now.getYear(), 12, 31));
        }

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
    private final ChangeListener<LocalDate> axisUpperBoundlistener = (obs, oVal, nVal) -> {
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
        };
            
    private final ChangeListener<LocalDate> axisLowerBoundlistener = (obs, oVal, nVal) -> {
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
        };
}
