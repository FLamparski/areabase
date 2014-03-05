package lamparski.areabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CacheDbOpenHelper extends SQLiteOpenHelper {

	public static class BaseCacheTable {
		public static final String FIELD_ID = "_id";
		public static final String FIELD_URL = "url";
		public static final String FIELD_CACHED_OBJECT = "cachedObject";
		public static final String FIELD_RETRIEVED_ON = "retrievedOn";
		public static final String[] PROJECTION_ALL = new String[] { FIELD_ID,
				FIELD_URL, FIELD_CACHED_OBJECT, FIELD_RETRIEVED_ON };

        public static String getSchema(String TABLE_NAME){
            return "CREATE TABLE " + TABLE_NAME + " (" + FIELD_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + FIELD_URL
                    + " TEXT," + FIELD_RETRIEVED_ON + " INTEGER,"
                    + FIELD_CACHED_OBJECT + " TEXT)";
        }
	}

    public static class OnsCacheTable {
        public static final String TABLE_NAME = "onsCache";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(BaseCacheTable.getSchema(TABLE_NAME));
        }

        public static void onUpgrade(SQLiteDatabase db) {
            Log.w("OnsCacheTable",
                    "Upgrading the cache table, entries will be deleted.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        }
    }

    public static class PoliceCacheTable {
        public static final String TABLE_NAME = "policeCache";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(BaseCacheTable.getSchema(TABLE_NAME));
        }

        public static void onUpgrade(SQLiteDatabase db) {
            Log.w("PoliceCacheTable",
                    "Upgrading the cache table, entries will be deleted.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        }
    }

    public static class MapitCacheTable {
        public static final String TABLE_NAME = "mapitCache";

        public static void onCreate(SQLiteDatabase db){
            db.execSQL(BaseCacheTable.getSchema(TABLE_NAME));
        }

        public static void onUpgrade(SQLiteDatabase db) {
            Log.w("MapitCacheTable",
                    "Upgrading the cache table, entries will be deleted.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        }
    }

	private static final int VERSION = 3;

	public CacheDbOpenHelper(Context context) {
		super(context, "CacheDb", null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		OnsCacheTable.onCreate(db);
        PoliceCacheTable.onCreate(db);
        MapitCacheTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		OnsCacheTable.onUpgrade(db);
        PoliceCacheTable.onUpgrade(db);
        MapitCacheTable.onUpgrade(db);
		onCreate(db);
	}

}
