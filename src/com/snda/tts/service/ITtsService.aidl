/**
 * ****************************************************************
 *
 * Copyright (C) SNDA Corporation. All rights reserved.
 *
 * FileName : ITtsService.aidl
 * Description : Interface for remote TTS service revoke.
 *
 ******************************************************************
 */
package com.snda.tts.service;
import com.snda.tts.service.TtsTask;

interface ITtsService {
        /**
         * Activates the permission to access all other methods,
         * which need internet connection and some very small data transition.
         * It is only needed to call once after apk install.
         * @param
         * @return
         *     0 if successful.
         *     -1 if network connection failed.
         *     -2 if there is no permission for the calling application. Read online doc to apply permission.
         */
        int activate();

        /**
         * Sends speak task to TTS service
         * @param ttsTask
         *     The tts task object used in TTS task queue
         * @return
         *     0 if successful.
         */
        int speak(in TtsTask ttsTask);

         /**
         * Removes the first task sent by the caller.
         * @param caller
         *     The caller who sends the speak task
         * @return
         *     0 if successful.
         */
        int stopCaller(String caller);

         /**
         * Removes all tasks sent by the caller.
         * @param caller
         *     The caller who sends the speak task
         * @return
         *     0 if successful.
         */
        int stopCallerAll(String caller);
}
