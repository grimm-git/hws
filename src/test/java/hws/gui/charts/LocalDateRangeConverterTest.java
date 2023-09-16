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
import hws.testhelper.TestHelper;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/**
 *
 * @author grimm
 */
@ExtendWith(ApplicationExtension.class)
public class LocalDateRangeConverterTest
{
    
    public LocalDateRangeConverterTest()
    {
    }
    
    @Test
    public void testConverionLowerBoundToPercent()
    {
        ObservableList<LocalDate> dataList = FXCollections.observableArrayList();
        dataList.add(LocalDate.of(2022,3,12));
        dataList.add(LocalDate.of(2022,11,4));
        dataList.add(LocalDate.of(2022,9,3));
        dataList.add(LocalDate.of(2022,6,3));
        dataList.add(LocalDate.of(2022,1,1));
        dataList.add(LocalDate.of(2022,12,31));
        dataList.add(LocalDate.of(2022,8,21));
        dataList.add(LocalDate.of(2022,3,13));
        
        LocalDateAxis axis = new LocalDateAxis(LocalDate.of(2022, 1, 1), LocalDate.of(2022,12,31));
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();
        
        // Range: 1.1.2022 - 31.12.2022
        LocalDateRangeConverter instance = new LocalDateRangeConverter(axis, dataList);
        instance.link(ctrlSet);

        axis.setLowerBound(LocalDate.of(2022, 12, 31));
        assertEquals(100, ctrlSet.getLowerLimit());

        axis.setLowerBound(LocalDate.of(2022, 1, 1));
        assertEquals(0, ctrlSet.getLowerLimit());

        axis.setLowerBound(LocalDate.of(2022, 7, 2));
        assertEquals(50, ctrlSet.getLowerLimit());
    }
    
    @Test
    public void testConverionUpperBoundToPercent()
    {
        ObservableList<LocalDate> dataList = FXCollections.observableArrayList();
        dataList.add(LocalDate.of(2022,3,12));
        dataList.add(LocalDate.of(2022,11,4));
        dataList.add(LocalDate.of(2022,9,3));
        dataList.add(LocalDate.of(2022,6,3));
        dataList.add(LocalDate.of(2022,1,1));
        dataList.add(LocalDate.of(2022,12,31));
        dataList.add(LocalDate.of(2022,8,21));
        dataList.add(LocalDate.of(2022,3,13));
        
        LocalDateAxis axis = new LocalDateAxis(LocalDate.of(2022, 1, 1), LocalDate.of(2022,12,31));
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();
        
        // Range: 1.1.2022 - 31.12.2022
        LocalDateRangeConverter instance = new LocalDateRangeConverter(axis, dataList);
        instance.link(ctrlSet);

        axis.setUpperBound(LocalDate.of(2022, 12, 31));
        assertEquals(100, ctrlSet.getUpperLimit());

        axis.setUpperBound(LocalDate.of(2022, 1, 1));
        assertEquals(0, ctrlSet.getUpperLimit());

        axis.setUpperBound(LocalDate.of(2022, 7, 2));
        assertEquals(50, ctrlSet.getUpperLimit());
    }

    @Test
    public void testConverionPercentToLowerBound()
    {
        ObservableList<LocalDate> dataList = FXCollections.observableArrayList();
        dataList.add(LocalDate.of(2022,3,12));
        dataList.add(LocalDate.of(2022,11,4));
        dataList.add(LocalDate.of(2022,9,3));
        dataList.add(LocalDate.of(2022,6,3));
        dataList.add(LocalDate.of(2022,1,1));
        dataList.add(LocalDate.of(2022,12,31));
        dataList.add(LocalDate.of(2022,8,21));
        dataList.add(LocalDate.of(2022,3,13));
        
        LocalDateAxis axis = new LocalDateAxis(LocalDate.of(2022, 1, 1), LocalDate.of(2022,12,31));
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();

        // Data range: 1.1.2022 - 31.12.2022 but axis boundaries 1.1.2022 - 31.12.2022
        LocalDateRangeConverter instance = new LocalDateRangeConverter(axis, dataList);
        instance.link(ctrlSet);

        ctrlSet.setLowerLimit(50);
        assertEquals(LocalDate.of(2022,7,2), axis.getLowerBound());
    }

    @Test
    public void testConverionPercentToUpperBound()
    {
        ObservableList<LocalDate> dataList = FXCollections.observableArrayList();
        dataList.add(LocalDate.of(2022,3,12));
        dataList.add(LocalDate.of(2022,11,4));
        dataList.add(LocalDate.of(2022,9,3));
        dataList.add(LocalDate.of(2022,6,3));
        dataList.add(LocalDate.of(2022,1,1));
        dataList.add(LocalDate.of(2022,12,31));
        dataList.add(LocalDate.of(2022,8,21));
        dataList.add(LocalDate.of(2022,3,13));
        
        LocalDateAxis axis = new LocalDateAxis(LocalDate.of(2022, 1, 1), LocalDate.of(2022,12,31));
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();

        // Data range: 1.1.2022 - 31.12.2022 but axis boundaries 1.1.2022 - 31.12.2022
        LocalDateRangeConverter instance = new LocalDateRangeConverter(axis, dataList);
        instance.link(ctrlSet);

        ctrlSet.setUpperLimit(50);
        assertEquals(LocalDate.of(2022,7,2), axis.getUpperBound());
    }

    @Test
    public void testLowerBound_ConversionOutOfRange()
    {
        ObservableList<LocalDate> dataList = FXCollections.observableArrayList();
        dataList.add(LocalDate.of(2022,1,1));
        dataList.add(LocalDate.of(2022,12,31));
        
        LocalDateAxis axis = new LocalDateAxis(LocalDate.of(2022, 1, 1), LocalDate.of(2022,12,31));
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();

        // Data range: 1.1.2022 - 31.12.2022 but axis boundaries 1.1.2022 - 31.12.2022
        LocalDateRangeConverter instance = new LocalDateRangeConverter(axis, dataList);
        instance.link(ctrlSet);

        LocalDate testDate = LocalDate.of(2021, 11, 1);
        LocalDate expected = LocalDate.of(2022, 1, 1);

        axis.setLowerBound(testDate);
        assertEquals(expected, axis.getLowerBound());

        testDate = LocalDate.of(2024, 2, 1);
        expected = LocalDate.of(2022, 12, 31);

        axis.setLowerBound(testDate);
        assertEquals(expected, axis.getLowerBound());
    }

    @Test
    public void testLowerBound_ConversionEmptyList()
    {
        ObservableList<LocalDate> dataList = FXCollections.observableArrayList();

        LocalDateAxis axis = new LocalDateAxis(LocalDate.of(2022, 1, 1), LocalDate.of(2022,12,31));
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();

        // No data range but axis boundaries 1.1.2022 - 31.12.2022
        LocalDateRangeConverter instance = new LocalDateRangeConverter(axis, dataList);
        instance.link(ctrlSet);
        
        LocalDate testDate = LocalDate.of(2022, 3, 12);

        axis.setLowerBound(testDate);
        assertEquals(testDate, axis.getLowerBound());
        assertEquals(19, (int) ctrlSet.getLowerLimit());

        testDate = LocalDate.of(2023, 3, 12);
        LocalDate expected = LocalDate.of(2022,12, 31);

        axis.setLowerBound(testDate);
        assertEquals(expected, axis.getLowerBound());
        assertEquals(100, (int) ctrlSet.getLowerLimit());
    }

    @Test
    public void testLowerBound_ConversionEmptyList2()
    {
        ObservableList<LocalDate> dataList = FXCollections.observableArrayList();

        LocalDateAxis axis = new LocalDateAxis();
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();

        // No data range and none from the axis boundaries -> default range: 1.1.<CY> - 31.12.<CY>
        LocalDateRangeConverter instance = new LocalDateRangeConverter(axis, dataList);
        instance.link(ctrlSet);
        
        LocalDate now = LocalDate.now();
        LocalDate testDate = LocalDate.of(now.getYear(),3,12);

        axis.setLowerBound(testDate);
        assertEquals(testDate, axis.getLowerBound());
        assertEquals(19, (int) ctrlSet.getLowerLimit());

        testDate = LocalDate.of(now.getYear()+1,3,12);
        LocalDate expected = LocalDate.of(now.getYear(),12, 31);

        axis.setLowerBound(testDate);
        assertEquals(expected, axis.getLowerBound());
        assertEquals(100, (int) ctrlSet.getLowerLimit());
    }

    @Test
    public void testLowerBound_ConversionSingleElement()
    {
        ObservableList<LocalDate> dataList = FXCollections.observableArrayList();
        dataList.add(LocalDate.of(2022,3,12));

        LocalDateAxis axis = new LocalDateAxis(LocalDate.of(2022, 1, 1), LocalDate.of(2022,12,31));
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();

        // Data range: 12.3.2023 - 12.3.2023 but axis boundaries 1.1.2022 - 31.12.2022
        LocalDateRangeConverter instance = new LocalDateRangeConverter(axis, dataList);
        instance.link(ctrlSet);

        LocalDate expected = LocalDate.of(2022,3,12);
        LocalDate testDate = LocalDate.of(2022,3,12);
        
        axis.setLowerBound(testDate);
        assertEquals(expected, axis.getLowerBound());
        assertEquals(19, (int) ctrlSet.getLowerLimit());

        testDate = LocalDate.of(2022,7,2);
        axis.setLowerBound(testDate);
        assertEquals(testDate, axis.getLowerBound());
        assertEquals(50, ctrlSet.getLowerLimit());
    }
}
