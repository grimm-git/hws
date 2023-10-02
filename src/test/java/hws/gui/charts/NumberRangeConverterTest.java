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
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ScrollBar;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/**
 *
 * @author grimm
 */
@ExtendWith(ApplicationExtension.class)
public class NumberRangeConverterTest
{
    
    public NumberRangeConverterTest()
    {
    }
        
    @Test
    public void testConverionLowerBoundToPercent()
    {
        ObservableList<Number> dataList = FXCollections.observableArrayList();
        Number[] data = { 13, 3, 19, 20, 24, 98, 87, 72, 36, 46 } ;
        dataList.addAll(Arrays.<Number>asList(data));
        
        NumberAxis axis = new NumberAxis(3, 98, 10);
        RangeControlSet ctrlSet = createRangeControlSet();

        // Data range: 3 - 98 but axis boundaries 3 - 98
        NumberRangeConverter instance = new NumberRangeConverter(axis);
        instance.link(ctrlSet);
        instance.updateData(dataList);

        axis.setLowerBound(98);
        assertEquals(100, ctrlSet.getLowerLimit());

        axis.setLowerBound(3);
        assertEquals(0, ctrlSet.getLowerLimit());

        axis.setLowerBound(50.5);
        assertEquals(50, ctrlSet.getLowerLimit());
    }
    
    @Test
    public void testConverionUpperBoundToPercent()
    {
        ObservableList<Number> dataList = FXCollections.observableArrayList();
        Number[] data = { 13, 3, 19, 20, 24, 98, 87, 72, 36, 46 } ;
        dataList.addAll(Arrays.<Number>asList(data));
        
        NumberAxis axis = new NumberAxis(3, 98, 10);
        RangeControlSet ctrlSet = createRangeControlSet();

        // Data range: 3 - 98 but axis boundaries 3 - 98
        NumberRangeConverter instance = new NumberRangeConverter(axis);
        instance.link(ctrlSet);
        instance.updateData(dataList);

        axis.setUpperBound(98);
        assertEquals(100, ctrlSet.getUpperLimit());

        axis.setUpperBound(3);
        assertEquals(0, ctrlSet.getUpperLimit());

        axis.setUpperBound(50.5);
        assertEquals(50, ctrlSet.getUpperLimit());
    }

    @Test
    public void testConverionPercentToLowerBound()
    {
        ObservableList<Number> dataList = FXCollections.observableArrayList();
        Number[] data = { 13, 3, 19, 20, 24, 98, 87, 72, 36, 46 } ;
        dataList.addAll(Arrays.<Number>asList(data));
        
        NumberAxis axis = new NumberAxis(3, 98, 10);
        RangeControlSet ctrlSet = createRangeControlSet();

        // data range set: 3 - 98 but axis boundaries 3 - 98
        NumberRangeConverter instance = new NumberRangeConverter(axis);
        instance.link(ctrlSet);
        instance.updateData(dataList);

        ctrlSet.setLowerLimit(50);
        assertEquals(50.5, axis.getLowerBound());
    }

    @Test
    public void testConverionPercentToUpperBound()
    {
        ObservableList<Number> dataList = FXCollections.observableArrayList();
        Number[] data = { 13, 3, 19, 20, 24, 98, 87, 72, 36, 46 } ;
        dataList.addAll(Arrays.<Number>asList(data));
        
        NumberAxis axis = new NumberAxis(3, 98, 10);
        RangeControlSet ctrlSet = createRangeControlSet();

        // data range set: 3 - 98 but axis boundaries 3 - 98
        NumberRangeConverter instance = new NumberRangeConverter(axis);
        instance.link(ctrlSet);
        instance.updateData(dataList);

        ctrlSet.setUpperLimit(50);
        assertEquals(50.5, axis.getUpperBound());
    }

    @Test
    public void testLowerBound_ConversionOutOfRange()
    {
        ObservableList<Number> dataList = FXCollections.observableArrayList();
        Number[] data = { 13, 3, 19, 20, 24, 98, 87, 72, 36, 46 } ;
        dataList.addAll(Arrays.<Number>asList(data));
        
        NumberAxis axis = new NumberAxis(3, 100, 10);
        RangeControlSet ctrlSet = createRangeControlSet();

        // Data range: 3 - 98 but axis boundaries 3 - 100
        NumberRangeConverter instance = new NumberRangeConverter(axis);
        instance.link(ctrlSet);
        instance.updateData(dataList);

        axis.setLowerBound(2);
        assertEquals(3.0, axis.getLowerBound());
        assertEquals(0, ctrlSet.getLowerLimit());

        axis.setLowerBound(300);
        assertEquals(100.0, axis.getLowerBound());
        assertEquals(100, ctrlSet.getLowerLimit());
    }

    @Test
    public void testLowerBound_ConversionEmptyList()
    {
        ObservableList<Number> dataList = FXCollections.observableArrayList();

        NumberAxis axis = new NumberAxis(0, 100, 10);
        RangeControlSet ctrlSet = createRangeControlSet();

        // no data range but axis boundaries 0 - 100
        NumberRangeConverter instance = new NumberRangeConverter(axis);
        instance.link(ctrlSet);
        instance.updateData(dataList);

        axis.setLowerBound(42);
        assertEquals(42, (int) axis.getLowerBound());
        assertEquals(42.0, ctrlSet.getLowerLimit());
    }
    
    @Test
    public void testLowerBound_ConversionSingleElement()
    {
        ObservableList<Number> dataList = FXCollections.observableArrayList();
        dataList.add(13);

        NumberAxis axis = new NumberAxis(0, 100, 10);
        RangeControlSet ctrlSet = createRangeControlSet();

        // data range: 13 - 13 but axis boundaries 0 - 100
        NumberRangeConverter instance = new NumberRangeConverter(axis);
        instance.link(ctrlSet);
        instance.updateData(dataList);

        axis.setLowerBound(13);
        assertEquals(13, (int) axis.getLowerBound());
        assertEquals(13.0, ctrlSet.getLowerLimit());

        axis.setLowerBound(42);
        assertEquals(42, (int) axis.getLowerBound());
        assertEquals(42,0, ctrlSet.getLowerLimit());
    }

    private RangeControlSet createRangeControlSet()
    {
        ScrollBar bar1 = new ScrollBar();
        ScrollBar bar2 = new ScrollBar();
        ScrollBar bar3 = new ScrollBar();
        return new RangeControlSet(bar1, bar2, bar3);
    }

}
