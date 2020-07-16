package name.hampton.mike.kotlin.playground.contentProvider

import android.net.Uri
import android.os.Bundle
import android.widget.Toast

/**
 * Created by michaelhampton on 6/11/20.
 */
class ProtocolContentProvider : CallContentProvider() {

    init {
        putCallHandler("alert", object : CallHandler {
            override fun call(arg: String?, extras: Bundle?): Bundle? {
                Toast.makeText(context, "Alert $arg", Toast.LENGTH_LONG).show()
                val understood = Bundle()
                understood.putBoolean("UNDERSTOOD", true)
                understood.putBoolean("PROCESSED", true)

                val uri =
                    Uri.parse("content://name.hampton.mike.kotlin.playground/alerts")
                context!!.contentResolver.notifyChange(uri, null)

                return understood
            }
        })
    }
}