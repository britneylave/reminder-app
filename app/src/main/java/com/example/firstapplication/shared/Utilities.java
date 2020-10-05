package com.example.firstapplication.shared;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.firstapplication.builder.TimeBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.WIFI_SERVICE;
import static com.example.firstapplication.shared.Constant.BINUS_IP_ADDRESS;
import static com.example.firstapplication.shared.Constant.CHAT_TIME_PATTERN;
import static com.example.firstapplication.shared.Constant.TOKEN_LIFETIME;
import static com.facebook.FacebookSdk.getApplicationContext;

public class Utilities {

    final static int SECOND = 1000;        // no. of ms in a second
    final static int MINUTE = SECOND * 60; // no. of ms in a minute
    final static int HOUR = MINUTE * 60;   // no. of ms in an hour
    final static int DAY = HOUR * 24;      // no. of ms in a day
    final static int WEEK = DAY * 7;       // no. of ms in a week
    final static int MONTH = WEEK * 4;       // no. of ms in a week
    private static final String TAG = "Utilities";


    public static String formatDateTime(long createdAt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CHAT_TIME_PATTERN);
        Date created = new Date(createdAt);
        return simpleDateFormat.format(created);
    }

    public static String differentTime(long createdAt) {
        Date created = new Date(createdAt);
        Date now = new Date();
        long differences = Math.abs(now.getTime() - created.getTime());

        int second = (int) (differences % SECOND);
        int minute = (int) (differences / MINUTE);
        int hour = (int) (differences / HOUR);
        int day = (int) (differences / DAY);
        int week = (int) (differences / WEEK);
        int month = (int) (differences / MONTH);

        TimeBuilder timeBuilder = new TimeBuilder()
                .setDay(day)
                .setHour(hour)
                .setSecond(second)
                .setMinute(minute)
                .setWeek(week)
                .setMonth(month);

        return timeBuilder.toString();
    }


    public enum Weekday {
        Sunday,
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday
    }

    public static boolean isTokenExpired(long l) {
        long diff = Math.abs(l - new Date().getTime());
        long diffHours = diff / (60 * 60 * 1000) % 24;
        return diffHours > TOKEN_LIFETIME;
    }

    public static String getIP() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();

            return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        } catch (Exception ex) {
            Log.e(TAG, Objects.requireNonNull(ex.getMessage()));
            return null;
        }
    }

    public static boolean isInBinusAddress() {
        String ip = getIP();
        assert ip != null;
        return ip.startsWith(BINUS_IP_ADDRESS);
    }

    public static String removeAllNewLineCharacters(String input) {
        String str = input;
        str = str.replaceAll("\n", "");
        return str;
    }


}