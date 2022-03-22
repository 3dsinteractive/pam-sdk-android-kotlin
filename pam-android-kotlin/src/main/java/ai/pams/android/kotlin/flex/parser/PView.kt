package ai.pams.android.kotlin.flex.parser

import android.view.View

open class PView {
    var props = mutableMapOf<String, String>()

    open fun describe(): String{
        var r = ""
        when (this) {
            is PImage -> {
                r += "Image\n"
            }
            is PContainer -> {
                r += "Container\n"
            }
            is PText -> {
                r += "TextView\n"
            }
        }

        for((k,v) in props ){
            r += "$k=$v\n"
        }
        return r
    }

    open fun createView(parser: FlexParser): View? {
        if(parser.context != null) return  null
        return View(parser.context)
    }

}