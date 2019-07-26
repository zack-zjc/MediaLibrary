package com.zack.media.loader

import android.content.Context
import android.os.Bundle
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader

/**
 * @Author zack
 * @Date 2019/7/26
 * @Description 基础loader
 * @Version 1.0
 */
abstract class MediaBaseLoader<T>(context: Context) : AsyncTaskLoader<T>(context), LoaderManager.LoaderCallbacks<T>{

    private var bundleArgs: Bundle? = null

    fun getBundleArgs(): Bundle? {
        return bundleArgs
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<T> {
        this.bundleArgs = args
        return this
    }

    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()
    }

    override fun onStopLoading() {
        super.onStopLoading()
        cancelLoad()
    }

    override fun onReset() {
        super.onReset()
        onStopLoading()
    }

    override fun onLoaderReset(loader: Loader<T>) {

    }
}