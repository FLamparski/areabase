package lamparski.areabase;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
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

    /**
     * AreaRankTable stores some area scores.
     *
     * DEFINITION:
     * CREATE TABLE areaRank (
     *     _id INTEGER PRIMARY KEY AUTOINCREMENT,
     *     areaId INTEGER,
     *     score REAL,
     *     computedOn INTEGER);
     */
    public static class AreaRankTable {
        public static final String FIELD_ID = "_id";
        public static final String FIELD_RETRIEVED_ON = "computedOn";
        public static final String FIELD_AREA_ID = "areaId";
        public static final String FIELD_AREA_RANK = "score";
        public static final String TABLE_NAME = "areaRank";

        public static void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + FIELD_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + FIELD_AREA_ID
                    + " INTEGER," + FIELD_RETRIEVED_ON + " INTEGER,"
                    + FIELD_AREA_RANK + " REAL)");
        }

        public static void onUpgrade(SQLiteDatabase db) {
            Log.w("AreaRankTable",
                    "Upgrading the cache table, entries will be deleted.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
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

	private static final int VERSION = 4;

	public CacheDbOpenHelper(Context context) {
		super(context, "CacheDb", null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		OnsCacheTable.onCreate(db);
        PoliceCacheTable.onCreate(db);
        MapitCacheTable.onCreate(db);
        AreaRankTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		OnsCacheTable.onUpgrade(db);
        PoliceCacheTable.onUpgrade(db);
        MapitCacheTable.onUpgrade(db);
        AreaRankTable.onUpgrade(db);
		onCreate(db);
	}

}
