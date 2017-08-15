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

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class DependencyTracker extends BroadcastReceiver {
    static final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
    static final String UPDATE_ACTION = "android.intent.action.MY_PACKAGE_REPLACED";
    static final String DEPENDENCY_STARTED_ACTION = "com.gitlab.nicktgn.android.dependencytracker.action.DEPENDENCY_STARTED";
    static final String DEPENDENCY_STOPPED_ACTION = "com.gitlab.nicktgn.android.dependencytracker.action.DEPENDENCY_STOPPED";
    static final String EXTRA_COMPONENT_NAME = "com.gitlab.nicktgn.android.dependencytracker.extra.COMPONENT_NAME";


    private DependencyListener mDependencyListener;

    public DependencyTracker(){

    }

    public abstract Context getContext();

    public abstract String[] getDependencies(Context context);

    public abstract Class getComponent();

    public abstract Intent startOnBoot(Context context);

    public abstract Intent restartOnUpdate(Context context);

    public abstract Intent startOnDependecyStared(Context context, String dependency);

    public abstract Intent startOnAllDependenciesStarted(Context context);

    public boolean isServiceRunning(String serviceClassName) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClassName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private String[] _getDependencies(Context context){
        DependencyTrackingApplication store = getDependencyStore(context);
        String[] storedDependecies = store.getDependenciesList(getComponent());
        if(storedDependecies == null){
            storedDependecies = getDependencies(context);
            store.setDependenciesList(getComponent(), storedDependecies);
        }
        return storedDependecies;
    }

    private boolean isDependency(Context context, String className){
        String[] dependencies = _getDependencies(context);
        if(dependencies != null) {
            for (int i = 0; i < dependencies.length; i++) {
                if (dependencies[i].equals(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    private DependencyTrackingApplication getDependencyStore(Context context){
        return (DependencyTrackingApplication) context.getApplicationContext();
    }

    private void startComponent(Context context, Intent intent){
        if(intent.getComponent() == null){
            return;
        }
        String componentName = intent.getComponent().getClassName();
        if(componentName.endsWith("Activity")){
            context.startActivity(intent);
        }
        else {
            context.startService(intent);
        }
    }

    private boolean areAllDependeciesSatisfied(Context context){
        DependencyTrackingApplication store = getDependencyStore(context);
        String[] dependencies = _getDependencies(context);
        if(dependencies != null) {
            int count = 0;
            for (int i = 0; i < dependencies.length; i++) {
                if (store.isDependencyStarted(getComponent(), dependencies[i])) {
                    count++;
                }
            }
            return count == dependencies.length;
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // BOOT_COMPLETED” start Service
        if (intent.getAction().equals(BOOT_ACTION)) {
            Intent startIntent = startOnBoot(context);
            if(startIntent != null){
                startComponent(context, startIntent);
            }
        }
        else if(intent.getAction().equals(UPDATE_ACTION)){
            Intent startIntent = restartOnUpdate(context);
            if(startIntent != null){
                startComponent(context, startIntent);
            }
        }
        else if(intent.getAction().equals(DEPENDENCY_STARTED_ACTION)){
            String depName = intent.getStringExtra(EXTRA_COMPONENT_NAME);
            if(depName == null) {
                return;
            }

            // this component hasn't been started yet
            if(getContext() == null){
                if(isDependency(context, depName)){
                    getDependencyStore(context).storeDependencyStarted(getComponent(), depName);
                    if(areAllDependeciesSatisfied(context)){
                        Intent startIntent = startOnAllDependenciesStarted(context);
                        if(startIntent != null) {
                            startComponent(context, startIntent);
                        }
                    }
                    else{
                        Intent startIntent = startOnDependecyStared(context, depName);
                        if(startIntent != null){
                            startComponent(context, startIntent);
                        }
                    }
                }
            }
            // this component is already running
            else{
                if(mDependencyListener != null){
                    mDependencyListener.onDependencyStarted(depName);
                }
            }
        }
        else if(intent.getAction().equals(DEPENDENCY_STOPPED_ACTION)){
            String depName = intent.getStringExtra(EXTRA_COMPONENT_NAME);
            if(depName == null){
                return;
            }

            // this component hasn't been started yet
            if(getContext() == null){
                if(isDependency(context, depName)){
                    getDependencyStore(context).storeDependencyStopped(getComponent(), depName);
                }
            }
            // this component is already running
            else{
                if(mDependencyListener != null){
                    mDependencyListener.onDependencyStopped(depName);
                }
            }
        }
    }

    void sendStartedBroadcast(){
        Intent intent = new Intent();
        intent.setAction(DEPENDENCY_STARTED_ACTION);
        intent.putExtra(EXTRA_COMPONENT_NAME, getComponent().getCanonicalName());
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        getContext().sendBroadcast(intent);
    }

    void sendStoppedBroadcast(){
        Intent intent = new Intent();
        intent.setAction(DEPENDENCY_STOPPED_ACTION);
        intent.putExtra(EXTRA_COMPONENT_NAME, getComponent().getCanonicalName());
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        getContext().sendBroadcast(intent);
    }

    void setDependencyListener(DependencyListener listener){
        mDependencyListener = listener;
    }

    interface DependencyListener{
        void onDependencyStarted(String dependency);
        void onDependencyStopped(String dependency);
    }
}
