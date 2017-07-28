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

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.nicktgn.android.dependencytracker.DependencyTrackingService;

public class SampleServiceA extends DependencyTrackingService<SampleDependencyTrackerA> {

    private static final String TAG = "SampleServiceA";

    @Override
    public SampleDependencyTrackerA getDependencyTracker() {
        return new SampleDependencyTrackerA(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDependencyStarted(String dependency) {
        Log.d(TAG, "Dependency " + dependency + " started...");
    }

    @Override
    public void onDependencyStopped(String dependency) {
        Log.d(TAG, "Dependency " + dependency + " stopped...");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "on Create");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "on Destroy");
        super.onDestroy();
    }
}
