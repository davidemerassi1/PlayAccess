package it.unimi.di.ewlab.iss.common.utils

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import it.unimi.di.ewlab.iss.common.model.Configuration
import java.util.*

object PermissionsHandler {

    private const val TAG = "PermissionsHandler"

    fun askAllPermissions(activity: Activity, requestCode: Int) {
        Log.d(TAG, "askAllPermissions")
        val permissionsToAskFor = mutableListOf<String>()

        if (!checkBluetoothPermission(activity)) {
            permissionsToAskFor.add(Manifest.permission.BLUETOOTH)
            permissionsToAskFor.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!checkBluetoothConnectPermission(activity) || !checkBluetoothScanPermission(activity)) {
                permissionsToAskFor.add(Manifest.permission.BLUETOOTH_SCAN)
                permissionsToAskFor.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }

        if (!checkCameraPermission(activity)) {
            permissionsToAskFor.add(Manifest.permission.CAMERA)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!checkPostNotifications(activity)) {
                permissionsToAskFor.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        askPermissions(
            activity,
            requestCode,
            permissionsToAskFor.toTypedArray()
        )
    }

    fun askBluetoothPermissions(activity: Activity, requestCode: Int) {
        Log.d(TAG, "askBluetoothPermissions")
        val permissionsToAskFor = mutableListOf<String>()

        if (!checkBluetoothPermission(activity)) {
            permissionsToAskFor.add(Manifest.permission.BLUETOOTH)
            permissionsToAskFor.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!checkBluetoothConnectPermission(activity) || !checkBluetoothScanPermission(activity)) {
                permissionsToAskFor.add(Manifest.permission.BLUETOOTH_SCAN)
                permissionsToAskFor.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }

        askPermissions(
            activity,
            requestCode,
            permissionsToAskFor.toTypedArray()
        )
    }

    fun askCameraPermission(activity: Activity, requestCode: Int) {
        Log.d(TAG, "askCameraPermission")
        val permissionsToAskFor = mutableListOf<String>()

        if (!checkCameraPermission(activity)) {
            permissionsToAskFor.add(Manifest.permission.CAMERA)
        }

        askPermissions(
            activity,
            requestCode,
            permissionsToAskFor.toTypedArray()
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun askNotificationPermission(activity: Activity, requestCode: Int) {
        Log.d(TAG, "askNotificationPermission")
        val permissionsToAskFor = mutableListOf<String>()

        if (!checkCameraPermission(activity)) {
            permissionsToAskFor.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        askPermissions(
            activity,
            requestCode,
            permissionsToAskFor.toTypedArray()
        )
    }

    fun askConfigurationPermissions(
        configuration: Configuration,
        activity: Activity,
        requestCode: Int,
    ) {
        Log.d(TAG, "askConfigurationPermissions")
        val permissionsToAskFor = mutableListOf<String>()

        if (configuration.buttonActions.isNotEmpty()) {
            if (!checkBluetoothPermission(activity)) {
                permissionsToAskFor.add(Manifest.permission.BLUETOOTH)
                permissionsToAskFor.add(Manifest.permission.BLUETOOTH_ADMIN)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!checkBluetoothConnectPermission(activity) || !checkBluetoothScanPermission(
                        activity
                    )
                ) {
                    permissionsToAskFor.add(Manifest.permission.BLUETOOTH_SCAN)
                    permissionsToAskFor.add(Manifest.permission.BLUETOOTH_CONNECT)
                }
            }
        }

        if (configuration.facialExpressionActions.isNotEmpty()) {
            if (!checkCameraPermission(activity)) {
                permissionsToAskFor.add(Manifest.permission.CAMERA)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!checkPostNotifications(activity)) {
                permissionsToAskFor.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        askPermissions(
            activity,
            requestCode,
            permissionsToAskFor.toTypedArray()
        )
    }

    private fun askPermissions(activity: Activity, requestCode: Int, permissions: Array<String>) {
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissions,
                requestCode
            )
        }
    }

    fun checkConfigPermissions(configuration: Configuration, context: Context): Boolean {
        var res = true
        if (configuration.buttonActions.isNotEmpty())
            res = res && checkAllBluetoothPermissions(context)
        if (configuration.facialExpressionActions.isNotEmpty())
            res = res && checkCameraPermission(context)
        return res
    }

    fun checkIfPermissionIsNeeded(context: Context): Boolean {

        var otherBluetoothPermissionsNeeded = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            otherBluetoothPermissionsNeeded =
                !checkBluetoothConnectPermission(context) || !checkBluetoothScanPermission(context)
        }

        return otherBluetoothPermissionsNeeded ||
                !checkCameraPermission(context) ||
                !checkBluetoothPermission(context)
    }

    fun checkAllPermissions(context: Context): Boolean {
        Log.d(TAG, "checkAllPermissions")

        return checkAllBluetoothPermissions(context) && checkCameraPermission(context)
    }

    //METODO PER CONTROLLARE CHE SIANO GARANTITI I PERMESSI DI LETTURA DALLA MEMORIA DEL DEVICE
    fun checkReadPermission(context: Context): Boolean {
        return checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    //METODO PER CONTROLLARE CHE SIANO GARANTITI I PERMESSI DI SCRITTURA DELLE PREFERENZE DI SISTEMA
    fun checkSettingsPermissions(context: Context): Boolean {
        return checkPermission(context, Manifest.permission.WRITE_SETTINGS)
    }

    fun checkPostNotifications(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            checkPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        else
            true
    }

    //METODO PER CONTROLLARE CHE SIANO GARANTITI I PERMESSI DI ACCESSO ALLA CAMERA DEL DEVICE
    fun checkCameraPermission(context: Context): Boolean {
        return checkPermission(context, Manifest.permission.CAMERA)
    }

    fun checkAllBluetoothPermissions(context: Context): Boolean {
        var otherBluetoothPermissions = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            otherBluetoothPermissions =
                checkBluetoothConnectPermission(context) && checkBluetoothScanPermission(context)
        }

        return otherBluetoothPermissions &&
                checkBluetoothPermission(context)
    }

    //METODO PER CONTROLLARE CHE SIANO GARANTITI I PERMESSI DI BLUETOOTH DEL DEVICE
    private fun checkBluetoothPermission(context: Context): Boolean {
        return checkPermission(context, Manifest.permission.BLUETOOTH)
    }

    //METODO PER CONTROLLARE CHE SIANO GARANTITI I PERMESSI DI BLUETOOTH SCAN DEL DEVICE
    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkBluetoothScanPermission(context: Context): Boolean {
        return checkPermission(context, Manifest.permission.BLUETOOTH_SCAN)
    }

    //METODO PER CONTROLLARE CHE SIANO GARANTITI I PERMESSI DI BLUETOOTH CONNECTION DEL DEVICE
    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkBluetoothConnectPermission(context: Context): Boolean {
        return checkPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
    }

    private fun checkPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
                    context, permission
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val expectedComponentName = ComponentName(
            context,
            "it.unimi.di.ewlab.iss.accessibilityservice.AccessibilityServiceManager"
        )

        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)

        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledService = ComponentName.unflattenFromString(componentNameString)
            if (enabledService != null && enabledService == expectedComponentName)
                return true
        }

        return false
    }
}