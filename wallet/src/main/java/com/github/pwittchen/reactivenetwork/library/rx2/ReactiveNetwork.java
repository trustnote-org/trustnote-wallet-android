/*
 * Copyright (C) 2016 Piotr Wittchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pwittchen.reactivenetwork.library.rx2;

import android.Manifest;
import android.content.Context;
import android.support.annotation.RequiresPermission;

import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.NetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.LollipopNetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.MarshmallowNetworkObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.network.observing.strategy.PreLollipopNetworkObservingStrategy;

import io.reactivex.Observable;

/**
 * ReactiveNetwork is an Android library
 * listening network connection state and change of the WiFi signal strength
 * with RxJava Observables. It can be easily used with RxAndroid.
 */
public class ReactiveNetwork {
    public final static String LOG_TAG = "ReactiveNetwork";
    private static final String DEFAULT_PING_HOST = "http://clients3.google.com/generate_204";
    private static final int DEFAULT_PING_PORT = 80;
    private static final int DEFAULT_PING_INTERVAL_IN_MS = 2000;
    private static final int DEFAULT_INITIAL_PING_INTERVAL_IN_MS = 0;
    private static final int DEFAULT_PING_TIMEOUT_IN_MS = 2000;

    protected ReactiveNetwork() {
    }

    /**
     * Creates a new inst of the ReactiveNetwork class
     *
     * @return ReactiveNetwork object
     */
    public static ReactiveNetwork create() {
        return new ReactiveNetwork();
    }

    /**
     * Observes network connectivity. Information about network state, type and typeName are contained
     * in
     * observed Connectivity object.
     *
     * @param context Context of the activity or an application
     * @return RxJava Observable with Connectivity class containing information about network state,
     * type and typeName
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static Observable<Connectivity> observeNetworkConnectivity(final Context context) {
        final NetworkObservingStrategy strategy;

        if (Preconditions.isAtLeastAndroidMarshmallow()) {
            strategy = new MarshmallowNetworkObservingStrategy();
        } else if (Preconditions.isAtLeastAndroidLollipop()) {
            strategy = new LollipopNetworkObservingStrategy();
        } else {
            strategy = new PreLollipopNetworkObservingStrategy();
        }

        return observeNetworkConnectivity(context, strategy);
    }

    /**
     * Observes network connectivity. Information about network state, type and typeName are contained
     * in observed Connectivity object. Moreover, allows you to define NetworkObservingStrategy.
     *
     * @param context  Context of the activity or an application
     * @param strategy NetworkObserving strategy to be applied - you can use one of the existing
     *                 strategies {@link PreLollipopNetworkObservingStrategy},
     *                 {@link LollipopNetworkObservingStrategy} or create your own custom strategy
     * @return RxJava Observable with Connectivity class containing information about network state,
     * type and typeName
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static Observable<Connectivity> observeNetworkConnectivity(final Context context,
                                                                      final NetworkObservingStrategy strategy) {
        Preconditions.checkNotNull(context, "context == null");
        Preconditions.checkNotNull(strategy, "strategy == null");
        return strategy.observeNetworkConnectivity(context);
    }

}
