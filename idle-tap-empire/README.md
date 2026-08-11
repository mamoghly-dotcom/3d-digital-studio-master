# Tık İmparatorluğu (Tap Empire)

Ekrana dokunarak altın kazandığın, kazandıkça yükseltmeler satın alıp üretimini
katlayarak büyüttüğün, bağımlılık yaratacak şekilde tasarlanmış bir **idle/clicker**
oyunu. Kotlin ve Jetpack Compose ile yazılmış, native bir Android uygulamasıdır ve
Google Play Store'a yayınlanmaya hazır bir proje iskeleti sağlar.

## Oynanış

- **Dokun, altın kazan**: Büyük altın butonuna her dokunuşunda "Dokunuş Gücü" kadar
  altın kazanırsın.
- **Kombo sistemi**: Arka arkaya hızlı dokunuşlar geçici bir kombo çarpanı biriktirir
  (dokunuş başına +%2, en fazla +%100) — oyuncuyu ekranda aktif tutan temel bağımlılık
  döngüsü.
- **Yükseltmeler**: İki kategori — Dokunuş Yükseltmeleri (her dokunuşun değerini artırır)
  ve Otomatik Üreticiler (saniyede pasif altın üretir, "Stajyer"den "Galaksi
  İmparatorluğu"na kadar 10 kademe). Fiyatlar her seviyede katlanarak artar; x1/x10/x100/MAKS
  toplu satın alma seçeneği var.
- **Boşta kazanç (offline earnings)**: Uygulamayı kapatıp geri döndüğünde, yokken geçen
  süreye göre (en fazla 8 saat, %50 oranla) altın biriktirmiş olursun — klasik idle-oyun
  geri çağırma mekaniği.
- **Başarımlar**: 15 adet başarım, açıldıklarında bonus altın verir ve bildirim (snackbar)
  ile gösterilir.
- **Yeniden Doğuş (Prestige)**: Yeterli altın biriktirdiğinde tüm ilerlemeni sıfırlayıp
  kalıcı "Elmas" kazanabilirsin; her elmas tüm altın kazancına %2 kalıcı bonus verir.
  Bu, uzun vadeli ilerleme hissi ve tekrar oynanabilirlik sağlar.
- **Otomatik kayıt**: İlerleme 5 saniyede bir ve uygulama kapanırken cihaza kaydedilir.

## Proje yapısı

```
idle-tap-empire/
├── app/
│   ├── build.gradle.kts          # Uygulama modülü, applicationId, sürüm bilgisi
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/tapempire/idle/
│       │   ├── MainActivity.kt
│       │   ├── data/GameRepository.kt        # SharedPreferences kalıcılığı
│       │   ├── model/Upgrade.kt              # Yükseltme tanımları + fiyat formülleri
│       │   ├── model/Achievement.kt          # Başarım tanımları
│       │   ├── viewmodel/GameViewModel.kt    # Oyun mantığı (tek kaynak)
│       │   └── ui/                           # Jetpack Compose ekranları
│       └── res/                              # Tema, adaptif ikon, strings
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew / gradlew.bat
```

Tüm oyun mantığı `GameViewModel` içinde toplanmıştır: dokunuş, satın alma, yeniden
doğuş, başarım kontrolü, oyun döngüsü (tick) ve otomatik kayıt. UI katmanı tamamen
`StateFlow<GameUiState>` üzerinden state'i okur; state mutasyonu sadece ViewModel
içinde yapılır.

## Yerelde çalıştırma

Bu proje **Android Studio** (Hedgehog/Iguana veya üzeri, Android SDK 34 kurulu) ile
açılmak üzere tasarlanmıştır:

1. Android Studio → **Open** → `idle-tap-empire` klasörünü seç.
2. Gradle senkronizasyonunun tamamlanmasını bekle (ilk açılışta bağımlılıklar internetten
   indirilir — Jetpack Compose, AndroidX kütüphaneleri vb.).
3. Bir emülatör veya fiziksel cihaz seç, **Run ▶** ile başlat.

Komut satırından derlemek için (Android SDK ve `ANDROID_HOME` kurulu olmalı):

```bash
./gradlew assembleDebug        # Debug APK
./gradlew bundleRelease        # Play Store için .aab (imzalama gerekir, aşağıya bak)
```

> Not: Bu geliştirme ortamında Android SDK bulunmadığı için proje burada derlenip
> test edilememiştir. Kod, standart Kotlin/Jetpack Compose API'lerine göre elle
> gözden geçirilmiştir; Android Studio'da ilk açılışta küçük sürüm uyumsuzlukları
> çıkarsa (ör. Compose BOM / AGP sürümü) Android Studio'nun önerdiği "Upgrade" adımını
> uygulaman yeterlidir.

## Play Store'a yayınlama adımları

1. **Uygulama kimliği**: `app/build.gradle.kts` içindeki `applicationId` (`com.tapempire.idle`)
   kalıcıdır — Play Console'a yüklendikten sonra değiştirilemez. Yayınlamadan önce
   kendi paket adınla değiştirmen önerilir.
2. **İmzalama anahtarı oluştur**:
   ```bash
   keytool -genkey -v -keystore tap-empire-release.keystore \
     -alias tapempire -keyalg RSA -keysize 2048 -validity 10000
   ```
   Bu anahtarı güvenli bir yerde sakla — kaybedersen uygulamayı bir daha güncelleyemezsin.
3. `app/build.gradle.kts` içine bir `signingConfigs` bloğu ekleyip `release`
   `buildType`'ına bağla (anahtar bilgilerini `local.properties` veya ortam
   değişkenlerinden oku, asla repoya commit etme).
4. Yayına hazır paketi oluştur:
   ```bash
   ./gradlew bundleRelease
   ```
   Çıktı: `app/build/outputs/bundle/release/app-release.aab`
5. **Play Console** (https://play.google.com/console) üzerinde yeni bir uygulama oluştur,
   `.aab` dosyasını yükle, mağaza girişini (ikon, ekran görüntüleri, açıklama, gizlilik
   politikası, içerik derecelendirmesi anketi) tamamla ve incelemeye gönder.
6. **Gizlilik politikası**: Bu oyun hiçbir kişisel veri toplamaz, tüm ilerleme sadece
   cihazda (`SharedPreferences`) saklanır — yine de Play Console yayın için bir gizlilik
   politikası URL'si isteyecektir.
7. **İkon/ekran görüntüleri**: Uygulama ikonu adaptif vektör ikon olarak hazır
   (`res/drawable/ic_launcher_*`, `res/mipmap-anydpi-v26/`); mağaza listelemesi için
   ayrıca 512x512 yüksek çözünürlüklü ikon ve en az 2 ekran görüntüsü hazırlaman gerekir.

## Sonraki adımlar için fikirler

- Ses efektleri ve arka plan müziği (şu an sessiz; tık/başarım seslerinin eklenmesi
  bağımlılık hissini güçlendirir).
- Günlük giriş ödülleri, sınırlı süreli olaylar (event) ve reklamla-2x-kazanç gibi
  monetizasyon kancaları (AdMob entegrasyonu).
- Bulut kaydı (Google Play Games Services) ile ilerlemeyi cihazlar arası senkronize etme.
- Daha fazla üretici/yükseltme kademesi ve ikinci bir prestige katmanı.
