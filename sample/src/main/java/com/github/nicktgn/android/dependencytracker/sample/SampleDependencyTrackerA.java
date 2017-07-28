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

public class SampleDependencyTrackerA extends DependencyTracker {

    private static final String TAG = "SampleDepTrackerA";
    private static final String[] dependecies = {
            "com.github.nicktgn.android.dependencytracker.sample.SampleServiceB"
    };

    private Context context;

    public SampleDependencyTrackerA(){

    }

    public SampleDependencyTrackerA(Context context){
        this.context = context;
    }


    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String[] getDependencies(Context context) {
        return dependecies;
    }

    @Override
    public Class getComponent() {
        return SampleServiceA.class;
    }

    @Override
    public Intent startOnBoot(Context context) {
        Log.d(TAG, "Starting on boot...");
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
        return new Intent(context, SampleServiceA.class);
    }
}
