package io.mycat.uoclient;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAInfo;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InfoException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import utils.ChaincodeExecutor;
import utils.FabricUser;

public class MainActivity extends Activity {

    File directory;

    private WebView mWebView;


    private static final long waitTime = 6000;
    private static String connectionProfilePath;

    private static String UICChannelName = "uic";
    private static String HDCChannelName = "hdc";
    private static String DRCChannelName = "drc";
    private static String userName = "UOUser1";
    private static String secret = "mycat2018";
    private static String UICCName = "uicc";
    private static String HDCCName = "hdcc";
    private static String DRCCName = "drcc";
    private static String chaincodeVersion = "1.0.1";
    Channel uic;
    Channel hdc;
    Channel drc;
    HFClient client;
    ChaincodeExecutor uicExecutor;
    ChaincodeExecutor hdcExecutor;
    ChaincodeExecutor drcExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("On create");

        mWebView = (WebView) findViewById(R.id.webview);

        InsideWebViewClient mInsideWebViewClient = new InsideWebViewClient();
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(mInsideWebViewClient);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);  // 开启 DOM storage 功能
        webSettings.setAppCacheMaxSize(1024*1024*8);
        mWebView.setNetworkAvailable(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        String appCachePath = this.getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);    // 可以读取文件缓存
        webSettings.setAppCacheEnabled(false);    //开启H5(APPCache)缓存功能

        mWebView.addJavascriptInterface(MainActivity.this, "obj");
//        mWebView.loadUrl("http://39.104.99.78/views/uo/index.html");
        mWebView.loadUrl("file:///android_asset/views/uo/index.html");
//        mWebView.loadUrl("file:///android_asset/web.html");

        FabricInitial init = new FabricInitial();
        init.execute();
    }

    @JavascriptInterface
    public void getHO(String arg) {
        Toast.makeText(this, "getHO", Toast.LENGTH_SHORT).show();
        FabricRequest request = new FabricRequest();
        request.execute("getHO", arg);
    }

    @JavascriptInterface
    public void putIndex(String arg) {
        Toast.makeText(this, "putIndex", Toast.LENGTH_SHORT).show();
        FabricRequest request = new FabricRequest();
        request.execute("putIndex", arg);
    }

    @JavascriptInterface
    public void getDataList(String arg) {
        Toast.makeText(this, "getDataList", Toast.LENGTH_SHORT).show();
        FabricRequest request = new FabricRequest();
        request.execute("getDataList", arg);
    }

    @JavascriptInterface
    public void getDeparts(String arg) {
        Toast.makeText(this, "getDeparts", Toast.LENGTH_SHORT).show();
        FabricRequest request = new FabricRequest();
        request.execute("getDeparts", arg);
    }

    @JavascriptInterface
    public void getInviteList(String arg) {
        Toast.makeText(this, "getInviteList", Toast.LENGTH_SHORT).show();
        FabricRequest request = new FabricRequest();
        request.execute("getInviteList", arg);
    }

    @JavascriptInterface
    public void accept(String arg) {
        Toast.makeText(this, "accept", Toast.LENGTH_SHORT).show();
        FabricRequest request = new FabricRequest();
        request.execute("accept", arg);
    }

    @JavascriptInterface
    public void setItem(String key, String value) {
        SharedPreferences kvStore = getPreferences(0);
        SharedPreferences.Editor editor = kvStore.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @JavascriptInterface
    public String getItem(String key) {
        SharedPreferences kvStore = getPreferences(0);
        return kvStore.getString(key, "123");
    }

    @JavascriptInterface
    public void removeItem(String key) {
        SharedPreferences kvStore = getPreferences(0);
        SharedPreferences.Editor editor = kvStore.edit();
        editor.remove(key);
        editor.apply();
    }

    private class InsideWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            //mWebView.loadUrl("javascript:" + "window.alert('Js injection success')" );
            super.onPageFinished(view, url);
        }


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下返回键并且webview界面可以返回
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {

            mWebView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    private void initChain() {
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            directory = cw.getDir("newartifacts", Context.MODE_PRIVATE);

            AssetManager assetManager = getAssets();
            InputStream publickey = assetManager.open("connection-profile-standard.yaml");
            File temp = new File(directory.getAbsolutePath() + File.separator + "connection-profile-standard.yaml");
            OutputStream output = new FileOutputStream(temp);
            writeToFile(publickey,output);

            File f = new File(directory.getAbsolutePath() + File.separator + "connection-profile-standard.yaml");
            NetworkConfig networkConfig = NetworkConfig.fromYamlFile(f);
            NetworkConfig.OrgInfo clientOrg = networkConfig.getClientOrganization();
            NetworkConfig.CAInfo caInfo = clientOrg.getCertificateAuthorities().get(0);

            FabricUser user = getFabricUser(clientOrg, caInfo);

            client = HFClient.createNewInstance();
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            client.setUserContext(user);

            uic = client.loadChannelFromConfig(UICChannelName, networkConfig);
            hdc = client.loadChannelFromConfig(HDCChannelName, networkConfig);
            drc = client.loadChannelFromConfig(DRCChannelName, networkConfig);

            uic.initialize();
            hdc.initialize();
            drc.initialize();

            uicExecutor = new ChaincodeExecutor(UICCName, chaincodeVersion);
            hdcExecutor = new ChaincodeExecutor(HDCCName, chaincodeVersion);
            drcExecutor = new ChaincodeExecutor(DRCCName, chaincodeVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static FabricUser getFabricUser(NetworkConfig.OrgInfo clientOrg, NetworkConfig.CAInfo caInfo) throws
            MalformedURLException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, InfoException,
            EnrollmentException
    {
        HFCAClient hfcaClient = HFCAClient.createNewInstance(caInfo);
        HFCAInfo cainfo = hfcaClient.info();

        // Persistence is not part of SDK.

        System.out.println("Going to enroll user: " + userName);
        Enrollment enrollment = hfcaClient.enroll(userName, secret);
        System.out.println("Enroll user: " + userName +  " successfully.");

        FabricUser user = new FabricUser();
        user.setMspId(clientOrg.getMspId());
        user.setName(userName);
        user.setOrganization(clientOrg.getName());
        user.setEnrollment(enrollment);
        return user;
    }

    class FabricInitial extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (client == null) {
                Log.d("uoclient", "fabric start initialize...");
                initChain();
                Log.d("uoclient", "success");
            }
            return null;
        }
    }

    class FabricRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (client == null) {
                    Log.d("uoclient", "fabric is initializing...");
                    return null;
                }
                String ret;
                String func = strings[0];
                if (func.equalsIgnoreCase("getHO")) {
                    ret = uicExecutor.executeTransaction(client, uic, false, func, strings[1]);
                    final String fret = ret.replace("\"", "\\\"");
                    runOnUiThread(() -> mWebView.loadUrl("javascript:getHO_ret(\""+fret+"\")"));
                } else if (func.equalsIgnoreCase("putIndex")) {
                    ret = uicExecutor.executeTransaction(client, uic, true, func, strings[1]);
                    final String fret = ret = ret.replace("\"", "\\\"");
                    runOnUiThread(() -> mWebView.loadUrl("javascript:putIndex_ret(\""+fret+"\")"));
                } else if (func.equalsIgnoreCase("getDataList")) {
                    ret = uicExecutor.executeTransaction(client, uic, false, func, strings[1]);
                    final String fret = ret = ret.replace("\"", "\\\"");
                    runOnUiThread(() -> mWebView.loadUrl("javascript:getDataList_ret(\""+fret+"\")"));
                } else if (func.equalsIgnoreCase("getDeparts")) {
                    ret = hdcExecutor.executeTransaction(client, hdc, false, func, strings[1]);
                    final String fret = ret = ret.replace("\"", "\\\"");
                    runOnUiThread(() -> mWebView.loadUrl("javascript:getDeparts_ret(\""+fret+"\")"));
                } else if (func.equalsIgnoreCase("getInviteList")) {
                    ret = drcExecutor.executeTransaction(client, drc, false, func, strings[1]);
                    final String fret = ret = ret.replace("\"", "\\\"");
                    runOnUiThread(() -> mWebView.loadUrl("javascript:getInviteList_ret(\""+fret+"\")"));
                } else if (func.equalsIgnoreCase("accept")) {
                    ret = drcExecutor.executeTransaction(client, drc, true, func, strings[1]);
                    final String fret;
                    if (ret.contains("ok") && ret.contains("1000")) {
                        fret = "授权成功！";
                    } else {
                        fret = "授权失败，请稍后重试";
                    }
                    runOnUiThread(() -> mWebView.loadUrl("javascript:accept_ret(\""+fret+"\")"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String arg) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    public void writeToFile(InputStream input, OutputStream output){

        try {
            byte[] buffer = new byte[4 * 1024]; // or other buffer size
            int read;

            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
        }catch(Exception e){
            System.out.println("Exception in writing files: "+e.getMessage());
        } finally{
            try{
                output.close();
                input.close();
            }catch(IOException e){
                System.out.println("IOException in closing files: "+e.getMessage());
            }

        }
    }

}
