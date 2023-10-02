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

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import static java.time.temporal.ChronoUnit.DAYS;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;

/**
 *
 * @author grimm
 */
public class LocalDateAxis
extends Axis<LocalDate>
{
    private final static int AVERAGE_TICK_GAP = 70;

    private final Path minorTickPath  = new Path();

    private LocalDate minRangeDate;
    private LocalDate maxRangeDate;
    private AxisTick tickInterval;

    List<LocalDate> minorTickMarkValues = new ArrayList<>();
    private boolean minorTickMarksDirty = true;

    public LocalDateAxis()
    {
        tickInterval = AxisTick.MONTHS;
        minorTickPath.getStyleClass().add("axis-minor-tick-mark");
        getChildren().add(minorTickPath);
        setTickMarkVisible(false);  // hide major Ticks
        
        this.getChildrenUnmodifiable().addListener((ListChangeListener<Node>) c -> {
                while (c.next()) {
                    for (Node mark : c.getAddedSubList()) {
                        if (mark instanceof Text)
                            mark.getStyleClass().add("tick-label");
                    }
                }
            });
    }

    public LocalDateAxis(LocalDate lBound, LocalDate uBound)
    {
        this();
        setAutoRanging(false);
        lowerBound.set(lBound);
        upperBound.set(uBound);
    }

    /**
     * Unmodifiable observable list of minor tick marks, each tick mark directly represents a
     * tick mark on this axis. This is updated whenever the displayed tickmarks changes.
     *
     * This method allows the Chart to draw the grid also for minor Tickmarks.
     * 
     * @return Unmodifiable observable list of minor TickMarks on this axis
     */
    public List<LocalDate> getMinorTickMarks()
    {
        return Collections.unmodifiableList(minorTickMarkValues);
    }

    @Override
    public void invalidateRange(List<LocalDate> list)
    {
        super.invalidateRange(list);

        Collections.sort(list);
        if (list.isEmpty()) {
            minRangeDate = maxRangeDate = LocalDate.now();
        } else if (list.size() == 1) {
            minRangeDate = maxRangeDate = list.get(0);
        } else {
            minRangeDate = list.get(0);
            maxRangeDate = list.get(list.size() - 1);
        }
    }

    private ObjectProperty<LocalDate> lowerBound =
            new SimpleObjectProperty<LocalDate>(LocalDateAxis.this, "lowerBound") {
                @Override
                protected void invalidated() {
                    if (!isAutoRanging()) {
                        invalidateRange();
                        requestAxisLayout();
                    }
                }
            };
    public final LocalDate getLowerBound() { return lowerBound.get(); }
    public final void setLowerBound(LocalDate date) {lowerBound.set(date); }
    public final ObjectProperty<LocalDate> lowerBoundProperty() { return lowerBound; }

    private ObjectProperty<LocalDate> upperBound =
            new SimpleObjectProperty<LocalDate>(LocalDateAxis.this, "upperBound") {
                @Override
                protected void invalidated() {
                    if (!isAutoRanging()) {
                        invalidateRange();
                        requestAxisLayout();
                    }
                }
            };
    public final LocalDate getUpperBound() { return upperBound.get(); }
    public final void setUpperBound(LocalDate date) {upperBound.set(date); }
    public final ObjectProperty<LocalDate> upperBoundProperty() { return upperBound; }

    private DoubleProperty minorTickLength =
            new SimpleDoubleProperty(LocalDateAxis.this, "minorTickLength", 8) {
                @Override
                protected void invalidated() {
                    requestAxisLayout();
                }
    };
    public final double getMinorTickLength() { return minorTickLength.get(); }
    public final void setMinorTickLength(double value) { minorTickLength.set(value); }
    public final DoubleProperty minorTickLengthProperty() { return minorTickLength; }

    /** @inheritDoc */
    @Override
    protected Object autoRange(double length)
    {
        if (isAutoRanging())
            return new Object[]{minRangeDate, maxRangeDate};

        else if (getLowerBound() == null || getUpperBound() == null) 
            throw new IllegalArgumentException("If autoRanging is false, a lower and upper bound must be set.");

        return getRange();
    }

    /** @inheritDoc */
    @Override
    protected void setRange(Object range, boolean animate)
    {
        Object[] newRange = (Object[]) range;
        lowerBound.set((LocalDate) newRange[0]);
        upperBound.set((LocalDate) newRange[1]);
    }

    /** @inheritDoc */
    @Override
    protected Object getRange()
    {
        return new Object[]{lowerBound.get(), upperBound.get()};
    }

    /** @inheritDoc */
    @Override
    public double getZeroPosition()
    {
        return 0d;
    }

    /** @inheritDoc */
    @Override
    public double getDisplayPosition(LocalDate value)
    {
        double visibleDays = DAYS.between(lowerBound.get(), upperBound.get());
        double valueDays = DAYS.between(lowerBound.get(), value);
        double relPosition = valueDays / visibleDays;

        if(getSide().isVertical()) {
            double heightInPixel = getHeight() - getZeroPosition();
            return heightInPixel - relPosition * heightInPixel + getZeroPosition();
            
        } else {
            double widthInPixel = getWidth() - getZeroPosition();
            return relPosition * widthInPixel + getZeroPosition();
        }
    }

    /** @inheritDoc */
    @Override
    public LocalDate getValueForDisplay(double displayPosition)
    {
        double visibleDays = DAYS.between(lowerBound.get(), upperBound.get());
        double posInPixel = displayPosition - getZeroPosition();

        double relPosition;
        if(getSide().isVertical()) {
            double heightInPixel = getHeight() - getZeroPosition();
            relPosition = (heightInPixel - posInPixel) / heightInPixel;
            
        } else {
            double widthInPixel = getWidth() - getZeroPosition();
            relPosition = posInPixel / widthInPixel;
        }

        return lowerBound.get().plusDays((long)(visibleDays * relPosition));
    }

    /** @inheritDoc */
    @Override
    public boolean isValueOnAxis(LocalDate value)
    {
        LocalDate earliestDate = lowerBound.get();
        if (earliestDate.isAfter(value))
            return false;
        
        LocalDate latestDate = upperBound.get();
        if (latestDate.isBefore(value))
            return false;
        
        return true;
    }

    /** @inheritDoc */
    @Override
    public double toNumericValue(LocalDate value)
    {    
        return value.toEpochDay();
    }

    /** @inheritDoc */
    @Override
    public LocalDate toRealValue(double value)
    {
        return LocalDate.ofEpochDay((long) value);
    }

    /** @inheritDoc */
    @Override
    protected List<LocalDate> calculateTickValues(double length, Object range)
    {
        Object[] newRange = (Object[]) range;
        LocalDate earliestDate = (LocalDate) newRange[0];
        LocalDate latestDate = (LocalDate) newRange[1];
                
        List<LocalDate> dateList = new ArrayList<>();
        
        long axisLength = (int) length;
        long numTicks = axisLength / AVERAGE_TICK_GAP;
        long numDays = DAYS.between(earliestDate, latestDate);
        
        tickInterval = AxisTick.findInterval(numDays, (int) numTicks);
        
        LocalDate tickDate = tickInterval.normalizeToCenter(earliestDate);
        while (tickDate.isBefore(latestDate)) {
            dateList.add(tickDate);
            tickDate = tickInterval.nextInterval(tickDate);
        }
        return dateList;
    }

    protected List<LocalDate> calculateMinorTickValues()
    {
        List<LocalDate> tickDateList = new ArrayList<>();

        LocalDate lastTickDate = null; 
        for (Axis.TickMark<LocalDate> tick : getTickMarks()) {
            lastTickDate = tick.getValue();
            LocalDate tickDate = tickInterval.normalizeToBegin(tick.getValue());
            tickDateList.add(tickDate);
        }
        
        if (lastTickDate != null)
            tickDateList.add(tickInterval.normalizeToEnd(lastTickDate));
        
        return tickDateList;
    }

    /** @inheritDoc */
    @Override
    protected void tickMarksUpdated()
    {
        super.tickMarksUpdated();

        minorTickMarkValues = calculateMinorTickValues();
        minorTickMarksDirty = true;
    }

    /** @inheritDoc */
    @Override
    protected String getTickMarkLabel(LocalDate date)
    {
        return tickInterval.getLabel(date);
    }
    
    /** @inheritDoc */
    @Override
    protected void layoutChildren()
    {
        super.layoutChildren();

        final Side side = this.getSide();
        final double length = side.isVertical() ? getHeight() :getWidth() ;
        if (minorTickMarksDirty) {
            minorTickMarksDirty = false;
            updateMinorTickPath(side, length);
        }
    }
    
    private void updateMinorTickPath(Side side, double length)
    {
        if (tickInterval == null)
            throw new IllegalArgumentException("Chart side for Axis not defined!");
                    
        if (tickInterval.equals(AxisTick.DAYS))
            return;
        
        double neededLength = getTickMarks().size()*2;

        // Update minor tickmarks
        minorTickPath.getElements().clear();

        // Don't draw minor tick marks if there isn't enough space for them!
        double tickLength = Math.max(0, getMinorTickLength());
        if (tickLength > 0 && length > neededLength) {
            if (Side.LEFT.equals(side)) {
                // snap minorTickPath to pixels
                minorTickPath.setLayoutX(-0.5);
                minorTickPath.setLayoutY(0.5);
                for (LocalDate value : minorTickMarkValues) {
                    double y = getDisplayPosition(value);
                    if (y >= 0 && y <= length) {
                        minorTickPath.getElements().addAll(
                                new MoveTo(getWidth() - tickLength, y),
                                new LineTo(getWidth() - 1, y));
                    }
                }
            } else if (Side.RIGHT.equals(side)) {
                // snap minorTickPath to pixels
                minorTickPath.setLayoutX(0.5);
                minorTickPath.setLayoutY(0.5);
                for (LocalDate value : minorTickMarkValues) {
                    double y = getDisplayPosition(value);
                    if (y >= 0 && y <= length) {
                        minorTickPath.getElements().addAll(
                                new MoveTo(1, y),
                                new LineTo(tickLength, y));
                    }
                }
            } else if (Side.TOP.equals(side)) {
                // snap minorTickPath to pixels
                minorTickPath.setLayoutX(0.5);
                minorTickPath.setLayoutY(-0.5);
                for (LocalDate value : minorTickMarkValues) {
                    double x = getDisplayPosition(value);
                    if (x >= 0 && x <= length) {
                        minorTickPath.getElements().addAll(
                                new MoveTo(x, getHeight() - 1),
                                new LineTo(x, getHeight() - tickLength));
                    }
                }
            } else { // BOTTOM
                // snap minorTickPath to pixels
                minorTickPath.setLayoutX(0.5);
                minorTickPath.setLayoutY(0.5);
                for (LocalDate value : minorTickMarkValues) {
                    double x = getDisplayPosition(value);
                    if (x >= 0 && x <= length) {
                        minorTickPath.getElements().addAll(
                                new MoveTo(x, 1.0F),
                                new LineTo(x, tickLength));
                    }
                }
            }
        }
    }

    protected enum AxisTick
    {
        DAYS(Period.ofDays(1), "dd MM yy"),
        WEEKS(Period.ofWeeks(1), "'W'%2d\nyyyy"),
        MONTHS(Period.ofMonths(1), "LLL\nyyyy"),
        QUARTERS(Period.ofMonths(3), "'Q'%d yy"),
        YEARS(Period.ofYears(1), "yyyy"),
        DECADE(Period.ofYears(10), "yyyy"),
        SEMICENTURY(Period.ofYears(50), "yyyy"),
        CENTURY(Period.ofYears(100), "yyyy");
        
        private Period period;
        private String format;
        
        private AxisTick(Period p, String f)
        {
            period = p;
            format = f;
        }
        
        public static AxisTick findInterval(long spanInDays, int cntTicks)
        {
            float[] distances = new float[AxisTick.values().length];
            
            distances[0] = (spanInDays     - cntTicks) * (spanInDays     - cntTicks);
            distances[1] = (spanInDays/7   - cntTicks) * (spanInDays/7   - cntTicks);
            distances[2] = (spanInDays/30  - cntTicks) * (spanInDays/30  - cntTicks);
            distances[3] = (spanInDays/90  - cntTicks) * (spanInDays/90  - cntTicks);
            distances[4] = (spanInDays/365 - cntTicks) * (spanInDays/365 - cntTicks);
            distances[5] = (spanInDays/3650 - cntTicks) * (spanInDays/3650 - cntTicks);
            distances[6] = (spanInDays/18250 - cntTicks) * (spanInDays/18250 - cntTicks);
            distances[7] = (spanInDays/36500 - cntTicks) * (spanInDays/36500 - cntTicks);

            int smalestDistanceIdx = 0;
            float temp = distances[0];
            for (int n=0; n< AxisTick.values().length; n++)
                if (distances[n] < temp) {
                    temp = distances[n];
                    smalestDistanceIdx = n;
                }

            return AxisTick.valueOf(smalestDistanceIdx);
        }
        
        public static AxisTick valueOf(int idx)
        {
            if (idx < AxisTick.values().length)
                return AxisTick.values()[idx];
            
            return AxisTick.DAYS;
        }
        
        public String getLabel(LocalDate date)
        {
            String dateFormat = format;
            
            switch(this) {
                case WEEKS -> {
                    TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(); 
                    dateFormat = String.format(format, date.get(woy));
                }
                case QUARTERS -> {
                    int quarter = (date.getMonthValue()-1) / 3 + 1;
                    dateFormat = String.format(format, quarter);
                }
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            return date.format(formatter);
        }
        
        public LocalDate normalizeToBegin(LocalDate date)
        {
            switch (this) {
                case WEEKS -> {
                    long dayofweek = date.getLong(ChronoField.DAY_OF_WEEK);
                    return date.minusDays(dayofweek-1);
                }
                case MONTHS -> {
                    return LocalDate.of(date.getYear(), date.getMonthValue(), 1);
                }
                case QUARTERS -> {
                    int quarter = (date.getMonthValue()-1) / 3;
                    return LocalDate.of(date.getYear(), quarter * 3 + 1, 1);
                }
                case YEARS -> {
                    return LocalDate.of(date.getYear(), 1, 1);
                }
                case DECADE -> {
                    int decade = date.getYear() / 10 * 10;
                    return LocalDate.of(decade, 1, 1);
                }
                case SEMICENTURY -> {
                    int semicentury = date.getYear() / 50 * 50;
                    return LocalDate.of(semicentury, 1, 1);
                }
                case CENTURY -> {
                    int century = date.getYear() / 100 * 100;
                    return LocalDate.of(century, 1, 1);
                }
                default -> {
                    return date;
                }
            }
        }

        public LocalDate normalizeToCenter(LocalDate date)
        {
            switch (this) {
                case WEEKS -> {
                    long dayofweek = date.getLong(ChronoField.DAY_OF_WEEK);
                    return date.plusDays(4 - dayofweek);
                }
                case MONTHS -> {
                    return LocalDate.of(date.getYear(), date.getMonthValue(), 15);
                }
                case QUARTERS -> {
                    int quarter = (date.getMonthValue()-1) / 3;
                    return LocalDate.of(date.getYear(), quarter * 3 + 2, 15);
                }
                case YEARS -> {
                    return LocalDate.of(date.getYear(), 7, 1);
                }
                case DECADE -> {
                    int decade = date.getYear() / 10 * 10;
                    return LocalDate.of(decade+5, 7, 1);
                }
                case SEMICENTURY -> {
                    int semicentury = date.getYear() / 50 * 50;
                    return LocalDate.of(semicentury+25, 7, 1);
                }
                case CENTURY -> {
                    int century = date.getYear() / 100 * 100;
                    return LocalDate.of(century+50, 7, 1);
                }
                default -> {
                    return date;
                }
            }
        }

        public LocalDate normalizeToEnd(LocalDate date)
        {
            switch (this) {
                case WEEKS -> {
                    long dayofweek = date.getLong(ChronoField.DAY_OF_WEEK);
                    return date.plusDays(7 - dayofweek);
                }
                case MONTHS -> {
                    int day = date.getMonth().length(date.isLeapYear());
                    return LocalDate.of(date.getYear(), date.getMonthValue(), day);
                }
                case QUARTERS -> {
                    int quarter = (date.getMonthValue()-1) / 3;
                    LocalDate tmp = LocalDate.of(date.getYear(), quarter * 3 + 3, 15);
                    int day = tmp.getMonth().length(date.isLeapYear());
                    return LocalDate.of(date.getYear(), quarter * 3 + 3, day);
                }
                case YEARS -> {
                    return LocalDate.of(date.getYear(), 12, 31);
                }
                case DECADE -> {
                    int decade = date.getYear() / 10 * 10;
                    return LocalDate.of(decade+9, 12, 31);
                }
                case SEMICENTURY -> {
                    int semicentury = date.getYear() / 50 * 50;
                    return LocalDate.of(semicentury+49, 12, 31);
                }
                case CENTURY -> {
                    int century = date.getYear() / 100 * 100;
                    return LocalDate.of(century+99, 12, 31);
                }
                default -> {
                    return date;
                }
            }
        }

        public LocalDate nextInterval(LocalDate date)
        {
            return date.plus(period);
        }
    }

    private Text findTextNode(LocalDate value)
    {
        String label = getTickMarkLabel(value);
        
        for (Node node : getChildrenUnmodifiable()) {
            if (node instanceof Text textNode) {
                if (textNode.getText().equals(label))
                    return textNode;
            }
        }
        return null;
    }
}
