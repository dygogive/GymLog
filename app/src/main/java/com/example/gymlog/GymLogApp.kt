// Визначаємо пакет, де знаходиться цей файл
package com.example.gymlog

// Імпортуємо необхідні бібліотеки та класи
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Головний клас додатка, який успадковує Application.
 *
 * @HiltAndroidApp - анотація Dagger Hilt, яка:
 *   1. Генерує всі необхідні компоненти DI (Dependency Injection)
 *   2. Відповідає за ініціалізацію Dagger Hilt у додатку
 *   3. Автоматично створює ApplicationComponent - кореневий компонент DI
 *
 * Цей клас має бути вказаний в AndroidManifest.xml у тезі <application>
 */
@HiltAndroidApp
class GymLogApp: Application() {
    // Можна додати власну логіку ініціалізації додатка, якщо потрібно
    // Наприклад, ініціалізація аналітики, логування тощо
}

/**
 * Ключові моменти:
 *
 * Призначення класу:
 *
 * Це точка входу для Dagger Hilt у вашому додатку
 *
 * Замінює стандартний клас Application для підтримки DI
 *
 * Робота анотації @HiltAndroidApp:
 *
 * Генерує код для Dependency Injection на рівні всього додатка
 *
 * Створює ієрархію компонентів Dagger Hilt
 *
 * Дозволяє використовувати ін'єкцію в Activity, Fragment, Service та ін.
 *
 * Налаштування в AndroidManifest.xml:
 *
 * xml
 * Copy
 * <application
 *     android:name=".GymLogApp"
 *     ...>
 * </application>
 * Run HTML
 * Переваги:
 *
 * Спрощує налаштування Dagger Hilt
 *
 * Автоматизує процес Dependency Injection
 *
 * Забезпечує єдину точку ініціалізації для DI
 *
 * Можливі розширення:
 *
 * Можна перевизначити onCreate() для додаткової ініціалізації
 *
 * Додати глобальні обробники помилок
 *
 * Ініціалізувати інші бібліотеки (наприклад, Firebase, Analytics)
 *
 * Цей клас є обов'язковим для роботи Dagger Hilt у Android-додатку і служить основою для всієї системи Dependency Injection у вашому проекті.
 * */