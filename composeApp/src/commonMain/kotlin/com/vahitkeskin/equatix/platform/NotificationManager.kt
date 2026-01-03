package com.vahitkeskin.equatix.platform

interface NotificationManager {
    // Bildirim izni var mı?
    fun hasPermission(): Boolean

    // İzin iste (Callback ile sonucu döner)
    fun requestPermission(onResult: (Boolean) -> Unit)

    // Bildirimi planla (Her gün belirli bir saatte)
    fun scheduleDailyReminder(hour: Int = 1, minute: Int = 1, title: String, body: String)

    // Planlanmış bildirimleri iptal et
    fun cancelDailyReminder()

    // Kullanıcıyı sistem ayarlarına yönlendir
    fun openSystemSettings()

    // Bildirim izin popup'ı
    fun isReminderEnabled(): Boolean
}

// Bu nesneye DI (AppModule) üzerinden erişeceğiz
expect fun createNotificationManager(): NotificationManager