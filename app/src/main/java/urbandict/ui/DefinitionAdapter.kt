package urbandict.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import nike.urbandict.R
import urbandict.api.Definition


class DefinitionsAdapter : RecyclerView.Adapter<DefinitionViewHolder>() {

    private val differ: AsyncListDiffer<Definition> =
        AsyncListDiffer(this, object : DiffUtil.ItemCallback<Definition>() {
            override fun areItemsTheSame(
                oldItem: Definition,
                newItem: Definition
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Definition,
                newItem: Definition
            ): Boolean {
                return oldItem.thumbsUp == newItem.thumbsUp && oldItem.thumbsDown == newItem.thumbsDown
                        && oldItem.definition == newItem.definition
            }
        })

    private var recyclerView: RecyclerView? = null

    init {
        differ.addListListener { _, currentList ->
            if (currentList.isNotEmpty()) {
                recyclerView?.let { view ->
                    view.post {
                        val linearSmoothScroller = object : LinearSmoothScroller(view.context) {
                            override fun getVerticalSnapPreference(): Int {
                                return SNAP_TO_START
                            }
                        }
                        linearSmoothScroller.targetPosition = 0
                        view.layoutManager?.startSmoothScroll(linearSmoothScroller)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Definition>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefinitionViewHolder {
        return DefinitionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_definition, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DefinitionViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.titleView.text = item.word
        holder.definitionView.text = item.definition
        holder.thumbsUpView.text = item.thumbsUp.toString()
        holder.thumbsDownView.text = item.thumbsDown.toString()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (this.recyclerView == recyclerView) {
            this.recyclerView = null
        }
    }
}