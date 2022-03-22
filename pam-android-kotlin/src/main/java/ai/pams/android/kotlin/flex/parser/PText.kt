package ai.pams.android.kotlin.flex.parser

import android.view.View
import android.widget.TextView

class PText: PView() {
    override fun createView(parser: FlexParser): View? {
        if(parser.context == null) return  null
        val view = TextView(parser.context)
//        view.numberOfLines = 3
        view.text = props["text"] ?: ""
//        let size = props["size"]?.CGFloatValue() ?? 0
//        view.font = UIFont.systemFont(ofSize: size)
//        view.sizeToFit()
//
//        view.widthAnchor.constraint(equalToConstant: view.frame.size.width).isActive = true
//        view.heightAnchor.constraint(equalToConstant: view.frame.size.height).isActive = true

        return view
    }
}