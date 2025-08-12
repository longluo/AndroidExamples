package me.longluo.tcpclient

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean


class TcpClientService : Service() {
    private val working = AtomicBoolean(true)
    private var socket: Socket? = null
    private var dataInputStream: DataInputStream? = null
    private var dataOutputStream: DataOutputStream? = null
    private val message = "Hello Server"
    private val runnable = Runnable {
        try {
            val ip = InetAddress.getByName(IP)
            socket = Socket(ip, PORT)
            dataInputStream = DataInputStream(socket!!.getInputStream())
            dataOutputStream = DataOutputStream(socket!!.getOutputStream())
            while (working.get()) {
                try {
                    dataOutputStream!!.writeUTF(message)
                    Log.i(TAG, "Received: " + dataInputStream!!.readUTF())
                    Thread.sleep(2000L)
                } catch (e: IOException) {
                    e.printStackTrace()
                    try {
                        dataInputStream!!.close()
                        dataOutputStream!!.close()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    try {
                        dataInputStream!!.close()
                        dataOutputStream!!.close()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        startMeForeground()
        Thread(runnable).start()
    }

    override fun onDestroy() {
        working.set(false)
    }

    private fun startMeForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = packageName
            val channelName = "Tcp Client Background Service"
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Tcp Client is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)
        } else {
            startForeground(1, Notification())
        }
    }

    companion object {
        val TAG = TcpClientService::class.java.simpleName
//        private const val IP = "192.168.31.250"
        private const val IP = "192.168.31.53"
        private const val PORT = 9876
    }
}