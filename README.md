<div align="center">

# 🚀 Equatix: The Ultimate Matrix Engine 🧩

<p align="center">
  <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/fullbackground.png" alt="Equatix Featured" width="100%">
</p>

### *Premium Cross-Platform Math Puzzle Experience*

<img src="https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
<img src="https://img.shields.io/badge/Compose%20Multiplatform-1.7.1-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose"/>
<img src="https://img.shields.io/badge/Koin-4.0.0-F5A623?style=for-the-badge&logo=koin&logoColor=white" alt="Koin"/>
<img src="https://img.shields.io/badge/Room-2.7.0--alpha12-16A085?style=for-the-badge&logo=android&logoColor=white" alt="Room"/>

<br/>

[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen?logo=github-actions&logoColor=white)](https://github.com/vahitkeskin/Equatix)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform Support](https://img.shields.io/badge/Platforms-Android%20%7C%20iOS%20%7C%20Desktop-success)](https://github.com/vahitkeskin/Equatix)
[![API Layer](https://img.shields.io/badge/Min%20SDK-24-orange)](https://developer.android.com)
[![SDK Compliance](https://img.shields.io/badge/Target%20SDK-35-blue)](https://developer.android.com/about/versions/15)

---

[🎮 Özellikler](#-ana-özellikler) • [🏛️ Mimari](#️-mimari) • [🛠️ Tech Stack](#️-tech-stack) • [📂 Yapı](#-proje-yapısı) • [🎨 Tasarım](#-tasarım-sistemi) • [🚀 Başlangıç](#-hızlı-başlangıç)

</div>

---

## ✨ Neden Equatix?

> 💡 **Equatix**, sadece bir bulmaca oyunu değil; matematiksel akıl yürütmeyi görsel bir şölenle birleştiren, **Clean Architecture** prensipleriyle döşenmiş kurumsal düzeyde bir **multiplatform** projesidir.

<table>
<tr>
<td align="center" width="33%">
<h3>🎯 Bilişsel Eğitim</h3>
<p>Matris tabanlı bulmacalarla işlem hızınızı ve dikkat sürenizi %30 artırın.</p>
</td>
<td align="center" width="33%">
<h3>🏎️ Yerel Performans</h3>
<p>KMP sayesinde Android, iOS ve Desktop üzerinde 60 FPS akıcı deneyim.</p>
</td>
<td align="center" width="33%">
<h3>✨ Premium Tasarım</h3>
<p>Modern Glassmorphism ilkeleriyle hazırlanan, gözü yormayan arayüz.</p>
</td>
</tr>
</table>

---

## 📱 Ana Özellikler

### 🧬 Akıllı Matris Üretim Motoru
> *Sonsuz olasılık, tek hedef: Matematiksel kusursuzluk.*

<table>
<tr>
<td width="60%">

**Motorun Kalbi:**
- 🧩 **High-Entropy Generation**: Her oyun başlangıcında benzersiz bir matris matematiksel olarak garanti edilir.
- ⚡ **Real-time Validation**: Kullanıcı girişi yaptığı anda O(1) karmaşıklığında anlık doğrulama.
- 🟠 **Kademeli Zorluk**: 
    - 🟢 Easy (3x3 - Toplama/Çıkarma)
    - 🟠 Medium (4x4 - Çarpma Dahil)
    - 🔴 Hard (5x5 - Tüm İşlemler)
- 💡 **AI-Powered Hints**: Tıkandığınız yerlerde mantıklı hücre açılımları yapabilen zeki ipucu sistemi.

</td>
<td width="40%">

<img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image2.png" width="100%">

</td>
</tr>
</table>

---

### 🎨 Görsel Mükemmellik (Glassmorphism)
> *Şeffaflığın ve derinliğin matematiği.*

<table>
<tr>
<td width="40%">

<img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image1.png" width="100%">

</td>
<td width="60%">

**Tasarım Detayları:**
- ✨ **GlassBox Container**: Özel olarak geliştirilen bulanık arka planlı (blur) içerik kutuları.
- 🌓 **Dinamik Tema Motoru**: Sistem ayarlarına veya kullanıcı tercihine göre Dark/Light mode arası akışkan geçiş.
- 🎭 **Lottie & Compose Animations**: Etkileşimli geri bildirimler için optimize edilmiş animasyon katmanları.
- 🎡 **Custom Time Picker**: Süre ve zorluk seçimleri için geliştirilmiş premium wheel-picker bileşeni.

</td>
</tr>
</table>

---

### 📊 İlerleme ve Veri Yönetimi
> *Her başarınız kayıt altında.*

<table>
<tr>
<td width="60%">

**Sistem Detayları:**
- 💾 **Room KMP persistence**: Tüm oyun geçmişiniz ve istatistikleriniz yerel veritabanında güvenle saklanır.
- 📜 **Detailed Logs**: Skor, hamle sayısı ve tamamlama süresi üzerinden performans analizi.
- ⚙️ **DataStore Sync**: Uygulama ayarları (ses, tema, zorluk) asenkron olarak saklanır ve anında yüklenir.
- 🔔 **Achievement System**: Belirli başarılarda tetiklenen görsel bildirimler ve ödüller.

</td>
<td width="40%">

<img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image3.png" width="100%">

</td>
</tr>
</table>

---

## 🏗️ Mimari (The Blueprint)

Equatix, sürdürülebilir ve test edilebilir bir yapı için **Layered Architecture** kullanır.

### 💎 Mimari Katman Görseli
<p align="center">
  <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/architecture_3d.png" alt="Equatix 3D Architecture" width="550">
</p>

### 🛠️ Teknik Katman Detayları
```mermaid
graph TD
    subgraph UI_Presentation ["🎨 UI Katmanı (Compose)"]
        A[Screens / Voyager] --> B[Common Components]
        B --> C[ViewModels / StateFlow]
    end

    subgraph Domain_Layer ["🧠 Domain Katmanı (Logic)"]
        D[Matrix UseCases] --> E[Entity Models]
        E --> F[Repository Interfaces]
    end

    subgraph Data_Layer ["💾 Data Katmanı (Storage)"]
        G[Room DB] --> H[DataStore Repository]
        H --> I[AdMob / Google Ads]
    end

    UI_Presentation --> Domain_Layer
    Domain_Layer --> Data_Layer
```

- **Domain Layer**: Hiçbir dış bağımlılığı olmayan, saf Kotlin kodundan oluşur. Tüm matris hesaplama ve kural motoru buradadır.
- **Data Layer**: Veritabanı işlemleri, shared preferences (DataStore) ve üçüncü parti servislerin (Ads) yönetimini yapar.
- **Presentation Layer**: Compose Multiplatform kullanılarak yazılmış, UI State yönetimini ViewModel'lar üzerinden yapan katmandır.

---

## 🛠️ Detaylı Tech Stack

### 🏗️ Ana Teknolojiler

| Kategori | Teknoloji | Sürüm | Açıklama |
| :--- | :--- | :--- | :--- |
| **Dil** | ![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?logo=kotlin) | 2.1.0 | K2 Compiler desteği ile tip güvenli kod |
| **UI** | ![Compose](https://img.shields.io/badge/Compose-1.7.1-4285F4?logo=jetpackcompose) | 1.7.1 | Common UI Framework (95% Shared) |
| **DI** | ![Koin](https://img.shields.io/badge/Koin-4.0.0-F5A623?logo=koin) | 4.0.0 | Lightweight KMP dependency injection |
| **Persistence** | ![Room](https://img.shields.io/badge/Room-2.7.0--alpha12-16A085?logo=android) | 2.7.0 | SQLite tabanlı ortak veritabanı |

### ⚡ Yardımcı Sistemler
- **Voyager**: KMP için geliştirilmiş, State management uyumlu multiplatform navigasyon.
- **Coroutines & Flow**: Reaktif veri akışları için standart asenkron çözüm.
- **DataStore**: Modern, asenkron Key-Value depolama.
- **AdMob**: Android/iOS üzerinde banner, interstitial ve rewarded reklam entegrasyonu.

---

## 📂 Proje Yapısı

```bash
Equatix/
├── 📂 composeApp/                  # 🚀 Ortak Uygulama Katmanı
│   └── 📂 src/
│       ├── 📂 commonMain/          # 🧠 Business Logic & UI (%95 Paylaşım)
│       │   ├── 📂 ui/              # Tasarım Sistemi ve Ekranlar
│       │   │   ├── 📂 game/        # Matris Logic ve Oyun Görselleri
│       │   │   ├── 📂 home/        # Karşılama ve Mod Seçimi
│       │   │   └── 📂 theme/       # Renk Paleti ve Yazı Tipleri
│       │   ├── 📂 domain/          # Saf Kotlin Modelleri ve Kurallar
│       │   ├── 📂 data/            # Room DB ve DataStore yönetimi
│       │   └── 📂 di/              # Koin Dependency Injection
│       ├── 📂 androidMain/         # 🤖 Android-specific implementation
│       ├── 📂 iosMain/             # 🍎 iOS Objective-C/Swift interoperability
│       └── 📂 desktopMain/         # 🖥️ JVM/Desktop Hooks
└── 📂 iosApp/                      # 🍏 Native iOS entry (XCode Project)
```

---

## 🎨 Tasarım Sistemi (Equatix Design System)

### 🎨 Renk Paleti

| Mod | Renk Adı | Hex | Kullanım Alanı |
| :--- | :--- | :--- | :--- |
| 🌑 **Dark** | **Deep Base** | `#0B1121` | Ana Arka Plan (Göz Dostu) |
| 🌑 **Dark** | **Sky Primary** | `#38BDF8` | Neon Mavi Accent & Odak |
| ☀️ **Light** | **Slate White** | `#F8FAFC` | Akademik Beyaz Arka Plan |
| ⚠️ **Common** | **Error Red** | `#FF453A` | Hatalı İşlemler ve Uyarılar |

---

## 📸 Ekran Görüntüleri

| <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image1.png" width="250"> | <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image2.png" width="250"> | <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image3.png" width="250"> |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image4.png" width="250"> | <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image5.png" width="250"> | <img src="https://raw.githubusercontent.com/vahitkeskin/Equatix/refs/heads/main/screenshots/image6.png" width="250"> |

---

## 📄 Lisans
Bu proje **MIT Lisansı** altında lisanslanmıştır.

<div align="center">

**Vahit Keskin** tarafından ❤️ ile geliştirildi

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/vahit-keskin/)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/vahitkeskin)

---

<sub>⭐ Bu projeyi beğendiyseniz yıldız vermeyi unutmayın!</sub>

</div>