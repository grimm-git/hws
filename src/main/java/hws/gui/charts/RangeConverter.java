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
import javafx.scene.chart.Axis;


/**
 * This Converter connects a chart axis with a range control set which consists out of three
 * ScrollBars
 * <ul>
 * <li>One for cntrolling the upper limit of the axis
 * <li>One for cntrolling the range (position and amount)
 * <li>One for cntrolling the lowerr limit of the axis
 * </ul>
 * The ScrollBars are all set in percent (0..100%) while the axis has its native data type<br>
 * The RangeConverter translates between both back and forth.
 *
 * @param <T>    Native Datatype of the axis
 * 
 * @author grimm
 */
public abstract class RangeConverter<T>
{
    protected final ArrayList<RangeControlSet> listControlSets = new ArrayList<>();
    
    /**
     * Constructor of the RangeConverter. This must be overloaded by the inherit class.<p>
     * For examle: If the axis is a x-axis so this list contains all x-values from the XYData
     * items from all series of the chart.
     * 
     * @param axis   Axis that should be linked with a range control set
     */
    protected RangeConverter(Axis<? extends T> axis) { }

    /**
     * Inform the RangeConverter about the data values of the maintained axis. The range will
     * automatically extracted from this list.
     * 
     * @param list  List of data values.
     */
    public abstract void updateData(List<T> list);
    
    /**
     * Link a specific RangeControlSet to the axis maintained by this RangeConverter. It is
     * possible to link several control sets to the same axis. They all will be handled in
     * parallel and fed with the same data. On the other side each RangeControlSet is individually
     * able to control the axis limits.
     * 
     * @param rangeCTRL   {@link RangeControlSet}
     */
    public abstract void link(RangeControlSet rangeCTRL);
    
    /**
     * Registered a RangeControlSet for interaction with the axis
     *
     * @param rangeCTRL   The RangeCOntrolSet to register
     * @see RangeControlSet
     */
    protected void registerControlSet(RangeControlSet rangeCTRL)
    {
        if (!listControlSets.contains(rangeCTRL))
            listControlSets.add(rangeCTRL);
    }
    
    /**
     * Unregister a RangeControlSet again
     *
     * @param rangeCTRL  The RangeCOntrolSet to unregister
     * @see RangeControlSet
     */
    protected void unregisterControlSet(RangeControlSet rangeCTRL)
    {
        listControlSets.remove(rangeCTRL);
    }

    /**
     * This method compares two Numbers within a certain precission. Numbers can contain
     * Floats and Doubles which simply cannot be compared with "==" or compare() because they
     * cannot contain all real numbers which lead to weird situations like 1.0 != 0.999999999999.
     * In this case Float cannot express the simple number 1. It just approximate it.<p>
     * This method takes care of such cases and delivers the correct result.
     * 
     * @param obj1
     * @param obj2
     * @return      true, if the two Numbers are equal
     */
    public static boolean areEqual(Number obj1, Number obj2)
    {
        if (obj1 == null) 
            return (obj2 == null);
            
        return Math.abs(obj1.doubleValue() - obj2.doubleValue()) <= 0.000001;
    }
}
