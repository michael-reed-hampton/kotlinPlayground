package name.hampton.mike.kotlin.playground.contentProvider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.Bundle
import android.util.Log

/**
 * Created by michaelhampton on 6/11/20.
 */
open class CallContentProvider : ContentProvider() {

    private var dbHelper: SQLiteOpenHelper? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        TODO("Not yet implemented")
    }

    fun getDataBaseName(): String {
        return "default"
    }

    fun getDataBaseVersion(): Int {
        return 1
    }

    fun getCreateTablesStatements(): List<String?>? {
        return ArrayList()
    }

    fun getDropTablesStatements(): List<String?>? {
        return ArrayList()
    }

    override fun onCreate(): Boolean {
        dbHelper = object : SQLiteOpenHelper(
            context,
            getDataBaseName(),
            null,
            getDataBaseVersion()
        ) {
            override fun onCreate(db: SQLiteDatabase) {
                Log.d(this.javaClass.simpleName, "Creating tables for ${this.javaClass.simpleName}")

                val createTablesStatements = getCreateTablesStatements()

                createTablesStatements?.forEach { statement ->
                    try {
                        db.execSQL(statement)
                    } catch (ex: Exception) {
                        Log.e(this.javaClass.simpleName, "Error running statement $statement")
                    }
                }
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                if (oldVersion < newVersion) {
                    Log.i(
                        this.javaClass.simpleName,
                        "upgrading database from version $oldVersion to $newVersion"
                    )
                    val dropTablesStatements = getDropTablesStatements()

                    dropTablesStatements?.forEach { statement ->
                        try {
                            db.execSQL(statement)
                        } catch (ex: Exception) {
                            Log.e(this.javaClass.simpleName, "Error running statement $statement")
                        }
                    }
                    onCreate(db)
                }
            }
        }
        return true
    }

    private fun getCallHandler(method: String): CallHandler? {
        return callHandlers[method]
    }

    fun putCallHandler(method: String, callHandler: CallHandler): CallHandler? {
        return callHandlers.put(method, callHandler)
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        val NOT_UNDERSTOOD = Bundle();
        NOT_UNDERSTOOD.putBoolean("UNDERSTOOD", false)

        val handler = getCallHandler(method)
        if (null != handler) {
            return handler.call(arg, extras)
        }
        return NOT_UNDERSTOOD
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    private var callHandlers: MutableMap<String, CallHandler> =
        mutableMapOf()
}

interface CallHandler {
    /**
     * @param arg provider-defined String argument.  May be {@code null}.
     * @param extras provider-defined Bundle argument.  May be {@code null}.
     * @return provider-defined return value.  May be {@code null}, which is also
     *   the default for providers which don't implement any call methods.
     */
    fun call(arg: String?, extras: Bundle?): Bundle?
}
