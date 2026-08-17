package com.henrisusanto.rentipro.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.henrisusanto.rentipro.RentiproApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Reschedules all active rental alarms after device boot or app update,
 * because AlarmManager alarms do not survive a reboot.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val container = (appContext as RentiproApplication).container
                container.rentalRepository.rescheduleAllAlarms()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
