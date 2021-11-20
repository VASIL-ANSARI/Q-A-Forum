package com.example.myapplication

import android.view.View
import android.widget.TextView
import com.speechly.client.slu.Segment
import com.speechly.client.speech.Client
import com.speechly.ui.SpeechlyButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SpeechlyClass(private var btn: SpeechlyButton,val speechlyClient: Client,val textView: TextView){

    private var languageFilter: String? = null
    private var say: String? = null
    private var order: String? = null
    private var notifications: String? = null
    private var email: String? = null
    private var account: String? = null
    private var rate: String? = null
    private var type: String? = null
    private var list:ArrayList<String?>?  =null

    init {


        GlobalScope.launch(Dispatchers.Default){
            speechlyClient.onSegmentChange { segment: Segment ->
                val transcript: String = segment.words.values.joinToString(" ") { it.value }

                GlobalScope.launch(Dispatchers.Main){


                    textView?.setText("${transcript}")

                    if (segment.intent != null) {
                        when(segment.intent?.intent) {
                            "search" -> {
                                languageFilter = segment.getEntityByType("language")?.value
                                //languageFilter?.let { Log.d("message", it) }

                                func(arrayListOf("search",languageFilter))

                            }
                            "sort" -> {
                                say = segment.getEntityByType("say")?.value
                                order = segment.getEntityByType("order")?.value
                                // say?.let { Log.d("message", it) }
                                // order?.let { Log.d("message", it) }
                                func(arrayListOf("sort",say,order))

                            }
                            "show_mine" -> {
                                // Log.d("message","show mine")
                                func(arrayListOf("show_mine"))

                            }
                            "show_all" -> {
                                // Log.d("message"," show all ")
                                func(arrayListOf("show_all"))
                            }
                            "show_all_users" -> {
                                // Log.d("message","show all users")
                                func(arrayListOf("show_all_users"))
                            }
                            "show_all_tags" -> {
                                //   Log.d("message"," show all tags")
                                func(arrayListOf("show_all_tags"))
                            }
                            "notification" -> {
                                notifications = segment.getEntityByType("notifications")?.value
                                // notifications?.let { Log.d("message", it) }
                                func(arrayListOf("notification",notifications))
                            }
                            "login" -> {
                                account=segment.getEntityByType("account")?.value
                                email=segment.getEntityByType("email")?.value
                                // account?.let { Log.d("message", it) }
                                // email?.let { Log.d("message", it) }
                                func(arrayListOf("login",account,email))
                            }
                            "logout" -> {
                                // Log.d("message","log out")
                                func(arrayListOf("logout"))
                            }
                            "rating" -> {
                                rate=segment.getEntityByType("rate")?.value
                                func(arrayListOf("rating",rate))
                            }
                            "posting" -> {
                                type=segment.getEntityByType("type")?.value
                                func(arrayListOf("posting",type))
                            }
                            "setting" -> {
                                func(arrayListOf("setting"))
                            }
                            "reset" -> {
                                func(arrayListOf("reset"))
                            }
                        }
                    }
                }
            }
        }
    }

    fun func(str: ArrayList<String?>){
        this.list=str
    }

    fun getList(): ArrayList<String?>? {
        return list
    }
}