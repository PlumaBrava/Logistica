package com.abuseret.logistica;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.abuseret.logistica.util.Util.atPosition;
import static com.abuseret.logistica.util.Util.setNumberPicker;
import static org.hamcrest.Matchers.allOf;

//import android.support.test.uiautomator.UiDevice;


@RunWith(AndroidJUnit4.class)

public class PinckingTestVerification {


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(
            MainActivity.class,true,true);
    @Test
    public void verifyPickingOrder1() throws InterruptedException {
        // Select the picking Order Frame
        onView(withText("Picking")).perform(click());



        // Select the first Picking
        onView(allOf(withId(R.id.pickingNumberOrderCard), withText("1"))).perform(click());

        // Verify Position 0
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(0, hasDescendant(withText("Productos 2")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(0, hasDescendant(withText("200")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.numberPicker1)).perform(setNumberPicker(201));
        onView(withId(R.id.button1)).perform(click());
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeUp()));

        // Verify Position 1
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(1, hasDescendant(withText("Productos 3")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(1, hasDescendant(withText("165")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.numberPicker1)).perform(setNumberPicker(160));
        onView(withId(R.id.button1)).perform(click());
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(1, swipeUp()));


        // Verify Position 2
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(2, hasDescendant(withText("Productos 4")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(2, hasDescendant(withText("20")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.numberPicker1)).perform(setNumberPicker(20));
        onView(withId(R.id.button1)).perform(click());
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(2, swipeDown()));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(2, swipeDown()));


        // close de picking order
        onView(allOf(withId(R.id.save_picking_Button),isDisplayed())).perform(click());

        // Send the Picking Order to deliver
        onView(allOf(withId(R.id.pickingNumberOrderCard), withText("1"))).perform(swipeRight());

    }






}