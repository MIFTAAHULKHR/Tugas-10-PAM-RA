package org.example.project

import android.os.Build

actual class DeviceInfo {
    actual val model: String = Build.MODEL
    actual val osVersion: String = "Android ${Build.VERSION.RELEASE}"
}