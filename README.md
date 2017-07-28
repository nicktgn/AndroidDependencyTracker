## Android Depependency Tracker

Simple SDK for automatic tracking of dependencies between separate components(services/activities) 
across multiple APKs.

#### Feautres:
* auto-start component on device boot, after a specific dependency started or after all 
dependencies have been started
* receive runtime notifications when dependency have stopped or started to know when dependency 
can be used.

#### Usage:

1) Add the following to `build.gradle` of the main module you want to use this interface with:
       
   ```
   dependencies {
       compile 'com.github.nicktgn.android:android-dependency-tracker:0.1.0'
       // use the following if you need mvp support from TinyAndroidMVP https://github.com/nicktgn/TinyAndroidMVP
       compile 'com.github.nicktgn.android:android-dependency-tracker-mvp:0.1.0'
   }
   ```

2) In your project create a subclass of `DependencyTrackingApplication` and add declare this subclass
  in your project's `AndroidManifest.xml`:
   ```
   <manifest xmlns:android="http://schemas.android.com/apk/res/android"
            ... >
  
      <application
          android:name=".%subclass of DependencyTrackingApplication%"
          ... >
      </application>
   </manifest>
   ```
3) For each component (service or activity):

   a) Subclass `DependencyTracker` and declare this subclass in your project's `AndroidManifest.xml`
   as a `<receiver>`:
   ```
   <manifest xmlns:android="http://schemas.android.com/apk/res/android"
               ... >
     
       <application
           android:name=".%subclass of DependencyTrackingApplication%"
             ... >
             
           <receiver
               android:name=".%subclass of DependencyTracker%"
               android:enabled="true"
               android:exported="true">
               <intent-filter>
                   <action android:name="android.intent.action.BOOT_COMPLETED"/>
                   <action android:name="com.gitlab.nicktgn.android.dependencytracker.action.DEPENDENCY_STARTED"/>
                   <action android:name="com.gitlab.nicktgn.android.dependencytracker.action.DEPENDENCY_STOPPED"/>
                   <category android:name="android.intent.category.DEFAULT"/>
               </intent-filter>
           </receiver>
       </application>
   </manifest>
   ```
   Note that you need `android:exported="true"` to be able to receive broadcast notifications from components outside
   of your APK.
   
   Your component's dependencies are declared in the subclass of `DependencyTracker`.
   
   b) Subclass your component from component base classes: `DependencyTrackingAppCompatActivity` for activity components,
   `DependencyTrackingService` for service components. Declare these components as usual services or activities in your
   `AndroidManifest.xml`.
      

### API:

#### `DependencyTracker`:

*  **`public abstract Context getContext()`**
   
     Override this method and return the `Context` of your component;
   
*  **`public abstract String[] getDependencies(Context context)`**

     * `context` - application context

     Override this method and return the `String` array of your component's dependencies. 
     Dependency is identified using a fully qualified name of the component's class, e.g.:     
     
     ```
     public String[] getDependencies(Context context){
         return new String[]{
             "my.package.MyComponentService",
             "my.other.package.MyOtherComponentActivity"
         };
     }
     ```

*  **`public abstract Class getComponent()`**

     Override this method and return the `Class` of your component. e.g.:
     
     ```
     public Class getComponent(){
         return MyComponentService.class;
     }
     ```
     **Component naming convention** is same as for any Android app: end your service class on `Service`,
     and your activity class on `Activity`. This SDK uses this naming convention to distinguish between
     component types so that it can start them properly.

*  **`public abstract Intent startOnBoot(Context context)`**
       
     * `context` - application context
          
     Override this method and return an `Intent` that can be used to start your component
     after the device boot process completed, e.g.:
     ```
     public Intent startOnBoot(Context context){
        return new Intent(context, MyAwesomeService.class);
     }
     ``` 
     If this component does not need to be started on device boot, simply return `null`.
     
*  **`public abstract Intent startOnDependecyStared(Context context, String dependency)`**

     * `context` - application context
     * `dependency` - fully qualified name of the dependency component class

     Override this method and return and `Intent` that can be used to start your component after
     it's dependency, specified in `dependency` parameter, have started, e.g.:
     ```
     public Intent startOnDependecyStared(Context context, String dependency){
        if(dependency.isEqual("my.package.MyDependencyService"){
            return new Intent(context, MyOtherAwesomeService.class);
        }
        return null;
     }
     ```      
     If this component does not need to be started after its dependency started, simply return `null`.

*  **`public abstract Intent startOnAllDependenciesStarted(Context context)`**

     * `context` - application context
     
     Override this method and return and `Intent` that can be used to start your component after
     all it's dependencies have started, e.g.:
     ```
     public Intent startOnAllDependeciesStared(Context context){
        return new Intent(context, MyAwesomeService.class);
     }
     ```      
     If this component does not need to be started after all its dependencies have started, simply return `null`.     
     
### `DependencyTrackingService`:

*  **`public void onCreate()`**

     Override this method and call its supper method, e.g.:
     ```
     public void onCreate() {
         super.onCreate();
     }
     ```
     This is necessary for a proper broadcast notification reporting by each component.
 
*  **`public void onDestroy()`**

     Override this method and call its supper method, e.g.:
     ```
     public void onDestroy() {
         super.onDestroy();
     }
     ```
     This is necessary for a proper broadcast notification reporting by each component.     
 
*  **`public <T extends DependencyTracker> getDependencyTracker()`**

     Override this method and return an instance of the `DependencyTracker` subclass for this component.
     
*  **`public void onDependencyStarted(String dependency)`**

     * `dependency` - fully qualified name of the dependency component class

     This method is called when one of your component's dependencies have started (or restarted). In this 
     method you can re-establish connection to your component's dependency (e.g. `bindService()`)

*  **`public void onDependencyStopped(String dependency)`**

     * `dependency` - fully qualified name of the dependency component class

     This method is called when one of your component's dependencies have stopped. In this 
     method you can make sure that connection to your component's dependency is properly closed and this
     connection will not be used until the next call to `onDependencyStarted`.
     
### `DependencyTrackingAppCompatActivity`:

*  **`protected void onCreate(Bundle savedInstanceState)`**

     Override this method and call its supper method, e.g.:
     ```
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
     }
     ```
     This is necessary for a proper broadcast notification reporting by each component.
 
*  **`protected void onDestroy()`**

     Override this method and call its supper method, e.g.:
     ```
     protected void onDestroy() {
         super.onDestroy();
     }
     ```
     This is necessary for a proper broadcast notification reporting by each component.     
 
*  **`public <T extends DependencyTracker> getDependencyTracker()`**

     Override this method and return an instance of the `DependencyTracker` subclass for this component.
     
*  **`public void onDependencyStarted(String dependency)`**

     * `dependency` - fully qualified name of the dependency component class

     This method is called when one of your component's dependencies have started (or restarted). In this 
     method you can re-establish connection to your component's dependency (e.g. `bindService()`)

*  **`public void onDependencyStopped(String dependency)`**

     * `dependency` - fully qualified name of the dependency component class

     This method is called when one of your component's dependencies have stopped. In this 
     method you can make sure that connection to your component's dependency is properly closed and this
     connection will not be used until the next call to `onDependencyStarted`.