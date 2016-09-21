package com.nextnut.logistica;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.NumberPicker;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.matchers.JUnitMatchers.containsString;

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
        onView(withId(R.id.fab)).perform(click());
        // Select the first Picking
//        onView(withId(R.id.pickingOrder_list)).perform(RecyclerViewActions.scrollToPosition(0));
//        onView(withText("1")).perform( click());


        onView(allOf(withId(R.id.pickingNumberOrderCard),withText("4"))).perform(click());

//        onView(withId(R.id.pickingOrder_list))
//                .perform(RecyclerViewActions.actionOnItem(
//                        hasDescendant(withText("4")), click()));

        // Select the orders
//        onView(withId(R.id.pickingNumberOrderCard))
//                .check(matches(withText(containsString("4"))));
//                .perform(click());
        onView(withText("CUSTOM ORDERS")).perform(click());

        pauseTestFor(1000);
        onView(withId(R.id.customorder_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, mYswipeRight()));
        pauseTestFor(1000);
        onView(withId(R.id.customorder_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, mYswipeRight()));
        pauseTestFor(1000);
        // Retur to the picking Order Frame
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


        onView(withId(R.id.total_products_pickingOrder))
                .check(matches(isDisplayed()));

        onView(withId(R.id.total_products_pickingOrder))
                .check(matches(isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, mYswipeRight()));


        onData(withId(R.id.total_products_pickingOrder))
//                .inAdapterView(withId(R.id.cantidadPicking))

                .atPosition(0)
//                .check(matches(isDisplayed()))
                .perform(click());

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
//        onView(withText("200")).check(matches(isDisplayed()));
//        onView(withText("165")).check(matches(isDisplayed()));
//        onView(withText("20")).check(matches(isDisplayed()));

        // verify the Total Price
        onView(withText("$2,031.00")).check(matches(isDisplayed()));
        onView(withText("$13,404.60")).check(matches(isDisplayed()));
        onView(withText("$12,186.00")).check(matches(isDisplayed()));


    }



public static ViewAction mYswipeRight() {
    return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER,
            GeneralLocation.CENTER_RIGHT, Press.FINGER);
}

    public static ViewAction setNumberPicker(final int number) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);

            }

            @Override
            public String getDescription() {
                return "Set the passed number into the NumberPicker";
            }

            @Override
            public void perform(UiController uiController, View view) {
                NumberPicker tp = (NumberPicker) view;
                tp.setValue(number);

            }
        };
    }

    private void pauseTestFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}