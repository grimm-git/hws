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

public class LocalDateAxisStub
extends LocalDateAxis
{
    public static LocalDateAxis.AxisTick findInterval(long spanInDays, int cntTicks)
    {
        return LocalDateAxis.AxisTick.findInterval(spanInDays, cntTicks);
    }
    
    public static LocalDateAxis.AxisTick valueOf(int idx)
    {
        return LocalDateAxis.AxisTick.valueOf(idx);
    }

    public LocalDateAxis.AxisTick getIntervalDays()
    {
        return LocalDateAxis.AxisTick.DAYS;
    }

    public LocalDateAxis.AxisTick getIntervalWeeks()
    {
        return LocalDateAxis.AxisTick.WEEKS;
    }

    public LocalDateAxis.AxisTick getIntervalMonths()
    {
        return LocalDateAxis.AxisTick.MONTHS;
    }

    public LocalDateAxis.AxisTick getIntervalQuarters()
    {
        return LocalDateAxis.AxisTick.QUARTERS;
    }

    public LocalDateAxis.AxisTick getIntervalYears()
    {
        return LocalDateAxis.AxisTick.YEARS;
    }
}
