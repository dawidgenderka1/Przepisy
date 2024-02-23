package com.example.przepisy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent activityIntent = new Intent(context, MainActivity.class);
        String channelId = "YOUR_CHANNEL_ID";
        String title = intent.getStringExtra("title");

        activityIntent.putExtra("title", intent.getStringExtra("title"));
        activityIntent.putExtra("recipeId", intent.getIntExtra("recipeId", -1));
        activityIntent.putExtra("description", intent.getStringExtra("description"));
        activityIntent.putExtra("cookingTime", intent.getIntExtra("cookingTime", 0));
        activityIntent.putExtra("cuisineType", intent.getStringExtra("cuisineType"));
        activityIntent.putExtra("instruction", intent.getStringExtra("instruction"));

        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentTitle("Przypomnienie o posiłku.")
                .setContentText(title)
                 .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Przypomnienie o posiłku";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, builder.build());

    }
}
