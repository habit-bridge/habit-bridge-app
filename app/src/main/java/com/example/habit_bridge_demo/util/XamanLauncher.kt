package com.example.habit_bridge_demo.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Opens a Xumm/Xaman sign URL trying to bypass the browser, so that only the Xaman app
 * subscribes to the payload. Opening the same `https://xumm.app/sign/<uuid>` URL in a browser
 * tab first causes Xumm to refuse the second client with
 * "payload handled by another client".
 *
 * Strategy:
 *  1. Try the original URL with the Xaman package explicitly set.
 *  2. Convert `https://xumm.app/...` to the `xumm://...` deeplink and try again.
 *  3. As a last resort, fall back to a regular ACTION_VIEW (browser/chooser).
 */
object XamanLauncher {

    private const val TAG = "XamanLauncher"
    private const val XAMAN_PACKAGE = "com.xrpllabs.xumm"

    /** Extracts the trailing UUID from a `https://xumm.app/sign/<uuid>` URL. */
    fun extractUuid(signUrl: String): String? {
        val cleaned = signUrl.substringBefore('?').substringBefore('#').trimEnd('/')
        val tail = cleaned.substringAfterLast('/')
        return tail.takeIf { it.length in 8..64 }
    }

    /** Returns true when an app was actually launched. */
    fun open(context: Context, signUrl: String): Result {
        Log.d(TAG, "open() signUrl=$signUrl uuid=${extractUuid(signUrl)}")

        val httpsUri = runCatching { Uri.parse(signUrl) }.getOrNull()
            ?: return Result.InvalidUrl

        // 1. Try Xaman directly with the original URL.
        if (tryLaunch(context, httpsUri, packageName = XAMAN_PACKAGE)) {
            Log.d(TAG, "Launched Xaman via package + https URL")
            return Result.LaunchedXaman
        }

        // 2. Try xumm:// custom-scheme deeplink.
        val deeplink = toXummDeeplink(signUrl)
        if (deeplink != null && tryLaunch(context, deeplink, packageName = null)) {
            Log.d(TAG, "Launched Xaman via xumm:// deeplink: $deeplink")
            return Result.LaunchedXaman
        }

        // 3. Browser fallback. This may produce the "payload handled by another client"
        //    error if the user already had a tab open, but at least the link is not lost.
        if (tryLaunch(context, httpsUri, packageName = null)) {
            Log.w(TAG, "Falling back to browser for $signUrl")
            return Result.LaunchedBrowser
        }
        Log.e(TAG, "No handler found for $signUrl")
        return Result.NoHandler
    }

    private fun toXummDeeplink(httpsUrl: String): Uri? {
        if (!httpsUrl.startsWith("https://xumm.app/")) return null
        val withoutScheme = httpsUrl.removePrefix("https://")
        return runCatching { Uri.parse("xumm://$withoutScheme") }.getOrNull()
    }

    private fun tryLaunch(
        context: Context,
        uri: Uri,
        packageName: String?,
    ): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (packageName != null) setPackage(packageName)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: SecurityException) {
            false
        }
    }

    sealed interface Result {
        /** Xaman app was launched. */
        data object LaunchedXaman : Result

        /** Xaman is not installed; the link was opened in a browser as a fallback. */
        data object LaunchedBrowser : Result

        /** No app on the device could open the link. */
        data object NoHandler : Result

        /** The supplied URL was malformed. */
        data object InvalidUrl : Result
    }
}
