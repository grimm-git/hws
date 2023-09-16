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

import hws.gui.charts.skins.ChartRangePaneSkin;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 *
 * @author grimm
 */
public class ChartRangePane
extends Control
{
    private static final String DEFAULT_STYLE_CLASS = "chart-range-pane";

    public static enum HControlsPolicy { OFF, TOP, BOTTOM }
    public static enum VControlsPolicy   { OFF, LEFT, RIGHT }

    private static final StyleablePropertyFactory<ChartRangePane> FACTORY = 
                           new StyleablePropertyFactory<>(Control.getClassCssMetaData()); 


    public final BooleanProperty fitToWidthProperty() { return fitToWidth; }
    public final boolean isFitToWidth()               { return fitToWidth.get(); }
    public final void setFitToWidth(boolean value)    { fitToWidth.set(value); }
    private final SimpleStyleableBooleanProperty fitToWidth =
        (SimpleStyleableBooleanProperty) FACTORY.createStyleableBooleanProperty(
            this, "fitToWidth", "-fx-fit-to-width", s -> s.fitToWidth, false);

    
    public final BooleanProperty fitToHeightProperty() { return fitToHeight; }
    public final boolean isFitToHeight()               { return fitToHeight.get(); }
    public final void setFitToHeight(boolean value)    { fitToHeight.set(value); }
    private final SimpleStyleableBooleanProperty fitToHeight =
        (SimpleStyleableBooleanProperty) FACTORY.createStyleableBooleanProperty(
            this, "fitToHeight", "-fx-fit-to-height", s -> s.fitToHeight, false);

    
    public ObservableValue<HControlsPolicy> hcontrolsPolicyProperty() { return hcontrolsPolicy; }
    public final HControlsPolicy getHControlsPolicy() { return hcontrolsPolicy.getValue(); }
    public final void setHControlsPolicy(HControlsPolicy value) { hcontrolsPolicy.setValue(value); }
    private final SimpleStyleableObjectProperty<HControlsPolicy> hcontrolsPolicy =
        (SimpleStyleableObjectProperty<HControlsPolicy>) FACTORY.createStyleableEnumProperty(
                this, "hcontrolsPolicy", "-fx-hcontrols-policy", s -> s.hcontrolsPolicy,
                HControlsPolicy.class, HControlsPolicy.BOTTOM);

    public ObservableValue<VControlsPolicy> vcontrolsPolicyProperty() { return vcontrolsPolicy; }
    public final VControlsPolicy getVControlsPolicy() { return vcontrolsPolicy.getValue(); }
    public final void setVControlsPolicy(VControlsPolicy value) { vcontrolsPolicy.setValue(value); }
    private final SimpleStyleableObjectProperty<VControlsPolicy> vcontrolsPolicy = 
        (SimpleStyleableObjectProperty<VControlsPolicy>) FACTORY.createStyleableEnumProperty(
                this, "vcontrolsPolicy", "-fx-vcontrols-policy", s -> s.vcontrolsPolicy,
                VControlsPolicy.class, VControlsPolicy.LEFT);

    
    private final ObjectProperty<XYChart<?,?>> content = new SimpleObjectProperty<>(this, "content");
    public final XYChart<?,?> getContent() { return content.get(); }
    public final ObjectProperty<XYChart<?,?>> contentProperty() { return content; }
    public final void setContent(XYChart<?,?> newContent) { content.set(newContent); }

    /**
     * Specify the preferred width of the ChartRangePane Viewport.
     * This is the width that will be available to the Chart node.
     * The overall width of the ChartRangePane is the ViewportWidth + padding
     */
    private final DoubleProperty prefViewportWidth = new SimpleDoubleProperty(this, "prefViewportWidth");
    public final double getPrefViewportWidth() { return prefViewportWidth.get(); }
    public final void setPrefViewportWidth(double value) { prefViewportWidthProperty().set(value); }
    public final DoubleProperty prefViewportWidthProperty() { return prefViewportWidth; }

    /** Minimal width repectively */
    private final DoubleProperty minViewportWidth = new SimpleDoubleProperty(this, "minViewportWidth");
    public final double getMinViewportWidth() { return minViewportWidth.get(); }
    public final void setMinViewportWidth(double value) { minViewportWidthProperty().set(value); }
    public final DoubleProperty minViewportWidthProperty() { return minViewportWidth; }

    /**
     * Specify the preferred height of the ChartRangePane Viewport.
     * This is the height that will be available to the Chart node.
     * The overall height of the ChartRangePane is the ViewportHeight + padding
     */
    private final DoubleProperty prefViewportHeight = new SimpleDoubleProperty(this, "prefViewportHeight");
    public final double getPrefViewportHeight() { return prefViewportHeight.get(); }
    public final void setPrefViewportHeight(double value) { prefViewportHeightProperty().set(value); }
    public final DoubleProperty prefViewportHeightProperty() { return prefViewportHeight; }

    /** Minimal height repectively */
    private final DoubleProperty minViewportHeight = new SimpleDoubleProperty(this, "minViewportHeight");
    public final double getMinViewportHeight() { return minViewportHeight.get(); }
    public final void setMinViewportHeight(double value) { minViewportHeightProperty().set(value); }
    public final DoubleProperty minViewportHeightProperty() { return minViewportHeight; }

    /***************************************************************************************/
    /*                                                                                     */
    /*                                    Constructors                                     */
    /*                                                                                     */
    /***************************************************************************************/
    public ChartRangePane()
    {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        setAccessibleRole(null);

        // focusTraversable is styleable through css. Calling setFocusTraversable
        // makes it look to css like the user set the value and css will not
        // override. Initializing focusTraversable by calling applyStyle with
        // null StyleOrigin ensures that css will be able to override the value.
        ((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
    }

    /**
     * Creates a new ChartRangePane.
     * 
     * @param content the initial content for the ChartRangePane, must be a Chart
     */
    public ChartRangePane(XYChart<?,?> content)
    {
        this();
        setContent(content);
    }
    
    /** {@inheritDoc} */
    @Override
    protected Skin<?> createDefaultSkin()
    {
        return new ChartRangePaneSkin(this);
    }

    /** {@inheritDoc} */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return FACTORY.getCssMetaData();
    }
}
