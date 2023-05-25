package systems.hammer.adsdk.data

import android.util.Log
import org.json.JSONObject
import systems.hammer.adsdk.AdSettings
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

object RequestHandler {

    private const val GET: String = "GET"
    private const val POST: String = "POST"

    @Throws(Exception::class)
    fun requestPOST(r_url: String?, postDataParams: JSONObject?): String? {
        val url = URL(r_url)
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        conn.readTimeout = AdSettings.readTimeout
        conn.connectTimeout = AdSettings.connectTimeOut
        conn.requestMethod = POST
        conn.doInput = true
        conn.doOutput = true
        postDataParams?.let {
            val os: OutputStream = conn.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(encodeParams(it))
            writer.flush()
            writer.close()
            os.close()
        }
        val responseCode: Int = conn.responseCode // To Check for 200
        Log.d(this.javaClass.simpleName,"Post response code = $responseCode")
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            BufferedReader(InputStreamReader(conn.inputStream)).use {reader ->
                val sb = StringBuffer("")
                var line: String? = ""
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                    break
                }
                reader.close()
                return sb.toString()
            }
        }
        return null
    }

    @Throws(IOException::class)
    fun requestGET(url: String?): String? {
        val obj = URL(url)
        val con = obj.openConnection() as HttpURLConnection
        con.readTimeout = AdSettings.readTimeout
        con.connectTimeout = AdSettings.connectTimeOut
        con.requestMethod = GET
        val responseCode = con.responseCode
        Log.d(this.javaClass.simpleName,"Get response code :: $responseCode")
        return if (responseCode == HttpURLConnection.HTTP_OK) { // connection ok
            BufferedReader(InputStreamReader(con.inputStream)).use { reader ->
                var inputLine: String?
                val response = StringBuffer()
                while (reader.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                reader.close()
                response.toString()
            }
        } else {
            null
        }
    }

    @Throws(java.lang.Exception::class)
    private fun encodeParams(params: JSONObject): String? {
        val result = StringBuilder()
        var first = true
        val itr = params.keys()
        while (itr.hasNext()) {
            val key = itr.next()
            val value = params[key]
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }
        return result.toString()
    }
}