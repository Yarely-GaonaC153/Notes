package com.example.inventory.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.inventory.R
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Obtener los datos de la notificación (fecha y hora)
        val fecha = inputData.getLong("fecha", 0L)
        val hora = inputData.getLong("hora", 0L)

        if (fecha != 0L && hora != 0L) {
            showNotification(fecha, hora)
        }

        return Result.success()
    }

    private fun showNotification(fecha: Long, hora: Long) {
        val context = applicationContext

        val channelId = "notes_notifications"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Verificar si la versión de Android es Oreo o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notes Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Cambia el ícono según tu diseño
            .setContentTitle("Recordatorio de Nota")
            .setContentText("Es hora de revisar tu nota.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification) // Notificación ID 1
    }

    companion object {
        fun scheduleNotification(context: Context, fecha: Long, hora: Long) {
            // Calcula el tiempo hasta la fecha y hora seleccionada
            val currentTimeMillis = System.currentTimeMillis()
            val timeUntilNotification = fecha - currentTimeMillis + hora

            // Verifica que la hora de la notificación esté en el futuro
            if (timeUntilNotification > 0) {
                val workData = Data.Builder()
                    .putLong("fecha", fecha)
                    .putLong("hora", hora)
                    .build()

                // Configura el WorkManager para ejecutar la notificación después de la diferencia de tiempo
                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(timeUntilNotification, TimeUnit.MILLISECONDS)
                    .setInputData(workData)
                    .build()

                // Encolar el trabajo con WorkManager
                androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
            } else {
                // Si la fecha y hora ya han pasado, no programar la notificación
                // Aquí puedes hacer alguna acción en caso de que se haya intentado programar para una hora pasada
            }
        }
    }
}
