package it.unimi.di.ewlab.iss.common.model

import android.content.res.Resources
import android.os.Build
import android.os.Build.VERSION_CODES
import java.io.Serializable

class DeviceInfo : Serializable {
    var screenHeightDp = 0
    var screenWidthDp = 0
    var densityDpi = 0
    var heightPixels = 0
    var widthPixels = 0
    var SDK_INT = 0
    var manufacturer: String? = null
    var model: String? = null
    var baseOs: String? = null
    var buildVersionRelease: String? = null

    constructor(
        screenHeightDp: Int,
        screenWidthDp: Int,
        densityDpi: Int,
        heightPixels: Int,
        widthPixels: Int,
    ) {
        this.screenHeightDp = screenHeightDp
        this.screenWidthDp = screenWidthDp
        this.densityDpi = densityDpi
        this.heightPixels = heightPixels
        this.widthPixels = widthPixels
    }

    companion object {
        val Local = DeviceInfo()
    }

    private constructor() {
        screenHeightDp = Resources.getSystem().configuration.screenHeightDp
        screenWidthDp = Resources.getSystem().configuration.screenWidthDp
        densityDpi = Resources.getSystem().displayMetrics.densityDpi
        heightPixels = Resources.getSystem().displayMetrics.heightPixels
        widthPixels = Resources.getSystem().displayMetrics.widthPixels

        manufacturer = Build.MANUFACTURER
        model = Build.MODEL
        SDK_INT = Build.VERSION.SDK_INT
        var baseOs = Build.VERSION.BASE_OS
        var buildVersionRelease = Build.VERSION.RELEASE

        val fields = VERSION_CODES::class.java.fields
        for (field in fields) {
            val fieldName = field.name
            var fieldValue = -1
            try {
                fieldValue = field.getInt(Any())
            }
            catch (_: IllegalArgumentException) {}
            catch (_: IllegalAccessException) {}
            catch (_: NullPointerException) {}
            if (fieldValue == Build.VERSION.SDK_INT) {
                baseOs = fieldName
                buildVersionRelease = Build.VERSION.RELEASE
            }
        }

        this.baseOs = baseOs
        this.buildVersionRelease = buildVersionRelease
    }
}