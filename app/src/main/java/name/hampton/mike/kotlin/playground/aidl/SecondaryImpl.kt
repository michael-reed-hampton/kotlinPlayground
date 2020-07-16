package name.hampton.mike.kotlin.playground.aidl

import android.os.Process
import android.util.Log
import name.hampton.mike.kotlin.playground.ISecondary

/**
 * Created by michaelhampton on 6/11/20.
 */
class SecondaryImpl : ISecondary.Stub() {

    override fun getPid(): Int =
        Process.myPid()

    override fun basicTypes(
        anInt: Int,
        aLong: Long,
        aBoolean: Boolean,
        aFloat: Float,
        aDouble: Double,
        aString: String
    ) {
        Log.d(
            this.javaClass.simpleName,
            "Types int $anInt, Long $aLong, Boolean $aBoolean, Float $aFloat, Double $aDouble, String $aString"
        )
    }
}