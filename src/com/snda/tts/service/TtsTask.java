/**
 * ****************************************************************
 *
 * Copyright (C) SNDA Corporation. All rights reserved.
 *
 * FileName : TtsTask.java
 * Description : Data structure used to exchange information between
 * tts service and its binder.
 *
 ******************************************************************
 */
package com.snda.tts.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A pojo class describes the data structure used to exchange
 * information between TTS service and its binder.
 */
public class TtsTask implements Parcelable {
    /**The content which you want to speak.*/
    public String content;
    /**The caller who sends speak task to TTS service.*/
    public String caller;
    /**The extra information which can be null.*/
    public String extra;
    /**The id of this speak task*/
    public Long id;

    public static final Parcelable.Creator<TtsTask> CREATOR = new Parcelable.Creator<TtsTask>() {

     
        public TtsTask createFromParcel(Parcel source) {
            return new TtsTask(source);
        }

      
        public TtsTask[] newArray(int size) {
            return new TtsTask[size];
        }
    };

    public TtsTask() {
        content = "";
        caller = "";
        extra = "";
        id = 0L;
    }

    private TtsTask(Parcel source) {
        readFromParcel(source);
    }

  
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(caller);
        dest.writeString(extra);
        dest.writeLong(id);
    }

    public void readFromParcel(Parcel source) {
        content = source.readString();
        caller = source.readString();
        extra = source.readString();
        id = source.readLong();
    }


    public int describeContents() {
        return 0;
    }
}
