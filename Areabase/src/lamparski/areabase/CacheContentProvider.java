package lamparski.areabase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class CacheContentProvider extends ContentProvider {

	private CacheDbOpenHelper helper;
	public static final String AUTHORITY = "lamparski.areabase.content";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final Uri CACHE_URI = Uri.withAppendedPath(CONTENT_URI,
			CacheDbOpenHelper.CacheTable.TABLE_NAME);

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
