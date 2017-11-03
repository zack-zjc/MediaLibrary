package com.realcloud.media.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LoochaAsyncTaskLoader;

/**
 * Created by zack on 2017/9/29.
 */

public abstract class MediaBaseLoader<E> extends LoochaAsyncTaskLoader<E> implements LoaderManager.LoaderCallbacks<E> {

    private Bundle bundleArgs;

    public Bundle getBundleArgs() {
        return bundleArgs;
    }

    public MediaBaseLoader(Context context) {
        super(context);
    }

    @Override
    protected DATA_SOURCE getDataSource() {
        return DATA_SOURCE.LOCAL;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }

    @Override
    public Loader<E> onCreateLoader(int i, Bundle bundle) {
        this.bundleArgs = bundle;
        return this;
    }

    @Override
    public void onLoaderReset(Loader<E> loader) {

    }

}
