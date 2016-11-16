package com.simonsal.uppstart;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.ubtechinc.alpha2ctrlapp.network.action.ClientAuthorizeListener;
import com.ubtechinc.alpha2robot.Alpha2RobotApi;
import com.ubtechinc.alpha2serverlib.interfaces.AlphaActionClientListener;
import com.ubtechinc.alpha2serverlib.interfaces.IAlpha2RobotClientListener;
import com.ubtechinc.alpha2serverlib.interfaces.IAlpha2SpeechGrammarInitListener;
import com.ubtechinc.alpha2serverlib.interfaces.IAlpha2SpeechGrammarListener;
import com.ubtechinc.alpha2serverlib.util.Alpha2SpeechMainServiceUtil;
import com.ubtechinc.contant.LauguageType;
import com.ubtechinc.developer.DeveloperAppData;
import com.ubtechinc.developer.DeveloperAppStaticValue;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends Activity implements
        IAlpha2RobotClientListener,
        AlphaActionClientListener,
        Alpha2SpeechMainServiceUtil.ISpeechInitInterface {

    private static final String TAG = "==MainActivity==";

    private Alpha2RobotApi mRobot;
    private static long mButtonPressedTimeStamp = 0L;
    private static long mSoundFinishedPlayingTimeStamp = 0L;
    protected static long mMoveFinishedTimeStamp = 0L;
    private UppstartDemoHandler mUppstartDemoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.registerApp();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mEventBroadcastReceiver != null) {
            this.unregisterReceiver(mEventBroadcastReceiver);
            mEventBroadcastReceiver = null;
        }

        /**
         * Before destroy, stop TTS and action.
         */
        if (mRobot != null) {
            mRobot.speech_StopTTS();
            mRobot.action_StopAction();
        }

        if (mRobot != null) {
            mRobot.releaseApi();
            mRobot = null;
        }
    }

    // ------------------- IAlpha2RobotClientListener interface

    @Override
    public void onServerCallBack(String var1) {
        Log.i(TAG, "onServerCallBack(" + var1 + ")");
    }

    @Override
    public void onServerPlayEnd(boolean var1) {
        Log.i(TAG, "onServerPlayEnd(" + var1 + ")");
        if( (System.currentTimeMillis()-mSoundFinishedPlayingTimeStamp) > Alpha2Constants.BUTTON_PRESSED_TIME_HACK ) {
            mSoundFinishedPlayingTimeStamp = System.currentTimeMillis();
            this.mUppstartDemoHandler.actionHasEnded(null, var1);
        }
    }


    // ------------------- AlphaActionClientListener interface

    @Override
    public void onActionStop(String var1) {
        Log.i(TAG, "onActionStop(" + var1 + ") ");
        if( (System.currentTimeMillis()-mMoveFinishedTimeStamp) > Alpha2Constants.BUTTON_PRESSED_TIME_HACK ) {
            this.mUppstartDemoHandler.actionHasEnded(var1, null);
        }
    }

    // ------------------- Alpha2SpeechMainServiceUtil.ISpeechInitInterface

    /**
     * The callback api after the initialization of ASR.
     */
    @Override
    public void initOver() {

        Log.i(TAG, "initOver()");
        mRobot.speech_setVoiceName("xiaoyan");
        /**
         * Specify the ASR language, such as LauguageType.LAU_CHINESE, LauguageType.LAU_ENGLISH.
         */
        mRobot.speech_setRecognizedLanguage(LauguageType.LAU_CHINESE);
        /**
         * IMPORTANT: the mLocalGrammar can't be null when you specify the ASR language as LauguageType.LAU_CHINESE.
         * And you should set mLocalGrammar as null when you specify the ASR language as LauguageType.LAU_ENGLISH.
         */
        mRobot.speech_initGrammar(readFile(this, "call.bnf", "utf-8"), new IAlpha2SpeechGrammarInitListener() {
            @Override
            public void speechGrammarInitCallback(String arg0, int nErrorCode) {

                Log.i(TAG, "speechGrammarInitCallback( " + arg0 + ") ");
                mRobot.speeh_startGrammar(new IAlpha2SpeechGrammarListener() {

                    @Override
                    public void onSpeechGrammarResult(int SpeechResultType,
                                                      String strResult) {

                        Log.i(TAG, "SpeechResultType =" + SpeechResultType);
                        Log.i(TAG, "strResult =" + strResult);
                    }

                    @Override
                    public void onSpeechGrammarError(int nErrorCode) {

                        Log.i(TAG, "onSpeechGrammarError( " + nErrorCode + ") ");
                    }

                });
            }
        });

        /**
         * IMPORTANT: It must be called when you specify the ASR language as LauguageType.LAU_CHINESE.
         * And you should not call this when you specify the ASR language as LauguageType.LAU_ENGLISH.
         */
        mRobot.header_setNoise(false);

        mRobot.requestRobotUUID();
    }

    // ------------------- Broadcast receivers


    BroadcastReceiver mEventBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context cContext, Intent intent) {

            Log.i(TAG, "Broadcast receiver got a new intent: '" + intent + "'");
            printAllIntentBundleData(intent);
            String action = intent.getAction();


            // This is odd, please note that this gets called two times for each press,
            // With the help of a static internal variable we are making sure that it only
            // gets called once...
            if (action.contains(DeveloperAppStaticValue.APP_BUTOON_EVENT_CLICK) ) {

                String index = mRobot.parseClickEvent(intent, getPackageName());
                if (index.equals(Alpha2Constants.ButtonIndex.START_STOP.buttonJasonIndex) &&
                        (System.currentTimeMillis() - mButtonPressedTimeStamp > Alpha2Constants.BUTTON_PRESSED_TIME_HACK) &&
                        mUppstartDemoHandler != null) {
                    mButtonPressedTimeStamp = System.currentTimeMillis();
                    mUppstartDemoHandler.buttonPressed();
                }
            } else if (action.contains(DeveloperAppStaticValue.APP_BUTTON_EVENT) ) {

                /**
                 * The Alpha2Ctrl installed in mobile phone gets the button of this app.
                 * You must put a 'button.json' in assets
                 * and add the following info in AndroidManifest.xml:
                 * <meta-data
                 *      android:name="alpha2_buttonevent"
                 *      android:value="buttonevent" />
                 */
                mRobot.sendButtonEvent2Server(intent, getPackageName(), Alpha2Constants.ALPHA2_JSON_FILE_ENCODING);
            } else if (action.contains(DeveloperAppStaticValue.APP_EXIT) ) {
                Log.i(TAG, "Terminating, as requested");
                finish();
            }
        }
    };

    // ------------------- Helpers

    /**
     * Register the app with the Alpha2 robot, aka get the app showing in the Apps
     * installed for the robot.
     */
    public void registerApp() {

        /**
         * It's recommended to register broadcastreceiver in onCreate().
         * If you register it later, you may miss receiving some broadcasts.
         */
        String packageName = this.getPackageName();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DeveloperAppStaticValue.APP_EXIT);
        filter.addAction(packageName);
        filter.addAction(DeveloperAppStaticValue.APP_ROBOT_UUID_INFO);
        filter.addAction(packageName + DeveloperAppStaticValue.APP_BUTTON_EVENT);
        filter.addAction(packageName + DeveloperAppStaticValue.APP_BUTOON_EVENT_CLICK);

        registerReceiver(mEventBroadcastReceiver, filter);

        // Register the app and get the result...
        mRobot = new Alpha2RobotApi(this, Alpha2Constants.ALPHA2_STATIC_APP_ID,
                new ClientAuthorizeListener() {
                    @Override
                    public void onResult(int code, String info) {
                        Log.i(TAG, "alpha2 registration result code = '" + code + "' info= '" + info + "'");
                        if (code == 1) {
                            // This is assumed to be the movements
                            mRobot.initActionApi(MainActivity.this);
                            // This is assumed to be the speech, TTS is our focus.
                            mRobot.initSpeechApi(MainActivity.this, MainActivity.this);
                            mUppstartDemoHandler = new UppstartDemoHandler(mRobot);
                        }
                    }
                });

    }

    public static String readFile(Context mContext, String file, String code) {
        int len;
        byte[] buf;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // -------- Debug

    /**
     * Simple debug helper to print all data in an intent.
     *
     * @param i The intent to dump the data for.
     */
    private void printAllIntentBundleData(Intent i) {

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.i(TAG, "----> Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                if (key.equals("appdata")) {
                    DeveloperAppData apd = (DeveloperAppData) bundle.get(key);
                    StringBuilder sb = new StringBuilder();
                    if( apd != null ) {
                        sb.append("cmd='").append(apd.getCmd()).append("',");
                        sb.append("packageName='").append(apd.getPackageName()).append("',");
                        if (apd.getDatas() != null) {
                            sb.append("datas='").append(new String(apd.getDatas())).append("',");
                        }
                    }
                    Log.i(TAG, "[" + key + "=" + sb.toString() + "]");
                } else {
                    Log.i(TAG, "[" + key + "=" + bundle.get(key) + "]");
                }
            }
            Log.i(TAG, "Dumping Intent end <----");
        }
    }

}
