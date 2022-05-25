package com.example.economy_manager.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.economy_manager.model.MyCustomTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RunWith(MockitoJUnitRunner.class)
public class MyCustomMethodsUnitTest {

    private LocalDateTime currentDateTime;

    @Before
    public void initializeVariables() {
        currentDateTime = LocalDateTime.now();
    }

    @Test
    public void stringIsNotEmpty() {
        assertTrue(MyCustomMethods.stringIsNotEmpty("NOT_EMPTY_STRING"));
        assertFalse(MyCustomMethods.stringIsNotEmpty(""));
        assertFalse(MyCustomMethods.stringIsNotEmpty(" "));
        assertFalse(MyCustomMethods.stringIsNotEmpty(null));
    }

    @Test
    public void getCurrencySymbolFromCurrencyName() {
        final String[] currencyNamesArray =
                {"AUD", "BRL", "CAD", "CHF", "CNY", "EUR", "GBP", "INR", "JPY", "RON", "RUB", "USD", "", null};
        final String[] currencySymbolsArray =
                {"A$", "R$", "C$", "CHF", "元", "€", "£", "₹", "¥", "RON", "₽", "$", "$", "$"};
        int counter = -1;

        for (final String currencyName : currencyNamesArray) {
            ++counter;
            assertEquals(MyCustomMethods.getCurrencySymbolFromCurrencyName(currencyName),
                    currencySymbolsArray[counter]);
        }
    }

    @Test
    public void getFormattedDate() {
        assertEquals(MyCustomMethods.getFormattedDate(LocalDate.of(2014, 1, 1)),
                "Wednesday, January 1, 2014");
        assertEquals(MyCustomMethods.getFormattedDate(null), MyCustomMethods.getFormattedDate(LocalDate.now()));
    }

    @Test(expected = DateTimeException.class)
    public void getFormattedDateMonthOfYearException() {
        MyCustomMethods.getFormattedDate(LocalDate.of(2022, 0, 1));
    }

    @Test(expected = DateTimeException.class)
    public void getFormattedDateDayOfMonthException() {
        MyCustomMethods.getFormattedDate(LocalDate.of(2019, 1, 0));
    }

    @Test
    public void nameIsValid() {
        final String[] namesArray = {"John", "Michael J", null, "B", "0X", "Z..", "Jack1", "Anne ", "Marie F.", ""};
        final int[] isExpectedResult = {1, 1, -1, 0, -1, -1, -1, -1, -1, 0};
        int counter = -1;

        for (final String name : namesArray) {
            ++counter;
            assertEquals(MyCustomMethods.nameIsValid(name), isExpectedResult[counter]);
        }
    }

    @Test
    public void transactionWasMadeInTheLastWeekMyCustomTime() {
        final int currentYear = currentDateTime.getYear();
        final int currentMonth = currentDateTime.getMonthValue();
        final int currentDay = currentDateTime.getDayOfMonth();
        final int currentHour = currentDateTime.getHour();
        final int currentMinute = currentDateTime.getMinute();
        final int currentSecond = currentDateTime.getSecond();
        final MyCustomTime currentCustomTime =
                new MyCustomTime(currentYear, currentMonth, currentDay, currentHour, currentMinute, currentSecond);
        final MyCustomTime currentCustomTimeWithEmptyNames =
                new MyCustomTime(currentYear, currentMonth, "", currentDay, "", currentHour,
                        currentMinute, currentSecond);

        assertTrue(MyCustomMethods.transactionWasMadeInTheLastWeek(currentCustomTime));
        assertFalse(MyCustomMethods.transactionWasMadeInTheLastWeek((MyCustomTime) null));
        assertTrue(MyCustomMethods.transactionWasMadeInTheLastWeek(currentCustomTimeWithEmptyNames));
    }

    @Test(expected = DateTimeException.class)
    public void transactionWasMadeInTheLastWeekMyCustomTimeMonthOfYearException() {
        MyCustomMethods.transactionWasMadeInTheLastWeek(new MyCustomTime());
    }

    @Test(expected = DateTimeException.class)
    public void transactionWasMadeInTheLastWeekMyCustomTimeDayOfMonthException() {
        MyCustomMethods.transactionWasMadeInTheLastWeek(new MyCustomTime(2016, 1, 0, 0, 0, 0));
    }

    @Test
    public void transactionWasMadeInTheLastWeekLocalDateTime() {
        assertTrue(MyCustomMethods.transactionWasMadeInTheLastWeek(currentDateTime));
        assertFalse(MyCustomMethods.transactionWasMadeInTheLastWeek(currentDateTime.plusDays(1)));
        assertTrue(MyCustomMethods.transactionWasMadeInTheLastWeek(currentDateTime.minusDays(1)));
        assertTrue(MyCustomMethods.transactionWasMadeInTheLastWeek(currentDateTime.minusWeeks(1)));
        assertFalse(MyCustomMethods.transactionWasMadeInTheLastWeek(currentDateTime.minusDays(8)));
        assertFalse(MyCustomMethods.transactionWasMadeInTheLastWeek((LocalDateTime) null));
    }
}