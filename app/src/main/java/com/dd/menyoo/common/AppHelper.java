package com.dd.menyoo.common;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 22-Feb-16.
 */
public class AppHelper {
    private ProgressDialog dialog = null;
    private Context appUtilityContext = null;
    private static AppHelper instance = null;
    private static NotificationCompat.InboxStyle inboxStyle;
    private static int id =0;

    public static AppHelper getInstance() {
        if (instance == null) {
            instance = new AppHelper();
            inboxStyle = new NotificationCompat.InboxStyle();
        }
        return instance;
    }


    public static boolean isEmailValid(String emailAddress) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    public static boolean isPasswordValid(String password) {
        return (password.length() > 5);
    }

    public static boolean isPasswordsMatch(String pwd1, String pwd2) {
        return pwd1.equals(pwd2);
    }

    public void showProgressDialog(String text, Context currContext) {
        try {
            hideProgressDialog();
            appUtilityContext = currContext;
            dialog = new ProgressDialog(currContext);
            dialog.setProgressStyle(dialog.STYLE_SPINNER);
            dialog.setMessage(text);
            // dialog.setMessage(currContext.getString(R.string.loading));
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {

        }

    }

    public static boolean isContainSpecialCharachters(String s) {
        Pattern p = Pattern.compile("[^a-zA-Z0-9]");
        return p.matcher(s).find();
    }

    public void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public static String getDateTime(String dateobj) {
        String d = /* "/Date(1418346257000+0700)/" */dateobj;
        String results = d.replaceAll("^/Date\\(", "");
        results = results.substring(0, results.length() - 2);
        long time = Long.parseLong(results);
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM,yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        Log.d("Time zone: ", tz.getDisplayName());
        sdf.setTimeZone(tz);
        // SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        return sdf.format(date);
    }

    public static void showConnectionAlert(Context ctx) {
        TextView myMsg = new TextView(ctx);
        myMsg.setText("No Internet Connection");
        myMsg.setPadding(40, 40, 40, 40);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);

        AlertDialog dialog = new AlertDialog.Builder(ctx)
                .setView(myMsg)
                .show();
    }

    public void newNotificationStyle(String message, Context context, NotificationManager nm) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        inboxStyle.setBigContentTitle("Menyoo");

        if(inboxStyle!=null)
            inboxStyle.addLine(message);
        else
            inboxStyle.setSummaryText(message);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Menyoo")
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        notificationBuilder.setVisibility(1);
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);

        notificationBuilder.setContentText(message);
        nm.notify(1, notificationBuilder.build());
    }

    public void NotificationsBuilder(String message, Context context, NotificationManager nm){
        // Create notification
        /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        notificationBuilder.setVisibility(1);
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        Intent intent = new Intent(context, TabActivity.class);
        Bundle mBundle = new Bundle();
        intent.putExtra("To","Action");
        intent.putExtras(mBundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, id, intent, 0);

        notificationBuilder.setContentIntent(contentIntent);
        nm.notify(id, notificationBuilder.build());
        id++;*/
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return false;
            }
        };

        // 1dp/ms
        a.setDuration((int)((targetHeight / v.getContext().getResources().getDisplayMetrics().density)*10));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)((initialHeight / v.getContext().getResources().getDisplayMetrics().density)*10));
        v.startAnimation(a);
    }
    public static boolean isNetworkAvailable(Context conxt) {
        ConnectivityManager connectivityManager = (ConnectivityManager) conxt
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isTextViewEllipsized(final TextView textView) {
        // Initialize the resulting variable
        boolean result = false;
        // Check if the supplied TextView is not null
        if (textView != null) {
            // Check if ellipsizing the text is enabled
            final TextUtils.TruncateAt truncateAt = textView.getEllipsize();
            if (truncateAt != null && !TextUtils.TruncateAt.MARQUEE.equals(truncateAt)) {
                // Retrieve the layout in which the text is rendered
                final Layout layout = textView.getLayout();
                if (layout != null) {
                    // Iterate all lines to search for ellipsized text
                    for (int index = 0; index < layout.getLineCount(); ++index) {
                        // Check if characters have been ellipsized away within this line of text
                        result = layout.getEllipsisCount(index) > 0;
                        // Stop looping if the ellipsis character has been found
                        if (result) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }


}


