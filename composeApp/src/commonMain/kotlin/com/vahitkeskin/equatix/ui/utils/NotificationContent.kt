package com.vahitkeskin.equatix.utils

// Veri yapısı (Pair yerine Data Class daha okunaklıdır)
data class NotificationMessage(
    val title: String,
    val body: String
)

object NotificationContent {

    // Mesaj Havuzu
    private val messages = listOf(
        NotificationMessage(
            "🧠 Zihin Egzersizi Zamanı!",
            "Sadece %5'lik dilim bu matrisi hatasız çözebiliyor. Sen yapabilir misin?"
        ),
        NotificationMessage(
            "🔥 Zinciri Kırma!",
            "Beynin de kasların gibidir, çalışmazsa paslanır. Bugünkü antrenmanını tamamla."
        ),
        NotificationMessage(
            "⏳ 60 Saniyen Var mı?",
            "Günün stresinden uzaklaşmak ve odaklanmak için kısa bir Equatix molası ver."
        ),
        NotificationMessage(
            "🚀 Sınırları Zorla",
            "Bugünkü bulmaca dünkünden biraz daha zor. Bakalım rekorunu geliştirebilecek misin?"
        ),
        NotificationMessage(
            "👀 Gözden Kaçırma",
            "Matematik, görmeyi bilenler için bir sanattır. Bugünkü gizli deseni keşfet."
        ),
        NotificationMessage(
            "🌙 Gece Kuşu musun?",
            "Uyumadan önce zihnini sayılarla arındır. İyi bir uyku için son egzersiz!"
        ),
        NotificationMessage(
            "🏆 Rekabet Kızışıyor",
            "Sıralamada yerini korumak için hamle yapma sırası sende."
        )
    )

    // Rastgele mesaj veren fonksiyon
    fun getRandomMessage(): NotificationMessage {
        return messages.random()
    }
}