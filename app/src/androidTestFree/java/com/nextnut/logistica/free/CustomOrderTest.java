package com.nextnut.logistica.free;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.nextnut.logistica.MainActivity;
import com.nextnut.logistica.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.nextnut.logistica.util.Util.pauseTestFor;
import static com.nextnut.logistica.util.Util.recyclerClick;
import static com.nextnut.logistica.util.Util.setNumberPicker;

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
public class CustomOrderTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(
            MainActivity.class, true, true);

    @Test
    public void createCustomOrderFree1() throws InterruptedException {

        // Custom Order Creation.
        pauseTestFor(5000);
        onView(withId(R.id.fab)).perform(click());
        pauseTestFor(10000);
        pressBack();
        pauseTestFor(5000);

        // /select the first customer.
        onView(withId(R.id.content_custom_selection)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        pauseTestFor(5000);
        // select Product button
        onView(withId(R.id.botonSelecionProdcuto)).perform(click());
        // Select the first product product of the list
        onView(withId(R.id.content_product_selection)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));


        // Select the first product of the list
        onView(withId(R.id.botonSelecionProdcuto)).perform(click());
        onView(withId(R.id.content_product_selection)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // SwipeUP to see the adapter
        onView(withId(R.id.montoToal)).perform(swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform(swipeUp());
        onView(withId(R.id.montoToal)).perform(swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform(swipeUp());
        onView(withId(R.id.montoToal)).perform(swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform(swipeUp());
        onView(withId(R.id.montoToal)).perform(swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform(swipeUp());
        onView(withId(R.id.montoToal)).perform(swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform(swipeUp());


        // set  15 for quantity of first podruct
        onView(withId(R.id.product_list_customOrder))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("Productos 3")), click()));
        onView(withId(R.id.numberPicker1)).perform(setNumberPicker(15));
        onView(withId(R.id.button1)).perform(click());

        // set 20 for quantity of the second product.
        onView(withId(R.id.product_list_customOrder))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("Productos 4")), click()));
        onView(withId(R.id.numberPicker1)).perform(setNumberPicker(20));
        onView(withId(R.id.button1)).perform(click());


        // Verify totales
        onView(withId(R.id.cantidadTotal)).check(matches(withText("cantidad:2")));
        onView(withId(R.id.montoToal)).check(matches(withText("Monto Total:$3,249.60-$3,249.60")));
        pressBack();




        // Custom Order Creation.

        onView(withId(R.id.fab)).perform(click());
        pauseTestFor(10000);
        pressBack();
        // /select the second customer.
        onView(withId(R.id.content_custom_selection)).perform(RecyclerViewActions.actionOnItemAtPosition(1, recyclerClick()));
        pauseTestFor(5000);
        // select Product button
        onView(withId(R.id.botonSelecionProdcuto)).perform(click());
        // Select the Second product product of the list
        onView(withId(R.id.content_product_selection)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));


        // Select the second product of the list
        onView(withId(R.id.botonSelecionProdcuto)).perform(click());
        onView(withId(R.id.content_product_selection)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // SwipeUP to see the adapter
        onView(withId(R.id.montoToal)).perform(swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform(swipeUp());
        onView(withId(R.id.montoToal)).perform(swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform(swipeUp());
        onView(withId(R.id.montoToal)).perform(swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform(swipeUp());
        onView(withId(R.id.montoToal)).perform(swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform(swipeUp());
        onView(withId(R.id.montoToal)).perform(swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform(swipeUp());


        // set  15 for quantity of first podruct
        onView(withId(R.id.product_list_customOrder))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("Productos 3")), click()));
        onView(withId(R.id.numberPicker1)).perform(setNumberPicker(150));
        onView(withId(R.id.button1)).perform(click());

        // set 20 for quantity of the second product.
        onView(withId(R.id.product_list_customOrder))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("Productos 2")), click()));
        onView(withId(R.id.numberPicker1)).perform(setNumberPicker(200));
        onView(withId(R.id.button1)).perform(click());


        // Verify totales
        onView(withId(R.id.cantidadTotal)).check(matches(withText("cantidad:2")));
        onView(withId(R.id.montoToal)).check(matches(withText("Monto Total:$24,372.00-$24,372.00")));
        pressBack();



        // verify the Prouctus Loaded

        onView(withText("Productos 2")).check(matches(isDisplayed()));
        onView(withText("Productos 3")).check(matches(isDisplayed()));
        onView(withText("Productos 4")).check(matches(isDisplayed()));

        // verify the Quantities Loaded
        onView(withText("200")).check(matches(isDisplayed()));
        onView(withText("165")).check(matches(isDisplayed()));
        onView(withText("20")).check(matches(isDisplayed()));

        // verify the Total Price
        onView(withText("$2,031.00")).check(matches(isDisplayed()));
        onView(withText("$13,404.60")).check(matches(isDisplayed()));
        onView(withText("$12,186.00")).check(matches(isDisplayed()));

    }






}