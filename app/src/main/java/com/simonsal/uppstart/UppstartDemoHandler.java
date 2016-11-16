package com.simonsal.uppstart;

import android.util.Log;

import com.ubtechinc.alpha2robot.Alpha2RobotApi;
import com.ubtechinc.alpha2robot.constant.UbxErrorCode;

import static com.simonsal.uppstart.Alpha2Constants.DANCE_MOVE;

/**
 * The demo play list.
 */
public class UppstartDemoHandler {

    private static final String TAG = "--UppstartDemo--";

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

    public void startDemo() {

        Log.i(TAG, "startDemo called (mDemoFlag="+mDemoFlag+") " );
        if (!mDemoFlag) {
            mDemoFlag = true;

            UbxErrorCode.API_EEROR_CODE cCode = mRobot.speech_StartTTS("Welcome to UPPSTART! Hope you have fun!");
            Log.i(TAG, "Speech return code: '" + cCode + "'");
            mRobot.action_PlayActionName(DANCE_MOVE);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                Log.e(TAG, "Exception in sleeping: ", e);
            }
        }

    }

    public void stopDemo() {

        Log.i(TAG, "stopDemo called (mDemoFlag="+mDemoFlag+") " );

        mDemoFlag = false;
        mRobot.action_StopAction();
        mRobot.speech_StopTTS();
    }


    // ---------- Movment has stoped
    public void actionHasEnded(String actionType, boolean serverTttsEnd){
        Log.i(TAG, "-- actionHasEnded( '" + actionType + "', '"+ serverTttsEnd+"') mDemoFlag "+mDemoFlag );

        if( mDemoFlag ){
//            if( actionType.equals(mDanceMove) ){
//                UbxErrorCode.API_EEROR_CODE  cCode = mRobot.speech_StartTTS("Welcome to UPPSTART! Hope you have fun!");
//                Log.i(TAG, "Song return code: '" + cCode + "'");
//            }

            Log.i(TAG, "onServerPlayEnd( " + serverTttsEnd + ")");
            if( serverTttsEnd && mDemoFlag ){
                Log.i(TAG, "Starting dance...");
                UbxErrorCode.API_EEROR_CODE cCode = mRobot.action_PlayActionName(DANCE_MOVE);
                Log.i(TAG, "Action return code: '" + cCode + "'");
            }

        }
    }
}
