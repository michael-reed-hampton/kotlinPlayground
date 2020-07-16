package name.hampton.mike.kotlin.playground.localbinder

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.*


/**
 * https://developer.android.com/guide/components/bound-services
 *
 * The service and client must be in the same application so that the client can cast
 * the returned object and properly call its APIs. The service and client must also be
 * <strong>in the same process</strong>,
 * because this technique does not perform any marshaling across processes.
 */
class LocalService : Service() {
    // Binder given to clients
    private val binder = LocalBinder()

    // Random number generator
    private val mGenerator = Random()

    /** method for clients  */
    val randomNumber: Int
        get() = mGenerator.nextInt(100)

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): LocalService = this@LocalService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}