package com.simonsal.uppstart;

import android.util.Log;

import com.ubtechinc.alpha2robot.Alpha2RobotApi;
import com.ubtechinc.alpha2robot.constant.UbxErrorCode;

import static com.simonsal.uppstart.Alpha2Constants.DANCE_MOVE;
import static com.simonsal.uppstart.Alpha2Constants.TEXT_STRING;

/**
 * The demo play list.
 *
 * One note:
 *  Noticed that the Exceptions are not thrown when callbacks are concerned. Seems to
 */
public class UppstartDemoHandler {

    private static final String TAG = "==UppstartDemo==";

    private static boolean mDemoFlag = false;
    private Alpha2RobotApi mRobot;

    public UppstartDemoHandler(Alpha2RobotApi robot){
        mRobot = robot;
    }

    public void buttonPressed(){
        if (mDemoFlag) {
            stopDemo();
        } else {
            startDemo();
        }
    }

    /**
     * Called when the action is done, could be a speech action or a move action.
     *
     * @param actionType The action name
     * @param serverTttsEnd The TTS as ended
     */
    public void actionHasEnded(String actionType, Boolean serverTttsEnd){
        Log.i(TAG, "-- actionHasEnded( '" + actionType + "', '"+ serverTttsEnd+"') mDemoFlag "+mDemoFlag );

        try {
            if (mDemoFlag) {
                if (actionType != null && (actionType.isEmpty() || actionType.equals(DANCE_MOVE)) ) {
                    UbxErrorCode.API_EEROR_CODE cCode = mRobot.speech_StartTTS(TEXT_STRING);
                    Log.i(TAG, "Song return code: '" + cCode + "'");
                } else if (serverTttsEnd != null && serverTttsEnd) {
                    UbxErrorCode.API_EEROR_CODE cCode = mRobot.action_PlayActionName(DANCE_MOVE);
                    Log.i(TAG, "Action return code: '" + cCode + "'");
                    MainActivity.mMoveFinishedTimeStamp = System.currentTimeMillis();
                }

            }
        }catch(Exception e){
            Log.e(TAG, "Exception in processing new commands actionHasEnded('" + actionType + "', '"+ serverTttsEnd+"') mDemoFlag "+mDemoFlag,e);
        }
    }


    // --------------------- Internal helpers
    private void startDemo() {

        Log.i(TAG, "startDemo called (mDemoFlag="+mDemoFlag+") " );
        if (!mDemoFlag) {
            mDemoFlag = true;

            UbxErrorCode.API_EEROR_CODE cCode = mRobot.speech_StartTTS(TEXT_STRING);
            Log.i(TAG, "Speech return code: '" + cCode + "'");
            mRobot.action_PlayActionName(DANCE_MOVE);
            try{
                Thread.sleep(1000);
            }catch(Exception e){
                Log.e(TAG, "Could not wait for that long!",e);
            }
        }

    }

    private void stopDemo() {

        Log.i(TAG, "stopDemo called (mDemoFlag="+mDemoFlag+") releasing resources " );

        mDemoFlag = false;
        mRobot.action_StopAction();
        mRobot.speech_StopTTS();
    }
}
