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

import hws.gui.charts.ChartRangePane;
import hws.gui.charts.ChartRangePane.HControlsPolicy;
import hws.gui.charts.ChartRangePane.VControlsPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import javafx.scene.layout.RowConstraints;

/**
 *
 * @author grimm
 */
public class ChartRangePaneSkin
extends SkinBase<ChartRangePane>
{
    private final static int VERTICAL_LIMIT_LENGTH = 80;
    private final static int VERTICAL_RANGE_LENGTH = 80;
    private final static int HORIZONTAL_LIMIT_LENGTH = 100;
    private final static int HORIZONTAL_RANGE_LENGTH = 280;

    private final static int COL_AXIS = 1;
    private final static int COL_LLIMIT = 2;
    private final static int COL_DYNAMIC = 3;
    private final static int COL_RLIMIT = 4;

    private final static int ROW_LLIMIT = 1;
    private final static int ROW_DYNAMIC = 2;
    private final static int ROW_RLIMIT = 3;
    private final static int ROW_AXIS = 4;
    
    // substructure
    private GridPane  gridNode;        // root node of ChartRangePane
    private XYChart<?,?>  chartNode;   // Chart to embed
    private ScrollBar rangeBarBottom_Left;
    private ScrollBar rangeBarBottom_Range;
    private ScrollBar rangeBarBottom_Right;
    private ScrollBar rangeBarLeft_Top;
    private ScrollBar rangeBarLeft_Range;
    private ScrollBar rangeBarLeft_Bottom;
    private ScrollBar rangeBarTop_Left;
    private ScrollBar rangeBarTop_Range;
    private ScrollBar rangeBarTop_Right;
    private ScrollBar rangeBarRight_Top;
    private ScrollBar rangeBarRight_Range;
    private ScrollBar rangeBarRight_Bottom;

    private RangeControlSet rangeControlSetTop;
    private RangeControlSet rangeControlSetBottom;
    private RangeControlSet rangeControlSetLeft;
    private RangeControlSet rangeControlSetRight;
        
    private List<ColumnConstraints> colConstraints;
    private List<RowConstraints> rowConstraints;

    private final ChartRangePaneBehavior behavior;
    
    /**
     * Creates a new ScrollPaneSkin instance, installing the necessary child
     * nodes into the Control {@link Control#getChildren() children} list, as
     * well as the necessary input mappings for handling key, mouse, etc events.
     *
     * @param control The control that this skin should be installed onto.
     */
    public ChartRangePaneSkin(final ChartRangePane control)
    {
        super(control);

        initialize();

        behavior = new ChartRangePaneBehavior(control);
        behavior.setControlSetsHorizontal(rangeControlSetTop, rangeControlSetBottom);
        behavior.setControlSetsVertical(rangeControlSetLeft, rangeControlSetRight);
        behavior.createAxisConverter();
        
        setHorizontalRangeControls(control);
        setVerticalRangeControls(control);

        registerChangeListener(control.contentProperty(), e -> {
            if (chartNode != getSkinnable().getContent()) {
                if (chartNode != null) {
                    gridNode.getChildren().remove(chartNode);
                }
                chartNode = getSkinnable().getContent();
                if (chartNode != null) {
                    gridNode.getChildren().add(chartNode);
                    GridPane.setConstraints(chartNode, 1, 1, 3, 3, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
                }
            }
            getSkinnable().requestLayout();
        });

        registerChangeListener(control.hcontrolsPolicyProperty(), e -> {
            setHorizontalRangeControls(getSkinnable());
            getSkinnable().requestLayout();
        });

        registerChangeListener(control.vcontrolsPolicyProperty(), e -> {
            setVerticalRangeControls(getSkinnable());
            getSkinnable().requestLayout();
        });

        registerChangeListener(control.fitToWidthProperty(), e -> {
            boolean val = (boolean) e.getValue();
            setHGrowth(COL_DYNAMIC, val ? Priority.ALWAYS : Priority.NEVER);
            GridPane.setHgrow(rangeBarTop_Range, val ? Priority.ALWAYS : Priority.NEVER);
            GridPane.setHgrow(rangeBarBottom_Range, val ? Priority.ALWAYS : Priority.NEVER);
            getSkinnable().requestLayout();
        });
        registerChangeListener(control.fitToHeightProperty(), e -> {
            boolean val = (boolean) e.getValue();
            setVGrowth(ROW_DYNAMIC, val ? Priority.ALWAYS : Priority.NEVER);
            GridPane.setVgrow(rangeBarLeft_Range, val ? Priority.ALWAYS : Priority.NEVER);
            GridPane.setVgrow(rangeBarRight_Range, val ? Priority.ALWAYS : Priority.NEVER);
            getSkinnable().requestLayout();
        });

        Consumer<ObservableValue<?>> viewportSizeHintConsumer = e -> { getSkinnable().requestLayout(); };
        registerChangeListener(control.prefViewportWidthProperty(), viewportSizeHintConsumer);
        registerChangeListener(control.prefViewportHeightProperty(), viewportSizeHintConsumer);
        registerChangeListener(control.minViewportWidthProperty(), viewportSizeHintConsumer);
        registerChangeListener(control.minViewportHeightProperty(), viewportSizeHintConsumer);
    }

    /** {@inheritDoc} */
    @Override public void dispose() {
        super.dispose();

        if (behavior != null) {
            behavior.dispose();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset)
    {
        final ChartRangePane pane = getSkinnable();

        double computedWidth = gridNode.prefWidth(height);
        double axisWidth = chartNode.getYAxis().prefWidth(height);

        if (pane.getPrefViewportWidth() > 0) {
            double chartWidth = chartNode.prefWidth(height);
            computedWidth = computedWidth + pane.getPrefViewportWidth() - chartWidth;
        }

        return computedWidth + axisWidth;
    }
    
    /** {@inheritDoc} */
    @Override 
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset)
    {
        final ChartRangePane pane = getSkinnable();

        double computedHeight = gridNode.prefHeight(width);
        double axisHeight = chartNode.getXAxis().prefHeight(width);

        if (pane.getPrefViewportHeight() > 0) {
            double chartHeight = chartNode.prefHeight(width);
            computedHeight = computedHeight + pane.getPrefViewportHeight() - chartHeight;
        }
        
        return computedHeight + axisHeight;
    }
  
    /** {@inheritDoc} */
    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset)
    {
        final ChartRangePane control = getSkinnable();
  
        double computedWidth = gridNode.minWidth(height);
        double axisWidth = chartNode.getYAxis().minWidth(height);
        
        if (control.getMinViewportWidth() > 0) {
            double chartWidth = chartNode.minWidth(height);
            computedWidth = computedWidth + control.getMinViewportWidth() - chartWidth;
        }

        return computedWidth + axisWidth;
    }

    /** {@inheritDoc} */
    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset)
    {
        final ChartRangePane control = getSkinnable();
  
        double computedHeight = gridNode.minHeight(width);
        double axisHeight = chartNode.getXAxis().minHeight(width);
        
        if (control.getMinViewportHeight() > 0) {
            double chartHeight = chartNode.minHeight(width);
            computedHeight = computedHeight + control.getMinViewportHeight() - chartHeight;
        }

        return computedHeight + axisHeight;
    }

    private void initialize()
    {
        ChartRangePane control = getSkinnable();
        chartNode = control.getContent();

        gridNode = new GridPane();
        gridNode.setHgap(0);
        gridNode.setVgap(0);
        gridNode.setCache(true);
        gridNode.getStyleClass().add("viewport");

        rangeBarTop_Left     = createHorizontalScrollBar(  0, "limit");
        rangeBarTop_Range    = createHorizontalScrollBar( 50, "range");
        rangeBarTop_Right    = createHorizontalScrollBar(100, "limit");
        rangeBarBottom_Left  = createHorizontalScrollBar(  0, "limit");
        rangeBarBottom_Range = createHorizontalScrollBar( 50, "range");
        rangeBarBottom_Right = createHorizontalScrollBar(100, "limit");
        rangeBarLeft_Top     = createVerticalScrollBar(  100, "limit");
        rangeBarLeft_Range   = createVerticalScrollBar(   50, "range");
        rangeBarLeft_Bottom  = createVerticalScrollBar(    0, "limit");
        rangeBarRight_Top    = createVerticalScrollBar(  100, "limit");
        rangeBarRight_Range  = createVerticalScrollBar(   50, "range");
        rangeBarRight_Bottom = createVerticalScrollBar(    0, "limit");
       
        gridNode.getChildren().addAll(chartNode,
                rangeBarTop_Left,    rangeBarTop_Range,    rangeBarTop_Right,
                rangeBarBottom_Left, rangeBarBottom_Range, rangeBarBottom_Right,
                rangeBarRight_Top,   rangeBarRight_Range,    rangeBarRight_Bottom,
                rangeBarLeft_Top,    rangeBarLeft_Range,     rangeBarLeft_Bottom);

        GridPane.setConstraints(rangeBarTop_Left,     2, 0, 1, 1);
        GridPane.setConstraints(rangeBarTop_Range,    3, 0, 1, 1, HPos.CENTER, VPos.BOTTOM, Priority.ALWAYS, Priority.NEVER);
        GridPane.setConstraints(rangeBarTop_Right,    4, 0, 1, 1);
        GridPane.setConstraints(rangeBarBottom_Left,  2, 5, 1, 1);
        GridPane.setConstraints(rangeBarBottom_Range, 3, 5, 1, 1, HPos.CENTER, VPos.BOTTOM, Priority.ALWAYS, Priority.NEVER);
        GridPane.setConstraints(rangeBarBottom_Right, 4, 5, 1, 1);
        GridPane.setConstraints(chartNode,            1, 1, 4, 4, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(rangeBarLeft_Top,     0, 1, 1, 1);
        GridPane.setConstraints(rangeBarLeft_Range,   0, 2, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(rangeBarLeft_Bottom,  0, 3, 1, 1);
        GridPane.setConstraints(rangeBarRight_Top,    5, 1, 1, 1);
        GridPane.setConstraints(rangeBarRight_Range,  5, 2, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
        GridPane.setConstraints(rangeBarRight_Bottom, 5, 3, 1, 1);

        colConstraints = initColumnConstraints(gridNode);
        setColWidth(COL_LLIMIT, HORIZONTAL_LIMIT_LENGTH, HORIZONTAL_LIMIT_LENGTH);
        setColWidth(COL_DYNAMIC, HORIZONTAL_RANGE_LENGTH, USE_COMPUTED_SIZE);
        setColWidth(COL_RLIMIT, HORIZONTAL_LIMIT_LENGTH, HORIZONTAL_LIMIT_LENGTH);
        setHGrowth(COL_DYNAMIC, Priority.ALWAYS);

        Platform.runLater(() -> {
            Bounds axisBounds = chartNode.getXAxis().getBoundsInParent();
            double colWidth = axisBounds.getMinX();
            setColWidth(COL_AXIS, colWidth, colWidth);
        });
      
        rowConstraints = initRowConstraints(gridNode);
        setRowHeight(ROW_LLIMIT, VERTICAL_LIMIT_LENGTH, VERTICAL_LIMIT_LENGTH);
        setRowHeight(ROW_DYNAMIC, VERTICAL_RANGE_LENGTH, USE_COMPUTED_SIZE);
        setRowHeight(ROW_RLIMIT, VERTICAL_LIMIT_LENGTH, VERTICAL_LIMIT_LENGTH);
        setVGrowth(ROW_DYNAMIC, Priority.ALWAYS);

        Platform.runLater(() -> {
            Bounds axisBounds = chartNode.getXAxis().getBoundsInParent();
            double rowHeight = chartNode.getHeight()-axisBounds.getMaxY();
            setRowHeight(ROW_AXIS, rowHeight, rowHeight);
        });
        
        rangeControlSetTop = new RangeControlSet(rangeBarTop_Left, rangeBarTop_Range, rangeBarTop_Right);
        rangeControlSetBottom = new RangeControlSet(rangeBarBottom_Left, rangeBarBottom_Range, rangeBarBottom_Right);
        rangeControlSetLeft = new RangeControlSet(rangeBarLeft_Bottom, rangeBarLeft_Range, rangeBarLeft_Top);
        rangeControlSetRight = new RangeControlSet(rangeBarRight_Bottom, rangeBarRight_Range, rangeBarRight_Top);
        
        getChildren().clear();
        getChildren().add(gridNode);
    }

    private void setHorizontalRangeControls(ChartRangePane control)
    {
        boolean policy;
        
        policy= control.getHControlsPolicy() == HControlsPolicy.TOP;
        rangeControlSetTop.setVisible(policy);

        policy = control.getHControlsPolicy() == HControlsPolicy.BOTTOM;
        rangeControlSetBottom.setVisible(policy);
    }
    
    private void setVerticalRangeControls(ChartRangePane control)
    {
        boolean policy;

        policy = control.getVControlsPolicy() == VControlsPolicy.LEFT;
        rangeControlSetLeft.setVisible(policy);

        policy = control.getVControlsPolicy() == VControlsPolicy.RIGHT;
        rangeControlSetRight.setVisible(policy);
    }
 
    private ScrollBar createHorizontalScrollBar(int initVal, String styleClass)
    {
        ScrollBar bar = createScrollBar(initVal, styleClass);
        bar.setOrientation(Orientation.HORIZONTAL);
        return bar;
    }
    
    private ScrollBar createVerticalScrollBar(int initVal, String styleClass)
    {
        ScrollBar bar = createScrollBar(initVal, styleClass);
        bar.setOrientation(Orientation.VERTICAL);
        bar.setRotate(180);
        return bar;
    }

    private ScrollBar createScrollBar(int initVal, String styleClass)
    {
        ScrollBar bar= new ScrollBar();
        bar.setMin(0);
        bar.setMax(100);
        bar.setValue(initVal);
        bar.getStyleClass().add(styleClass);
        bar.addEventFilter(MouseEvent.MOUSE_PRESSED, barHandler);
        return bar;
    }

    private final EventHandler<MouseEvent> barHandler = ev -> {
            if (getSkinnable().isFocusTraversable())
                getSkinnable().requestFocus();
        };

    private List<ColumnConstraints> initColumnConstraints(GridPane pane)
    {
        List<ColumnConstraints> list = new ArrayList<>();
        
        for (int n=0; n < pane.getColumnCount(); n++)
            list.add(new ColumnConstraints());
        
        pane.getColumnConstraints().setAll(list);
        return list;
    }

    private void setColWidth(int idx, double minLimit, double maxLimit)
    {
        if (idx >= colConstraints.size())
            throw new IndexOutOfBoundsException("Illegal column address in ChartRangePane!");
            
        colConstraints.get(idx).setMinWidth(minLimit);
        colConstraints.get(idx).setMaxWidth(maxLimit);
    }

    private void setHGrowth(int idx, Priority prio)
    {
        if (idx >= colConstraints.size())
            throw new IndexOutOfBoundsException("Illegal column address in ChartRangePane!");
            
        colConstraints.get(idx).setHgrow(prio);
    }

    private void setRowHeight(int idx, double minLimit, double maxLimit)
    {
        if (idx >= rowConstraints.size())
            throw new IndexOutOfBoundsException("Illegal row address in ChartRangePane!");
            
        rowConstraints.get(idx).setMinHeight(minLimit);
        rowConstraints.get(idx).setMaxHeight(maxLimit);
    }

    private void setVGrowth(int idx, Priority prio)
    {
        if (idx >= rowConstraints.size())
            throw new IndexOutOfBoundsException("Illegal row address in ChartRangePane!");
            
        rowConstraints.get(idx).setVgrow(prio);
    }
    
    private List<RowConstraints> initRowConstraints(GridPane pane)
    {
        List<RowConstraints> list = new ArrayList<>();
        
        for (int n=0; n < pane.getRowCount(); n++)
            list.add(new RowConstraints());
        
        pane.getRowConstraints().setAll(list);
        return list;
    }
}
