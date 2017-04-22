package com.abuseret.logistica.free;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.abuseret.logistica.MainActivity;
import com.abuseret.logistica.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.abuseret.logistica.util.Util.atPosition;
import static com.abuseret.logistica.util.Util.mYswipeRight;
import static com.abuseret.logistica.util.Util.pauseTestFor;
import static com.abuseret.logistica.util.Util.setNumberPicker;
import static com.abuseret.logistica.util.Util.withRecyclerView;
import static org.hamcrest.Matchers.allOf;

//import android.support.test.uiautomator.UiDevice;


@RunWith(AndroidJUnit4.class)

public class ZDeliveryTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(
            MainActivity.class,true,true);
    @Test
    public void verifyDeliver() throws InterruptedException {
        // Select the picking Order Frame
        onView(withText("Delivery")).perform(click());



        // Select the first Picking
        onView(allOf(withId(R.id.pickingNumberOrderCard), withText("1"))).perform(click());

        // SwipeUP to see the adapter
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(swipeUp());

        // **                       First Order                                **//
        onView(allOf(withId(R.id.customOrderInpickingOrder_list),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Swipe the screen up
        onView(allOf(withId(R.id.IVA),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.IVA),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.IVA),isDisplayed())).perform(swipeUp());


        // Select the first custom Order
        onView(allOf(withId(R.id.product_list_customOrder),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.button1)).perform(click());


        // Swipe the screen up

        // this swipe can made errors. We want to see the second item of the recylerVier but
        // scrolltoPosition(position) is not working.
        // also withRecyclerView doesnt work if the item is not Visible.
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());

//        onView(allOf(withId(R.id.product_list_customOrder),isDisplayed())).perform(swipeUp());
//        onView(allOf(withId(R.id.product_list_customOrder),isDisplayed())).perform(swipeUp());
//        onView(allOf(withId(R.id.product_list_customOrder),isDisplayed())).perform(swipeUp());
//        onView(allOf(withId(R.id.product_list_customOrder),isDisplayed())).perform(swipeUp());

        // Select the SECOND product
        onView(withId(R.id.product_list_customOrder)).perform(scrollToPosition(1));
        onView(withRecyclerView(R.id.product_list_customOrder).atPosition(1)).perform(click());

//        onView(withId(R.id.product_list_customOrder)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));


//        onView(withId(R.id.nombreProducto))
//                .check(matches(withText("Productos 4")))
//                .check(matches(isDisplayed()))
//                .perform(click());



        onView(withId(R.id.numberPicker1)).perform(setNumberPicker(15));
        onView(withId(R.id.button1)).perform(click());

//        onView(withId(R.id.montoToal)).check(matches(withText("Monto Total:$24,372.00-$24,372.00")));

        onView(withId(R.id.montoToal)).check(matches(withText("Monto Total:$3,249.60-$3,249.60")));
        onView(withId(R.id.montoToalDelivery)).check(matches(withText("Monto Total Delivery:$2,741.85-$2,741.85")));

        pressBack();





        // **                       Second Order                                **//




        // SwipeUP to see the adapter
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(swipeUp());

        // Select the first custom Order
//        onView(allOf(withId(R.id.customOrderInpickingOrder_list),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
//        onView(withRecyclerView(R.id.customOrderInpickingOrder_list).atPosition(1)).perform(click());
        onView(withText("Juan1")).perform(click());

        // Swipe the screen up
        onView(allOf(withId(R.id.montoToalDelivery),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.montoToalDelivery),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.montoToalDelivery),isDisplayed())).perform(swipeUp());


        // Select the first custom Order
        onView(allOf(withId(R.id.product_list_customOrder),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.button1)).perform(click());


        // Swipe the screen up

        // this swipe can made errors. We want to see the second item of the recylerVier but
        // scrolltoPosition(position) is not working.
        // also withRecyclerView doesnt work if the item is not Visible.
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());
        onView(withId(R.id.montoToalDelivery)).perform( swipeUp());

//        onView(allOf(withId(R.id.product_list_customOrder),isDisplayed())).perform(swipeUp());
//        onView(allOf(withId(R.id.product_list_customOrder),isDisplayed())).perform(swipeUp());
//        onView(allOf(withId(R.id.product_list_customOrder),isDisplayed())).perform(swipeUp());
//        onView(allOf(withId(R.id.product_list_customOrder),isDisplayed())).perform(swipeUp());

        // Select the SECOND custom Order
        onView(withId(R.id.product_list_customOrder)).perform(scrollToPosition(1));
        onView(withRecyclerView(R.id.product_list_customOrder).atPosition(1)).perform(click());

//        onView(withId(R.id.product_list_customOrder)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));


//        onView(withId(R.id.nombreProducto))
//                .check(matches(withText("Productos 4")))
//                .check(matches(isDisplayed()))
//                .perform(click());



        onView(withId(R.id.numberPicker1)).perform(setNumberPicker(15));
        onView(withId(R.id.button1)).perform(click());

        onView(withId(R.id.montoToal)).check(matches(withText("Monto Total:$24,372.00-$24,372.00")));
        onView(withId(R.id.montoToalDelivery)).check(matches(withText("Monto Total Delivery:$13,404.60-$13,404.60")));

        pressBack();



        // Verifica totales posicion 0
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(0, hasDescendant(withText("Productos 2")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(0, hasDescendant(withText("200")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(0, hasDescendant(withText("200")))));


        // Verifica totales posicion 1
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(1, hasDescendant(withText("Productos 3")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(1, hasDescendant(withText("165")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(1, hasDescendant(withText("160")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(1, hasDescendant(withText("30")))));


        // Verifica totales posicion 2
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(2, hasDescendant(withText("Productos 4")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(2, hasDescendant(withText("20")))));
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed()))
                .check(matches(atPosition(2, hasDescendant(withText("15")))));



        // SwipeUP to see the adapter
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(swipeUp());
        onView(allOf(withId(R.id.total_products_pickingOrder),isDisplayed())).perform(swipeUp());
        pauseTestFor(5000);

        //Close the order
        onView(allOf(withId(R.id.customOrderInpickingOrder_list),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(0,mYswipeRight()));
        onView(allOf(withId(R.id.customOrderInpickingOrder_list),isDisplayed())).perform(RecyclerViewActions.actionOnItemAtPosition(1,mYswipeRight()));


    }






}