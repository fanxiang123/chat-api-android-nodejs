package com.spake.brony.openai.model

class SSEModel {
     val id: String = ""
//    private val `object`: String? = null
//    private val created: Long = 0
//    private val model: String? = null
     val choices: ArrayList<SSEChoices> = ArrayList()
}
class SSEChoices {
    var delta: Delta = Delta()
    var index = 0
    var finish_reason: String? = null
}


class Delta {
    var content: String = ""
    var role: String = ""
}