package com.spake.brony.openai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spake.brony.openai.model.Message


class ChatViewModel : ViewModel() {

    //用户的文本
    var userText = ArrayList<String>()
    //openai的文本
    var openAiText  = HashMap<String,String>()

    val titleName = MutableLiveData<String>().apply {
        value = "Openai"
    }

}
