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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DependencyTrackerTest {

    Context mockedContext;

    String dependency1 = "com.github.nicktgn.android.dependencytracker.DependencyTrackerTest.FakeDependency1";
    String dependency2 = "com.github.nicktgn.android.dependencytracker.DependencyTrackerTest.FakeDependency2";
    String[] dependeciesList = new String[]{dependency1, dependency2};

    class FakeComponent {

    }

    class TestDependencyTracker extends DependencyTracker{

        @Override
        public Context getContext() {
            return null;
        }

        @Override
        public String[] getDependencies(Context context) {
            return new String[0];
        }

        @Override
        public Class getComponent() {
            return null;
        }

        @Override
        public Intent startOnBoot(Context context) {
            return null;
        }

        @Override
        public Intent startOnDependecyStared(Context context, String dependency) {
            return null;
        }

        @Override
        public Intent startOnAllDependenciesStarted(Context context) {
            return null;
        }
    }


    @Test
    public void should_sendStartedBroadcast(){
        //arrange
        Context mockedContext = mock(Context.class);
        TestDependencyTracker testDT = new TestDependencyTracker();
        TestDependencyTracker spyTestDT = spy(testDT);
        when(spyTestDT.getContext()).thenReturn(mockedContext);
        when(spyTestDT.getComponent()).thenReturn(FakeComponent.class);

        //act
        spyTestDT.sendStartedBroadcast();

        //assert
        ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        verify(mockedContext).sendBroadcast(argument.capture());

        assertThat(argument.getValue()).isNotNull();
        assertThat(argument.getValue().getAction()).isEqualTo(DependencyTracker.DEPENDENCY_STARTED_ACTION);
        assertThat(argument.getValue().getStringExtra(DependencyTracker.EXTRA_COMPONENT_NAME)).isEqualTo(FakeComponent.class.getCanonicalName());
    }

    @Test
    public void should_sendStoppedBroadcast(){
        //arrange
        Context mockedContext = mock(Context.class);
        TestDependencyTracker testDT = new TestDependencyTracker();
        TestDependencyTracker spyTestDT = spy(testDT);
        when(spyTestDT.getContext()).thenReturn(mockedContext);
        when(spyTestDT.getComponent()).thenReturn(FakeComponent.class);

        //act
        spyTestDT.sendStoppedBroadcast();

        //assert
        ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        verify(mockedContext).sendBroadcast(argument.capture());

        assertThat(argument.getValue()).isNotNull();
        assertThat(argument.getValue().getAction()).isEqualTo(DependencyTracker.DEPENDENCY_STOPPED_ACTION);
        assertThat(argument.getValue().getStringExtra(DependencyTracker.EXTRA_COMPONENT_NAME)).isEqualTo(FakeComponent.class.getCanonicalName());
    }

    @Test
    public void should_callStartOnBoot_andNotStartService(){
        //arrange
        Context applicationContext = mock(Context.class);
        TestDependencyTracker testDT = new TestDependencyTracker();
        TestDependencyTracker spyTestDT = spy(testDT);
        when(spyTestDT.getComponent()).thenReturn(FakeComponent.class);
        when(spyTestDT.startOnBoot(applicationContext)).thenReturn(null);

        //act
        Intent bootAction = new Intent();
        bootAction.setAction(DependencyTracker.BOOT_ACTION);
        spyTestDT.onReceive(applicationContext, bootAction);

        //assert
        verify(spyTestDT).startOnBoot(applicationContext);
        verify(applicationContext, never()).startService(any(Intent.class));
    }

    @Test
    public void should_callStartOnBoot_andStartService(){
        //arrange
        Context applicationContext = mock(Context.class);
        TestDependencyTracker testDT = new TestDependencyTracker();
        TestDependencyTracker spyTestDT = spy(testDT);
        when(spyTestDT.getComponent()).thenReturn(FakeComponent.class);
        Intent intent = new Intent(applicationContext, FakeComponent.class);
        when(spyTestDT.startOnBoot(applicationContext)).thenReturn(intent);

        //act
        Intent bootAction = new Intent();
        bootAction.setAction(DependencyTracker.BOOT_ACTION);
        spyTestDT.onReceive(applicationContext, bootAction);

        //assert
        verify(spyTestDT).startOnBoot(applicationContext);
        verify(applicationContext).startService(intent);
    }

    @Test
    public void should_notCallOnAllDependenciesStarted_whenNotAllDependenciesHaveStarted(){
        //arrange
        DependencyTrackingApplication mockedDTApp = mock(DependencyTrackingApplication.class);
        when(mockedDTApp.getDependenciesList(FakeComponent.class)).thenReturn(dependeciesList);
        when(mockedDTApp.isDependencyStarted(FakeComponent.class, dependency1)).thenReturn(true);
        when(mockedDTApp.isDependencyStarted(FakeComponent.class, dependency2)).thenReturn(false);

        Context applicationContext = mock(Context.class);
        when(applicationContext.getApplicationContext()).thenReturn(mockedDTApp);

        TestDependencyTracker testDT = new TestDependencyTracker();
        TestDependencyTracker spyTestDT = spy(testDT);
        when(spyTestDT.getComponent()).thenReturn(FakeComponent.class);
        when(spyTestDT.getDependencies(applicationContext))
                .thenReturn(dependeciesList);
        when(spyTestDT.startOnAllDependenciesStarted(applicationContext)).thenReturn(null);

        //act
        Intent dependencyStartedAction = new Intent();
        dependencyStartedAction.setAction(DependencyTracker.DEPENDENCY_STARTED_ACTION);
        dependencyStartedAction.putExtra(DependencyTracker.EXTRA_COMPONENT_NAME, dependency1);
        spyTestDT.onReceive(applicationContext, dependencyStartedAction);

        //assert
        verify(spyTestDT, never()).startOnAllDependenciesStarted(applicationContext);
        verify(applicationContext, never()).startService(any(Intent.class));
    }

    @Test
    public void should_callStartOnAllDependenciesStarted_andNotStartService(){
        //arrange
        DependencyTrackingApplication mockedDTApp = mock(DependencyTrackingApplication.class);
        when(mockedDTApp.getDependenciesList(FakeComponent.class)).thenReturn(dependeciesList);
        when(mockedDTApp.isDependencyStarted(FakeComponent.class, dependency1)).thenReturn(true);
        when(mockedDTApp.isDependencyStarted(FakeComponent.class, dependency2)).thenReturn(true);

        Context applicationContext = mock(Context.class);
        when(applicationContext.getApplicationContext()).thenReturn(mockedDTApp);

        TestDependencyTracker testDT = new TestDependencyTracker();
        TestDependencyTracker spyTestDT = spy(testDT);
        when(spyTestDT.getComponent()).thenReturn(FakeComponent.class);
        when(spyTestDT.getDependencies(applicationContext))
                .thenReturn(dependeciesList);
        when(spyTestDT.startOnAllDependenciesStarted(applicationContext)).thenReturn(null);

        //act
        Intent dependencyStartedAction = new Intent();
        dependencyStartedAction.setAction(DependencyTracker.DEPENDENCY_STARTED_ACTION);
        dependencyStartedAction.putExtra(DependencyTracker.EXTRA_COMPONENT_NAME, dependency2);
        spyTestDT.onReceive(applicationContext, dependencyStartedAction);

        //assert
        verify(spyTestDT).startOnAllDependenciesStarted(applicationContext);
        verify(applicationContext, never()).startService(any(Intent.class));
    }

    @Test
    public void should_callStartOnAllDependenciesStarted_andStartService(){
        //arrange
        DependencyTrackingApplication mockedDTApp = mock(DependencyTrackingApplication.class);
        when(mockedDTApp.getDependenciesList(FakeComponent.class)).thenReturn(dependeciesList);
        when(mockedDTApp.isDependencyStarted(FakeComponent.class, dependency1)).thenReturn(true);
        when(mockedDTApp.isDependencyStarted(FakeComponent.class, dependency2)).thenReturn(true);

        Context applicationContext = mock(Context.class);
        when(applicationContext.getApplicationContext()).thenReturn(mockedDTApp);

        TestDependencyTracker testDT = new TestDependencyTracker();
        TestDependencyTracker spyTestDT = spy(testDT);
        when(spyTestDT.getComponent()).thenReturn(FakeComponent.class);
        when(spyTestDT.getDependencies(applicationContext))
                .thenReturn(dependeciesList);
        Intent intent = new Intent(applicationContext, FakeComponent.class);
        when(spyTestDT.startOnAllDependenciesStarted(applicationContext)).thenReturn(intent);

        //act
        Intent dependencyStartedAction = new Intent();
        dependencyStartedAction.setAction(DependencyTracker.DEPENDENCY_STARTED_ACTION);
        dependencyStartedAction.putExtra(DependencyTracker.EXTRA_COMPONENT_NAME, dependency2);
        spyTestDT.onReceive(applicationContext, dependencyStartedAction);

        //assert
        verify(spyTestDT).startOnAllDependenciesStarted(applicationContext);
        verify(applicationContext).startService(intent);
    }

    @Test
    public void should_callListenerOnDependencyStarted(){
        //arrange
        Context mockedContext = mock(Context.class);
        Context applicationContext = mock(Context.class);

        TestDependencyTracker testDT = new TestDependencyTracker();
        TestDependencyTracker spyTestDT = spy(testDT);
        when(spyTestDT.getContext()).thenReturn(mockedContext);
        when(spyTestDT.getComponent()).thenReturn(FakeComponent.class);
        when(spyTestDT.getDependencies(applicationContext))
                .thenReturn(dependeciesList);

        DependencyTracker.DependencyListener mockedDependencyListener = mock(DependencyTracker.DependencyListener.class);
        testDT.setDependencyListener(mockedDependencyListener);

        //act
        Intent dependencyStartedAction = new Intent();
        dependencyStartedAction.setAction(DependencyTracker.DEPENDENCY_STARTED_ACTION);
        dependencyStartedAction.putExtra(DependencyTracker.EXTRA_COMPONENT_NAME, dependency1);
        spyTestDT.onReceive(applicationContext, dependencyStartedAction);

        //assert
        verify(mockedDependencyListener).onDependencyStarted(dependency1);
    }

    public void should_callListenerOnDependencyStopped(){
        //arrange
        Context mockedContext = mock(Context.class);
        Context applicationContext = mock(Context.class);

        TestDependencyTracker testDT = new TestDependencyTracker();
        TestDependencyTracker spyTestDT = spy(testDT);
        when(spyTestDT.getContext()).thenReturn(mockedContext);
        when(spyTestDT.getComponent()).thenReturn(FakeComponent.class);
        when(spyTestDT.getDependencies(applicationContext))
                .thenReturn(dependeciesList);

        DependencyTracker.DependencyListener mockedDependencyListener = mock(DependencyTracker.DependencyListener.class);
        testDT.setDependencyListener(mockedDependencyListener);

        //act
        Intent dependencyStartedAction = new Intent();
        dependencyStartedAction.setAction(DependencyTracker.DEPENDENCY_STOPPED_ACTION);
        dependencyStartedAction.putExtra(DependencyTracker.EXTRA_COMPONENT_NAME, dependency1);
        spyTestDT.onReceive(applicationContext, dependencyStartedAction);

        //assert
        verify(mockedDependencyListener).onDependencyStopped(dependency1);
    }

    @Test
    public void should_startActivity_whenComponentNameEndsOnActivity(){

    }

    @Test
    public void should_startService_whenComponentNameEndsOnService(){

    }

    @Test
    public void should_startService_whenComponentNameEndsOnNeitherActivityNorService(){

    }
}
