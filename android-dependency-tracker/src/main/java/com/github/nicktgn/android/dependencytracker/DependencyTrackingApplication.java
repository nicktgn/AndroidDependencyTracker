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

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

public class DependencyTrackingApplication extends Application {

    private Map<Class, Map<String, Boolean>> dependenciesState = new HashMap<>();
    private Map<Class, String[]> dependencies = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    void setDependenciesList(Class component, String[] dependenciesList){
        dependencies.put(component, dependenciesList);
    }

    String[] getDependenciesList(Class component){
        return dependencies.get(component);
    }

    boolean isDependencyStarted(Class component, String dependency){
        Map<String, Boolean> deps = dependenciesState.get(component);
        if(deps != null){
            Boolean isStarted = false;
            return ((isStarted = deps.get(dependency)) != null
                    && isStarted.booleanValue());
        }
        return false;
    }

    private Map<String, Boolean> getComponentDependencies(Class component){
        Map<String, Boolean> deps = dependenciesState.get(component);
        if(deps == null){
            deps = new HashMap<>();
            dependenciesState.put(component, deps);
        }
        return deps;
    }

    void storeDependencyStarted(Class component, String dependency){
        Map<String, Boolean> deps = getComponentDependencies(component);
        deps.put(dependency, true);
    }

    void storeDependencyStopped(Class component, String dependency){
        Map<String, Boolean> deps = getComponentDependencies(component);
        deps.put(dependency, false);
    }

}
