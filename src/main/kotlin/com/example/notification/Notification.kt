package com.example.notification

interface Notification {
    fun send(data: Map<String, String>, registeredToken: String)
}
