package lamparski.areabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CacheDbOpenHelper extends SQLiteOpenHelper {

	public static class CacheTable {
		public static final String TABLE_NAME = "onsCache";
		public static final String FIELD_ID = "_id";
		public static final String FIELD_URL = "url";
		public static final String FIELD_CACHED_OBJECT = "cachedObject";
		public static final String FIELD_RETRIEVED_ON = "retrievedOn";
		public static final String[] PROJECTION_ALL = new String[] { FIELD_ID,
				FIELD_URL, FIELD_CACHED_OBJECT, FIELD_RETRIEVED_ON };

		public static void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + FIELD_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT," + FIELD_URL
					+ " TEXT," + FIELD_RETRIEVED_ON + " INTEGER,"
					+ FIELD_CACHED_OBJECT + " TEXT)");
		}

		public static void onUpgrade(SQLiteDatabase db) {
			Log.w("CacheTable",
					"Upgrading the cache table, entries will be deleted.");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		}
	}

	private static final int VERSION = 1;

	public CacheDbOpenHelper(Context context) {
		super(context, "CacheDb", null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		CacheTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		CacheTable.onUpgrade(db);
		onCreate(db);
	}

}
