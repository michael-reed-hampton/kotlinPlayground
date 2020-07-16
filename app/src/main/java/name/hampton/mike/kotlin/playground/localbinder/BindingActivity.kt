package name.hampton.mike.kotlin.playground.localbinder

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import name.hampton.mike.kotlin.playground.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by michaelhampton on 6/11/20.
 */
class BindingActivity : Activity() {
    private lateinit var mService: LocalService
    private var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocalService.LocalBinder
            mService = binder.getService()
            mBound = true
            Log.d(this.javaClass.simpleName, "bound=true")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
            Log.d(this.javaClass.simpleName, "bound=false")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view -> onButtonClick(view) }
    }

    override fun onStart() {
        super.onStart()
        // Bind to LocalService
        Intent(this, LocalService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        Log.d(this.javaClass.simpleName, "trying to bind")
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
        Log.d(this.javaClass.simpleName, "trying to unbind")
    }

    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute)  */
    fun onButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        Log.d(this.javaClass.simpleName, "bound is $mBound")
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            val num: Int = mService.randomNumber
            Toast.makeText(this, "number: $num", Toast.LENGTH_SHORT).show()
        }
    }
}