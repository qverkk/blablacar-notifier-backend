package com.example.notification.firebase

import com.example.notification.Notification
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message

class FirebaseNotification : Notification {
    override fun send(data: Map<String, String>, registeredToken: String) {
        FirebaseMessaging.getInstance().send(
            Message.builder()
                .putAllData(data)
                .setToken(registeredToken)
                .build()
        )
    }
}