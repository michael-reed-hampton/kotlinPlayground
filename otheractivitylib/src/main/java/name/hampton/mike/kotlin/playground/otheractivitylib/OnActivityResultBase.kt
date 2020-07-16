package name.hampton.mike.kotlin.playground.otheractivitylib

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView

const val STARTED:String = "%s.isStarted"
const val FINISHED:String = "%s.isFinished"

/**
 * A very simple base activity used to track lifecycle events in an
 * Activity, and see how an activity that has siblings from a common 'parent' activity
 * acts in relation to their lifecycle events.
 *
 * Created by michaelhampton on 7/15/20.
 */
@SuppressLint("Registered")
open class OnActivityResultBase(val name: String) : Activity() {

    private val startedKey = STARTED.format(name);
    private val finishedKey = FINISHED.format(name);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.on_activity_result_sub)
        val textView: TextView = findViewById(R.id.name)
        textView.text = name

        registerReceiver(broadCastReceiver, IntentFilter(name))
        Log.d(javaClass.simpleName, "Sending broadcast $startedKey")
        sendBroadcast(Intent(startedKey))
    }

    override fun onDestroy() {
        unregisterReceiver(broadCastReceiver)
        super.onDestroy()
    }


    fun stop(@Suppress("UNUSED_PARAMETER") view: View?) {
        Log.d(javaClass.simpleName, "Stop called")
        finish()
    }


    override fun finish() {
        sendBroadcast(Intent(finishedKey))
        super.finish()
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            stop(null)
        }
    }
}