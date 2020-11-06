package com.ygmpkk.flutter_umplus;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.statistics.common.DeviceConfig;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class FlutterUmplusPlugin implements MethodCallHandler {
    private Activity activity;


    private FlutterUmplusPlugin(Activity activity) {
        this.activity = activity;
    }


    public static void registerWith(Registrar registrar) {
        final MethodChannel channel =
                new MethodChannel(registrar.messenger(), "ygmpkk/flutter_umplus");
        channel.setMethodCallHandler(new FlutterUmplusPlugin(registrar.activity()));
    }


    public static void preInit(Context context) {
        String channel = getMetadata(context, "MARKET_CHANNEL_VALUE");
        String appKey = getMetadata(context, "umAppKey");

        UMConfigure.preInit(context, appKey, channel);

        getDeviceInfo(context);
    }

    public static void onResume(Context context) {
        MobclickAgent.onResume(context);

    }

    public static void onPause(Context context) {
        MobclickAgent.onPause(context);

    }


    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + Build.VERSION.RELEASE);
                break;
            case "init":
                initSetup(call, result);
                break;
            case "beginPageView":
                beginPageView(call, result);
                break;
            case "endPageView":
                endPageView(call, result);
                break;
            case "loginOnAction":
                loginOnAction(call, result);
                break;
            case "loginOffAction":
                loginOffAction(call, result);
                break;
            case "event":
                event(call, result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }


    private static String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        return null;
    }


    private void initSetup(MethodCall call, Result result) {

        Boolean logEnable = (Boolean) call.argument("logEnable");
        Boolean reportCrash = (Boolean) call.argument("reportCrash");

//        Log.d("UMTestLog","logEnable---"+logEnable);
//        Log.d("UMTestLog","reportCrash---"+reportCrash);

        String channel = getMetadata(activity, "MARKET_CHANNEL_VALUE");
        String appKey = getMetadata(activity, "umAppKey");
        if (channel == null || "".equals(channel)) {
            channel = "unKnow";
        }
        UMConfigure.setLogEnabled(logEnable == null ? false : true);

        MobclickAgent.setCatchUncaughtExceptions(reportCrash == null ? false : true); //错误统计
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO); //采集模式
        UMConfigure.init(activity, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE,
                null);

//    UMConfigure.setEncryptEnabled(encrypt); //加密启用
//    MobclickAgent.setSessionContinueMillis(30000L); //安卓独立启动时间间隔,默认30秒，ios退到后台就算一次
        result.success(true);
    }


    public void loginOnAction(MethodCall call, Result result) {
        String userId = (String) call.argument("userId");
//        Log.d("UMTestLog","loginOnAction---"+userId);
        MobclickAgent.onProfileSignIn(userId!=null?userId:"unKnowId");//该方法传的参数后台无法查看，似乎只是用来做区分活跃用户的唯一key，来统计活跃数
        result.success(null);
    }



    public void loginOffAction(MethodCall call, Result result) {
        MobclickAgent.onProfileSignOff();
//        Log.d("UMTestLog","onProfileSignOff---触发");
        result.success(null);
    }



    public void beginPageView(MethodCall call, Result result) {
        String name = (String) call.argument("name");
        Log.d("UMTestLog", "beginPageView: " + name);
        MobclickAgent.onPageStart(name);
        MobclickAgent.onResume(activity);
        result.success(null);
    }


    public void endPageView(MethodCall call, Result result) {
        String name = (String) call.argument("name");
        Log.d("UMTestLog", "endPageView: " + name);
        MobclickAgent.onPageEnd(name);
        MobclickAgent.onPause(activity);
        result.success(null);
    }




    public void event(MethodCall call, Result result) {
        String name = (String) call.argument("name");
        String label = (String) call.argument("label");
//        Log.d("UMTestLog","event--name-"+name);
//        Log.d("UMTestLog","event--label-"+label);
        MobclickAgent.onEvent(activity, name, label==null?"":label);
        result.success(null);
    }

    static void getDeviceInfo(Context context) {
        String deviceID = DeviceConfig.getDeviceIdForGeneral(context);
        String deviceMac = DeviceConfig.getMac(context);
        Log.d("UM", "deviceID: " + deviceID);
        Log.d("UM", "deviceMac: " + deviceMac);
    }
}
