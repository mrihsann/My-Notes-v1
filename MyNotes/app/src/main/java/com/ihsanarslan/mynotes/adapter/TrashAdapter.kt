package com.ihsanarslan.mynotes.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ihsanarslan.mynotes.R
import kotlin.collections.ArrayList


class TrashAdapter(private val notes: ArrayList<Note>) : RecyclerView.Adapter<TrashAdapter.TrashViewHolder>() {
    private var filteredList: List<Note> = notes
    class TrashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //itemviewin içeriğinde ki bilgileri buluyoruz
        private val titleTextView: TextView = itemView.findViewById(R.id.itemtitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.itemcontent)
        private val noteCardd:CardView=itemView.findViewById(R.id.cardView)

        fun bind(note: Note) {
            if(note.title.length>40){
                titleTextView.text = note.title.substring(0,40)+"..."
            }
            else{
                titleTextView.text = note.title
            }
            if(note.content.length>80){
                descriptionTextView.text = note.content.substring(0,80)+"..."
            }
            else{
                descriptionTextView.text = note.content
            }
            noteCardd.setCardBackgroundColor(note.color)
            //buraya yazının tarihini vs ekleyceğiz
        }
    }

    //filtreleme fonksiyonunu yazıyoruz
    fun filter(query: String) {
        filteredList = notes.filter {
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
        }
        notifyDataSetChanged()
    }
    fun filterLiked() {
        filteredList = notes.filter {
            it.liked==true
        }
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trash_item, parent, false)
        return TrashViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrashViewHolder, position: Int) {
        val currentItem = filteredList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

}