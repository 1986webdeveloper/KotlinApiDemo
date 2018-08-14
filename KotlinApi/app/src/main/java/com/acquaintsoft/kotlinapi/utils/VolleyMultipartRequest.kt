package com.acquaintsoft.kotlinapi.utils


import android.net.Uri

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException

/**
 * Created by agc-android on 9/3/17.
 */
open class VolleyMultipartRequest : Request<NetworkResponse> {
    private val twoHyphens = "--"
    private val lineEnd = "\r\n"
    private val boundary = "apiclient-" + System.currentTimeMillis()

    private var mListener: Response.Listener<NetworkResponse>? = null
    private var mErrorListener: Response.ErrorListener? = null
    private var mHeaders: Map<String, String>? = null
    private var transferred = 0

    /**
     * Custom method handle data payload.
     *
     * @return Map data part label with data byte
     * @throws AuthFailureError
     */

    /**
     * Default constructor with predefined header and post method.
     *
     * @param url           request destination
     * @param headers       predefined custom header
     * @param listener      on success achieved 200 code from request
     * @param errorListener on error http or library timeout
     */
    constructor(url: String, headers: Map<String, String>,
                listener: Response.Listener<NetworkResponse>,
                errorListener: Response.ErrorListener) : super(Request.Method.POST, url, errorListener) {
        this.mListener = listener
        this.mErrorListener = errorListener
        this.mHeaders = headers
    }

    /**
     * Constructor with option method and default header configuration.
     *
     * @param method        method for now accept POST and GET only
     * @param url           request destination
     * @param listener      on success event handler
     * @param errorListener on error event handler
     */
    constructor(method: Int, url: String,
                listener: Response.Listener<NetworkResponse>,
                errorListener: Response.ErrorListener) : super(method, url, errorListener) {
        this.mListener = listener
        this.mErrorListener = errorListener

    }

    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        return mHeaders ?: super.getHeaders()
    }

    override fun getBodyContentType(): String {
        return "multipart/form-data;boundary=$boundary"
    }

    @Throws(AuthFailureError::class)
    override fun getBody(): ByteArray? {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)

        try {
            // populate text payload
            val params = params
            if (params != null && params.size > 0) {
                textParse(dos, params, paramsEncoding)
            }

            // populate data byte payload
            val data = getByteData()
            if (data != null && data.size > 0) {
                dataParse(dos, data)
            }

            // close multipart form data after text and file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

            return bos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    @Throws(AuthFailureError::class)
    protected open fun getByteData(): Map<String, DataPart>? {
        return null
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            return Response.error(ParseError(e))
        }

    }

    override fun deliverResponse(response: NetworkResponse) {
        mListener!!.onResponse(response)
    }

    override fun deliverError(error: VolleyError) {
        mErrorListener!!.onErrorResponse(error)
    }

    /**
     * Parse string map into data output stream by key and value.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param params           string inputs collection
     * @param encoding         encode the inputs, default UTF-8
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun textParse(dataOutputStream: DataOutputStream, params: Map<String, String>, encoding: String) {
        try {
            for ((key, value) in params) {
                buildTextPart(dataOutputStream, key, value)
            }
        } catch (uee: UnsupportedEncodingException) {
            throw RuntimeException("Encoding not supported: $encoding", uee)
        }

    }

    /**
     * Parse data into data output stream.
     *
     * @param dataOutputStream data output stream handle file attachment
     * @param data             loop through data
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun dataParse(dataOutputStream: DataOutputStream, data: Map<String, DataPart>) {
        for ((key, value) in data) {
            buildDataPart(dataOutputStream, value, key)
        }
    }

    /**
     * Write string data into header and data output stream.
     *
     * @param dataOutputStream data output stream handle string parsing
     * @param parameterName    name of input
     * @param parameterValue   value of input
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun buildTextPart(dataOutputStream: DataOutputStream, parameterName: String, parameterValue: String) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$parameterName\"$lineEnd")
        //dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd)
        dataOutputStream.write(parameterValue.toByteArray(charset("utf-8")))
        dataOutputStream.writeBytes(lineEnd)
        //        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }

    /**
     * Write data file into header and data output stream.
     *
     * @param dataOutputStream data output stream handle data parsing
     * @param dataFile         data byte as DataPart from collection
     * @param inputName        name of data input
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun buildDataPart(dataOutputStream: DataOutputStream, dataFile: DataPart, inputName: String) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                inputName + "\"; filename=\"" + dataFile.fileName + "\"" + lineEnd)
        if (dataFile.type != null && !dataFile.type!!.trim { it <= ' ' }.isEmpty()) {
            dataOutputStream.writeBytes("Content-Type: " + dataFile.type + lineEnd)
        }
        dataOutputStream.writeBytes(lineEnd)

        val fileInputStream = ByteArrayInputStream(dataFile.content)
        var bytesAvailable = fileInputStream.available()

        val maxBufferSize = 1024 * 1024
        var bufferSize = Math.min(bytesAvailable, maxBufferSize)
        val buffer = ByteArray(bufferSize)

        var bytesRead = fileInputStream.read(buffer, 0, bufferSize)
        var total: Long = 0

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize)
            total = dataOutputStream.size().toLong()
            transferred = (total * 100 / dataFile.content!!.size).toInt()
            //            onProgressUpdate.onTransferred(transferred);
            bytesAvailable = fileInputStream.available()
            bufferSize = Math.min(bytesAvailable, maxBufferSize)
            bytesRead = fileInputStream.read(buffer, 0, bufferSize)
        }

        dataOutputStream.writeBytes(lineEnd)

    }

    /**
     * Simple data container use for passing byte file
     */
    class DataPart {
        /**
         * Getter file name.
         *
         * @return file name
         */
        /**
         * Setter file name.
         *
         * @param fileName string file name
         */
        var fileName: String? = null
        /**
         * Getter content.
         *
         * @return byte file data
         */
        /**
         * Setter content.
         *
         * @param content byte file data
         */
        var content: ByteArray? = null
        /**
         * Getter mime type.
         *
         * @return mime type
         */
        /**
         * Setter mime type.
         *
         * @param type mime type
         */
        var type: String? = null
        var imageUri: Uri? = null
        var imageFile: File? = null
        var isEdit: Boolean = false

        /**
         * Default data part
         */
        constructor() {}

        /**
         * Constructor with data.
         *
         * @param name label of data
         * @param data byte data
         */
        constructor(name: String, data: ByteArray) {
            fileName = name
            content = data
        }

        /**
         * Constructor with mime data type.
         *
         * @param name     label of data
         * @param data     byte data
         * @param mimeType mime data like "image/jpeg"
         */
        constructor(name: String, data: ByteArray, mimeType: String) {
            fileName = name
            content = data
            type = mimeType
        }
    }


}
