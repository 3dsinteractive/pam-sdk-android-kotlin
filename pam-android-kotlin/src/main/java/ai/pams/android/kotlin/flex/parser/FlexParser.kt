package ai.pams.android.kotlin.flex.parser

import android.content.Context
import android.view.View

enum class TextSize{
    body ,title
}

public class FlexParser(val context: Context?) {

    var textSize = mutableMapOf (
        "body" to 14,
        "title" to 18
    )

    var containerWidth: Int? = null
    var containerHeight: Int? = null

    fun setTextSize(sizeName: TextSize, size: Int){
        textSize[sizeName.name] = size
    }

    fun getTextSize(sizeName: TextSize): Int {
        return textSize[sizeName.name] ?: 6
    }

    public fun parse(flexString: String): PContainer? {
        val flex = removingWhitespaces(flexString)
        var buffer = ""
        var root: PContainer? = null
        var currentElement: PView? = null

        var propName = ""
        var startProp = false
        for (i in flex.indices) {
            val c = flex[i]

            if (c == '(') {
                val e = createElement(buffer)
                if (buffer == "root") {
                    root = e as? PContainer
                }else{
                    root?.appendChild(e)
                }
                currentElement = e
                buffer = ""
            }else if( c == '=' ){
                propName = buffer
                buffer = ""
            }else if (c == '"') {
                if (!startProp) {
                    startProp = true
                }else{
                    startProp = false
                    var tmpSize = buffer
                    if (currentElement != null && currentElement is PText && propName == "size") {
                        val k = TextSize.values().firstOrNull {it.name == buffer} ?: TextSize.body
                        val size = getTextSize(k)
                        tmpSize = size.toString()
                    }
                    currentElement?.props?.set(propName, tmpSize)
                    buffer = ""
                }
            }else if (c == ')') {
                currentElement = null
                buffer = ""
            }else{
                buffer += c
            }
        }
        return root
    }

    fun createElement(type: String): PView {
        if (type == "root") {
            return PContainer()
        }else if (type == "image") {
            return PImage()
        }else if (type == "text") {
            return PText()
        }
        return PView()
    }

    fun createView(pView: PContainer?, width: Int? = null, height: Int? = null): View? {
        containerWidth = width
        containerHeight = height
        val root = pView?.createView(this)
        return root
    }

    private fun removingWhitespaces(str:String): String {
        return str.filter { s ->
             s != ' ' && s != '\n'
        }
    }
}

