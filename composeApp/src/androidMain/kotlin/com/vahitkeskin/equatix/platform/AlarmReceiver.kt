package com.vahitkeskin.equatix.platform

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vahitkeskin.equatix.MainActivity
import com.vahitkeskin.equatix.R

class AlarmReceiver : BroadcastReceiver() {

    // KULLANICIYI ÇEKECEK MESAJ HAVUZU
    private val morningMessages = listOf(
        "☀️ Güne Zinde Başla" to "Kahvenden bile daha etkili! Güne tam odaklanmış bir zihinle başlamak için bugünkü bulmacanı çöz.",
        "🧠 Sabah Sporu Zamanı" to "Vücudun uyandı, peki ya beynin? Nöronlarını ateşlemek ve güne %100 kapasiteyle başlamak için tıkla.",
        "☕ Kahvenin En İyi Eşlikçisi" to "Sabah kahvenden aldığın keyfi ikiye katla. Bir yudum kahve, bir doz zeka egzersizi!",
        "🚀 Günün İlk Zaferi" to "Küçük bir galibiyetle güne başlamak tüm gününü değiştirir. Bugünkü matrisi çöz ve motive ol.",
        "🚌 Yolda veya Masanda" to "İşe ya da okula başlamadan önce zihninin pasını sil. 60 saniyede sabah mahmurluğunu üzerinden at.",
        "🌅 Potansiyelini Açığa Çıkar" to "Bugün harika bir gün olacak, özellikle de zihnin açıkken. Equatix ile sınırlarını zorlamaya başla."
    )

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val prefs = context.getSharedPreferences("equatix_alarm_prefs", Context.MODE_PRIVATE)

        // Varsayılan TRUE (Kullanıcı kapatmadığı sürece açık)
        val isEnabled = prefs.getBoolean("is_enabled", true)

        Log.d("ALARM_SYSTEM", "Receiver Çalıştı. Action: $action, Enabled: $isEnabled")

        // Eğer kullanıcı ayarlardan kapattıysa işlem yapma
        if (!isEnabled) return

        // Hafızadaki saat bilgilerini al
        val hour = prefs.getInt("alarm_hour", 22)
        val minute = prefs.getInt("alarm_minute", 0)

        // --- SENARYO 1: TELEFON YENİDEN BAŞLATILDI (BOOT) ---
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            val manager = AndroidNotificationManager(context)
            manager.scheduleDailyReminder(hour, minute, "Equatix", "Zamanı geldi!")
            Log.d("ALARM_SYSTEM", "Boot sonrası alarm tazelendi.")
            return
        }

        // --- SENARYO 2: NORMAL ALARM ZAMANI ---

        // A. Mesaj Seçimi
        // Eğer test amaçlı özel bir başlık gelmediyse havuzdan seç
        val (randomTitle, randomBody) = morningMessages.random()
        val title = intent.getStringExtra("TITLE") ?: randomTitle
        val body = intent.getStringExtra("BODY") ?: randomBody

        // B. Görseli Oluştur ve Bildirimi Göster
        val dynamicImage = generateCoolBitmap(context)
        showStyledNotification(context, title, body, dynamicImage)

        // C. OTOMATİK TEKRAR (Yarına Kur)
        // Manager şu anki saatin geçtiğini fark edip otomatik olarak yarına kuracaktır.
        val manager = AndroidNotificationManager(context)
        manager.scheduleDailyReminder(hour, minute, title, body)
        Log.d("ALARM_SYSTEM", "Döngü sağlandı: Alarm yarına tekrar kuruldu.")
    }

    private fun showStyledNotification(context: Context, title: String, body: String, image: Bitmap) {
        // Sesli kanalı oluştur
        createChannel(context)
        val channelId = "daily_challenge_sound_v2" // Ses için yeni ID

        // Standart bildirim sesi
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Tıklama İntenti
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Bildirimi İnşa Et
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(image) // Sağdaki ikon
            .setContentTitle(title)
            .setContentText(body)

            // --- GÖRSEL ŞOV (Aşağı çekince büyüyen resim) ---
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(image)
                .bigLargeIcon(null as Bitmap?) // Büyüyünce küçük ikonu gizle
                .setSummaryText(body)
            )
            // -----------------------------------------------

            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(soundUri)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Işık, Titreşim, Ses
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(Color.parseColor("#6200EE")) // Tema Rengi
            // Aksiyon Butonu
            .addAction(android.R.drawable.ic_media_play, "Hemen Çöz", pendingIntent)

        // Gönder
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            try {
                NotificationManagerCompat.from(context).notify(1001, builder.build())
            } catch (e: Exception) {
                Log.e("ALARM_SYSTEM", "Bildirim hatası: ${e.message}")
            }
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "daily_challenge_sound_v2"
            val channelName = "Günlük Hatırlatıcı"

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Equatix Günlük Sesli Bildirimleri"
                enableVibration(true)
                enableLights(true)
                lightColor = Color.MAGENTA

                // SES AYARLARI (Kritik Kısım)
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(soundUri, audioAttributes)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Kod ile dinamik görsel çizer (Mor Gradient + Matematik Sembolleri + EQUATIX yazısı)
     */
    private fun generateCoolBitmap(context: Context): Bitmap {
        val width = 600
        val height = 300
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Arka Plan (Mor Gradient)
        val paint = Paint()
        paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(),
            Color.parseColor("#4A148C"), // Koyu Mor
            Color.parseColor("#7C4DFF"), // Açık Mor
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // 2. Silik Semboller
        val symbolPaint = Paint().apply {
            color = Color.WHITE
            alpha = 25 // Çok silik
            textSize = 100f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("∑", 50f, 150f, symbolPaint)
        canvas.drawText("π", 450f, 100f, symbolPaint)
        canvas.drawText("√", 250f, 250f, symbolPaint)

        // 3. Ortaya EQUATIX Yazısı
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 100f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            setShadowLayer(12f, 0f, 0f, Color.BLACK)
        }

        val xPos = (canvas.width / 2).toFloat()
        val yPos = (canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)

        canvas.drawText("EQUATIX", xPos, yPos, textPaint)

        return bitmap
    }
}