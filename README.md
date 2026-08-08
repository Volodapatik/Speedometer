# Speedometer

[![Build Debug APK](https://github.com/Volodapatik/Speedometer/actions/workflows/build.yml/badge.svg)](https://github.com/Volodapatik/Speedometer/actions/workflows/build.yml)

Простой GPS-спидометр для Android.

Открыл приложение — сразу видишь текущую скорость. Никаких кнопок Start/Stop.

## Возможности

- Автоматический запуск GPS при открытии приложения
- Большой круглый спидометр с текущей скоростью в км/ч
- Современный тёмный интерфейс (Material 3 + Jetpack Compose)
- Скорость округляется до целого числа
- Лёгкая фильтрация GPS-шума без сильного отставания
- Работает без интернета (только GPS)

## Требования

- **minSdk**: 26 (Android 8.0)
- **targetSdk / compileSdk**: 35
- Android Gradle Plugin 8.7.3
- Kotlin 2.0.21
- JDK 17

## Разрешения

- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`

Приложение запрашивает разрешение при первом запуске. Без разрешения скорость не отображается.

## Как скачать APK

### Из GitHub Actions (рекомендуется)

1. Открой вкладку [Actions](https://github.com/Volodapatik/Speedometer/actions)
2. Выбери последний успешный workflow **Build Debug APK**
3. Внизу страницы скачай артефакт **speedometer-debug-apk**
4. Распакуй ZIP и установи APK на телефон

### Локальная сборка

```bash
git clone https://github.com/Volodapatik/Speedometer.git
cd Speedometer
./gradlew assembleDebug
```

APK появится в:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Как работает GPS

1. При запуске проверяются разрешения на геолокацию.
2. Если разрешений нет — запрашиваются.
3. После получения разрешений запускается Fused Location Provider (высокий приоритет точности).
4. Скорость берётся из `Location.speed` (м/с) и переводится в км/ч: `× 3.6`.
5. Значение округляется до ближайшего целого.
6. Пока GPS ещё не определился — показывается **—**.
7. При неподвижности / очень низкой скорости — **0 км/ч**.

## Release

Workflow `release.yml` срабатывает при создании GitHub Release / тега.

Сейчас собирается debug-подписанный APK (удобно для быстрого распространения).

### Подписанный release APK (опционально)

Для полноценной подписи release APK добавь в GitHub Secrets:

- `KEYSTORE_BASE64` — base64 содержимое `.jks` / `.keystore`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

После этого можно доработать `release.yml` для `assembleRelease` с подписью. Keystore в репозиторий не кладётся.

## Архитектура

- Kotlin
- Jetpack Compose + Material 3
- Fused Location Provider (Google Play Services Location)
- ViewModel + StateFlow
- Минимальное количество зависимостей

## Лицензия

MIT
