package com.rodolfonavalon.canadatransit.controller.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This manages the life cycle of the activities which handles the callbacks and the
 * pool of activities. Once the activity is registered it will call its life cycle callback
 * multiple times, until, either it is unregistered or when the activity is destroyed.
 */
public class LifecycleManager implements Application.ActivityLifecycleCallbacks {

    /**
     * Callback whenever an activity's life cycle is triggered.
     */
    public interface LifecycleCallback {
        void onActivityLifecycleCallback(LifecycleStage stage);
    }

    /**
     * Life cycle stages of the activity represented in enum values.
     */
    public enum LifecycleStage {
        CREATED,    // onActivityCreated(Activity, Bundle)
        STARTED,    // onActivityStarted(Activity)
        RESUMED,    // onActivityResumed(Activity)
        PAUSED,     // onActivityPaused(Activity)
        STOPPED,    // onActivityStopped(Activity)
        SAVED,      // onActivitySaveInstanceState(Activity, Bundle)
        DESTROYED   // onActivityDestroyed(Activity)
    }

    private final List<Pair<Activity, LifecycleCallback>> lifecycleItems = new ArrayList<>();

    private static LifecycleManager instance;

    /**
     *  Retrieves the {@link LifecycleManager} singleton instance
     */
    private static LifecycleManager getInstance() {
        if (instance == null) {
            instance = new LifecycleManager();
        }
        return instance;
    }

    /**
     *  Watches the activity that registers the life cycle that can trigger every {@link LifecycleStage}.
     *
     *  ATTENTION:  This wont remove the activity cycle callback until either the activity
     *              is destroyed or unregister the activity through {@link #ignoreActivity(Activity)}.
     *
     *  @param activity
     *              The activity to listen for life cycle callbacks
     *  @param callback
     *              The callback method whenever the activity triggered the life cycle callbacks
     */
    public static void watchActivity(@NonNull Activity activity, @NonNull LifecycleCallback callback) {
        LifecycleManager manager = getInstance();
        activity.getApplication().registerActivityLifecycleCallbacks(manager);
        manager.lifecycleItems.add(new Pair<>(activity, callback));
    }

    /**
     *  Ignores the activity that will unregister the life cycle callbacks.
     *
     *  ATTENTION:  This must be called whenever an activity is registered for callbacks
     *              through {@link #watchActivity(Activity, LifecycleCallback)}
     *
     *  @param activity
     *              The activity to unregister the life cycle callbacks
     */
    public static void ignoreActivity(@NonNull Activity activity) {
        LifecycleManager manager = getInstance();
        activity.getApplication().unregisterActivityLifecycleCallbacks(manager);
        Iterator<Pair<Activity, LifecycleCallback>> iterator = manager.lifecycleItems.iterator();
        while (iterator.hasNext()) {
            Pair<Activity, LifecycleCallback> item = iterator.next();
            if (activity == item.first) {
                iterator.remove();
                return;
            }
        }
    }

    /**
     *  Signals the activity for life cycle callback which will trigger the {@link LifecycleCallback}
     *  if it exist in the pool.
     *
     *  @param activity
     *              The activity to signal for life cycle callbacks
     *  @param stage
     *              The stage of the activity life cycle
     */
    private static void signalActivity(@NonNull Activity activity, LifecycleStage stage) {
        LifecycleManager manager = getInstance();
        for (Pair<Activity, LifecycleCallback> item : manager.lifecycleItems) {
            if (activity == item.first) {
                item.second.onActivityLifecycleCallback(stage);
                return;
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        signalActivity(activity, LifecycleStage.CREATED);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        signalActivity(activity, LifecycleStage.STARTED);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        signalActivity(activity, LifecycleStage.RESUMED);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        signalActivity(activity, LifecycleStage.PAUSED);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        signalActivity(activity, LifecycleStage.STOPPED);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        signalActivity(activity, LifecycleStage.SAVED);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        signalActivity(activity, LifecycleStage.DESTROYED);
        ignoreActivity(activity);
    }
}

