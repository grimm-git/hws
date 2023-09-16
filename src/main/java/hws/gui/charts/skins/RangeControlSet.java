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
package hws.gui.charts.skins;

import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ScrollBar;

/**
 *
 * @author grimm
 */
public class RangeControlSet
{
    ArrayList<ChangeListener<? super Number>> upperLimitListeners = new ArrayList<>();
    ArrayList<ChangeListener<? super Number>> rangePositionListeners = new ArrayList<>();
    ArrayList<ChangeListener<? super Number>> lowerLimitListeners = new ArrayList<>();

    ScrollBar upperLimit;
    ScrollBar range;
    ScrollBar lowerLimit;

    public RangeControlSet(ScrollBar lower, ScrollBar range, ScrollBar upper)
    {
        lowerLimit = lower;
        upperLimit = upper;
        this.range = range;
    }

    public void setVisible(boolean visible)
    {
        lowerLimit.setVisible(visible);
        lowerLimit.setManaged(visible);
        range.setVisible(visible);
        range.setManaged(visible);
        upperLimit.setVisible(visible);
        upperLimit.setManaged(visible);
    }
    
    public boolean isVisible()
    {
        return lowerLimit.isVisible();
    }
    
    public void addLowerLimitListener(ChangeListener<? super Number> listener)
    {
        lowerLimitListeners.add(listener);
        getLowerLimitProperty().addListener(listener);
    }
    
    public void addRangeListener(ChangeListener<? super Number> listener)
    {
        rangePositionListeners.add(listener);
        getRangePositionProperty().addListener(listener);
    }

    public void addUpperLimitListener(ChangeListener<? super Number> listener)
    {
        upperLimitListeners.add(listener);
        getUpperLimitProperty().addListener(listener);
    }
    
    public void removeAllListeners()
    {
        for (ChangeListener<? super Number> listener : lowerLimitListeners)
            getLowerLimitProperty().removeListener(listener);
        lowerLimitListeners.clear();
        
        for (ChangeListener<? super Number> listener : rangePositionListeners)
            getRangePositionProperty().removeListener(listener);
        rangePositionListeners. clear();
        
        for (ChangeListener<? super Number> listener : upperLimitListeners)
            getUpperLimitProperty().removeListener(listener);
        upperLimitListeners.clear();
    }
    
    private DoubleProperty getLowerLimitProperty()
    {
        return lowerLimit.valueProperty();
    }

    private DoubleProperty getUpperLimitProperty()
    {
        return upperLimit.valueProperty();
    }

    private DoubleProperty getRangePositionProperty()
    {
        return range.valueProperty();
    }

    public double getUpperLimit()
    {
        return upperLimit.getValue();
    }

    public void setUpperLimit(double value)
    {
        if (value < 0) value = 0;
        if (value > 100) value = 100;
        upperLimit.setValue(value);
    }

    public double getLowerLimit()
    {
        return lowerLimit.getValue();
    }

    public void setLowerLimit(double value)
    {
        if (value < 0) value = 0;
        if (value > 100) value = 100;
        lowerLimit.setValue(value);
    }

    /**
     * Set the position and the length of the thumb of the range ScrollBar
     * 
     * @param rangeLength    length of the thumb (value property)
     */
    public void setRangeLengthAndPosition(double rangeLength)
    {
        if (rangeLength < 0)   rangeLength = 0;
        if (rangeLength > 100) rangeLength = 100;
        
        double value = 0;
        
        if (getLowerLimit() > 0 && rangeLength < 100)
            value = 100/(100 - rangeLength) * getLowerLimit();

        setRangeLength(rangeLength);
        setRangePosition(value);
    }

    /**
     * RangePosition can take values greater than 100 so that these two functions fulfill eachother: 
     * <pre>
     *       position = 100/(100 - rangeLength) * LowLimit
     *       lowLimit = (100.0 - rangeLength)/100.0 * position
     * </pre>
     * This happens only when the axis resolution is low (for example with the Category Axis).
     * In this cases the lower or the upper limit jump to discrete values which could be not
     * sufficient to trigger the ChangeListener. The lower the resolution the bigger the effect.
     * The range thumb reacts a little bit strange in these cases.
     * 
     * The upper limit will be derived from the lower limit
     * <pre>
     *       uppLimit = (100.0 - rangeLength)/100.0 * position + rangeLength
     * </pre>
     */
    private void setRangePosition(double value)
    {
        range.setValue(value);
    }

    private void setRangeLength(double value)
    {
        if (value < 0) value = 0;
        if (value > 100) value = 100;
        range.setVisibleAmount(value);
    }

    public void moveLimits(double position)
    {
        double rangeLength = range.getVisibleAmount();
        double lowLimit = (100.0 - rangeLength)/100.0 * position;

        setLowerLimit(lowLimit);             
        setUpperLimit(lowLimit + rangeLength);
    }
}
