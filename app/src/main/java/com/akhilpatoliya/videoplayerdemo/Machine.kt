package com.akhilpatoliya.videoplayerdemo

import android.graphics.BitmapFactory

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.StoredCredential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.DataStore
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import org.jetbrains.anko.doAsync

import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.util.ArrayList
import java.util.Random


internal object Machine {


    var YOUTUBE_API_KEYS: ArrayList<String> = ArrayList()
    var apiKey: String? = null

    private var youtube: YouTube? = null
    private var search: YouTube.Search.List? = null
    lateinit var newUrl: URL
    var mainActivity = MainActivity()

    @JvmStatic
    fun something() {
        YOUTUBE_API_KEYS.add("A-dlBUjVQeuc4a6ZN4RkNUYDFddrVLxrA")
        YOUTUBE_API_KEYS.add("CXRRCFwKAXOiF1JkUBmibzxJF1cPuKNwA")
        YOUTUBE_API_KEYS.add("AgcQ6VzgBPjTY49pxeqHsIIDQgQ09Q4bQ")
        YOUTUBE_API_KEYS.add("AQt1mEVq6zwVBjwx_lcJkQoAAxGExgN7A")
        YOUTUBE_API_KEYS.add("AGosg8Ncdqw8IrwV4iT9E1xCIAVvg4CBw")
        YOUTUBE_API_KEYS.add("CbehD6DCeDZHaGl8SUWKh1koTiHwKcvKY")
        YOUTUBE_API_KEYS.add("B0k1OUQjkU3h-4xay3xno9cnwGkSeTjT4")
        YOUTUBE_API_KEYS.add("AGLRCjbt5cUqz2m3DGECsSit2SjOLlRro")
        YOUTUBE_API_KEYS.add("BtR6GsaU4fUKAI4tZuLJfljOD8fIiF0S8")
        YOUTUBE_API_KEYS.add("DU-ZZCyOB_kYkiMSZ6ooSipUZRukHU4ik")


        doAsync {
            for (i in 0..9) {
                var r = Random()
                apiKey = "AIzaSy" + YOUTUBE_API_KEYS[r.nextInt(YOUTUBE_API_KEYS.size)]
                var query: String? = MainActivity.getmInstanceActivity()!!.tag.first
                var category :String? = MainActivity.getmInstanceActivity()!!.tag.second
                var ret: String? = null
                val duration = "any"
                val type = "startVideo"
                val dateRange = mainActivity.getDateRange()
                when (type) {
                    "randomWord" -> {
                        //------------------------------randomWord------------------------------------------
                        if (MainActivity.getmInstanceActivity()!!.tag.first == "Наука") {
                            val doc: Document
                            var els: Elements? = null
                            try {
                                doc = Jsoup.connect("https://kartaslov.ru/ассоциации-к-слову/"  + "/").get()
                                els = doc.getElementsByClass("wordLink")
                            } catch (e: IOException) {
                            }

                            val list = els!!.eachText()

                            if (els == null || els.eachText().size == 0 || !els.hasText()) {
                                list.clear()
                              //  list.add(strings[1])
                            }
                            r = Random()

                            ret = list[r.nextInt(list.size)]
                            query = ret
                        }
                        //-----------------------------startVideo-------------------------------------------

                        try {
                            youtube = YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY,
                                    HttpRequestInitializer { }
                            ).setApplicationName("randVideo").build()
                            search = youtube!!.search().list("id,snippet")

                            r = Random()
                            search!!.q = query
                            search!!.key = apiKey
                            search!!.type = "video"
                            search!!.maxResults = 50.toLong()
                            search!!.videoDuration = duration

                            val searchResponse = search!!.execute()
                            val count = searchResponse.pageInfo.totalResults!!


                            var pageToken: String? = null
                         //   println(count)
                     //       println("FIRST: " + query!!)
                            if (count == 0) {
                                query = query!!.substring(1)
                            }
                    //        println("LAST: " + query!!)
                            if (!(MainActivity.last_taag_value == "Без тега" || MainActivity.last_taag_value == "Рандомный тег")) {
                                if (count <= 50) {
                                } else if (count > 500) {
                                    val random_number = (Math.random() * 10).toInt()
                                    when (random_number) {
                                        1 -> {
                                        }
                                        2 -> pageToken = "CDIQAA"
                                        3 -> pageToken = "CGQQAA"
                                        4 -> pageToken = "CJYBEAA"
                                        5 -> pageToken = "CMgBEAA"
                                        6 -> pageToken = "CPoBEAA"
                                        7 -> pageToken = "CKwCEAA"
                                        8 -> pageToken = "CN4CEAA"
                                        9 -> pageToken = "CJADEAA"
                                        10 -> pageToken = "CMIDEAA"
                                    }
                      //              println(">500")
                                } else {
                                    val random_number = (Math.random() * count / 50).toInt()
                                    when (random_number) {
                                        1 -> {
                                        }
                                        2 -> pageToken = "CDIQAA"
                                        3 -> pageToken = "CGQQAA"
                                        4 -> pageToken = "CJYBEAA"
                                        5 -> pageToken = "CMgBEAA"
                                        6 -> pageToken = "CPoBEAA"
                                        7 -> pageToken = "CKwCEAA"
                                        8 -> pageToken = "CN4CEAA"
                                        9 -> pageToken = "CJADEAA"
                                        10 -> pageToken = "CMIDEAA"
                                    }
                //                    println("50 < x < 500")
                                }
                            }
                            youtube = YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY,
                                    HttpRequestInitializer { }
                            ).setApplicationName("randVideo").build()
                            search = youtube!!.search().list("id,snippet")

                            r = Random()
                            search!!.q = query
                            search!!.key = apiKey
                            search!!.type = "video"
                            search!!.maxResults = 50.toLong()
                            search!!.videoDuration = duration
                            search!!.pageToken = pageToken
                            val searchResponse2 = search!!.execute()
                            val searchResultList = searchResponse2.items

                            ret = searchResultList[r.nextInt(searchResultList.size)].toString()

                        } catch (e: GoogleJsonResponseException) {
                            e.printStackTrace()
                            ret = "There was a service error: " + e.details.code + " : " + e.details.message
                        } catch (e: IOException) {
                            e.printStackTrace()
                            ret = "There was an IO error: " + e.cause + " : " + e.message
                        } catch (e: Exception) {
                            ret = e.message
                        }

                    }
                    "startVideo" -> try {
                        youtube = YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, HttpRequestInitializer { }).setApplicationName("randVideo").build()
                        search = youtube!!.search().list("id,snippet")
                        r = Random()
                        search!!.q = query
                        search!!.key = apiKey
                        search!!.regionCode = "RU"
                        search!!.relevanceLanguage = "ru_RU"
                        search!!.publishedAfter = dateRange
                        search!!.videoCategoryId = category
                        search!!.type = "video"
                        search!!.maxResults = 50.toLong()
                        search!!.videoDuration = duration
                        val searchResponse = search!!.execute()
                        val count = searchResponse.pageInfo.totalResults!!
                        var pageToken: String? = null
                   //     println(count)
                   //     println("FIRST: " + query!!)
                        if (count == 0) {
                            query = query!!.substring(1)
                        }
                  //      println("LAST: " + query!!)
                        if (!(MainActivity.last_taag_value == "Без тега" || MainActivity.last_taag_value == "Рандомный тег")) {
                            if (count <= 50) {
                            } else if (count > 500) {
                                val random_number = (Math.random() * 10).toInt()
                                when (random_number) {
                                    1 -> {
                                    }
                                    2 -> pageToken = "CDIQAA"
                                    3 -> pageToken = "CGQQAA"
                                    4 -> pageToken = "CJYBEAA"
                                    5 -> pageToken = "CMgBEAA"
                                    6 -> pageToken = "CPoBEAA"
                                    7 -> pageToken = "CKwCEAA"
                                    8 -> pageToken = "CN4CEAA"
                                    9 -> pageToken = "CJADEAA"
                                    10 -> pageToken = "CMIDEAA"
                                }
                  //              println(">500")
                            } else {
                                val random_number = (Math.random() * count / 50).toInt()
                                when (random_number) {
                                    1 -> {
                                    }
                                    2 -> pageToken = "CDIQAA"
                                    3 -> pageToken = "CGQQAA"
                                    4 -> pageToken = "CJYBEAA"
                                    5 -> pageToken = "CMgBEAA"
                                    6 -> pageToken = "CPoBEAA"
                                    7 -> pageToken = "CKwCEAA"
                                    8 -> pageToken = "CN4CEAA"
                                    9 -> pageToken = "CJADEAA"
                                    10 -> pageToken = "CMIDEAA"
                                }
                    //            println("50 < x < 500")
                            }
                        }
                        youtube = YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, HttpRequestInitializer { }).setApplicationName("randVideo").build()
                        search = youtube!!.search().list("id,snippet")
                        r = Random()
                        search!!.q = query
                        search!!.key = apiKey
                        search!!.regionCode = "RU"
                        search!!.relevanceLanguage = "ru_RU"
                        search!!.publishedAfter = dateRange
                        search!!.videoCategoryId = category
                        search!!.type = "video"
                        search!!.maxResults = 50.toLong()
                        search!!.videoDuration = duration
                        search!!.pageToken = pageToken
                        val searchResponse2 = search!!.execute()
                        println(searchResponse2)
                        val searchResultList = searchResponse2.items
                        ret = searchResultList[r.nextInt(searchResultList.size)].toString()
                    } catch (e: GoogleJsonResponseException) {
                        e.printStackTrace()
                        ret = "There was a service error: " + e.details.code + " : " + e.details.message
                    } catch (e: IOException) {
                        e.printStackTrace()
                        ret = "There was an IO error: " + e.cause + " : " + e.message
                    } catch (e: Exception) {
                        ret = e.message
                    }

                }
                var jsonObj: JSONObject? = null
                try {
                    jsonObj = JSONObject(ret)
                    newUrl = URL(jsonObj.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url"))
                    MainActivity.cartListvideoId = jsonObj.getJSONObject("id").getString("videoId")
                    MainActivity.cartListVideoTitle = jsonObj.getJSONObject("snippet").getString("title")
                    mainActivity.runOnUiThread {
                        if (MainActivity.cartList.size == 0) {
                            mainActivity.onFinishTask()
                        } else {
                            if (MainActivity.cartList.get(MainActivity.cartList.size - 1).videoId != MainActivity.cartListvideoId) {
                                mainActivity.onFinishTask()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }


    internal object Auth {
        val HTTP_TRANSPORT: HttpTransport = NetHttpTransport()
        val JSON_FACTORY: JsonFactory = JacksonFactory()
        private val CREDENTIALS_DIRECTORY = ".oauth-credentials"

        @Throws(IOException::class)
        fun authorize(scopes: List<String>, credentialDatastore: String): Credential {
            val clientSecretReader = InputStreamReader(Auth::class.java!!.getResourceAsStream("/client_secrets.json"))
            val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader)
            if (clientSecrets.details.clientId.startsWith("Enter") || clientSecrets.details.clientSecret.startsWith("Enter ")) {
                System.exit(1)
            }
            val fileDataStoreFactory = FileDataStoreFactory(File(System.getProperty("user.home") + "/" + CREDENTIALS_DIRECTORY))
            val datastore = fileDataStoreFactory.getDataStore<StoredCredential>(credentialDatastore)
            val flow = GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore)
                    .build()
            val localReceiver = LocalServerReceiver.Builder().setPort(8080).build()
            return AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user")
        }
    }
}

