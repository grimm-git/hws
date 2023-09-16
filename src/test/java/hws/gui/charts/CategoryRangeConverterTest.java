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
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/**
 *
 * @author grimm
 */
@ExtendWith(ApplicationExtension.class)
public class CategoryRangeConverterTest
{
    private ArrayList<String> dataBackup = new ArrayList<>();
    
    public CategoryRangeConverterTest()
    {
    }
    
    @Test
    public void testConversionToPercent_Lower()
    {
        ObservableList<String> dataList = FXCollections.observableArrayList();
        dataList.add("Category 5");
        dataList.add("Category 1");
        dataList.add("Category 2");
        dataList.add("Category 4");
        dataList.add("Category 6");
        dataList.add("Category 7");
        dataList.add("Category 8");
        dataList.add("Category 9");
        dataList.add("Category 3");
        
        dataBackup.clear();
        dataBackup.addAll(dataList);
        
        CategoryAxis axis = new CategoryAxis();
        axis.setCategories(dataList);
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();
        
        CategoryRangeConverter instance = new CategoryRangeConverter(axis, dataList);
        instance.link(ctrlSet);

        instance.setLowerBound(getIndex("Category 5"));
        assertEquals(0, instance.getLowerBound());
        assertEquals(0, ctrlSet.getLowerLimit());

        instance.setLowerBound(getIndex("Category 6"));
        assertEquals(4, instance.getLowerBound());
        assertEquals(50, ctrlSet.getLowerLimit());

        instance.setLowerBound(getIndex("Category 3"));
        assertEquals(8, instance.getLowerBound());
        assertEquals(100, ctrlSet.getLowerLimit());
    }

    @Test
    public void testConversionToPercent_Upper()
    {
        ObservableList<String> dataList = FXCollections.observableArrayList();
        dataList.add("Category 5");
        dataList.add("Category 1");
        dataList.add("Category 2");
        dataList.add("Category 4");
        dataList.add("Category 6");
        dataList.add("Category 7");
        dataList.add("Category 8");
        dataList.add("Category 9");
        dataList.add("Category 3");
        
        dataBackup.clear();
        dataBackup.addAll(dataList);
        
        CategoryAxis axis = new CategoryAxis();
        axis.setCategories(dataList);
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();
        
        CategoryRangeConverter instance = new CategoryRangeConverter(axis, dataList);
        instance.link(ctrlSet);

        instance.setUpperBound(getIndex("Category 5"));
        assertEquals(0, instance.getUpperBound());
        assertEquals(0, ctrlSet.getUpperLimit());

        instance.setUpperBound(getIndex("Category 6"));
        assertEquals(4, instance.getUpperBound());
        assertEquals(50, ctrlSet.getUpperLimit());

        instance.setUpperBound(getIndex("Category 3"));
        assertEquals(8, instance.getUpperBound());
        assertEquals(100, ctrlSet.getUpperLimit());
    }

    @Test
    public void testConversionToCategory_Lower()
    {
        ObservableList<String> dataList = FXCollections.observableArrayList();
        dataList.add("Category 5");
        dataList.add("Category 1");
        dataList.add("Category 2");
        dataList.add("Category 4");
        dataList.add("Category 6");
        dataList.add("Category 7");
        dataList.add("Category 8");
        dataList.add("Category 9");
        dataList.add("Category 3");
        
        dataBackup.clear();
        dataBackup.addAll(dataList);

        CategoryAxis axis = new CategoryAxis();
        axis.setCategories(dataList);
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();
        
        CategoryRangeConverter instance = new CategoryRangeConverter(axis, dataList);
        instance.link(ctrlSet);
          
        ctrlSet.setLowerLimit(0);
        assertEquals(getIndex("Category 5"), instance.getLowerBound());

        ctrlSet.setLowerLimit(50);
        assertEquals(getIndex("Category 6"), instance.getLowerBound());

        ctrlSet.setLowerLimit(100);
        assertEquals(getIndex("Category 3"), instance.getLowerBound());
    }
        
    @Test
    public void testConversionToCategory_Upper()
    {
        ObservableList<String> dataList = FXCollections.observableArrayList();
        dataList.add("Category 5");
        dataList.add("Category 1");
        dataList.add("Category 2");
        dataList.add("Category 4");
        dataList.add("Category 6");
        dataList.add("Category 7");
        dataList.add("Category 8");
        dataList.add("Category 9");
        dataList.add("Category 3");
        
        dataBackup.clear();
        dataBackup.addAll(dataList);

        CategoryAxis axis = new CategoryAxis();
        axis.setCategories(dataList);
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();
        
        CategoryRangeConverter instance = new CategoryRangeConverter(axis, dataList);
        instance.link(ctrlSet);
          
        ctrlSet.setUpperLimit(0);
        assertEquals(getIndex("Category 5"), instance.getUpperBound());

        ctrlSet.setUpperLimit(50);
        assertEquals(getIndex("Category 6"), instance.getUpperBound());

        ctrlSet.setUpperLimit(100);
        assertEquals(getIndex("Category 3"), instance.getUpperBound());
    }

    @Test
    public void testConversionOutOfRange()
    {
        ObservableList<String> dataList = FXCollections.observableArrayList();
        dataList.add("Category 1");
        dataList.add("Category 2");
        dataList.add("Category 3");
        dataList.add("Category 4");
        dataList.add("Category 5");
        dataList.add("Category 6");
        dataList.add("Category 7");
        dataList.add("Category 8");
        dataList.add("Category 9");

        dataBackup.clear();
        dataBackup.addAll(dataList);

        CategoryAxis axis = new CategoryAxis();
        axis.setCategories(dataList);
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();

        CategoryRangeConverter instance = new CategoryRangeConverter(axis, dataList);
        instance.link(ctrlSet);

        instance.setLowerBound(10);
        assertEquals(getIndex("Category 9"), instance.getLowerBound());

        instance.setLowerBound(-3);
        assertEquals(getIndex("Category 1"), instance.getLowerBound());
    }

    @Test
    public void testConversionEmptyList()
    {
        ObservableList<String> dataList = FXCollections.observableArrayList();
        dataBackup.clear();

        // No Range set
        CategoryAxis axis = new CategoryAxis();
        axis.setCategories(dataList);
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();

        CategoryRangeConverter instance = new CategoryRangeConverter(axis, dataList);
        instance.link(ctrlSet);
        
        instance.setLowerBound(getIndex("Category 5"));
        assertEquals(0, instance.getLowerBound());
        assertEquals(0, ctrlSet.getLowerLimit());
    }

    @Test
    public void testConversionSingleElement()
    {
        ObservableList<String> dataList = FXCollections.observableArrayList();
        dataList.add("Category 1");

        dataBackup.clear();
        dataBackup.addAll(dataList);

        CategoryAxis axis = new CategoryAxis();
        axis.setCategories(dataList);
        RangeControlSet ctrlSet = TestHelper.createRangeControlSet();

        // Range: "Category 1"
        CategoryRangeConverter instance = new CategoryRangeConverter(axis, dataList);
        instance.link(ctrlSet);

        instance.setLowerBound(getIndex("NotInList"));
        assertEquals(0, instance.getLowerBound());
        assertEquals(0, ctrlSet.getLowerLimit());

        instance.setLowerBound(getIndex("Category 1"));
        assertEquals(0, instance.getLowerBound());
        assertEquals(0, ctrlSet.getLowerLimit());
    }
    
    private int getIndex(String text)
    {
        return dataBackup.indexOf(text);
    }
}
