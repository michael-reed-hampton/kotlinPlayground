package name.hampton.mike.kotlin.playground.contentProvider

import android.app.Activity
import android.content.ContentProviderClient
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import name.hampton.mike.kotlin.playground.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by michaelhampton on 6/11/20.
 */
class ContentProviderActivity : Activity() {

    private var sessionContentProviderClient: ContentProviderClient? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view -> onButtonClick(view) }
    }

    override fun onStart() {
        super.onStart()
        // Get content provider interface
        val context: Context = this
        sessionContentProviderClient =
            context.contentResolver.acquireContentProviderClient("name.hampton.mike.kotlin.playground")


        val uri =
            Uri.parse("content://name.hampton.mike.kotlin.playground/alerts")
        val handler : Handler? = Handler()

        val co: ContentObserver = object : ContentObserver(handler) {
            override fun deliverSelfNotifications(): Boolean {
                return false
            }

            override fun onChange(selfChange: Boolean, uri: Uri) {
                Log.d(javaClass.simpleName, "changed data uri is $uri")
            }
        }

        context.contentResolver.registerContentObserver(uri, true, co)
        Log.d(this.javaClass.simpleName, "trying to get to content provider")
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sessionContentProviderClient!!.close()
        }
        sessionContentProviderClient = null
        Log.d(this.javaClass.simpleName, "trying to eliminate connection to content provider")
    }

    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute)  */
    fun onButtonClick(@Suppress("UNUSED_PARAMETER") v: View) {
        Log.d(this.javaClass.simpleName, "sessionContentProviderClient is $sessionContentProviderClient")
        if (sessionContentProviderClient != null) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            val returnBundle = sessionContentProviderClient!!.call("alert", "Content provider test", null)

            Log.d(this.javaClass.simpleName, "sessionContentProviderClient returned $returnBundle")
        }
    }
}