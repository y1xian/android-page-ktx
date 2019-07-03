package com.yyxnb.arch

import java.io.Serializable

class TestData : Serializable {


    /**
     * code : 200
     * message : 成功!
     * result : [{"title":"李白","content":"风骨神仙籍里人，诗狂酒圣且平生。|开元一遇成何事，留得千秋万古名。","authors":"徐钧"}]
     */


    /**
     * title : 李白
     * content : 风骨神仙籍里人，诗狂酒圣且平生。|开元一遇成何事，留得千秋万古名。
     * authors : 徐钧
     */

    var title: String? = null
    var content: String? = null
    var authors: String? = null
    var testDouble: Double = 0.toDouble()
    var testDouble2: Double = 0.toDouble()
    var testDouble3: Double = 0.toDouble()
    var testInt: Int = 0
    var testInt2: Int = 0
    var testInt3: Int = 0
    var testString: String? = null
    var testString2: String? = null
    var testString3: String? = null

    override fun toString(): String {
        return "TestData(title=$title, content=$content, authors=$authors, testDouble=$testDouble, testDouble2=$testDouble2, testDouble3=$testDouble3, testInt=$testInt, testInt2=$testInt2, testInt3=$testInt3, testString=$testString, testString2=$testString2, testString3=$testString3)"
    }


}
