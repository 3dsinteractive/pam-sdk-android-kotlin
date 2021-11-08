package ai.pams.android.kotlin.flex

class FlexLangParser {
    val whiteSpacaeChar = "\n\r\t "

    enum class ParseState {
        startView,
        endView,
        startProp,
        propValue,
        endProp,
        startDocument,
        endDocument,
        attrName
    }

    fun minify(flex: String): String {
        var out = StringBuilder()
        var ignoreSpace = true
        for (char in flex) {
            if (ignoreSpace) {
                var cut = false
                for (white in whiteSpacaeChar) {
                    if (char == white) {
                        cut = true
                    }
                }
                if (!cut) {
                    out.append(char)
                }
                if (char == '"') {
                    ignoreSpace = !ignoreSpace
                }
            } else {
                out.append(char)
                if (char == '"') {
                    ignoreSpace = !ignoreSpace
                }
            }
        }
        return out.toString()
    }

    private fun addTab(count: Int): String {
        var tab = StringBuilder()
        for (i in 1..count) {

            tab.append("\t")
        }
        return tab.toString()
    }

    fun beautify(flex: String): String {
        var out = StringBuilder()
        var tabCount = 0

        var buffer = StringBuilder()

        var state = ParseState.startDocument

        for (char in flex) {
            if (char == '(') {
                state = ParseState.startView
                out.append("${addTab(tabCount)}$buffer$char\n")
                tabCount += 1
                buffer.clear()
            } else if (char == ')') {
                state = ParseState.endView
                tabCount -= 1
                out.append("${addTab(tabCount)}$char\n")
            } else if (char == '"') {
                if (state == ParseState.startProp) {
                    state = ParseState.propValue
                    buffer.append("$char")
                } else if (state == ParseState.propValue) {
                    state = ParseState.endProp
                    out.append("${addTab(tabCount)}$buffer$char\n")
                    buffer.clear()
                }
            } else if (char == '=') {
                state = ParseState.startProp
                buffer.append(char)
            } else {
                if (state == ParseState.propValue) {
                    buffer.append(char)
                } else {
                    state = ParseState.attrName
                    buffer.append(char)
                }
            }
        }

        //state = ParseState.endDocument

        return out.toString()
    }

    fun parse(flexString: String): FlexElement? {
        val flex = minify(flexString)

        var buffer = StringBuilder()
        var state = ParseState.startDocument

        var renderStack = mutableListOf<FlexElement>()

        var currentPropKey = ""
        var root: FlexElement? = null

        for (char in flex) {
            if (char == '(') {
                state = ParseState.startView
                if (buffer.toString().lowercase() == "root") {
                    if (renderStack.size == 0) {
                        root = FlexElement(ElementType.root)
                        root.let {
                            renderStack.add(root)
                        }
                    } else {
                        return null
                    }
                } else if (buffer.toString().lowercase() == "vbox") {
                    val ele = FlexElement(ElementType.vbox)
                    ele.root = root
                    renderStack.last().addChild(ele)
                    renderStack.add(ele)
                } else if (buffer.toString().lowercase() == "label") {
                    val ele = FlexElement(ElementType.label)
                    ele.root = root
                    renderStack.last().addChild(ele)
                    renderStack.add(ele)
                } else if (buffer.toString().lowercase() == "hbox") {
                    val ele = FlexElement(ElementType.hbox)
                    ele.root = root
                    renderStack.last().addChild(ele)
                    renderStack.add(ele)
                } else if (buffer.toString().lowercase() == "image") {
                    val ele = FlexElement(ElementType.image)
                    ele.root = root
                    renderStack.last().addChild(ele)
                    renderStack.add(ele)
                }
                buffer.clear()
            } else if (char == ')') {
                state = ParseState.endView
                renderStack.removeAt(renderStack.lastIndex)
            } else if (char == '"') {
                if (state == ParseState.startProp) {
                    state = ParseState.propValue
                    buffer.clear()
                } else if (state == ParseState.propValue) {
                    state = ParseState.endProp
                    if (currentPropKey != "") {
                        renderStack.last().setProperty(currentPropKey, buffer.toString())
                        currentPropKey = ""
                    }
                    buffer.clear()
                }
            } else if (char == '=') {
                state = ParseState.startProp
                currentPropKey = buffer.toString()
                buffer.clear()
            } else {
                if (state == ParseState.propValue) {
                    buffer.append(char)
                } else {
                    state = ParseState.attrName
                    buffer.append(char)
                }
            }
        }

        //state = ParseState.endDocument

        return root
    }
}