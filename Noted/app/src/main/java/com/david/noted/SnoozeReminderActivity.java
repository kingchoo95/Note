package com.david.noted;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SnoozeReminderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        int snoozeReminderId,selectedId;
        snoozeReminderId = intent.getIntExtra("noteId",-1);
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 15);
        SimpleDateFormat afterSnoozeTime = new SimpleDateFormat("HH:mm");
        Log.i("snooze",Integer.toString(snoozeReminderId));
        SQLiteDatabase noteDB = this.openOrCreateDatabase("Reminders", MODE_PRIVATE, null);
        noteDB.execSQL("UPDATE reminders SET time = '"+ afterSnoozeTime.format(now.getTime()) +"',isTrigger= '0' WHERE id = "+Integer.toString(snoozeReminderId + 1)+"");


        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(snoozeReminderId);
        this.finish();
    }
}
