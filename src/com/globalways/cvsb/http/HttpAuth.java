/**
 * 
 */
package com.globalways.cvsb.http;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.globalways.cvsb.tools.MyLog;
import com.loopj.android.http.Base64;

/**
 * @author mint
 *
 */
public class HttpAuth {
	private static final String UTF8_CHARSET = "UTF-8";
	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
	private static final String AUTH_APP_ID_KEY = "appid";
	private static final String AUTH_APP_ID_VAL = "G88888888";
	private static final String AUTH_TIME_STAMP_KEY = "timestamp";
	private static final String AUTH_SIGNATURE_KEY = "signature";
	private static final String AUTH_APP_SECRIET = "lyzmwypfjzjswpuglobalways2015";

	private SecretKeySpec secretKeySpec = null;
	private Mac mac = null;

	private String requestMethod;
	private String requestURL;
	private Map<String, Object> requestParams;
	private URL url;

	public HttpAuth(String method, String url, Map<String, Object> params) {
		byte[] secretyKeyBytes;
		try {
			secretyKeyBytes = AUTH_APP_SECRIET.getBytes(UTF8_CHARSET);
			secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
			mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(secretKeySpec);

			requestMethod = method;
			requestURL = url;
			this.url = new URL(url);
			requestParams = params;
			if (requestParams == null) {
				requestParams = new HashMap<String, Object>();
			}
			if (!requestParams.containsKey(AUTH_APP_ID_KEY)) {
				requestParams.put(AUTH_APP_ID_KEY, AUTH_APP_ID_VAL);
			}
			if (!requestParams.containsKey(AUTH_TIME_STAMP_KEY)) {
				requestParams.put(AUTH_TIME_STAMP_KEY, timestamp());
			}
			query2Params(this.url.getQuery());
			MyLog.d("URL", this.url.getPath() + "");
			MyLog.d("URL", this.url.getQuery() + "");

			sign();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void query2Params(String querys) {
		if (null == querys || querys.isEmpty()) {
			return;
		}

		String[] queryArray = querys.split("&");
		int querysLen = queryArray.length;
		for (int i = 0; i < querysLen; i++) {
			String[] keyVal = queryArray[i].split("=");
			if (keyVal.length != 2) {
				continue;
			}
			String key = keyVal[0];
			String val = keyVal[1];
			if (key == null || key.isEmpty() || val == null || val.isEmpty()) {
				continue;
			}

			if (!requestParams.containsKey(key)) {
				requestParams.put(key, val);
			}
		}
	}

	public void sign() {
		SortedMap<String, Object> sortedParamMap = new TreeMap<String, Object>(requestParams);
		String canonicalQS = canonicalize(sortedParamMap);
		String toSign = requestMethod + "\n" + canonicalQS + "\n" + url.getPath() + "\n";

		String hmac = hmac(toSign).trim();
		// String sig = percentEncodeRfc3986(hmac);

		MyLog.d("AUTH", "query:" + canonicalQS);
		MyLog.d("AUTH", "toSign:" + toSign);
		// MyLog.d("AUTH", "signed:" + sig);
		MyLog.d("SIGN", hmac);

		requestParams.put(AUTH_SIGNATURE_KEY, hmac);
	}

	private String hmac(String stringToSign) {
		String signature = null;
		byte[] data;
		byte[] rawHmac;
		try {
			data = stringToSign.getBytes(UTF8_CHARSET);
			rawHmac = mac.doFinal(data);
			signature = Base64.encodeToString(rawHmac, 0);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
		}

		return signature;
	}

	private String timestamp() {
		String timestamp = null;
		Calendar cal = Calendar.getInstance();
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timestamp = dfm.format(cal.getTime());
		return timestamp;
	}

	private String canonicalize(SortedMap<String, Object> sortedParamMap) {
		if (sortedParamMap.isEmpty()) {
			return "";
		}

		StringBuffer buffer = new StringBuffer();
		Iterator<Map.Entry<String, Object>> iter = sortedParamMap.entrySet().iterator();

		while (iter.hasNext()) {
			Map.Entry<String, Object> kvpair = iter.next();
			String key = kvpair.getKey();
			String value = kvpair.getValue().toString();
			if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
				continue;
			}
			// buffer.append(percentEncodeRfc3986(kvpair.getKey()));
			buffer.append(key);
			// buffer.append("=");
			// buffer.append(percentEncodeRfc3986(kvpair.getValue().toString()));
			buffer.append(value);
			if (iter.hasNext()) {
				buffer.append("");
			}
		}
		String canonical = buffer.toString();
		return canonical;
	}

	private String percentEncodeRfc3986(String s) {
		String out;
		try {
			out = URLEncoder.encode(s, UTF8_CHARSET)
			// .replace("+", "%20")
			// .replace("*", "%2A")
			// .replace("%7E", "~")
			;
		} catch (UnsupportedEncodingException e) {
			out = s;
		}
		return out;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public Map<String, Object> getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(Map<String, Object> requestParams) {
		this.requestParams = requestParams;
	}

}
