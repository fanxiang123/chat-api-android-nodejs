package com.spake.brony.openai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spake.brony.openai.databinding.ActivityMainBinding
import com.spake.brony.openai.model.Message
import com.spake.brony.openai.model.SSEModel
import com.spake.brony.openai.utils.EditUtils
import com.spake.brony.openai.view_model.ChatViewModel
import com.spake.brony.openai.web_socket.WebSocketClient
import io.noties.markwon.Markwon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by lazy {
        ChatViewModel()
    }

    //所有的TextView
    private val hashMap = HashMap<String, TextView>()


    val url = "http://101.35.55.42:8080"
    val client = WebSocketClient(url)
    val listener = MyWebSocketListener()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.run {
            xmlViewModel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
        }
        setContentView(binding.root)

        // 设置 RecyclerView
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
//        binding.recyclerView.layoutManager = layoutManager
//        val adapter = ChatAdapter(this, viewModel.messages.value!!)
//        binding.recyclerView.adapter = adapter

        // 设置发送按钮点击事件
        binding.buttonSendMessage.setOnClickListener {
            val message = binding.editTextMessage.text.toString()
            if (message.isNotBlank()) {
                binding.editTextMessage.text.clear()
                val text = EditUtils.userText(this@MainActivity)
                text.text = message
                binding.layout.addView(text)
                client.connect(listener, callback = { webSocket ->
                    viewModel.viewModelScope.launch {
                        viewModel.userText.add(message)
                        val gson = Gson()
                        val jsonArray = gson.toJsonTree(viewModel.userText).asJsonArray
                        val jsonString = jsonArray.toString()
                        viewModel.titleName.value = "正在思考中..."
                        webSocket.send(jsonString)
                    }
                })
            }
        }



        viewModel.titleName.observe(this) {
            if (it == "Openai") {
                binding.progressBar.visibility = View.GONE
                binding.buttonSendMessage.visibility = View.VISIBLE
                binding.editTextMessage.isEnabled = true
            } else {
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSendMessage.visibility = View.GONE
                binding.editTextMessage.isEnabled = false
            }
        }
        viewModel.viewModelScope.launch {
            delay(500)
            binding.run {
                val text = EditUtils.openAiText(this@MainActivity)
                text.text = "欢迎使用Open AI"
                layout.addView(text)
            }
        }
    }


    inner class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            // 连接成功后的操作
            Log.e("MyWebSocketListener", "连接成功后的操作")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            // 接收到文本消息的操作
            viewModel.viewModelScope.launch {

                try {
                    /**
                     * 使用打字机模式一个字一个字的输出文本对android不太友好，所有需要很大的改动：
                     * input.split("data:") 以data:分开字符串，因为第一段的返回字符串为data:{...} data:{...}
                     * 如果看不动可以吧渲染全部注销，看返回代码就理解了
                     */

                    val input = text
                    val parts = input.split("data:")
                    val result = parts.map { it -> "data :$it" }

                    result.forEach {
                        var newStr = it.substring(6)
                        Log.e("MyWebSocketListener", "接收到文本消息的操作:${newStr}")
                        if (newStr.isNotEmpty() && newStr + "" != "[DONE]") {
                            val gson: SSEModel =
                                Gson().fromJson(newStr, object : TypeToken<SSEModel>() {}.type)
                            if (gson.choices[0].delta.content.isNotEmpty()) {
                                binding.run {
                                    binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                                    if (hashMap[gson.id] == null) {
                                        val text = EditUtils.openAiText(this@MainActivity)
                                        text.text = gson.choices[0].delta.content
                                        hashMap[gson.id] = text

                                        if (viewModel.openAiText[gson.id] == null)
                                            viewModel.openAiText[gson.id] = gson.choices[0].delta.content

                                        layout.addView(hashMap[gson.id])
                                    } else {
                                        viewModel.openAiText.forEach { s, message ->
                                            if (gson.id == s) {
                                                viewModel.openAiText[gson.id] =
                                                    message + gson.choices[0].delta.content
                                            }
                                        }
                                        if (viewModel.openAiText[gson.id] == null)
                                            viewModel.openAiText[gson.id] =
                                                gson.choices[0].delta.content

                                        var str = viewModel.openAiText[gson.id]!!
//                                            hashMap[gson.id]!!.text.toString() + gson.choices[0].delta.content
                                        // 创建 Markwon 对象
                                        val markwon = Markwon.create(this@MainActivity)
                                        // 渲染 Markdown
                                        str = str.replace("\\n", "\n")
                                        markwon.setMarkdown(hashMap[gson.id]!!, str)

                                    }
                                }

                            }
                        }
                    }
                    viewModel.titleName.value = "Openai"
                } catch (e: Exception) {
                    Log.e("Exception", e.message!!)
                }


            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            // 接收到二进制消息的操作
            Log.e("MyWebSocketListener", "接收到二进制消息的操作")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            // 连接关闭前的操作
            Log.e("MyWebSocketListener", "连接关闭前的操作")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            // 连接关闭后的操作
            Log.e("MyWebSocketListener", "连接关闭后的操作")
            viewModel.viewModelScope.launch {
                viewModel.titleName.value = "连接失败了，请重新打开app尝试"
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            // 连接失败后的操作
            Log.e("MyWebSocketListener", "连接失败后的操作${t.message}")
            viewModel.viewModelScope.launch {
                viewModel.titleName.value = "连接失败了，请重新打开app尝试"
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        client.close()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}