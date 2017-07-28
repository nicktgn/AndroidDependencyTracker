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

package com.github.nicktgn.android.dependencytracker;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class DependencyTrackingAppCompatActivity<D extends DependencyTracker>
        extends AppCompatActivity
        implements DependencyTracker.DependencyListener {

    public abstract D getDependencyTracker();

    DependencyTracker dependencyTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dependencyTracker = getDependencyTracker();

        IntentFilter filter = new IntentFilter();
        filter.addAction(DependencyTracker.DEPENDENCY_STARTED_ACTION);
        filter.addAction(DependencyTracker.DEPENDENCY_STOPPED_ACTION);
        this.registerReceiver(dependencyTracker, filter);
        dependencyTracker.setDependencyListener(this);

        dependencyTracker.sendStartedBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dependencyTracker.sendStoppedBroadcast();
        this.unregisterReceiver(dependencyTracker);
        dependencyTracker.setDependencyListener(null);
        dependencyTracker = null;
    }

}
