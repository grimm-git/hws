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
package hws.testhelper;

import hws.gui.charts.skins.RangeControlSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.control.ScrollBar;

/**
 *
 * @author grimm
 */
public class TestHelper
{
    public static boolean isEqual(double A, double B, double precission)
    {
        double val = Math.abs(A - B);
        return val < precission;
    }
 
    /**
     *
     * @param <T>
     * @param props
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> packInList(T... props)
    {
        return new ArrayList<>(Arrays.asList(props));
    }

    public static RangeControlSet createRangeControlSet()
    {
        ScrollBar bar1 = new ScrollBar();
        ScrollBar bar2 = new ScrollBar();
        ScrollBar bar3 = new ScrollBar();
        return new RangeControlSet(bar1, bar2, bar3);
    }

}
