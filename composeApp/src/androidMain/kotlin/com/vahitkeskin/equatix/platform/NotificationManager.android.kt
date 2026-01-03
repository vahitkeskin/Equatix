package com.vahitkeskin.equatix.platform

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.vahitkeskin.equatix.EquatixApp
import java.util.Calendar

class AndroidNotificationManager(private val context: Context) : NotificationManager {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val prefs = context.getSharedPreferences("equatix_alarm_prefs", Context.MODE_PRIVATE)

    override fun hasPermission(): Boolean {
        val notificationCheck = if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true

        val alarmCheck = if (Build.VERSION.SDK_INT >= 31) {
            alarmManager.canScheduleExactAlarms()
        } else true

        return notificationCheck && alarmCheck
    }

    override fun requestPermission(onResult: (Boolean) -> Unit) {
        onResult(hasPermission())
    }

    override fun scheduleDailyReminder(hour: Int, minute: Int, title: String, body: String) {
        // 1. Önce Tercihi Kaydet (Kalıcılık için şart)
        prefs.edit().apply {
            putBoolean("is_enabled", true)
            putInt("alarm_hour", hour)
            putInt("alarm_minute", minute)
            putString("alarm_title", title)
            putString("alarm_body", body)
            apply()
        }

        // 2. Alarmı Kur (Yardımcı fonksiyonu çağırıyoruz)
        scheduleAlarmInternal(hour, minute, title, body)
    }

    // Bu fonksiyonu Receiver da kullanacak, o yüzden public/internal yapabiliriz veya
    // Receiver içinde Manager'ı tekrar oluşturup çağırabiliriz.
    // Kolaylık olsun diye mantığı burada tutuyoruz.
    private fun scheduleAlarmInternal(hour: Int, minute: Int, title: String, body: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Eğer saat geçmişse yarına kur
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TITLE", title)
            putExtra("BODY", body)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            12345,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d("ALARM_SYSTEM", "Alarm kuruldu: ${calendar.time}")
        } catch (e: SecurityException) {
            Log.e("ALARM_SYSTEM", "Alarm izni hatası: ${e.message}")
        }
    }

    override fun cancelDailyReminder() {
        // Tercihi sil (Kullanıcı iptal etti)
        prefs.edit().putBoolean("is_enabled", false).apply()

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            12345,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("ALARM_SYSTEM", "Alarm iptal edildi.")
    }

    override fun openSystemSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override fun isReminderEnabled(): Boolean {
        // Hafızaya bak: "is_enabled" anahtarı true mu?
        return prefs.getBoolean("is_enabled", false)
    }
}

actual fun createNotificationManager(): NotificationManager {
    return AndroidNotificationManager(EquatixApp.instance)
}