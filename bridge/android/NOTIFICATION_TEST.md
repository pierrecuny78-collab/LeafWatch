# LeafWatch — premier test de notification Android

Cette étape déclenche une **vraie notification locale Android** depuis l'application compagnon LeafVibe. Galaxy Wearable peut ensuite la recopier sur la Galaxy Fit3 si les notifications de LeafVibe sont autorisées.

Ce test ne prétend pas envoyer directement un paquet Bluetooth à la montre : le relais est assuré par le système de notifications Android / Galaxy Wearable. Il fonctionne sans Internet.

## Source à intégrer

Copier `LeafWatchNotification.kt` dans le package :
`app.leafvibe.leafwatch.notifications`

La source utilise AndroidX Core (`NotificationCompat` et `ContextCompat`). Si le projet hôte ne possède pas déjà AndroidX Core, ajouter une version compatible avec le catalogue de versions / la configuration Gradle du projet hôte.

## Permission Android

Ajouter dans le `AndroidManifest.xml` de l'application hôte, à l'intérieur de `<manifest>` et avant `<application>` :

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

Sur Android 13 et versions ultérieures, l'application doit aussi demander cette permission à l'utilisateur depuis une Activity. Exemple Kotlin (dans l'Activity, pas dans le helper) :

```kotlin
private val requestNotifications =
    registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val sent = app.leafvibe.leafwatch.notifications.LeafWatchNotification
                .showTest001(this)
            // Afficher un retour dans l'interface selon sent.
        }
    }

private fun sendLeafWatchTest001() {
    if (android.os.Build.VERSION.SDK_INT >= 33 &&
        !app.leafvibe.leafwatch.notifications.LeafWatchNotification
            .hasPostNotificationPermission(this)
    ) {
        requestNotifications.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    } else {
        val sent = app.leafvibe.leafwatch.notifications.LeafWatchNotification
            .showTest001(this)
        // Afficher un retour dans l'interface selon sent.
    }
}
```

Relier `sendLeafWatchTest001()` à un bouton intitulé « Envoyer le message TEST 001 ».

## Vérification sur le téléphone et la montre

1. Installer la nouvelle APK de l'application hôte après intégration et compilation.
2. Autoriser les notifications LeafVibe / LeafWatch sur Android.
3. Dans Galaxy Wearable, vérifier que les notifications de cette application sont autorisées.
4. Désactiver les données mobiles et le Wi-Fi (ne pas désactiver Bluetooth).
5. Appuyer sur « Envoyer le message TEST 001 ».
6. Vérifier d'abord la notification sur le téléphone, puis sur la Fit3.

## Limites et sécurité

- Cette fonction crée une notification locale ; elle ne prouve pas le protocole BLE natif Fit3.
- Aucune connexion Internet, permission Internet, API distante, ni UUID BLE n'est utilisée.
- Si Android refuse la permission, la fonction renvoie `false` et n'annonce pas un envoi réussi.
- La Fit3 ne montrera la notification que si Galaxy Wearable autorise le relais de notifications pour l'application hôte et si la montre est connectée.
- Ce dépôt ne contient pas encore le projet Gradle complet de l'application Android : cette source est un module à intégrer, pas une APK autonome.
