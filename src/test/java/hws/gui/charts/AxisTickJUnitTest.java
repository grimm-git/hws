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
import static java.time.temporal.ChronoUnit.DAYS;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
public class AxisTickJUnitTest
{
    public AxisTickJUnitTest() {
    }

    @Test
    public void testAxisTick_findInterval()
    {
        LocalDateAxis.AxisTick foundInterval, expected;
        LocalDateAxisStub instance = new LocalDateAxisStub();

        LocalDate lowerDate = LocalDate.of(2023, 1, 1);
        LocalDate upperDate = LocalDate.of(2023, 12, 31);

        long spanInDays = DAYS.between(lowerDate, upperDate);

        foundInterval = LocalDateAxisStub.findInterval(spanInDays, 7);
        expected = instance.getIntervalQuarters();
        
        assertEquals(expected, foundInterval);
        
    }

    @Test
    public void testAxisTick_ValueOf()
    {
        int intervalValue;
        LocalDateAxis.AxisTick interval;
        LocalDateAxisStub instance = new LocalDateAxisStub();

        interval = instance.getIntervalDays();
        intervalValue = instance.getIntervalDays().ordinal();

        assertEquals(interval, LocalDateAxisStub.valueOf(intervalValue));
        assertEquals(interval, LocalDateAxisStub.valueOf(42));

        interval = instance.getIntervalQuarters();
        intervalValue = instance.getIntervalQuarters().ordinal();

        assertEquals(interval, LocalDateAxisStub.valueOf(intervalValue));
    }
    
    @Test
    public void testAxisTick_Days_Interval()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalDays();
        
        inputDate = LocalDate.of(2023, 6, 26);
        expectedDate = LocalDate.of(2023, 6, 27);  // Next Day
        assertEquals(expectedDate, interval.nextInterval(inputDate));
    }

    @Test
    public void testAxisTick_Days()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalDays();
        
        inputDate = LocalDate.of(2023, 6, 26);    
        expectedDate = LocalDate.of(2023, 6, 26); 
        assertEquals(expectedDate, interval.normalizeToBegin(inputDate));
        assertEquals(expectedDate, interval.normalizeToCenter(inputDate));
        assertEquals(expectedDate, interval.normalizeToEnd(inputDate));
    }

    @Test
    public void testAxisTick_Weeks_Interval()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalWeeks();

        inputDate = LocalDate.of(2023, 6, 26);  // Monday
        expectedDate = LocalDate.of(2023, 7, 3);  // Next Monday
        assertEquals(expectedDate, interval.nextInterval(inputDate));
    }

    @Test
    public void testAxisTick_Weeks_Begin()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalWeeks();
        
        inputDate = LocalDate.of(2023, 6, 26);     // Monday
        expectedDate = LocalDate.of(2023, 6, 26);  // Monday
        assertEquals(expectedDate, interval.normalizeToBegin(inputDate));

        inputDate = LocalDate.of(2023, 6, 30);     // Friday
        expectedDate = LocalDate.of(2023, 6, 26);  // Monday
        assertEquals(expectedDate, interval.normalizeToBegin(inputDate));
    }

    @Test
    public void testAxisTick_Weeks_Center()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalWeeks();
        
        inputDate = LocalDate.of(2023, 6, 26);     // Monday
        expectedDate = LocalDate.of(2023, 6, 29);  // Thurstday
        assertEquals(expectedDate, interval.normalizeToCenter(inputDate));

        inputDate = LocalDate.of(2023, 6, 30);     // Friday
        expectedDate = LocalDate.of(2023, 6, 29);  // Thursday
        assertEquals(expectedDate, interval.normalizeToCenter(inputDate));
    }

    @Test
    public void testAxisTick_Weeks_End()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalWeeks();
        
        inputDate = LocalDate.of(2023, 6, 26);     // Monday
        expectedDate = LocalDate.of(2023, 7, 2 );  // Sunday
        assertEquals(expectedDate, interval.normalizeToEnd(inputDate));

        inputDate = LocalDate.of(2023, 6, 30);     // Friday
        expectedDate = LocalDate.of(2023, 7, 2);   // Sunday
        assertEquals(expectedDate, interval.normalizeToEnd(inputDate));
    }

    @Test
    public void testAxisTick_Months_Interval()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalMonths();
        
        inputDate = LocalDate.of(2023, 11, 1); 
        expectedDate = LocalDate.of(2023, 12, 1);  // Next Month
        assertEquals(expectedDate, interval.nextInterval(inputDate));
    }

    @Test
    public void testAxisTick_Months_Begin()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalMonths();
        
        inputDate = LocalDate.of(2023, 6, 26);     
        expectedDate = LocalDate.of(2023, 6, 1); 
        assertEquals(expectedDate, interval.normalizeToBegin(inputDate));

        inputDate = LocalDate.of(2023, 11, 15);   
        expectedDate = LocalDate.of(2023, 11, 1); 
        assertEquals(expectedDate, interval.normalizeToBegin(inputDate));
    }

    @Test
    public void testAxisTick_Months_Center()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalMonths();
        
        inputDate = LocalDate.of(2023, 6, 26);     
        expectedDate = LocalDate.of(2023, 6, 15); 
        assertEquals(expectedDate, interval.normalizeToCenter(inputDate));

        inputDate = LocalDate.of(2023, 11, 20);   
        expectedDate = LocalDate.of(2023, 11, 15); 
        assertEquals(expectedDate, interval.normalizeToCenter(inputDate));
    }

    @Test
    public void testAxisTick_Months_End()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalMonths();
        
        inputDate = LocalDate.of(2023, 6, 26);     
        expectedDate = LocalDate.of(2023, 6, 30); 
        assertEquals(expectedDate, interval.normalizeToEnd(inputDate));

        inputDate = LocalDate.of(2023, 10, 20);   
        expectedDate = LocalDate.of(2023, 10, 31); 
        assertEquals(expectedDate, interval.normalizeToEnd(inputDate));
    }

    @Test
    public void testAxisTick_Quarter1()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalQuarters();
        
        inputDate = LocalDate.of(2023, 1, 26); 
        expectedDate = LocalDate.of(2023, 1, 1); 
        assertEquals(expectedDate, interval.normalizeToBegin(inputDate));

        inputDate = LocalDate.of(2023, 3, 26); 
        expectedDate = LocalDate.of(2023, 1, 1); 
        assertEquals(expectedDate, interval.normalizeToBegin(inputDate));

        inputDate = expectedDate;
        expectedDate = LocalDate.of(2023, 4, 1);  // Next Quarter
        assertEquals(expectedDate, interval.nextInterval(inputDate));
    }

    @Test
    public void testAxisTick_Quarter2()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalQuarters();
        
        inputDate = LocalDate.of(2023, 4, 12);
        expectedDate = LocalDate.of(2023, 5, 15); 
        assertEquals(expectedDate, interval.normalizeToCenter(inputDate));

        inputDate = LocalDate.of(2023, 6, 12);
        expectedDate = LocalDate.of(2023, 5, 15); 
        assertEquals(expectedDate, interval.normalizeToCenter(inputDate));

        inputDate = LocalDate.of(2023, 4, 1);
        expectedDate = LocalDate.of(2023, 7, 1);  // Next Quarter
        assertEquals(expectedDate, interval.nextInterval(inputDate));
    }

    @Test
    public void testAxisTick_Quarter3()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalQuarters();
        
        inputDate = LocalDate.of(2023, 7, 19); 
        expectedDate = LocalDate.of(2023, 9, 30); 
        assertEquals(expectedDate, interval.normalizeToEnd(inputDate));

        inputDate = LocalDate.of(2023, 9, 19); 
        expectedDate = LocalDate.of(2023, 9, 30); 
        assertEquals(expectedDate, interval.normalizeToEnd(inputDate));

        inputDate = LocalDate.of(2023, 7, 1);
        expectedDate = LocalDate.of(2023, 10, 1);  // Next Quarter
        assertEquals(expectedDate, interval.nextInterval(inputDate));
    }

    @Test
    public void testAxisTick_Quarter4()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalQuarters();
        
        inputDate = LocalDate.of(2023, 10, 15); 
        expectedDate = LocalDate.of(2023, 10, 1); 
        assertEquals(expectedDate, interval.normalizeToBegin(inputDate));

        inputDate = LocalDate.of(2023, 12, 15); 
        expectedDate = LocalDate.of(2023, 11, 15); 
        assertEquals(expectedDate, interval.normalizeToCenter(inputDate));

        inputDate = LocalDate.of(2023, 10, 1);
        expectedDate = LocalDate.of(2024, 1, 1);  // Next Quarter
        assertEquals(expectedDate, interval.nextInterval(inputDate));
    }

    @Test
    public void testAxisTick_Year_Interval()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalYears();
        
        inputDate = LocalDate.of(2023, 1, 1);
        expectedDate = LocalDate.of(2024, 1, 1);  // Next Year
        assertEquals(expectedDate, interval.nextInterval(inputDate));
    }

    @Test
    public void testAxisTick_Year_Begin()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalYears();
        
        inputDate = LocalDate.of(2023, 6, 26);
        expectedDate = LocalDate.of(2023, 1, 1);
        assertEquals(expectedDate, interval.normalizeToBegin(inputDate));
    }

    @Test
    public void testAxisTick_Year_Center()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalYears();
        
        inputDate = LocalDate.of(2023, 6, 26);
        expectedDate = LocalDate.of(2023, 7, 1);
        assertEquals(expectedDate, interval.normalizeToCenter(inputDate));
    }

    @Test
    public void testAxisTick_Year_End()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalYears();
        
        inputDate = LocalDate.of(2023, 6, 26);
        expectedDate = LocalDate.of(2023, 12, 31);
        assertEquals(expectedDate, interval.normalizeToEnd(inputDate));
    }

    @Test
    public void testAxisTick_MonthLabel()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalMonths();
        
        inputDate = LocalDate.of(2023, 6, 23); 
        assertEquals("Jun\n2023", interval.getLabel(inputDate));
    }

    @Test
    public void testAxisTick_QuarterLabel()
    {
        LocalDate inputDate, expectedDate;
        LocalDateAxisStub instance = new LocalDateAxisStub();
        LocalDateAxis.AxisTick interval = instance.getIntervalQuarters();
        
        inputDate = LocalDate.of(2023, 1, 1); 
        assertEquals("Q1 23", interval.getLabel(inputDate));
    }
}
