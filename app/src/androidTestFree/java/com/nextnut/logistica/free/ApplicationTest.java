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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.nextnut.logistica.util.Util.pauseTestFor;

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
public class ApplicationTest {


//    public ApplicationTest() {
//        super(Application.class);
//    }
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(
            MainActivity.class,true,true);
@Test
public void createCustom0() throws InterruptedException {
        pauseTestFor(4000);
    onView(withId(R.id.customs)).perform(click());

    onView(withId(R.id.fab_new)).perform(click());
//    pauseTestFor(4000);

        onView(withId(R.id.custom_name_text)).perform(typeText("Juan0"), closeSoftKeyboard());
        onView(withId(R.id.product_Lastname)).perform(typeText("Perez0"), closeSoftKeyboard());


        onView(withId(R.id.custom_delivery_address)).perform(typeText("Bulnes 2659"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(typeText("caba"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(swipeUp());

        onView(withId(R.id.custom_cuit)).perform(typeText("20-222222222-4"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(swipeUp());
        onView(withId(R.id.custom_iva)).perform(typeText("20"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(swipeUp());
        onView(withId(R.id.custom_special)).check(matches(isNotChecked())).perform(click()).check(matches(isChecked()));


        onView(withId(R.id.fab_save)).perform(click());


    }

    @Test
    public void createCustom1() throws InterruptedException {
        onView(withId(R.id.customs)).perform(click());
//        pauseTestFor(4000);
        onView(withId(R.id.fab_new)).perform(click());
//        pauseTestFor(4000);

        onView(withId(R.id.custom_name_text)).perform(typeText("Juan1"), closeSoftKeyboard());
        onView(withId(R.id.product_Lastname)).perform(typeText("Perez1"), closeSoftKeyboard());


        onView(withId(R.id.custom_delivery_address)).perform(typeText("Bulnes 2659"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(typeText("caba"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(swipeUp());

        onView(withId(R.id.custom_cuit)).perform(typeText("20-222222222-4"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(swipeUp());
        onView(withId(R.id.custom_iva)).perform(typeText("20"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(swipeUp());
        onView(withId(R.id.custom_special)).check(matches(isNotChecked())).perform(click()).check(matches(isChecked()));


        onView(withId(R.id.fab_save)).perform(click());



    }

    @Test
    public void verifyCustom0() throws InterruptedException {
//        pauseTestFor(3000); //wait to display the add
        onView(withId(R.id.customs)).perform(click());
//        pauseTestFor(5000); //wait to display the add

//        onData(withText("Juan0"))
//                .inAdapterView(withId(R.id.custom_list_content))
//                .perform(click());

        onView(withId(R.id.custom_list))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("Juan0")), click()));

//        onView(withId(R.id.custom_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

//        pauseTestFor(10000); //wait to display the add


        onView(withId(R.id.custom_name_text)).check(matches(withText("Juan0")));
        onView(withId(R.id.product_Lastname)).check(matches(withText("Perez0")));

//        onView(allOf(withId(R.id.place_description),withText("what"))).perform(click());

//        onView(withId(R.id.custom_list))
//                .check(matches(hasDescendant(withText("Juan0"))));



    }

    @Test
    public void verifyCustom1() throws InterruptedException {
        onView(withId(R.id.customs)).perform(click());
//        pauseTestFor(5000); //wait to display the add
        onView(withId(R.id.custom_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

//        pauseTestFor(10000); //wait to display the add


        onView(withId(R.id.custom_name_text)).check(matches(withText("Juan1")));
        onView(withId(R.id.product_Lastname)).check(matches(withText("Perez1")));
    }

    @Test
    public void createFiveProducts() throws InterruptedException {
        onView(withId(R.id.productos)).perform(click());
//        pauseTestFor(4000);
        int price =1917;
        int priceSpecial =2031;

        for(int i=0;i<5;i++) {

            onView(withId(R.id.fab_new)).perform(click());
            onView(withId(R.id.product_name_text)).perform(typeText("Product "+i), closeSoftKeyboard());
            onView(withId(R.id.product_price)).perform(typeText(String.valueOf(price*(i+1))), closeSoftKeyboard());
            onView(withId(R.id.product_pricespecial)).perform(typeText(String.valueOf(priceSpecial*(i+1))), closeSoftKeyboard());
            onView(withId(R.id.product_pricespecial)).perform(swipeUp());
            onView(withId(R.id.product_description)).perform(typeText("Description "+i), closeSoftKeyboard());

// Take Photo!

            onView(withId(R.id.product_imagen)).perform(click());
            onView(withText("Add Photo!")).inRoot(isDialog()).check(matches(isDisplayed()));
            onView(withText("Add Photo!")).perform(pressBack());
            onView(withId(R.id.fab_save)).perform(click());

        }

        // Click on the RecyclerView item at position 0
        onView(withId(R.id.product_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

//        pauseTestFor(4000); //wait to display the add

    }
    public void verifyProduc1() throws InterruptedException {
        onView(withId(R.id.productos)).perform(click());
//        pauseTestFor(5000); //wait to display the add
        onView(withId(R.id.product_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

//        pauseTestFor(10000); //wait to display the add


        onView(withId(R.id.product_name_text)).check(matches(withText("Product 1")));

    }





}