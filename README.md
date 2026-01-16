# Equatix

### Cross-Platform Math Matrix Puzzle — Built with Kotlin Multiplatform

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-purple?logo=kotlin)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-blue?logo=jetpackcompose)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Platforms](https://img.shields.io/badge/Platforms-Android%20%7C%20iOS%20%7C%20Desktop-lightgrey)]()
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture%20%2B%20MVVM-orange)]()
[![License](https://img.shields.io/badge/License-MIT-green)]()

**Equatix**, tek bir Kotlin kod tabanı üzerinden **Android, iOS ve Desktop** platformlarında
**native performans** ile çalışan, matematik temelli bir bulmaca oyunudur.

Bu proje bir “oyun demosu” değildir.  
Amaç; **Kotlin Multiplatform + Compose Multiplatform** kullanılarak,
gerçek dünyada sürdürülebilir, test edilebilir ve ölçeklenebilir
bir ürün mimarisinin nasıl kurulacağını göstermektir.

## İçindekiler

1. Proje Amacı ve Kapsamı
2. Temel Özellikler
3. Teknik Vizyon
4. Kullanılan Teknolojiler
5. Mimari Yaklaşım
6. Kritik Mühendislik Kararları
7. Kurulum ve Çalıştırma
8. Proje Yapısı
9. Yol Haritası
10. Lisans ve Geliştirici

---

## Proje Amacı ve Kapsamı

Equatix’in temel amacı:

- **Tek kod tabanı** ile çoklu platform desteği sağlamak
- UI dahil olmak üzere **maksimum kod paylaşımı** yapmak
- Platforma özgü gereksinimleri **kontrollü ve izole** biçimde ele almak
- “Cross-platform ama native olmayan” çözümlerden **bilinçli şekilde uzak durmak**

Flutter veya React Native gibi çözümlerin aksine:

- JavaScript bridge kullanılmaz
- WebView tabanlı render yoktur
- Android tarafında JVM bytecode
- iOS tarafında LLVM native binary üretilir

---

## Temel Özellikler

- Android, iOS ve Desktop (macOS / Windows / Linux) desteği
- Dinamik ve ölçeklenebilir matematik grid sistemi (2x2 → 5x5)
- Her zaman **çözülebilir** olacak şekilde üretilen oyun tahtaları
- Compose Multiplatform ile **tek UI kodu**
- Kalıcı skor ve ayar yönetimi
- Haptic feedback ve ses entegrasyonu
- Düşük donanımlı cihazlarda dahi stabil animasyon performansı

---

## Teknik Vizyon

Bu proje aşağıdaki teknik prensipler üzerine kuruludur:

- **Single Source of Truth**
- **Separation of Concerns**
- **Unidirectional Data Flow**
- **Framework-agnostic domain katmanı**
- **Test edilebilir iş mantığı**

UI bir “detaydır”.  
Asıl odak **iş kuralları**, **algoritmalar** ve **mimari tutarlılıktır**.

---

## Kullanılan Teknolojiler

### Dil & Platform

- **Kotlin 2.1.0**
- **Kotlin Multiplatform**
- **Gradle Kotlin DSL**

### UI

- **Compose Multiplatform**
- Declarative UI yaklaşımı
- StateFlow tabanlı state yönetimi

### Mimari

- **Clean Architecture**
- **MVVM**
- Platform bağımsız domain katmanı

### Veri & Eşzamanlılık

- **Room (KMP uyumlu)**
- **SQLite**
- **Kotlin Coroutines**
- **Flow**

### Bağımlılık Yönetimi

- Manual DI / Koin (platform bağımsız olacak şekilde)

---

## Mimari Yaklaşım

Proje **Clean Architecture** prensiplerine uygun olarak üç ana katmana ayrılmıştır.

### 1. Domain Layer

- Saf Kotlin
- Hiçbir platform veya UI bağımlılığı yoktur
- Tüm iş kuralları burada yer alır

İçerik:

- UseCase’ler
- Entity / Model sınıfları
- Repository arayüzleri

Bu katman **en stabil** katmandır ve kolay kolay değişmez.

---

### 2. Data Layer

- Domain katmanındaki repository arayüzlerini implemente eder
- Veri kaynağı detaylarını izole eder

İçerik:

- Room database
- DAO’lar
- Repository implementasyonları

UI bu katmanı **asla doğrudan bilmez**.

---

### 3. Presentation Layer

- Compose UI
- ScreenModel / ViewModel yapısı
- StateFlow ile state yönetimi

Bu katman:

- Domain use-case’lerini çağırır
- UI state üretir
- Platformdan bağımsızdır

---

## Kritik Mühendislik Kararları

### Çözülebilir Grid Üretimi

Oyun tahtası rastgele üretilmez.

- Önce geçerli matematiksel çözümler üretilir
- Backtracking algoritması ile doğrulanır
- Zorluk seviyesine göre hücreler gizlenir

Bu yaklaşım sayesinde:

- “İmkânsız” oyun senaryoları oluşmaz
- Oyuncu hatası ile algoritma hatası ayrılır

---

### Glassmorphism ve Performans

Gerçek zamanlı blur efektleri mobil cihazlarda maliyetlidir.

Bu nedenle:

- Gerçek blur yerine katmanlı yarı saydam yüzeyler
- Gradient ve noise kombinasyonları
- Canvas tabanlı özel çizimler kullanılmıştır

Sonuç:

- Görsel olarak tatmin edici
- Performans olarak stabil
- 60 FPS hedefi korunur

---

### Platforma Özgü Kod Yönetimi

Platform farkları `expect / actual` mekanizması ile çözülür.

- Android: Activity lifecycle entegrasyonu
- iOS: UIKit + ComposeUIViewController
- Desktop: JVM pencere ve lifecycle yönetimi

Platform kodları **minimumda tutulur**.

---

## Kurulum ve Çalıştırma

### Gereksinimler

- JDK 17 veya 21
- Android Studio (KMP destekli)
- Xcode 15+ (iOS için)

---

### Android

```bash
./gradlew :composeApp:installDebug

Desktop
./gradlew :composeApp:run
iOS
iosApp.xcodeproj dosyasını Xcode ile aç
Simulator veya gerçek cihaz seç
Run
İlk derleme süresi uzun olabilir (Kotlin/Native).

Proje Yapısı
Equatix/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/
│   │   │   ├── domain/
│   │   │   ├── data/
│   │   │   ├── ui/
│   │   │   └── App.kt
│   │   ├── androidMain/
│   │   ├── iosMain/
│   │   └── desktopMain/
├── iosApp/
├── gradle/
└── settings.gradle.kts
Kodun büyük kısmı commonMain altındadır.

Yol Haritası
Online leaderboard
Bulut senkronizasyonu
Çoklu dil desteği
Tema sistemi
Accessibility iyileştirmeleri
Lisans ve Geliştirici
MIT Lisansı altında sunulmaktadır.

Geliştirici: Vahit Keskin Computer Engineer — Senior Android Developer

Bu proje, modern mobil ve multiplatform yazılım geliştirme yaklaşımlarını gerçekçi bir örnek üzerinden göstermek amacıyla geliştirilmiştir.