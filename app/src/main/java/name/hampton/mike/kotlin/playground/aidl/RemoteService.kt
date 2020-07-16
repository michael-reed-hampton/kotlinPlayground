package name.hampton.mike.kotlin.playground.aidl

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import name.hampton.mike.kotlin.playground.IRemoteService
import name.hampton.mike.kotlin.playground.IRemoteServiceCallback
import name.hampton.mike.kotlin.playground.ISecondary
import name.hampton.mike.kotlin.playground.R

/**
 * Created by michaelhampton on 6/11/20.
 */

class RemoteService : Service() {
    /**
     * This is a list of callbacks that have been registered with the
     * service.  Note that this is package scoped (instead of private) so
     * that it can be accessed more efficiently from inner classes.
     */
    val mCallbacks: RemoteCallbackList<IRemoteServiceCallback> = RemoteCallbackList<IRemoteServiceCallback>()

    var mValue = 0
    private var mNM: NotificationManager? = null

    override fun onCreate() {
        mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // While this service is running, it will continually increment a
        // number.  Send the first message that is used to perform the
        // increment.
        mHandler.sendEmptyMessage(REPORT_MSG)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("RemoteService", "Received start id $startId: $intent")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        // Cancel the persistent notification.
        mNM!!.cancel(R.string.remote_service_started)

        // Tell the user we stopped.
        Toast.makeText(this, R.string.remote_service_stopped, Toast.LENGTH_SHORT).show()

        // Unregister all callbacks.
        mCallbacks.kill()

        // Remove the next pending message to increment the counter, stopping
        // the increment loop.
        mHandler.removeMessages(REPORT_MSG)
    }

    // BEGIN_INCLUDE(exposing_a_service)
    override fun onBind(intent: Intent): IBinder? {
        // Select the interface to return.  If your service only implements
        // a single interface, you can just return it here without checking
        // the Intent.
        if (IRemoteService::class.java.name == intent.action) {
            return mBinder
        }
        return if (ISecondary::class.java.name == intent.action) {
            mSecondaryBinder
        } else null
    }

    /**
     * The IRemoteInterface is defined through IDL
     */
    private val mBinder: IRemoteService.Stub = object : IRemoteService.Stub() {
        override fun registerCallback(cb: IRemoteServiceCallback?) {
            if (cb != null) mCallbacks.register(cb)
        }

        override fun unregisterCallback(cb: IRemoteServiceCallback?) {
            if (cb != null) mCallbacks.unregister(cb)
        }
    }

    /**
     * A secondary interface to the service.
     */
    private val mSecondaryBinder: ISecondary.Stub = object : ISecondary.Stub() {
        override fun getPid(): Int {
            return Process.myPid()
        }

        override fun basicTypes(
            anInt: Int, aLong: Long, aBoolean: Boolean,
            aFloat: Float, aDouble: Double, aString: String?
        ) {
        }
    }
// END_INCLUDE(exposing_a_service)

    // END_INCLUDE(exposing_a_service)
    override fun onTaskRemoved(rootIntent: Intent) {
        Toast.makeText(this, "Task removed: $rootIntent", Toast.LENGTH_LONG).show()
    }

    private val REPORT_MSG = 1

    /**
     * Our Handler used to execute operations on the main thread.  This is used
     * to schedule increments of our value.
     */
    private val mHandler: Handler = Handler1()

    inner class Handler1 : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                REPORT_MSG -> {

                    // Up it goes.
                    val value = ++mValue

                    // Broadcast to all clients the new value.
                    val N = mCallbacks.beginBroadcast()
                    var i = 0
                    while (i < N) {
                        try {
                            mCallbacks.getBroadcastItem(i).valueChanged(value)
                        } catch (e: RemoteException) {
                            // The RemoteCallbackList will take care of removing
                            // the dead object for us.
                        }
                        i++
                    }
                    mCallbacks.finishBroadcast()

                    // Repeat every 1 second.
                    sendMessageDelayed(obtainMessage(REPORT_MSG), 1 * 1000.toLong())
                }
                else -> super.handleMessage(msg)
            }
        }
    }
}