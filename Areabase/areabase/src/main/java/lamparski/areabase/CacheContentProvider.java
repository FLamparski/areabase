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
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import lamparski.areabase.CacheDbOpenHelper.AreaRankTable;
import lamparski.areabase.CacheDbOpenHelper.OnsCacheTable;

/**
 * Good for caching purposes.
 */
public class CacheContentProvider extends ContentProvider {

	private CacheDbOpenHelper helper;
	public static final String AUTHORITY = "lamparski.areabase.content";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final Uri ONS_CACHE_URI = Uri.withAppendedPath(CONTENT_URI,
			OnsCacheTable.TABLE_NAME);
    public static final Uri POLICE_CACHE_URI = Uri.withAppendedPath(CONTENT_URI,
            CacheDbOpenHelper.PoliceCacheTable.TABLE_NAME);
    public static final Uri MAPIT_CACHE_URI = Uri.withAppendedPath(CONTENT_URI,
            CacheDbOpenHelper.MapitCacheTable.TABLE_NAME);
    public static final Uri AREARANK_CACHE_URI = Uri.withAppendedPath(CONTENT_URI,
            AreaRankTable.TABLE_NAME);

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = helper.getWritableDatabase();
		return db.delete(getTableName(uri), selection, selectionArgs);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String tbl = getTableName(uri);
		SQLiteDatabase db = helper.getWritableDatabase();
		long newrow = db.insert(tbl, null, values);
		return Uri.withAppendedPath(CONTENT_URI, Long.toString(newrow));
	}

	@Override
	public boolean onCreate() {
		helper = new CacheDbOpenHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = helper.getReadableDatabase();
		return db.query(getTableName(uri), projection, selection,
				selectionArgs, null, null, sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String tbl = getTableName(uri);
		SQLiteDatabase db = helper.getWritableDatabase();
		return db.update(tbl, values, selection, selectionArgs);
	}

	private String getTableName(Uri uri) {
		String uriv = uri.getPath();
		return uriv.replace("/", "");
	}
}
