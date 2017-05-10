package moviles.cucei.udg.estacionamientocucei;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.internal.http2.Header;

/**
 * Created by deimi on 06/01/2017.
 */

public class ServerDataRetrieval implements Serializable {
    public static String TAG = "VOLLEY";
    private static String header;
    private static String method;
    private static Map<String,String> body;

    public interface onResponseListener{
        void onRequestResponded(String response);
        void onStringResponded(String response);
        void onVolleyError(VolleyError volleyError);
        void onStringError(String error);
    }
    private static onResponseListener onResponseListener;

    public static void getAppToken(final Context contexto, onResponseListener responseListener, String client_id, String client_secret){
        onResponseListener = responseListener;
        header = client_id;
        header += ":" + client_secret;
        header = Base64.encodeToString(header.getBytes(),Base64.NO_WRAP);
        method = "Basic";
        body = new HashMap<String, String>();
        body.put("grant_type", "client_credentials");
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onResponseListener.onRequestResponded(response);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onResponseListener.onVolleyError(error);
                error.printStackTrace();
            }
        };
        String url;
        url = contexto.getResources().getString(R.string.api_test);
        url += contexto.getResources().getString(R.string.app_login);
        TokenRequest tokenRequest = new TokenRequest(Request.Method.POST,url,listener,errorListener);
        RequestQueue requestQueue = Volley.newRequestQueue(contexto);
        requestQueue.add(tokenRequest);
    }

    static private RetryPolicy setRetryPolicy(int ms){
        return new DefaultRetryPolicy(ms,1,1);
    }

    public static void getAppPasswordToken(final Context context, final String user, final String pass, final String client_id, final String client_secret, onResponseListener responseListener){
        onResponseListener = responseListener;
        new AsyncTask<String,Integer,String>(){
            @Override
            protected String doInBackground(String... params){
                String sBody = "grant_type=password";
                sBody += "&username=" + user;
                sBody += "&password=" + pass;
                String url = context.getString(R.string.api_test);
                url += context.getString(R.string.app_login);
                header = client_id;
                header += ":";
                header += client_secret;
                String basic = "Basic ";
                basic += Base64.encodeToString((header).getBytes(),Base64.NO_WRAP);
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                RequestBody requestBody = RequestBody.create(mediaType,sBody);
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("authorization",basic)
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .addHeader("cache-control", "no-cache")
                        .build();
                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    return response.body().string();
                }catch (Exception ex){
                    ex.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result){
                if(result != null){
                    onResponseListener.onRequestResponded(result);
                }else{
                    onResponseListener.onStringError("error");
                }
            }
        }.execute();
    }

    private static String responsi = "";

    public static String syncGetStringServerData(final String url, final String bearer_method, final String bearer){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String basic = bearer_method + " " + bearer;
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("authorization",basic)
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .addHeader("cache-control", "no-cache")
                        .build();
                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    responsi = response.body().string();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return responsi;
    }

    public static void getStringServerData(final Context context, String url,String bearer_method, String bearer, int http_method, Map<String,String> mbody,onResponseListener responseListener){
        onResponseListener = responseListener;
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        if(mbody != null){
            body = mbody;
        }else {
            body = new HashMap<>();
            body.put("grant_type", "client_credentials");
        }
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onResponseListener.onStringResponded(response);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onResponseListener.onVolleyError(error);
                error.printStackTrace();
            }
        };
        header = bearer;
        method = bearer_method;

        Log.wtf(TAG,method + " " + header);
        TokenRequest tokenRequest = new TokenRequest(http_method,url,listener,errorListener);
        tokenRequest.setRetryPolicy(setRetryPolicy(30000));
        requestQueue.add(tokenRequest);
    }

    public static String getBase64AuthStringHeader(String auth,String client_id, String client_secret,int type){
        String head = client_id;
        head += ":";
        head += client_secret;
        String authorization_type = auth;
        authorization_type += " ";
        authorization_type += Base64.encodeToString((head).getBytes(),type);
        return authorization_type;
    }

    public static void xwwwFormUrlencodedRequest(final Context context, final String url, final Map<String , String> body, final String authString, onResponseListener responseListener){
        onResponseListener = responseListener;
        new AsyncTask<String,Integer,String>(){
            @Override
            protected String doInBackground(String... params){
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                String contents = "";
                Set<String> keys = body.keySet();
                for (String key : keys){
                    contents += key;
                    contents += "=";
                    contents += body.get(key);
                    contents += "&";
                }
                StringBuilder stringBuilder = new StringBuilder(contents);
                stringBuilder.deleteCharAt(contents.length()-1);
                contents = stringBuilder.toString();
                RequestBody requestBody = RequestBody.create(mediaType,contents);
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("Authorization",authString)
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .addHeader("cache-control", "no-cache")
                        .build();
                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    return response.body().string();
                }catch (Exception ex){
                    onResponseListener.onVolleyError(new VolleyError(ex.getMessage()));
                    ex.printStackTrace();
                    return null;
                }
            }
            protected void onPostExecute(String result){
                if(result != null){
                    onResponseListener.onRequestResponded(result);
                }
            }
        }.execute();

    }

    public static void JSONRequest(Context context, String url,String bearer_method, String bearer, int http_method, JSONObject json, onResponseListener responseListener){
        RequestQueue queue = Volley.newRequestQueue(context);
        onResponseListener = responseListener;
        header = bearer;
        method = bearer_method;
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onResponseListener.onStringResponded(response.toString());
            }
        };
        Response.ErrorListener error = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onResponseListener.onVolleyError(error);
                error.printStackTrace();
                String json;
                NetworkResponse networkResponse = error.networkResponse;
                if(networkResponse != null && networkResponse.data != null){
                    json = new String(networkResponse.data);
                    Log.w(TAG,json);
                }
            }
        };
        JSONRequest jsrequest = new JSONRequest(http_method,url,json,listener,error);
        jsrequest.setRetryPolicy(setRetryPolicy(30000));
        queue.add(jsrequest);
    }



    private static class TokenRequest extends StringRequest {
        TokenRequest(int method, String url, Response.Listener<String> listener,
                     Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return body;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();

            String auth = method + " " + header;
            headers.put("Authorization", auth);
            return headers;
        }
    }
    private static class JSONRequest extends JsonRequest{
        JSONRequest(int method, String url, JSONObject jsonRequest,
                           Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                    errorListener);
        }
        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString =
                        new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                return Response.success(new JSONObject(jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();

            String auth = method + " " + header;
            headers.put("Authorization", auth);
            return headers;
        }
    }
}
