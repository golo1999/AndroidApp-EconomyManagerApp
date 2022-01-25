package com.example.economy_manager.utility;

import org.junit.Assert;
import org.junit.Test;

public class MyCustomMethodsTest {
    // public static boolean stringIsNotEmpty(final @NonNull String enteredString)

    @Test
    public void stringEqualsAlex() {
        Assert.assertTrue(MyCustomMethods.stringIsNotEmpty("Alex"));
    }

    @Test
    public void stringEqualsNothing() {
        Assert.assertFalse(MyCustomMethods.stringIsNotEmpty(""));
    }

    @Test
    public void stringEqualsWhitespace() {
        Assert.assertFalse(MyCustomMethods.stringIsNotEmpty(" "));
    }

    // public static int nameIsValid(final String name)

    @Test
    public void nameEqualsAnne() {
        Assert.assertEquals(1, MyCustomMethods.nameIsValid("Anne"));
    }

    @Test
    public void nameEqualsAnneOne() {
        Assert.assertEquals(-1, MyCustomMethods.nameIsValid("Anne1"));
    }

    @Test
    public void nameEqualsLi() {
        Assert.assertEquals(1, MyCustomMethods.nameIsValid("Li"));
    }

    @Test
    public void nameEqualsNothing() {
        Assert.assertEquals(0, MyCustomMethods.nameIsValid(""));
    }

    @Test
    public void nameEqualsX() {
        Assert.assertEquals(0, MyCustomMethods.nameIsValid("X"));
    }
}