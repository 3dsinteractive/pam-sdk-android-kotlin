package ai.pams.android.kotlin.flex.parser

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

class PImage: PView() {

    override fun createView(parser: FlexParser): View? {
        if(parser.context == null) return null
        val view = ImageView(parser.context)
        props["src"]?.let{ urlStr ->
            Glide.with(parser.context).load(urlStr).into(view);
        }
        return view
    }
}