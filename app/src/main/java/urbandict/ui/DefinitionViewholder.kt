package urbandict.ui

import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nike.urbandict.R

class DefinitionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleView: TextView = itemView.findViewById(R.id.titleView)
    val definitionView: TextView = itemView.findViewById(R.id.definitionView)
    val thumbsUpView: TextView = itemView.findViewById(R.id.thumbsUpView)
    val thumbsDownView: TextView = itemView.findViewById(R.id.thumbsDownView)

    init {
        definitionView.movementMethod = LinkMovementMethod.getInstance()
    }
}