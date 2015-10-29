package xyz.homapay.hampay.mobile.android.test;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.test.InstrumentationTestCase;

import org.junit.Test;

/**
 * Created by amir on 10/28/15.
 */
public class UiAutomation extends InstrumentationTestCase {

    private UiDevice device;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        device = UiDevice.getInstance(getInstrumentation());
        device.pressHome();
        device.wait(Until.hasObject(By.desc("Apps")), 1000);
        UiObject2 appsButton = device.findObject(By.desc("Apps"));
        appsButton.click();
        device.wait(Until.hasObject(By.text("hampay")), 1000);
        UiObject2 calculatorApp = device.findObject(By.text("hampay"));
        calculatorApp.click();

    }

    @Test
    public void uiAutomation() throws Exception{

        device.wait(Until.hasObject(By.desc("slider")), 10000);

        UiObject2 slider =device.findObject(By.desc("slider"));
        slider.swipe(Direction.LEFT, 1.0f, 2000);
        device.wait(Until.hasObject(By.desc("slider")), 5000);
        slider.swipe(Direction.LEFT, 1.0f, 2000);
        device.wait(Until.hasObject(By.desc("slider")), 5000);
        slider.swipe(Direction.LEFT, 1.0f, 2000);
        device.wait(Until.hasObject(By.desc("slider")), 5000);
        slider.swipe(Direction.LEFT, 1.0f, 2000);
        device.wait(Until.hasObject(By.desc("slider")), 5000);
        slider.swipe(Direction.LEFT, 1.0f, 2000);

        device.wait(Until.hasObject(By.desc("register")), 4000);
        UiObject2 register = device.findObject(By.desc("register"));
        register.click();

        device.wait(Until.hasObject(By.desc("start")), 8000);
        UiObject2 start = device.findObject(By.desc("start"));
        start.click();

        device.wait(Until.hasObject(By.desc("confirm")), 8000);
        UiObject2 confirm = device.findObject(By.desc("confirm"));
        confirm.click();


        device.wait(Until.hasObject(By.desc("keep_on")), 8000);
        UiObject2 keep_on = device.findObject(By.desc("keep_on"));
        keep_on.click();





        device.wait(Until.hasObject(By.desc("bank_selection")), 10000);
        UiObject2 bank_selection = device.findObject(By.desc("bank_selection"));
        bank_selection.click();

        UiObject bank_list = new UiObject(new UiSelector().className("android.widget.ListView"));
        bank_list.click();


        device.wait(Until.hasObject(By.desc("account")), 8000);
        new UiObject(new UiSelector().description("account")).setText("4899915667");


        device.wait(Until.hasObject(By.desc("email")), 8000);
        new UiObject(new UiSelector().description("email")).setText("amir.sharafkar@homaxyz.com");


        device.wait(Until.hasObject(By.desc("keep_on")), 8000);
        keep_on = device.findObject(By.desc("keep_on"));
        keep_on.click();


        device.wait(Until.hasObject(By.desc("keep_on")), 8000);
        keep_on = device.findObject(By.desc("keep_on"));
        keep_on.click();


        device.wait(Until.hasObject(By.desc("activation_holder")), 8000);
        UiObject2 activation_holder = device.findObject(By.desc("activation_holder"));
        activation_holder.click();


        device.wait(Until.hasObject(By.desc("one")), 8000);
        UiObject2 one = device.findObject(By.desc("one"));
        one.click();

        device.wait(Until.hasObject(By.desc("two")), 8000);
        UiObject2 two = device.findObject(By.desc("two"));
        two.click();

        device.wait(Until.hasObject(By.desc("three")), 8000);
        UiObject2 three = device.findObject(By.desc("three"));
        three.click();

        device.wait(Until.hasObject(By.desc("four")), 8000);
        UiObject2 four = device.findObject(By.desc("four"));
        four.click();

        device.wait(Until.hasObject(By.desc("five")), 8000);
        UiObject2 five = device.findObject(By.desc("five"));
        five.click();


        device.wait(Until.hasObject(By.desc("verify")), 8000);
        UiObject2 verify = device.findObject(By.desc("verify"));
        verify.click();


//        UiObject2 buttonPlus = device.findObject(By.desc("plus"));
//        buttonPlus.click();
//
//        buttonNine.click();
//
//        UiObject2 buttonEquals = device.findObject(By.desc("equals"));
//        buttonEquals.click();
//
//        device.waitForIdle(3000);
//
//
//        UiObject2 resultText = device.findObject(By.clazz("android.widget.EditText"));
//        String result = resultText.getText();
//
//        assertTrue(result.equals("18"));

    }
}
