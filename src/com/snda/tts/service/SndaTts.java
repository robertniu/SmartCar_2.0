/**
 * ****************************************************************
 *
 * Copyright (C) SNDA Corporation. All rights reserved.
 *
 * FileName : SndaTts.java
 * Description : The SndaTts class stores some common data used in
 * SndaTtsService.
 *
 ******************************************************************
 */
package com.snda.tts.service;

/**
 * A class that stores some common data used in SndaTtsService.
 */
public class SndaTts {
    // The status means Tts of the current speaking task begin.
    public static String STATUS_SPEAK_BEGIN = "Status_Speak_Begin";
    // The status means Tts of the current speaking task finished,
    // (means playing finished, not stopped).
    public static String STATUS_SPEAK_FINISH = "Status_Speak_Finish";
    // The status means Tts of the current speaking task has been
    // stopped by the phone call.
    public static String STATUS_STOPPED_BY_PHONE = "Status_Stopped_By_Phone";
    // The status means Tts of the current speaking task has been
    // stopped manually (intentionally).
    public static String STATUS_STOPPED_MANUALLY = "Status_Stopped_Manually";
}
