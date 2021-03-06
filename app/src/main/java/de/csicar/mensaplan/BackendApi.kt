package de.csicar.mensaplan

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import java.util.*

/**
 * Created by csicar on 07.02.18.
 */
object BackendApi {
    val canteens = Collections.synchronizedList(ArrayList<Canteen>())
    private const val url = "http://www.sw-ka.de/json_interface/canteen/?mensa=adenauerring"
    private val updateListeners  =  ArrayList<Response.Listener<List<Canteen>>>()
    private var isRefreshing = false

    fun onUpdate(listener: Response.Listener<List<Canteen>>) {
        updateListeners.add(listener)
    }

    fun refresh(context: Context, errorListener: Response.ErrorListener) {
        synchronized(isRefreshing) {
            if (isRefreshing) return
            isRefreshing = true
        }

        val queue = Volley.newRequestQueue(context)
        val stringRequest = CanteenRequest(Request.Method.GET, url,
                Response.Listener {
                        if (!isRefreshing) return@Listener
                        canteens.clear()
                        canteens.addAll(it)

                        ArrayList(updateListeners).forEach {
                            it.onResponse(canteens)
                        }
                        isRefreshing = false
                },
                errorListener)
        queue.add(stringRequest)
    }
}