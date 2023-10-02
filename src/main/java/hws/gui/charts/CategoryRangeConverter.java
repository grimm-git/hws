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
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.chart.CategoryAxis;

/**
 *
 * @author grimm
 */
public class CategoryRangeConverter
extends RangeConverter<String>
{
    private CategoryAxis axis;
    private final ArrayList<String> categoryList = new ArrayList<>();
    private final IntegerProperty upperBoundProperty = new SimpleIntegerProperty();
    private final IntegerProperty lowerBoundProperty = new SimpleIntegerProperty();
    
    public CategoryRangeConverter(CategoryAxis axis)
    {
        super(axis);
        this.axis = axis;
        
        if (axis.isAutoRanging())
            axis.setAutoRanging(false);

        upperBoundProperty.addListener(axisUpperBoundlistener);
        lowerBoundProperty.addListener(axisLowerBoundlistener);
    }
  
    @Override
    public void updateData(List<String> list)
    {
        categoryList.clear();
        categoryList.addAll(list);
        axis.getCategories().setAll(categoryList);

        lowerBoundProperty.set(0);
        upperBoundProperty.set(list.isEmpty() ? 0 : categoryList.size() - 1);
    }

    @Override
    public void link(RangeControlSet rangeCTRL)
    {
        registerControlSet(rangeCTRL);

        // set initial values
        if (categoryList.isEmpty()) {
            rangeCTRL.setUpperLimit(100);
            rangeCTRL.setLowerLimit(0);
            rangeCTRL.setRangeLengthAndPosition(100);
        } else {
            rangeCTRL.setUpperLimit(categoryToPercent(getUpperBound()));
            rangeCTRL.setLowerLimit(categoryToPercent(getLowerBound()));
            rangeCTRL.setRangeLengthAndPosition(calcRangeLength());
        }

        ChangeListener<Number> upperRangeLimitlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                System.err.printf("%s%d UpperRangeLimit (%f / %f)\n", " ".repeat(level), level, oVal.doubleValue(), nVal.doubleValue());
                level += 1;

                int category = percentToCategory(nVal.doubleValue());
                if (category < getLowerBound())
                    rangeCTRL.setUpperLimit(oVal.doubleValue());
                
                else if (category != getUpperBound()) {
                    setUpperBound(category);             // calls axisUpperBoundListener
                    double rangeLen = calcRangeLength();
                    rangeCTRL.setRangeLengthAndPosition(calcRangePosition(), rangeLen);
                }

                level -= 1;
            };

        ChangeListener<Number> lowerRangeLimitlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;

                System.err.printf("%s%d LowerRangeLimit (%f / %f)\n", " ".repeat(level), level, oVal.doubleValue(), nVal.doubleValue());
                level += 1;

                int category = percentToCategory(nVal.doubleValue());
                if (category > getUpperBound())
                    rangeCTRL.setLowerLimit(oVal.doubleValue());
                
                else if (category != getLowerBound()) {
                    setLowerBound(category);              // calls axisLowerBoundListener
                    double rangeLen = calcRangeLength();
                    rangeCTRL.setRangeLengthAndPosition(calcRangePosition(), rangeLen);
                }

                level -= 1;
            };

        ChangeListener<Number> rangePositionlistener = (obs, oVal, nVal) -> {
                if (areEqual(oVal, nVal)) return;
               
                System.err.printf("%s%d RangePosition (%f / %f)\n", " ".repeat(level), level, oVal.doubleValue(), nVal.doubleValue());
                level += 1;

                int diff = getUpperBound() - getLowerBound();
                double lowLimit = (100.0 - calcRangeLength())/100 * nVal.doubleValue();
                int lowBound = (int) Math.round(lowLimit / 100.0 * (categoryList.size()));
                int upBound = lowBound + diff;
                
                setLowerBound(lowBound);
                setUpperBound(upBound);
                
                level -= 1;
            };

        rangeCTRL.addUpperLimitListener(upperRangeLimitlistener);
        rangeCTRL.addLowerLimitListener(lowerRangeLimitlistener);
        rangeCTRL.addRangeListener(rangePositionlistener);
    }

    double calcRangeLength()
    {
        return 100.0 * axis.getCategories().size() / categoryList.size();
    }
    
    double calcRangePosition()
    {
        int max = categoryList.size()-1;
        if (getLowerBound() == 0) return 0;
        if (getUpperBound() == max) return 100;

        double offs = getLowerBound() + (getUpperBound() - getLowerBound())/2.0;
        return 100.0 * offs / max;
    }
    
    double categoryToPercent(int idx)
    {
        if (idx == 0) return 0;
        if (idx == categoryList.size()-1) return 100;

        double step = 100.0 / (categoryList.size()-1);
        return idx * step;
    }

    int percentToCategory(double percent)
    {
        if (categoryList.size() <= 1) return 0;
        
        double step = 100.0 / (categoryList.size()-1);
        return (int) Math.round(percent / step);
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

    int level = 0;

    private final ChangeListener<? super Number> axisUpperBoundlistener = (obs, oVal, nVal) -> {
            System.err.printf("%s%d UpperAxisBoundariy (%s / %s)\n", " ".repeat(level), level, oVal, nVal);
            level += 1;

            double percent = categoryToPercent(nVal.intValue());
            for (RangeControlSet item : listControlSets)
                item.setUpperLimit(percent);              // calls upperRangeLimitListener twice

            axis.getCategories().setAll(categoryList.subList(getLowerBound(), getUpperBound() + 1));
            level -= 1;
        };
            
    private final ChangeListener<? super Number> axisLowerBoundlistener = (obs, oVal, nVal) -> {
            System.err.printf("%s%d LowerAxisBoundariy (%s / %s)\n", " ".repeat(level), level, oVal, nVal);
            level += 1;

            double percent = categoryToPercent(nVal.intValue());
            for (RangeControlSet item : listControlSets)
                item.setLowerLimit(percent);              // calls lowerRangeLimitListener twice

            axis.getCategories().setAll(categoryList.subList(getLowerBound(), getUpperBound() + 1));
            level -= 1;
        };
}
