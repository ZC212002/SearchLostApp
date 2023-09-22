import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 5
        const val TABLE_NAME = "tasks"
        const val COLUMN_TASK_ID = "task_id"
        const val COLUMN_TASK_TITLE = "task_title"
        const val COLUMN_TASK_NAME = "task_name"
        const val COLUMN_TASK_PHOTO_URL = "task_photo_url"
        const val COLUMN_TASK_DATE_TIME = "task_date_time"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = (
                "CREATE TABLE $TABLE_NAME (" +
                        "$COLUMN_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_TASK_TITLE TEXT NOT NULL, " +
                        "$COLUMN_TASK_NAME TEXT NOT NULL, " +
                        "$COLUMN_TASK_PHOTO_URL TEXT, " +
                        "$COLUMN_TASK_DATE_TIME TEXT" +
                        ")"
                )
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when {
            oldVersion < 2 -> {
                // If the old version is 1, perform the necessary upgrade to version 2
                val alterTableQuery = (
                        "ALTER TABLE $TABLE_NAME " +
                                "ADD COLUMN $COLUMN_TASK_TITLE TEXT NOT NULL DEFAULT ''"
                        )
                db.execSQL(alterTableQuery)
            }
            oldVersion < 3 -> {
                // If the old version is 1 or 2, perform the necessary upgrade to version 3
                // Add any other database changes for version 3 here
                // For example:
                // db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN new_column INTEGER DEFAULT 0")
            }
            oldVersion < 4 -> {
                // If the old version is 1, 2 or 3, perform the necessary upgrade to version 4
                // Add the new column for task photo URL
                val alterTableQuery = (
                        "ALTER TABLE $TABLE_NAME " +
                                "ADD COLUMN $COLUMN_TASK_PHOTO_URL TEXT"
                        )
                db.execSQL(alterTableQuery)
            }
            oldVersion < 5 -> {
                val alterTableQuery = (
                        "ALTER TABLE $TABLE_NAME " +
                                "ADD COLUMN $COLUMN_TASK_DATE_TIME TEXT"
                        )
                db.execSQL(alterTableQuery)
            }
        }
    }

    fun insertImageURL(taskId: Int, imageURL: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK_PHOTO_URL, imageURL)
        db.update(TABLE_NAME, values, "$COLUMN_TASK_ID=?", arrayOf(taskId.toString()))
        db.close()
    }
}
