package com.abuseret.logistica;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.abuseret.logistica.util.Util.mYswipeRight;
import static com.abuseret.logistica.util.Util.pauseTestFor;
import static org.hamcrest.Matchers.allOf;

//import android.support.test.uiautomator.UiDevice;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
//public class ApplicationTest extends ApplicationTestCase<Application> {
//    public ApplicationTest() {
//        super(Application.class);
//    }
//}

@RunWith(AndroidJUnit4.class)
//@LargeTest
//public class ApplicationTest extends ApplicationTestCase<Application> {
public class PinckingTest {


//    public ApplicationTest() {
//        super(Application.class);
//    }
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(
            MainActivity.class,true,true);
    @Test
    public void createPickingOrder1() throws InterruptedException {
        // Select the picking Order Frame
        onView(withText("Picking")).perform(click());
        // Picking Order Creation.
        pauseTestFor(5000);
        onView(withId(R.id.fab)).perform(click());


        pauseTestFor(10000);
        pressBack();
        pauseTestFor(3000);
        // Select the first Picking
//        onView(withId(R.id.pickingOrder_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(allOf(withId(R.id.pickingNumberOrderCard), withText("1"))).perform(click());



        // Move to Custom Orders Page
            onView(withText("Custom Orders")).perform(click());

        // SwipeUP to see the adapter
//        onView(allOf(withId(R.id.total_products_customOrder), withText("1"))).perform(click());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());
        onView(withText("Productos 4")).perform(swipeUp());


//        onView(withId(R.id.total_products_customOrder)).perform(swipeUp());
//        onView(withId(R.id.total_products_customOrder)).perform(swipeUp());
//        onView(withId(R.id.total_products_customOrder)).perform(swipeUp());
//        onView(withId(R.id.total_products_customOrder)).perform(swipeUp());
//        onView(withId(R.id.total_products_customOrder)).perform(swipeUp());

        //  Select orders to the Picking Order.
            pauseTestFor(1000);
            onView(withId(R.id.customorder_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, mYswipeRight()));
            pauseTestFor(1000);
            onView(withId(R.id.customorder_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, mYswipeRight()));
            pauseTestFor(1000);

         //Return to the picking Page
        onView(withText("Picking")).perform(click());
        pauseTestFor(1000);


        // verify the Prouctus Loaded

//        onData(withValue(27))
//                .inAdapterView(withId(R.id.list))
//                .perform(click());
//
//
//        onView(withId(R.id.nombreProducto))
//                .check(matches(withText("Productos 2")))
//                .check(matches(isDisplayed()));

//        onView(allOf( withId(R.id.nombreProducto), isDisplayed()))
//                .perform(scrollTo(hasDescendant(withText("Productos 2"))), click());


//        onView(withId(R.id.total_products_pickingOrder))
//                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));


//        onView(withId(R.id.total_products_pickingOrder))
//                .check(matches(isDisplayed()));
//
//        onView(withId(R.id.total_products_pickingOrder))
//                .check(matches(isDisplayed()))
//                .perform(RecyclerViewActions.actionOnItemAtPosition(0, mYswipeRight()));
//
//
//        onData(withId(R.id.total_products_pickingOrder))
////                .inAdapterView(withId(R.id.cantidadPicking))
//
//                .atPosition(0)
////                .check(matches(isDisplayed()))
//                .perform(click());

//        onView(withId(R.id.total_products_pickingOrder))
//                .check(matches(hasDescendant(withText("200"))));
//        onView(withId(R.id.total_products_pickingOrder))
//                .check(matches(hasDescendant(withText("165"))));
//        onView(withId(R.id.total_products_pickingOrder))
//                .check(matches(hasDescendant(withText("200"))));

        onView(withText("Productos 2")).check(matches(isDisplayed()));
        onView(withText("Productos 3")).check(matches(isDisplayed()));
        onView(withText("Productos 4")).check(matches(isDisplayed()));

//        // verify the Quantities Loaded

//        onView(allOf(withId(R.id.pickingNumberOrderCard), withText("1"))).perform(click());




//        onView(withText("165")).check(matches(isDisplayed()));
//        onView(withText("20")).check(matches(isDisplayed()));

//        // verify the Total Price
//        onView(withText("$2,031.00")).check(matches(isDisplayed()));
//        onView(withText("$13,404.60")).check(matches(isDisplayed()));
//        onView(withText("$12,186.00")).check(matches(isDisplayed()));


    }






}