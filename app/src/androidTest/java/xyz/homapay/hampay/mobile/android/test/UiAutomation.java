package xyz.homapay.hampay.mobile.android.test;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
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
        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("Apps")), 1000);
        UiObject2 appsButton = device.findObject(By.desc("Apps"));
        appsButton.click();
        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.text("ahampay")), 1000);
        UiObject2 calculatorApp = device.findObject(By.text("ahampay"));
        calculatorApp.click();

    }

    @Test
    public void uiAutomation() throws Exception{

        device.wait(Until.hasObject(By.desc("slider")), 10000);

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 500);

        UiObject2 slider =device.findObject(By.desc("slider"));
        slider.swipe(Direction.LEFT, 1.0f, 2000);
        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 500);
        device.wait(Until.hasObject(By.desc("slider")), 5000);
        slider.swipe(Direction.LEFT, 1.0f, 2000);
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 500);
//        device.wait(Until.hasObject(By.desc("slider")), 5000);
//        slider.swipe(Direction.LEFT, 1.0f, 2000);
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 500);
//        device.wait(Until.hasObject(By.desc("slider")), 5000);
//        slider.swipe(Direction.LEFT, 1.0f, 2000);
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 500);
//        device.wait(Until.hasObject(By.desc("slider")), 5000);
//        slider.swipe(Direction.LEFT, 1.0f, 2000);
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 500);

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


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("phone")), 8000);
        UiObject2 phone = device.findObject(By.desc("phone"));
        phone.click();
        phone.setText("09126157905");
        device.pressBack();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("bank_selection")), 10000);
        UiObject2 bank_selection = device.findObject(By.desc("bank_selection"));
        bank_selection.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        clickListViewItem("بانک خاورميانه");

//        UiObject bank_list = new UiObject(new UiSelector().className("android.widget.ListView"));
//        bank_list.click();


//        device.wait(Until.hasObject(By.desc("account")), 8000);
//        new UiObject(new UiSelector().description("account")).setText("4899915667");


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);

        device.wait(Until.hasObject(By.desc("account")), 8000);
        UiObject2 account = device.findObject(By.desc("account"));
        account.click();
        account.setText("444515485178485485");
        device.pressBack();

        device.wait(Until.hasObject(By.desc("national")), 8000);
        UiObject2 national = device.findObject(By.desc("national"));
        national.click();
        national.setText("4899915667");
        device.pressBack();


//        device.wait(Until.hasObject(By.desc("email")), 8000);
//        new UiObject(new UiSelector().description("email")).setText("amir.sharafkar@homaxyz.com");
        device.wait(Until.hasObject(By.desc("email")), 8000);
        UiObject2 email = device.findObject(By.desc("email"));
        email.click();
        email.setText("amir.sharafkar@homaxyz.com");
        device.pressBack();



        device.wait(Until.hasObject(By.desc("keep_on")), 8000);
        keep_on = device.findObject(By.desc("keep_on"));
        keep_on.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);

        device.wait(Until.hasObject(By.desc("keep_on")), 8000);
        keep_on = device.findObject(By.desc("keep_on"));
        keep_on.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 30000);

        device.wait(Until.hasObject(By.desc("activation_holder")), 15000);
        UiObject2 activation_holder = device.findObject(By.desc("activation_holder"));
        activation_holder.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("one")), 15000000);
        UiObject2 one = device.findObject(By.desc("one"));
        one.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("two")), 15000000);
        UiObject2 two = device.findObject(By.desc("two"));
        two.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("three")), 15000000);
        UiObject2 three = device.findObject(By.desc("three"));
        three.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("four")), 15000000);
        UiObject2 four = device.findObject(By.desc("four"));
        four.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("five")), 15000000);
        UiObject2 five = device.findObject(By.desc("five"));
        five.click();


        device.pressBack();


        device.wait(Until.hasObject(By.desc("verify")), 100000);
        UiObject2 verify = device.findObject(By.desc("verify"));
        verify.click();

        device.wait(Until.hasObject(By.desc("keep_on")), 8000);
        keep_on = device.findObject(By.desc("keep_on"));
        keep_on.click();


        device.wait(Until.hasObject(By.desc("confirm_check_ll")), 8000);
        UiObject2 confirm_check_ll = device.findObject(By.desc("confirm_check_ll"));
        confirm_check_ll.click();


        device.wait(Until.hasObject(By.desc("keeOn_with_button")), 8000);
        UiObject2 keeOn_with_button = device.findObject(By.desc("keeOn_with_button"));
        keeOn_with_button.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);

        device.wait(Until.hasObject(By.desc("keepOn_button")), 8000);
        UiObject2 keepOn_button = device.findObject(By.desc("keepOn_button"));
        keepOn_button.click();



        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);

        device.wait(Until.hasObject(By.desc("keepOn_button")), 8000);
        keepOn_button = device.findObject(By.desc("keepOn_button"));
        keepOn_button.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);


        device.wait(Until.hasObject(By.desc("password_holder")), 15000);
        UiObject2 password_holder = device.findObject(By.desc("password_holder"));
        password_holder.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 3000);

        device.wait(Until.hasObject(By.desc("one")), 3000);
        one = device.findObject(By.desc("one"));
        one.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("two")), 3000);
        two = device.findObject(By.desc("two"));
        two.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("three")), 3000);
        three = device.findObject(By.desc("three"));
        three.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("four")), 3000);
        four = device.findObject(By.desc("four"));
        four.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("five")), 3000);
        five = device.findObject(By.desc("five"));
        five.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);

        device.wait(Until.hasObject(By.desc("one")), 3000);
        one = device.findObject(By.desc("one"));
        one.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("two")), 3000);
        two = device.findObject(By.desc("two"));
        two.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("three")), 3000);
        three = device.findObject(By.desc("three"));
        three.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("four")), 3000);
        four = device.findObject(By.desc("four"));
        four.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("five")), 3000);
        five = device.findObject(By.desc("five"));
        five.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("memorable")), 8000);
        UiObject2 memorable = device.findObject(By.desc("memorable"));
        memorable.click();
        memorable.setText("BAGHALI");
        device.pressBack();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 2000);


        device.wait(Until.hasObject(By.desc("confirm")), 3000);
        confirm = device.findObject(By.desc("confirm"));
        confirm.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 10000);

        device.wait(Until.hasObject(By.desc("welcome")), 3000);
        UiObject2 welcome = device.findObject(By.desc("welcome"));
        welcome.click();


        device.wait(Until.hasObject(By.desc("password_holder")), 15000);
        password_holder = device.findObject(By.desc("password_holder"));
        password_holder.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);

        device.wait(Until.hasObject(By.desc("one")), 3000);
        one = device.findObject(By.desc("one"));
        one.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("two")), 3000);
        two = device.findObject(By.desc("two"));
        two.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("three")), 3000);
        three = device.findObject(By.desc("three"));
        three.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("four")), 3000);
        four = device.findObject(By.desc("four"));
        four.click();

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);

        device.wait(Until.hasObject(By.desc("five")), 3000);
        five = device.findObject(By.desc("five"));
        five.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 10000);

        device.wait(Until.hasObject(By.desc("accept")), 10000);
        UiObject2 accept = device.findObject(By.desc("accept"));
        accept.click();



//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 50000);
//
//        device.wait(Until.hasObject(By.desc("submenu1")), 10000);
//        UiObject2 submenu1 = device.findObject(By.desc("submenu1"));
//        submenu1.click();


//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
//        device.wait(Until.hasObject(By.desc("submenu2")), 10000);
//        UiObject2 submenu2 = device.findObject(By.desc("submenu2"));
//        submenu2.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("hampay_1_ll")), 10000);
        UiObject2 hampay_1_ll = device.findObject(By.desc("hampay_1_ll"));
        hampay_1_ll.click();
        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 2000);
        device.pressBack();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);

        device.wait(Until.hasObject(By.desc("menu")), 10000);
        UiObject2 menu = device.findObject(By.desc("menu"));
        menu.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 10000);

        clickListViewItem("تراکنشها");


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 10000);
        clickListViewItem("پرداخت");


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.pressBack();


        device.wait(Until.hasObject(By.desc("menu")), 10000);
        menu = device.findObject(By.desc("menu"));
        menu.click();


        //Pay to Business

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        clickListViewItem("پرداخت به کسب و کار");

        clickListViewItem("انتخاب");


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 2000);
        device.wait(Until.hasObject(By.desc("message")), 8000);
        UiObject2 message = device.findObject(By.desc("message"));
        message.click();
        message.setText("کمک هزینه خدمت سربازی امیر");
        device.pressBack();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 2000);
        device.wait(Until.hasObject(By.desc("price")), 8000);
        UiObject2 price = device.findObject(By.desc("price"));
        price.click();
        price.setText("2000");

        device.pressBack();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 3000);
        device.wait(Until.hasObject(By.desc("pay")), 10000);
        UiObject2 pay = device.findObject(By.desc("pay"));
        pay.click();



        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("done")), 10000);
        UiObject2 done = device.findObject(By.desc("done"));
        done.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("finish")), 10000);
        UiObject2 finish = device.findObject(By.desc("finish"));
        finish.click();

        //End Pay to Business

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 20000);
        device.wait(Until.hasObject(By.desc("menu")), 10000);
        menu = device.findObject(By.desc("menu"));
        menu.click();

        //Pay to One

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        clickListViewItem("پرداخت به فرد");


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 2000);
        device.wait(Until.hasObject(By.desc("search")), 8000);
        UiObject2 search = device.findObject(By.desc("search"));
        search.click();
        search.setText("امیر");
        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 2000);
        search.setText("");


//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 3000);
//        device.wait(Until.hasObject(By.desc("keep_on")), 10000);
//        keep_on = device.findObject(By.desc("keep_on"));
//        keep_on.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 20000);
//        device.wait(Until.hasObject(By.desc("menu")), 10000);
//        menu = device.findObject(By.desc("menu"));
//        menu.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        clickListViewItem("پرداخت به فرد");


        //New Pay

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        clickListViewItem("انتخاب");


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("message")), 8000);
        message = device.findObject(By.desc("message"));
        message.click();
        message.setText("کمک هزینه خدمت سربازی امیر");
        device.pressBack();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 2000);
        device.wait(Until.hasObject(By.desc("price")), 8000);
        price = device.findObject(By.desc("price"));
        price.click();
        price.setText("2000");

        device.pressBack();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 3000);
        device.wait(Until.hasObject(By.desc("pay")), 10000);
        pay = device.findObject(By.desc("pay"));
        pay.click();



        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("done")), 10000);
        done = device.findObject(By.desc("done"));
        done.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("finish")), 10000);
        finish = device.findObject(By.desc("finish"));
        finish.click();

        //Recent Pay
        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        clickListViewItem("کمک هزینه خدمت سربازی امیر");

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("message")), 8000);
        message = device.findObject(By.desc("message"));
        message.click();
        message.setText("کمک هزینه خدمت سربازی امیر");
        device.pressBack();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 2000);
        device.wait(Until.hasObject(By.desc("price")), 8000);
        price = device.findObject(By.desc("price"));
        price.click();
        price.setText("2000");

        device.pressBack();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 3000);
        device.wait(Until.hasObject(By.desc("pay")), 10000);
        pay = device.findObject(By.desc("pay"));
        pay.click();



        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("done")), 10000);
        done = device.findObject(By.desc("done"));
        done.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("finish")), 10000);
        finish = device.findObject(By.desc("finish"));
        finish.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 20000);
        device.wait(Until.hasObject(By.desc("menu")), 10000);
        menu = device.findObject(By.desc("menu"));
        menu.click();

        //End Pay to One

        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 20000);
        device.wait(Until.hasObject(By.desc("menu")), 10000);
        menu = device.findObject(By.desc("menu"));
        menu.click();

        clickListViewItem("خروج");
        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 3000);
        device.wait(Until.hasObject(By.desc("keep_on")), 10000);
        keep_on = device.findObject(By.desc("keep_on"));
        keep_on.click();


        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
        device.wait(Until.hasObject(By.desc("menu")), 10000);
        menu = device.findObject(By.desc("menu"));
        menu.click();


        clickListViewItem("تنظیمات");

        //Change Password

//        clickListViewItem("تغییر رمز عبور");
//
//        device.wait(Until.hasObject(By.desc("password_holder")), 15000);
//        password_holder = device.findObject(By.desc("password_holder"));
//        password_holder.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
//
//        device.wait(Until.hasObject(By.desc("one")), 3000);
//        one = device.findObject(By.desc("one"));
//        one.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("two")), 3000);
//        two = device.findObject(By.desc("two"));
//        two.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("three")), 3000);
//        three = device.findObject(By.desc("three"));
//        three.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("four")), 3000);
//        four = device.findObject(By.desc("four"));
//        four.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("five")), 3000);
//        five = device.findObject(By.desc("five"));
//        five.click();
//
//
//        device.wait(Until.hasObject(By.desc("one")), 3000);
//        one = device.findObject(By.desc("one"));
//        one.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("two")), 3000);
//        two = device.findObject(By.desc("two"));
//        two.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("three")), 3000);
//        three = device.findObject(By.desc("three"));
//        three.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("four")), 3000);
//        four = device.findObject(By.desc("four"));
//        four.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("five")), 3000);
//        five = device.findObject(By.desc("five"));
//        five.click();
//
//        device.wait(Until.hasObject(By.desc("one")), 3000);
//        one = device.findObject(By.desc("one"));
//        one.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("two")), 3000);
//        two = device.findObject(By.desc("two"));
//        two.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("three")), 3000);
//        three = device.findObject(By.desc("three"));
//        three.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("four")), 3000);
//        four = device.findObject(By.desc("four"));
//        four.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("five")), 3000);
//        five = device.findObject(By.desc("five"));
//        five.click();
//
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 10000);
//        device.wait(Until.hasObject(By.desc("done")), 10000);
//        done = device.findObject(By.desc("done"));
//        done.click();

        //End Change Password


        //Change Memorable

//        clickListViewItem("تغییر کلمه یادآوری");
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
//        device.wait(Until.hasObject(By.desc("memorable")), 8000);
//        memorable = device.findObject(By.desc("memorable"));
//        memorable.click();
//        memorable.setText("BAGHALI");
//        device.pressBack();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
//        device.wait(Until.hasObject(By.desc("change_memorable")), 10000);
//        UiObject2 change_memorable = device.findObject(By.desc("change_memorable"));
//        change_memorable.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
//        device.wait(Until.hasObject(By.desc("memorable")), 8000);
//        memorable = device.findObject(By.desc("memorable"));
//        memorable.click();
//        memorable.setText("BAGHALI");
//        device.pressBack();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
//        device.wait(Until.hasObject(By.desc("change_memorable")), 10000);
//        change_memorable = device.findObject(By.desc("change_memorable"));
//        change_memorable.click();
//
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 2000);
//        device.wait(Until.hasObject(By.desc("password_holder")), 15000);
//        password_holder = device.findObject(By.desc("password_holder"));
//        password_holder.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
//
//        device.wait(Until.hasObject(By.desc("one")), 3000);
//        one = device.findObject(By.desc("one"));
//        one.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("two")), 3000);
//        two = device.findObject(By.desc("two"));
//        two.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("three")), 3000);
//        three = device.findObject(By.desc("three"));
//        three.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("four")), 3000);
//        four = device.findObject(By.desc("four"));
//        four.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("five")), 3000);
//        five = device.findObject(By.desc("five"));
//        five.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 10000);
//        device.wait(Until.hasObject(By.desc("done")), 10000);
//        done = device.findObject(By.desc("done"));
//        done.click();

        //End Change Memorable


        //Unlink
//        clickListViewItem("لغو عضویت");
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 2000);
//        device.wait(Until.hasObject(By.desc("confirm")), 15000);
//        confirm = device.findObject(By.desc("confirm"));
//        confirm.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 5000);
//
//
//        device.wait(Until.hasObject(By.desc("password_holder")), 15000);
//        password_holder = device.findObject(By.desc("password_holder"));
//        password_holder.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 3000);
//
//        device.wait(Until.hasObject(By.desc("one")), 3000);
//        one = device.findObject(By.desc("one"));
//        one.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("two")), 3000);
//        two = device.findObject(By.desc("two"));
//        two.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("three")), 3000);
//        three = device.findObject(By.desc("three"));
//        three.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("four")), 3000);
//        four = device.findObject(By.desc("four"));
//        four.click();
//
//        device.waitForWindowUpdate("xyz.homapay.hampay.mobile.android", 1000);
//
//        device.wait(Until.hasObject(By.desc("five")), 3000);
//        five = device.findObject(By.desc("five"));
//        five.click();

        //End Unlink

    }

    public void clickListViewItem(String name) throws UiObjectNotFoundException {
        UiScrollable listView = new UiScrollable(new UiSelector());
        listView.setMaxSearchSwipes(100);
        listView.scrollTextIntoView(name);
        listView.waitForExists(5000);
        UiObject listViewItem = listView.getChildByText(new UiSelector()
                .className(android.widget.TextView.class.getName()), "" + name + "");
        listViewItem.click();
    }

    public void clickListViewItemDes(int index) throws UiObjectNotFoundException {
        UiScrollable listView = new UiScrollable(new UiSelector());
        listView.setMaxSearchSwipes(100);
        listView.scrollForward(index);
        listView.waitForExists(5000);
        UiObject listViewItem = listView.getChild(new UiSelector().clickable(true).index(index));
        listViewItem.click();
    }

}
