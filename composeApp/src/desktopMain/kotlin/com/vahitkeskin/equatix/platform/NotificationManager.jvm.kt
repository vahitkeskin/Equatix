package com.vahitkeskin.equatix.platform

import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.Toolkit
import java.awt.Image
import java.util.Timer
import java.util.TimerTask
import java.util.Calendar

class JvmNotificationManager : NotificationManager {

    private val timer = Timer("DailyReminderTimer", true)
    private var trayIcon: TrayIcon? = null

    init {
        // Masaüstü sistem tepsisi (System Tray) kontrolü
        if (SystemTray.isSupported()) {
            val tray = SystemTray.getSystemTray()

            // Boş bir görsel oluşturuyoruz (Hata vermemesi için)
            val image: Image = Toolkit.getDefaultToolkit().createImage(ByteArray(0))

            trayIcon = TrayIcon(image, "Equatix").apply {
                isImageAutoSize = true
                toolTip = "Equatix"
            }
            try {
                tray.add(trayIcon)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun hasPermission(): Boolean {
        // Masaüstünde izin varsayılan olarak vardır
        return true
    }

    override fun requestPermission(onResult: (Boolean) -> Unit) {
        onResult(true)
    }

    override fun scheduleDailyReminder(hour: Int, minute: Int, title: String, body: String) {
        timer.purge()

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = target.timeInMillis - now.timeInMillis
        val period = 24L * 60 * 60 * 1000 // 24 Saat

        try {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    showNotification(title, body)
                }
            }, delay, period)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun cancelDailyReminder() {
        try {
            timer.purge()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun openSystemSettings() {
        // Masaüstü ayarları için işlem yok
    }

    private fun showNotification(title: String, body: String) {
        if (SystemTray.isSupported() && trayIcon != null) {
            trayIcon?.displayMessage(
                title,
                body,
                TrayIcon.MessageType.INFO
            )
        } else {
            println("BİLDİRİM: $title - $body")
        }
    }
}

// ▼▼▼ BU KISIM DERLEME HATASINI ÇÖZEN KISIMDIR ▼▼▼
actual fun createNotificationManager(): NotificationManager {
    return JvmNotificationManager()
}