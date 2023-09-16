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

import hws.gui.charts.ChartRangePane.HControlsPolicy;
import hws.gui.charts.ChartRangePane.VControlsPolicy;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 *
 * @author grimm
 */
@ExtendWith(ApplicationExtension.class)
public class ChartRangePaneTest
{
    ChartRangePane rangePane;
    LineChart<Number,Number> chart;

    public ChartRangePaneTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }
    
    @Start
    private void start(Stage stage)
    {
        NumberAxis xAxis = new NumberAxis(0, 150, 10);
        NumberAxis yAxis = new NumberAxis(0, 150, 10);
        chart = new LineChart<>(xAxis,yAxis);

        rangePane = new ChartRangePane(chart);

        stage.setScene(new Scene(rangePane, 500, 400));
        stage.show();
    }

    @Test
    public void testHorizontalRangeControl(FxRobot robot)
    {
        ChartRangePane instance = rangePane;

        assertEquals(HControlsPolicy.BOTTOM, instance.getHControlsPolicy());
        
        instance.setHControlsPolicy(HControlsPolicy.OFF);
        assertEquals(HControlsPolicy.OFF, instance.getHControlsPolicy());
        
        robot.interact( () -> {
            instance.setStyle("-fx-hcontrols-policy: top;");
            instance.applyCss();
            instance.layout();
        });
        assertEquals(HControlsPolicy.TOP, instance.getHControlsPolicy());

        robot.interact( () -> {
            instance.setStyle("-fx-hcontrols-policy: bottom;");
            instance.applyCss();
            instance.layout();
        });
        assertEquals(HControlsPolicy.BOTTOM, instance.getHControlsPolicy());
    }

    @Test
    public void testVerticalRangeControl(FxRobot robot)
    {
        ChartRangePane instance = rangePane;

        assertEquals(VControlsPolicy.LEFT, instance.getVControlsPolicy());
        
        instance.setVControlsPolicy(VControlsPolicy.OFF);
        assertEquals(VControlsPolicy.OFF, instance.getVControlsPolicy());
        
        robot.interact( () -> {
            instance.setStyle("-fx-vcontrols-policy: right;");
            instance.applyCss();
            instance.layout();
        });
        assertEquals(VControlsPolicy.RIGHT, instance.getVControlsPolicy());

        robot.interact( () -> {
            instance.setStyle("-fx-vcontrols-policy: left;");
            instance.applyCss();
            instance.layout();
        });
        assertEquals(VControlsPolicy.LEFT, instance.getVControlsPolicy());
    }
    
    @Test
    public void testFitToWidth(FxRobot robot)
    {
        ChartRangePane instance = rangePane;

        assertFalse(instance.isFitToWidth());
        
        instance.setFitToWidth(true);
        assertTrue(instance.isFitToWidth());
        
        robot.interact( () -> {
            instance.setStyle("-fx-fit-to-width: false;");
            instance.applyCss();
            instance.layout();
        });
        assertFalse(instance.isFitToWidth());

        robot.interact( () -> {
            instance.setStyle("-fx-fit-to-width: true;");
            instance.applyCss();
            instance.layout();
        });
        assertTrue(instance.isFitToWidth());
    }

    @Test
    public void testFitToHeight(FxRobot robot)
    {
        ChartRangePane instance = rangePane;

        assertFalse(instance.isFitToHeight());
        
        instance.setFitToHeight(true);
        assertTrue(instance.isFitToHeight());
        
        robot.interact( () -> {
            instance.setStyle("-fx-fit-to-height: false;");
            instance.applyCss();
            instance.layout();
        });
        assertFalse(instance.isFitToHeight());

        robot.interact( () -> {
            instance.setStyle("-fx-fit-to-height: true;");
            instance.applyCss();
            instance.layout();
        });
        assertTrue(instance.isFitToHeight());
    }

    @Test
    public void testGetContent()
    {
        ChartRangePane instance = rangePane;
        
        assertEquals(chart, instance.getContent());
    }

    /**
     * Test of setContent method, of class ChartRangePane.
     */
    @Test
    public void testSetContent(FxRobot robot)
    {
        NumberAxis xAxis = new NumberAxis(0, 150, 10);
        NumberAxis yAxis = new NumberAxis(0, 150, 10);
        LineChart<Number,Number> secondChart = new LineChart<>(xAxis,yAxis);

        ChartRangePane instance = rangePane;

        robot.interact( () -> instance.setContent(secondChart) );
        assertEquals(secondChart, instance.getContent());
    }

    /**
     * Test of createDefaultSkin method, of class ChartRangePane.
     */
    @Test
    public void testCreateDefaultSkin()
    {
        ChartRangePane instance = rangePane;

        assertNotNull(instance.getSkin());
    }
    
}
