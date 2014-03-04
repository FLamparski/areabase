package police.methodcalls;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import lamparski.areabase.AreaActivity;
import lamparski.areabase.CacheContentProvider;
import police.errors.APIException;

/**
 * Base class from which all MethodCalls inherit. Contains
 * {@link BaseMethodCall#doCall(String, Map)}, which encodes the request into an
 * URL and fetches the JSON response from the Police API.
 * 
 * @author filip
 * 
 */
public abstract class BaseMethodCall {
	protected final static String ENDPOINT = "http://data.police.uk/api/";
	protected final static int TIMEOUT = 5000;

    private String doCallToAPI(String url) throws Exception{
        URL callUrl = new URL(url);
        HttpURLConnection callConnection = (HttpURLConnection) callUrl
                .openConnection();
        // The ten-second rule:
        // If there's no data in 5s (or TIMEOUT), assume the worst.
        callConnection.setReadTimeout(TIMEOUT);
        // Set the request method to GET.
        callConnection.setRequestMethod("GET");
        int code = callConnection.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new APIException("A non-200 code was returned", code);
        }

        String responseStr;
        responseStr = IOUtils.toString(callConnection.getInputStream());
        callConnection.disconnect();
        return responseStr;
    }

	/**
	 * Gets the response from the API
	 * 
	 * @param method
	 *            The method to call (URL after /api/)
	 * @param params
	 *            A map containing the GET parameters (query for the API)
	 * @return A String, which contains the JSON response.
	 * @throws SocketTimeoutException
	 *             Thrown if the time spent waiting for the response exceeds
	 *             TIMEOUT (10s).
	 * @throws IOException
	 *             Thrown if the connection fails
	 * @throws APIException
	 *             Thrown if the server returns a non-200 HTTP code.
	 */
	protected String doCall(String method, Map<String, String> params)
            throws Exception {
		StringBuilder paramsBuilder = new StringBuilder(ENDPOINT)
				.append(method);
		if (params != null) {
			paramsBuilder.append("?");
			Set<Entry<String, String>> paramsSet = params.entrySet();
			for (Entry<String, String> param : paramsSet) {
				paramsBuilder.append(param.getKey()).append("=")
						.append(param.getValue()).append("&");
			}
		}
		/*
		 * Remove the trailing &
		 */
		String paramString = paramsBuilder.toString();
		if (paramString.endsWith("&")) {
			paramString = paramString.substring(0, paramString.length() - 1);
		}

		String responseStr = doCallToDB(paramString);
        if(responseStr == null){
            responseStr = doCallToAPI(paramString);
        }

		return responseStr;
	}

    private @Nullable String doCallToDB(String url) {
        ContentResolver contentResolver = AreaActivity
                .getAreabaseApplicationContext().getContentResolver();

        String[] selectionArgs = { url,
                Long.toString(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000l) };
        Cursor c = contentResolver.query(CacheContentProvider.POLICE_CACHE_URI, new String[]{ "*" }, "url = ? AND retrievedOn > ?",
                selectionArgs, "retrievedOn DESC");

        if(c == null) { return null; }

        String response;
        if(c.moveToFirst()){
            Log.d("BaseMethodCall", String.format(
                    "A cached instance of %s is available, returning.\n",
                    url));
            response = c.getString(c.getColumnIndex("cachedObject"));
            c.close();
        } else {
            return null;
        }

        Log.d("Mapper", "A cached instance of " + url + " is available, returning.");

        return response;
    }

    private void saveurl(String paramString) {
		File logfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/policedata.log");
		FileWriterWithEncoding fwriter = null;
		try{
			fwriter = new FileWriterWithEncoding(logfile, Charset.forName("UTF-8"), true);
			fwriter.append(String.format("%tF,\"%s\"\n", new Date(), paramString));
		} catch (IOException e){} finally {
			try {
				fwriter.close();
			} catch (Exception e) {
			}
		}
	}
}
