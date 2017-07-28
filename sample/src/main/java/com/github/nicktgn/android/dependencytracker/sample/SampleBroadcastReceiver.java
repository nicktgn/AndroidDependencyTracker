/*
 * Copyright 2017 Nick Tsygankov (nicktgn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.nicktgn.android.dependencytracker.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SampleBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SampleBroadcastReceiver";
    private static final String SAMPLE_ACTION = "org.github.nicktgn.intent.action.SAMPLE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received action: " + intent.getAction());
        if (intent.getAction().equals(SAMPLE_ACTION)) {

            SampleApplication application = (SampleApplication) context.getApplicationContext();

            Log.d(TAG, "Application context counter: "+ application.counter);

            if(application != null){
                application.counter += 10;
            }
        }
    }
}
