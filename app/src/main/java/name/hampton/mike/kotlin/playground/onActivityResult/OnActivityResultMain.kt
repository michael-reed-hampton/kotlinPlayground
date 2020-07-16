package name.hampton.mike.kotlin.playground.onActivityResult

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import name.hampton.mike.kotlin.playground.R
import kotlin.random.Random

/**
 * This illustrates some lifecycle oddities we need to be aware of.
 *
 * This came out of the problem where the onActivityResult
 *
 *
 *
 *
 * Created by michaelhampton on 7/15/20.
 */
class OnActivityResultMain : Activity() {

    private val activityMap = mutableMapOf<Int, String>()

    override fun onResume() {
        Log.d(javaClass.simpleName, "onResume called")
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.on_activity_result_main)
        val startedFilter = IntentFilter(STARTED.format(A))
        startedFilter.addAction(STARTED.format(B))
        startedFilter.addAction(STARTED.format(C))
        registerReceiver(startedBroadCastReceiver, startedFilter)

        val finishedFilter = IntentFilter(
            FINISHED.format(A)
        )
        finishedFilter.addAction(FINISHED.format(B))
        finishedFilter.addAction(FINISHED.format(C))
        registerReceiver(finishedBroadCastReceiver, finishedFilter)
    }

    override fun onDestroy() {
        unregisterReceiver(startedBroadCastReceiver)
        unregisterReceiver(finishedBroadCastReceiver)
        super.onDestroy()
    }

    private val afterStartedTasks = mutableMapOf<String, Runnable>()
    private val startedBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            Log.d(javaClass.simpleName, "startedBroadCastReceiver ${intent?.action}")
            val runnable: Runnable? = afterStartedTasks.remove(intent?.action)
            runnable?.run()
        }
    }
    private val afterFinishedTasks = mutableMapOf<String, Runnable>()
    private val finishedBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            Log.d(javaClass.simpleName, "finishedBroadCastReceiver ${intent?.action}")
            val runnable: Runnable? = afterFinishedTasks.remove(intent?.action)
            runnable?.run()
        }
    }

    private fun startA(@Suppress("UNUSED_PARAMETER") view: View) {
        val activity = Intent(this, OnActivityResultA::class.java)
        start(activity)
    }

    private fun startB(@Suppress("UNUSED_PARAMETER") view: View) {
        val activity = Intent(this, OnActivityResultB::class.java)
        start(activity)
    }

    private fun startC(@Suppress("UNUSED_PARAMETER") view: View) {
        val activity = Intent(this, OnActivityResultC::class.java)
        start(activity)
    }

    /**
     * This illustrates starting an activity, then immediately starting another.
     *
     * By looking at the log statements, we can see that the first activity does not have 'onCreate'
     * called, until after the second activity exits.
     *
     */
    fun startAB(view: View) {
        startA(view)
        startB(view)
    }

    /**
     * This illustrates starting an activity, waiting for it to notify us that it started,
     * starting a second activity, waiting for it to notify us that it started, then
     * stopping the first activity - that is behind the second.
     *
     * By looking at the log statements, we can see that there is still no onActivityResult
     * that is returned, until after the second activity exits.
     *
     */
    fun startAWaitBWaitStopA(view: View) {
        startA(view)
        afterStartedTasks[STARTED.format(A)] = Runnable {
            startB(view)
            afterStartedTasks[STARTED.format(B)] = Runnable {
                sendBroadcast(Intent(A))
            }
        }
    }

    fun startAWaitBWaitCWaitStopB(view: View) {
        startA(view)
        afterStartedTasks[STARTED.format(A)] = Runnable {
            startB(view)
            afterStartedTasks[STARTED.format(B)] = Runnable {
                startC(view)
                afterStartedTasks[STARTED.format(C)] = Runnable {
                    sendBroadcast(Intent(B))
                }
            }
        }
    }

    private fun start(action: Intent) {
        val requestCode = Random.nextInt(0, Integer.MAX_VALUE)
        activityMap[requestCode] = action.resolveActivity(packageManager).flattenToString()
        startActivityForResult(action, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(
            javaClass.simpleName,
            "requestCode is $requestCode, which maps to the action ${activityMap.remove(requestCode)}"
        )
        super.onActivityResult(requestCode, resultCode, data)
    }
}