package com.acquaintsoft.kotlinapi.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast


import java.io.ByteArrayOutputStream
import java.sql.Time
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.regex.Pattern

object Common {

    private val dialog: Dialog? = null
    private val progressBar: ProgressBar? = null


    fun isEmail(email: String): Boolean {

        val emailPattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

        if (emailPattern.matcher(email).find())
            return true

        return false
    }

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    fun imageToString(BitmapData: Bitmap): String {

        val bos = ByteArrayOutputStream()
        BitmapData.compress(Bitmap.CompressFormat.PNG, 100, bos)
        val byte_arr = bos.toByteArray()

        val file = Base64.encodeToString(byte_arr, Base64.DEFAULT)
        //appendLog(file);
        return file
    }

    fun checkIsMarshMallow(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) true else false
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun isPermissionNotGranted(context: Context, permissions: Array<String>): Boolean {
        var flag = false
        for (i in permissions.indices) {
            if (context.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                flag = true
                break
            }


        }
        return flag
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestPermissions(activity: Activity, permissions: Array<String>, resultCode: Int) {
        activity.requestPermissions(permissions, resultCode)
    }

    fun whichPermisionNotGranted(context: Context, permissions: Array<String>, grantResults: IntArray) {
        for (i in grantResults.indices) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                showToast(context, "Authentication Permission Not Enabled")
            }
        }
    }

    fun getConvertDate(sourceFormat: String, destFormat: String, strDate: String): String {
        var finalDate = ""
        try {

            val srcDf = SimpleDateFormat(sourceFormat)
            // parse the date string into Date object
            val date = srcDf.parse(strDate)
            val destDf = SimpleDateFormat(destFormat)
            // format the date into another format
            finalDate = destDf.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return finalDate

    }


    fun dismissProgressDialog() {
        try {
            if (dialog != null && dialog.isShowing) {
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun isInternetAvailable(context: Context): Boolean {

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo

        if (netInfo != null && netInfo.isConnected && netInfo.isAvailable)
            return true

        return false
    }


    fun getDateTimeStamp(format: String, date: String): Long {
        var timeStamp: Long = 0
        val formatter = SimpleDateFormat(format)
        var mDate: Date? = null
        try {
            mDate = formatter.parse(date) as Date
            timeStamp = mDate.time
        } catch (e: ParseException) {
            timeStamp = 0
            e.printStackTrace()
        }

        return timeStamp
    }

    val currentTimeStamp: Long
        get() {

            val c = Calendar.getInstance()
            val date = c.get(Calendar.YEAR).toString() + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH)
            return getDateTimeStamp("yyyy-MM-dd", date)
        }


    fun getDateFromTimeStamp(timeStamp: Long, dateFormat: String): String {
        val objFormatter = SimpleDateFormat(dateFormat)

        val objCalendar = Calendar.getInstance()
        objCalendar.timeInMillis = timeStamp
        val result = objFormatter.format(objCalendar.time)
        objCalendar.clear()
        return result
    }


    fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: // pre-condition
                return

        var totalHeight = listView.paddingTop + listView.paddingBottom
        val desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.AT_MOST)
        for (i in 0..listAdapter.count - 1) {
            val listItem = listAdapter.getView(i, null, listView)

            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                totalHeight += listItem.measuredHeight

            }
        }

        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
        listView.requestLayout()
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.

     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * *
     * @param context Context to get resources and device specific display metrics
     * *
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val px = dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        return px
    }

    /**
     * This method converts device specific pixels to density independent pixels.

     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * *
     * @param context Context to get resources and device specific display metrics
     * *
     * @return A float value to represent dp equivalent to px value
     */
    fun convertPixelsToDp(px: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val dp = px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        return dp
    }

    fun getDeviceWidth(activity: Activity): Int {

        val wm = activity.windowManager
        val point = Point()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            wm.defaultDisplay.getSize(point)
            return point.x
        } else {
            return wm.defaultDisplay.width
        }
    }

    fun getDeviceHeight(activity: Activity): Int {
        val wm = activity.windowManager
        val point = Point()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            wm.defaultDisplay.getSize(point)
            return point.y
        } else {
            return wm.defaultDisplay.height
        }
    }

    fun isTablet(context: Context): Boolean {
        val xlarge = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == 4
        val large = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

    fun openKeyBoard(context: Context) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun closeKeyBoard(activity: Context) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun hideKeyboard(context: Context) {
        val activity = context as Activity

        val inputManager = context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focus = activity.currentFocus
        if (focus != null)
            inputManager.hideSoftInputFromWindow(focus.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /*  public static void hideKeyboardCall(Context mcontenxt, EditText edtm) {
        try {
            InputMethodManager imm = (InputMethodManager) mcontenxt.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtm.getWindowToken(), 0);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("ConstantUTIL", " error " + ex.toString());
        }
    }

    public static void showKeyboardForced(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }*/


    fun getDisplayWidth(context: Activity): Int {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        return width
    }

    fun getDisplayHeight(context: Activity): Int {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        return height
    }


}
