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

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.nicktgn.android.dependencytracker.DependencyTracker;

public class SampleDependencyTrackerB extends DependencyTracker {

    private static final String TAG = "SampleDepTrackerB";

    private Context context;

    public SampleDependencyTrackerB(){

    }

    public SampleDependencyTrackerB(Context context){
        this.context = context;
    }


    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String[] getDependencies(Context context) {
        return null;
    }

    @Override
    public Class getComponent() {
        return SampleServiceB.class;
    }

    @Override
    public Intent startOnBoot(Context context) {
        Log.d(TAG, "Starting on boot...");
        return new Intent(context, SampleServiceB.class);
    }

    @Override
    public Intent restartOnUpdate(Context context) {
        Log.d(TAG, "Re-starting on update...");
        return null;
    }

    @Override
    public Intent startOnDependecyStared(Context context, String dependency) {
        Log.d(TAG, "Starting on dependency " + dependency + "satisfied");
        return null;
    }

    @Override
    public Intent startOnAllDependenciesStarted(Context context) {
        Log.d(TAG, "Starting on dependencies satisfied...");
        return null;
    }
}
