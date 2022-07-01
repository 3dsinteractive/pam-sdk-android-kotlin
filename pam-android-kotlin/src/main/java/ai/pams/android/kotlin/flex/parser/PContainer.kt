package ai.pams.android.kotlin.flex.parser

import android.view.View

class PContainer: PView() {

    var childs = mutableListOf<PView>()

    fun appendChild(e: PView){
        childs.add(e)
    }

    override fun describe(): String{
        var r = super.describe()
        for (v in childs){
            r += v.describe()
        }
        return r
    }

    override fun createView(parser: FlexParser): View? {
//        if (parser.context == null) return null
//        val view = LinearLayout(parser.context)
//        view.distribution = .fill
//                view.axis = .vertical
//
//                view.frame = parser.containerSize ??
//        CGRect(x: 0, y: 0, width: 300, height: 300)
//        for v in childs {
//            let subView = v.createView(parser: parser)
//            print(subView)
//            view.addArrangedSubview(subView)
//        }
//        return view
        return View(parser.context);
    }
}