package com.nextnut.logistica;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by perez.juan.jose on 23/08/2016.
 */
// CameraActivityInstrumentationTest.java
public class CameraActivityInstrumentationTest {

    // IntentsTestRule is an extension of ActivityTestRule. IntentsTestRule sets up Espresso-Intents
    // before each Test is executed to allow stubbing and validation of intents.
    @Rule
    public IntentsTestRule<ProductDetailActivity> intentsRule = new IntentsTestRule<>(ProductDetailActivity.class);

    @Test
    public void validateCameaScenario() {
        // Create a bitmap we can use for our simulated camera image
        Bitmap icon = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher);

        // Build a result to return from the Camera app
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
        // with the ActivityResult we just created
        intending(toPackage("com.nextnut.logistica")).respondWith(result);

        // Now that we have the stub in place, click on the button in our app that launches into the Camera
//        onView(withId(R.id.btnTakePicture)).perform(click());

        // Take Photo!

        onView(withId(R.id.product_imagen_button)).perform(click());
        onView(withText("Add Photo!")).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText("Add Photo!")).perform(click());



        // We can also validate that an intent resolving to the "camera" activity has been sent out by our app
        intended(toPackage("com.nextnut.logistica"));

        // ... additional test steps and validation ...
    }
}
