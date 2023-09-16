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
import java.util.ArrayList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;

/**
 *
 * @author grimm
 */
public class CategoryRangeConverter
extends RangeConverter
{
    private CategoryAxis axis;
    private final ArrayList<String> categoryList = new ArrayList<>();
    private final IntegerProperty upperBoundProperty = new SimpleIntegerProperty();
    private final IntegerProperty lowerBoundProperty = new SimpleIntegerProperty();
    private boolean rangeMoveInProgress = false;
    
    public CategoryRangeConverter(CategoryAxis axis, ObservableList<? extends String> list)
    {
        super(axis, list);
        this.axis = axis;
        
        categoryList.clear();
        categoryList.addAll(list);
        lowerBoundProperty.set(0);
        upperBoundProperty.set(categoryList.size() - 1);
       
        if (axis.isAutoRanging()) {
            axis.setAutoRanging(false);
            axis.getCategories().setAll(categoryList);
        }
        
        upperBoundProperty.addListener(axisUpperBoundlistener);
        lowerBoundProperty.addListener(axisLowerBoundlistener);
    }

    @Override
    public void link(RangeControlSet rangeCTRL)
    {
        registerControlSet(rangeCTRL);

        // set initial values
        rangeCTRL.setUpperLimit(categoryToPercent(getUpperBound()));
        rangeCTRL.setLowerLimit(categoryToPercent(getLowerBound()));
            
        // set initial range length and position
        double rangeLength = calcRangeLength(axis);
        rangeCTRL.setRangeLengthAndPosition(rangeLength);

        ChangeListener<Number> upperRangeLimitlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                int category = percentToCategory(nVal.doubleValue());
                setUpperBound(category);                           // calls axisUpperBoundListener

                if (!rangeMoveInProgress) {
                    double rangeLen = calcRangeLength(axis);
                    rangeCTRL.setRangeLengthAndPosition(rangeLen); // calls rangePositionListener
                }
            };

        ChangeListener<Number> lowerRangeLimitlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                int category = percentToCategory(nVal.doubleValue());
                setLowerBound(category);                           // calls axisLowerBoundListener

                if (!rangeMoveInProgress) {
                    double rangeLen = calcRangeLength(axis);
                    rangeCTRL.setRangeLengthAndPosition(rangeLen); // calls rangePositionListener
                }
            };

        ChangeListener<Number> rangePositionlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;
                
                rangeMoveInProgress = true;
                rangeCTRL.moveLimits(nVal.doubleValue());          // calls lowerRangeLimitListener
                                                                   // calls upperRangeLimitListener
                rangeMoveInProgress = false;
            };

        rangeCTRL.addUpperLimitListener(upperRangeLimitlistener);
        rangeCTRL.addLowerLimitListener(lowerRangeLimitlistener);
        rangeCTRL.addRangeListener(rangePositionlistener);
    }

    private double calcRangeLength(CategoryAxis axis)
    {
        return 100.0 * axis.getCategories().size() / categoryList.size();
    }

    private double categoryToPercent(int idx)
    {
        int range = categoryList.size() - 1;
        
        return range == 0 ? 0 : 100.0 * idx / range;
    }

    private int percentToCategory(double percent)
    {
        int range = categoryList.size() - 1;
        return (int) Math.round(percent/100 * range);
    }

    int getUpperBound()
    {
        return upperBoundProperty.get();
    }

    void setUpperBound(int idx)
    {
        int max = categoryList.size() - 1;
        if (idx > max) idx = max;
        if (idx < 0 || max < 0) idx = 0;

        upperBoundProperty.set(idx);
    }

    int getLowerBound()
    {
        return lowerBoundProperty.get();
    }
    
    void setLowerBound(int idx)
    {
        int max = categoryList.size() - 1;
        if (idx > max) idx = max;
        if (idx < 0 || max < 0) idx = 0;

        lowerBoundProperty.set(idx);
    }


    /****************************************************************************************/
    /*                              Listener definitions                                    */    
    /****************************************************************************************/
    private final ChangeListener<? super Number> axisUpperBoundlistener = (obs, oVal, nVal) -> {
            double percent = categoryToPercent(nVal.intValue());
            if (getUpperBound() < getLowerBound()) {
                percent = categoryToPercent(getLowerBound());
                setUpperBound(getLowerBound());      // calls axisUpperBoundListener
            }

            for (RangeControlSet item : listControlSets)
                item.setUpperLimit(percent);              // calls upperRangeLimitListener twice

            axis.getCategories().setAll(categoryList.subList(getLowerBound(), getUpperBound() + 1));
        };
            
    private final ChangeListener<? super Number> axisLowerBoundlistener = (obs, oVal, nVal) -> {
            double percent = categoryToPercent(nVal.intValue());
            if (getLowerBound() > getUpperBound()) {
                percent = categoryToPercent(getUpperBound());
                setLowerBound(getUpperBound());           // calls axisLowerBoundListener
            }

            for (RangeControlSet item : listControlSets)
                item.setLowerLimit(percent);              // calls lowerRangeLimitListener twice

            axis.getCategories().setAll(categoryList.subList(getLowerBound(), getUpperBound() + 1));
        };
}
