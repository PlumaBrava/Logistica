package com.nextnut.logistica;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
//import android.support.test.uiautomator.UiDevice;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.supportsInputMethods;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

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
//
//    @Rule
//    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);
//

//    @Test
//    public void InstructionsIsNotNull(){
//        //  Verify instructions is a not null text
//
//        onView(withId(R.id.instructions_text_view)).check(matches(notNullValue()));
//
//    }

//    @Before
//    public void init(){
//        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
//        Point[] coordinates = new Point[4];
//        coordinates[0] = new Point(248, 1520);
//        coordinates[1] = new Point(248, 929);
//        coordinates[2] = new Point(796, 1520);
//        coordinates[3] = new Point(796, 929);
//        try {
//            if (!uiDevice.isScreenOn()) {
//                uiDevice.wakeUp();
//                uiDevice.swipe(coordinates, 10);
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
@Test
public void createCustom() throws InterruptedException {
    onView(withId(R.id.customs)).perform(click());
    pauseTestFor(4000);
    onView(withId(R.id.fab_new)).perform(click());
    pauseTestFor(4000);

        onView(withId(R.id.custom_name_text)).perform(typeText("juass"), closeSoftKeyboard());
        onView(withId(R.id.product_Lastname)).perform(typeText("perezss"), closeSoftKeyboard());


        onView(withId(R.id.custom_delivery_address)).perform(typeText("Bulnes 2659"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(typeText("caba"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(swipeUp());

        onView(withId(R.id.custom_cuit)).perform(typeText("20-24123456-4"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(swipeUp());
        onView(withId(R.id.custom_iva)).perform(typeText("20"), closeSoftKeyboard());
        onView(withId(R.id.custom_city)).perform(swipeUp());
        onView(withId(R.id.custom_special)).check(matches(isNotChecked())).perform(click()).check(matches(isChecked()));


        onView(withId(R.id.fab_save)).perform(click());
        pauseTestFor(10000); //wait to display the add
//
//
//        onView(withClassName(endsWith("View"))).perform(pressBack());//go back to
//        pauseTestFor(10000); //wait to backend responds
//
//        //Verify the answer is not null
//        onView(withId(R.id.libraryText)).check(matches(notNullValue()));
//
//        //Verify that the asyncTask does not return an error. Example is backend is off.
//        String errorText = "error:";
//        onView(withId(R.id.libraryText)).check(matches(not(withText(startsWith(errorText)))));
//        //Verify that the asyncTask returns an joke.
//        String jokeText = "This is totally a funny joke";
//        onView(withId(R.id.libraryText)).check(matches(withText(jokeText)));


//        // Type text into an EditText view, then close the soft keyboard
//        onView(withId(R.id.editTextUserInput))
//                .perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard());

    }


    @Test
    public void createProduct1() throws InterruptedException {
        onView(withId(R.id.productos)).perform(click());
//        pauseTestFor(4000);
        onView(withId(R.id.fab_new)).perform(click());
//        pauseTestFor(4000);

        onView(withId(R.id.product_name_text)).perform(typeText("Product1"), closeSoftKeyboard());
        onView(withId(R.id.product_price)).perform(typeText("1917"), closeSoftKeyboard());
        onView(withId(R.id.product_pricespecial)).perform(typeText("2031"), closeSoftKeyboard());
        onView(withId(R.id.product_pricespecial)).perform(swipeUp());
        onView(withId(R.id.product_description)).perform(typeText("01234567890123"), closeSoftKeyboard());
// Take Photo!

        onView(withId(R.id.product_imagen_button)).perform(click());
        onView(withText("Add Photo!")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Add Photo!")).perform(pressBack());

//
//        Bitmap icon = BitmapFactory.decodeResource(
//                InstrumentationRegistry.getTargetContext().getResources(),
//                R.mipmap.ic_launcher);
//
//        // Build a result to return from the Camera app
//        Intent resultData = new Intent();
//        resultData.putExtra("data", icon);
//        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
//
//        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
//        // with the ActivityResult we just created
//        intending(toPackage("com.nextnut.logistica")).respondWith(result);



//        onView(withText("Take Photo")).perform(click());



//        onView(withClassName(endsWith("View"))).perform(click());






        onView(withId(R.id.fab_save)).perform(click());

        pauseTestFor(4000); //wait to display the add

//
//
//        onView(withClassName(endsWith("View"))).perform(pressBack());//go back to
//        pauseTestFor(10000); //wait to backend responds
//
//        //Verify the answer is not null
//        onView(withId(R.id.libraryText)).check(matches(notNullValue()));
//
//        //Verify that the asyncTask does not return an error. Example is backend is off.
//        String errorText = "error:";
//        onView(withId(R.id.libraryText)).check(matches(not(withText(startsWith(errorText)))));
//        //Verify that the asyncTask returns an joke.
//        String jokeText = "This is totally a funny joke";
//        onView(withId(R.id.libraryText)).check(matches(withText(jokeText)));


//        // Type text into an EditText view, then close the soft keyboard
//        onView(withId(R.id.editTextUserInput))
//                .perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard());

    }


    private void pauseTestFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}