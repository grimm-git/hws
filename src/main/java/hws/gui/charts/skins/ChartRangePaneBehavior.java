/*
 * Copyright (c) 2010, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package hws.gui.charts.skins;

import hws.gui.charts.CategoryRangeConverter;
import hws.gui.charts.ChartRangePane;
import hws.gui.charts.DataExtra;
import hws.gui.charts.LocalDateAxis;
import hws.gui.charts.LocalDateRangeConverter;
import hws.gui.charts.NumberRangeConverter;
import hws.gui.charts.RangeConverter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
        
/**
 * Behavior for ChartRangePane.TODO: the function variables are a poor way to couple to the
 * rest of the system.This technique avoids a direct dependency on the skin class.
 *
 * However, this should really be coupled through the control itself instead of directly to the skin.
 */
public class ChartRangePaneBehavior
{
    private final ChartRangePane pane;

    private RangeConverter rangeConverter_X;
    private ObservableList<?> dataValues_X;
    private final ArrayList<RangeControlSet> rangeControlSetsHorizontal = new ArrayList<>();
    
    private RangeConverter rangeConverter_Y;
    private ObservableList<?> dataValues_Y;
    private final ArrayList<RangeControlSet> rangeControlSetsVertical = new ArrayList<>();;
    
    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public ChartRangePaneBehavior(ChartRangePane pane)
    {
        this.pane = pane;
                
        extractChartDataValues(getChart().getData());
        pane.contentProperty().addListener(contentListener);
    }

    public void dispose()
    {
        getChart().getData().removeListener(dataListener);
        for (RangeControlSet obj : rangeControlSetsHorizontal)
            obj.removeAllListeners();
        for (RangeControlSet obj : rangeControlSetsVertical)
            obj.removeAllListeners();
    }

    // extract the data from a chart if it changes (adding new series, etc)
    private final InvalidationListener dataListener = obs -> {
                 extractChartDataValues(getChart().getData());
            };

    private final ChangeListener<XYChart<?,?>> contentListener = (obs, oContent, nContent) -> {
                if (oContent != null) {
                    oContent.getData().removeListener(dataListener);
                    for (RangeControlSet obj : rangeControlSetsHorizontal)
                        obj.removeAllListeners();
                    for (RangeControlSet obj : rangeControlSetsVertical)
                        obj.removeAllListeners();
                }
                
                if (nContent != null) {
                    nContent.getData().addListener(dataListener);
                    extractChartDataValues(nContent.getData());
                    createAxisConverter();
                }
            };

    public void setControlSetsHorizontal(RangeControlSet... controlSets)
    {
        rangeControlSetsHorizontal.clear();
        rangeControlSetsHorizontal.addAll(Arrays.asList(controlSets));
    }

    public void setControlSetsVertical(RangeControlSet... controlSets)
    {
        rangeControlSetsVertical.clear();
        rangeControlSetsVertical.addAll(Arrays.asList(controlSets));
    }

    @SuppressWarnings("unchecked")
    public void createAxisConverter()
    {
        Axis<?> xAxis = getChart().getXAxis();
        if (xAxis instanceof LocalDateAxis axis) {
            rangeConverter_X = new LocalDateRangeConverter(axis, (ObservableList<LocalDate>) dataValues_X);
        } else if (xAxis instanceof NumberAxis axis) {
            rangeConverter_X = new NumberRangeConverter(axis, (ObservableList) dataValues_X);
        } else if (xAxis instanceof CategoryAxis axis) {
            rangeConverter_X = new CategoryRangeConverter(axis, (ObservableList<String>) dataValues_X);
        } else 
            throw new UnsupportedOperationException("Datatype for X-Axis not supported");
        
        for(RangeControlSet ctrlSet : rangeControlSetsHorizontal)
            rangeConverter_X.link(ctrlSet);
        
        
        Axis<?> yAxis = getChart().getYAxis();
        if (yAxis instanceof LocalDateAxis axis) {
            rangeConverter_Y = new LocalDateRangeConverter(axis, (ObservableList<LocalDate>) dataValues_Y);
        } else if (yAxis instanceof NumberAxis axis) {
            rangeConverter_Y = new NumberRangeConverter(axis, (ObservableList<Number>) dataValues_Y);
        } else if (yAxis instanceof CategoryAxis axis) {
           rangeConverter_Y = new CategoryRangeConverter(axis, (ObservableList<String>) dataValues_Y);
        } else 
            throw new UnsupportedOperationException("Datatype for Y-Axis not supported");

        for(RangeControlSet ctrlSet : rangeControlSetsVertical)
           rangeConverter_Y.link(ctrlSet);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <X,Y> void extractChartDataValues( ObservableList<? extends XYChart.Series<X,Y>> list)
    {
        ObservableList<X> xDataValues = FXCollections.observableArrayList();
        ObservableList<Y> yDataValues = FXCollections.observableArrayList();

        for (int sIdx=0; sIdx < getSaveListSize(list); sIdx++) {
            XYChart.Series<X,Y> series = list.get(sIdx);
            for (XYChart.Data<X,Y> item : series.getData()) {
                Object obj = item.getExtraValue();
                if (obj != null && obj instanceof DataExtra extra) {
                    extra.addToList_X(xDataValues);
                    extra.addToList_Y(yDataValues);
                } else {
                    if (!xDataValues.contains(item.getXValue()))
                        xDataValues.add(item.getXValue());
                    if (!yDataValues.contains(item.getYValue()))
                        yDataValues.add(item.getYValue());
                }
            }        
        }
        
        dataValues_X = xDataValues;
        dataValues_Y = yDataValues;
    }

    private int getSaveListSize(List<?> data)
    {
        return (data!=null) ? data.size() : 0;
    }

    private XYChart<?,?> getChart()
    {
        return pane.getContent();
    }
    
    public void mousePressed(MouseEvent e) {
        pane.requestFocus();
    }
}
