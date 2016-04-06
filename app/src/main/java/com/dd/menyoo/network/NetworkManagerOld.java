package com.dd.menyoo.network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyStore;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.dd.menyoo.common.SharedPrefManager;
import com.dd.menyoo.model.ProgressModel;


public class NetworkManagerOld extends AsyncTask<String, ProgressModel, Object> {

    private static Context thiz;
    long totalSize;
    private EnCallType currMode;
    private ReturnTypeForReponse currRetType;
    private List<NameValuePair> currData;
    private JSONObject jObj;
    private TaskCompleted listener;
    // private FileBody fileBody;
    private String downloadFilePath;
    private Object objToPassBack;
    private OperationType operation;
    private boolean isFirstTime;

    public NetworkManagerOld(Context thiz, EnCallType mode,
                             ReturnTypeForReponse retType, List<NameValuePair> data,
                             TaskCompleted listener) {
        this.thiz = thiz;
        this.currMode = mode;
        this.currRetType = retType;
        this.currData = data;
        this.listener = listener;
    }

    public NetworkManagerOld(Context thiz, EnCallType mode,
                             ReturnTypeForReponse retType, List<NameValuePair> data,
                             OperationType operation, TaskCompleted listener, boolean isFirstTime) {
        this.thiz = thiz;
        this.currMode = mode;
        this.currRetType = retType;
        this.currData = data;
        this.listener = listener;
        this.operation = operation;
        this.isFirstTime = isFirstTime;
    }

    public NetworkManagerOld(Context thiz, EnCallType mode,
                             ReturnTypeForReponse retType, JSONObject jObj,
                             TaskCompleted listener) {
        this.thiz = thiz;
        this.currMode = mode;
        this.currRetType = retType;
        this.jObj = jObj;
        this.listener = listener;
    }

    public static DefaultHttpClient getNewHttpClient() {
        try {
          //  KeyStore trustStore = KeyStore.getInstance(KeyStore
         //           .getDefaultType());
         //   trustStore.load(null, null);

            //SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
           // sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            //registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream callPostServiceForOthers(String Url, List<NameValuePair> data) {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(thiz);
        HttpClient httpclient = getNewHttpClient();
        HttpPost httppost = new HttpPost(Url);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(data));
            httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            //if (sharedPrefManager.getSproutAuth() != null)
            //		httppost.addHeader("Cookie", "SproutChatAuth=" + sharedPrefManager.getSproutAuth());

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            return entity.getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static InputStream callPostServiceForLogin(String Url, List<NameValuePair> data) {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(thiz);
        HttpClient httpclient = getNewHttpClient();
        HttpPost httppost = new HttpPost(Url);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(data));
            httppost.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            HttpResponse response = httpclient.execute(httppost);

            Header[] headers = response.getAllHeaders();
            if (response.getFirstHeader("Set-Cookie") == null) {
                sharedPrefManager.setSproutAuth(null);

            } else {
                sharedPrefManager.setSproutAuth(response.getFirstHeader(
                        "Set-Cookie").getElements()[0].getValue());
            }

            HttpEntity entity = response.getEntity();

            return entity.getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject InputStreamToJsonObject(InputStream is) {
        JSONObject jo = null;

        // Read it with BufferedReader
        try {
            Reader reader = new InputStreamReader(is);
            // Read it with buffer reader
            BufferedReader br = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String jsonString = sb.toString();
            jo = new JSONObject(jsonString);
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
            jo = new JSONObject();
        }

        return jo;
    }

    public static String InputStreamToString(InputStream is) {
        String jsonString = "";

        try {
            Reader reader = new InputStreamReader(is);
            // Read it with buffer reader
            BufferedReader br = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            jsonString = sb.toString();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    public static InputStream callPostServiceWithJSON(String Url, JSONObject jObj) {

        HttpClient httpclient = getNewHttpClient();
        HttpPost httppost = new HttpPost(Url);
        try {
            String json = "{\"userIds\": [45, 21]}";
            StringEntity se = new StringEntity(jObj.toString());
            httppost.setEntity(se);
            httppost.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            httppost.setHeader("Content-type", "application/json");
            /*httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");*/



            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            return entity.getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public Object callServiceByModeForJSON(String urlString,
                                           List<NameValuePair> data, EnCallType mode,
                                           ReturnTypeForReponse respnseType) {
        InputStream respInputStream = null;
        switch (mode) {

            case POSTFORLOGIN:
                respInputStream = callPostServiceForLogin(urlString, data);
                break;

            case POSTFORPROFILE:
                break;
            case POSTFORALL:
                respInputStream = callPostServiceForOthers(urlString, data);
                break;
            case POSTWITHJSON:
                respInputStream = callPostServiceWithJSON(urlString, jObj);
                break;

            case POSTFORREGISTRATION:
                break;
            default:
                break;
        }

        if (respInputStream != null) {
            switch (respnseType) {
                case String:
                    return InputStreamToString(respInputStream);
                case JSON:
                    return InputStreamToJsonObject(respInputStream);
                case INPUTSTREAM:
                    return respInputStream;
                case Class:
                default:
                    break;
            }
        }

        return null;
    }

    @Override
    protected Object doInBackground(String... params) {
        return callServiceByModeForJSON(params[0], currData, currMode,
                currRetType);
    }

    @Override
    protected void onProgressUpdate(ProgressModel... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Object result) {
        if (result != null) {
            if (this.listener != null)
                this.listener.onTaskCompletedSuccessfully(result);

        } else {
            if (this.listener != null)
                this.listener.onTaskFailed();
        }

        super.onPostExecute(result);
    }

    public enum EnCallType {
        POSTFORLOGIN, POSTFORALL, POSTFORREGISTRATION, POSTFORPROFILE,POSTWITHJSON
    }

    public enum OperationType {
    }

    public enum ReturnTypeForReponse {
        String, JSON, INPUTSTREAM, Class
    }
}
