package com.simonsal.uppstart;


/**
 * The constats that are used in the App.
 */
public class Alpha2Constants {

    public static final String ALPHA2_STATIC_APP_ID = "D4AD3FFFA1D03035CA18FE7A24FBDCD2";

    public static final String ALPHA2_JSON_FILE_ENCODING = "gbk";

    public static final long BUTTON_PRESSED_TIME_HACK = 1000l;

    public static final String DANCE_MOVE = "electric zone";

    public enum ButtonIndex{
        START_STOP("0");

        String buttonJasonIndex = "0";
        ButtonIndex( String bIndex){
            buttonJasonIndex = bIndex;
        }
    }
}
