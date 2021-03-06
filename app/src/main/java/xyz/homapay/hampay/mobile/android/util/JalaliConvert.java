package xyz.homapay.hampay.mobile.android.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by amir on 3/15/15.
 */
public class JalaliConvert {

    private int dayFromMonth, month, year;
    private int dayFromWeek;
    String timeDay;

    private int jY, jM, jD;
    private int gY, gM, gD;
    private int leap, march;
    private Date date;

    public JalaliConvert(){

    }

    public JalaliConvert(Date date){

        this.date = date;

        Calendar calendar = dateToCalendar(date);

        int jd = JG2JD(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                0);

        JD2Jal(jd);
        this.year = jY;
        this.month = jM;
        this.dayFromMonth = jD;
        this.dayFromWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        this.timeDay = sdf.format(calendar.getTime());
    }

    private Calendar dateToCalendar(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }


    private int JG2JD(int year, int month, int day, int J1G0) {
        int jd = (1461 * (year + 4800 + (month - 14) / 12)) / 4
                + (367 * (month - 2 - 12 * ((month - 14) / 12))) / 12
                - (3 * ((year + 4900 + (month - 14) / 12) / 100)) / 4 + day
                - 32075;

        if (J1G0 == 0)
            jd = jd - (year + 100100 + (month - 8) / 6) / 100 * 3 / 4 + 752;

        return jd;
    }

    private void JD2JG(int JD, int J1G0) {
        int i, j;

        j = 4 * JD + 139361631;

        if (J1G0 == 0) {
            j = j + (4 * JD + 183187720) / 146097 * 3 / 4 * 4 - 3908;
        }

        i = (j % 1461) / 4 * 5 + 308;
        gD = (i % 153) / 5 + 1;
        gM = ((i / 153) % 12) + 1;
        gY = j / 1461 - 100100 + (8 - gM) / 6;
    }

    private void JD2Jal(int JDN) {
        JD2JG(JDN, 0);

        jY = gY - 621;
        JalCal(jY);

        int JDN1F = JG2JD(gY, 3, march, 0);
        int k = JDN - JDN1F;
        if (k >= 0) {
            if (k <= 185) {
                jM = 1 + k / 31;
                jD = (k % 31) + 1;
                return;
            } else {
                k = k - 186;
            }
        } else {
            jY = jY - 1;
            k = k + 179;
            if (leap == 1)
                k = k + 1;
        }

        jM = 7 + k / 30;
        jD = (k % 30) + 1;
    }

    private int Jal2JD(int jY, int jM, int jD) {
        JalCal(jY);
        int jd = JG2JD(gY, 3, march, 1) + (jM - 1) * 31 - jM / 7 * (jM - 7)
                + jD - 1;
        return jd;
    }

    private void JalCal(int jY) {
        march = 0;
        leap = 0;

        int[] breaks = { -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
                1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178 };

        gY = jY + 621;
        int leapJ = -14;
        int jp = breaks[0];

        int jump = 0;
        for (int j = 1; j <= 19; j++) {
            int jm = breaks[j];
            jump = jm - jp;
            if (jY < jm) {
                int N = jY - jp;
                leapJ = leapJ + N / 33 * 8 + (N % 33 + 3) / 4;

                if ((jump % 33) == 4 && (jump - N) == 4)
                    leapJ = leapJ + 1;

                int leapG = (gY / 4) - (gY / 100 + 1) * 3 / 4 - 150;

                march = 20 + leapJ - leapG;

                if ((jump - N) < 6)
                    N = N - jump + (jump + 4) / 33 * 33;

                leap = ((((N + 1) % 33) - 1) % 4);

                if (leap == -1)
                    leap = 4;
                break;
            }

            leapJ = leapJ + jump / 33 * 8 + (jump % 33) / 4;
            jp = jm;
        }
    }

    String[] persianDay = { "یک شنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنج شنبه", "جمعه", "شنبه"};
    String[] persianMonth = {"فرودین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"};

    public String getPersianMonth(){
        return persianMonth[month - 1];
    }

    public String toString() {

        return persianDay[getDayFromWeek()]
                + " "
                + String.format("%04d/%02d/%02d", getYear(), getMonth(), getDayFromMonth())
                + " - " + getTimeDay();
    }

    public void GregorianToPersian(int year, int month, int day) {
        int jd = JG2JD(year, month, day, 0);
        JD2Jal(jd);
        this.year = jY;
        this.month = jM;
        this.dayFromMonth = jD;
    }

    public String GregorianToPersian(Date date) {

        Calendar calendar = dateToCalendar(date);

        int jd = JG2JD(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                0);

        JD2Jal(jd);
        this.year = jY;
        this.month = jM;
        this.dayFromMonth = jD;
        this.dayFromWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        this.timeDay = sdf.format(calendar.getTime());

        return this.toString();
    }

    public void PersianToGregorian(int year, int month, int day) {
        int jd = Jal2JD(year, month, day);
        JD2JG(jd, 0);
        this.year = gY;
        this.month = gM;
        this.dayFromMonth = gD;
    }

    public int getDayFromMonth() {
        return dayFromMonth;
    }

    public int getDayFromWeek() {
        return dayFromWeek;
    }

    public String getTimeDay(){
        return timeDay;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;

    }


    public String homeMessage(){
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.get(Calendar.HOUR_OF_DAY);
        String message = homeMessage(calendar.get(Calendar.HOUR_OF_DAY)) + " " + "بخیر";
         return message;
    }

    public String homeDate(){
        String message = "";
        message += this.dayFromMonth;
        message += " ";
        message += getPersianMonth();
        message += " ";
        message += getYear();
        return message;
    }


    private String homeMessage(int time){

        if (time >= 0 && time < 12){
            return "صبح";
        }
        else if (time >= 12 && time < 14){
            return "ظهر";
        }else if (time >= 14 && time < 20){
            return "عصر";
        }else {
            return "شب";
        }

    }

}
