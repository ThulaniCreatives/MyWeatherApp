package thulanicreatives.com.myweatherapp.weather.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import thulanicreatives.com.myweatherapp.R

class WeatherFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
       if (message.notification != null) {
           generateNotification(message.notification!!.title!! , message.notification!!.body!!)
       }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun generateNotification(title: String, message: String) {

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        var builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteViews(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0,builder.build())

    }

    private fun getRemoteViews(title: String, message: String): RemoteViews {

        val remoteViews = RemoteViews("thulanicreatives.com.myweatherapp", R.layout.notifications)
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.description, message)
        remoteViews.setImageViewResource(R.id.imageView, R.drawable.ic_cloudy)

        return remoteViews
    }

    companion object {
        const val channelId = "WEATHER_ID"
        const val channelName = "WEATHER_CHANNEL_NAME"
    }
}

