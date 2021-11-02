package ai.pams.android.kotlin.flex

enum class ElementType {
    label,
    image,
    vbox,
    hbox,
    root
}

class FlexElement(val type: ElementType) {

    var root: FlexElement? = null
    var props = mutableMapOf<String, String>()
    var parent: FlexElement? = null
    var child = mutableListOf<FlexElement>()
    var childNum = 0
    var hrefList = mutableListOf<String>()

    companion object {
        var _currentFlexWindow: FlexElement? = null
    }

    fun setProperty(prop: String, value: String) {
        props[prop] = value
    }

    fun addChild(ele: FlexElement) {
        ele.parent = this
        child.add(ele)
    }
}
