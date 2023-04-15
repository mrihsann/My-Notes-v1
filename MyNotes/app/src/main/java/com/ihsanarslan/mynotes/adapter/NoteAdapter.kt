package com.ihsanarslan.mynotes.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.ihsanarslan.mynotes.R
import com.ihsanarslan.mynotes.database.NoteDB
import com.ihsanarslan.mynotes.database.NoteDao
import com.ihsanarslan.mynotes.fragment.ListFragmentDirections
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

data class Note(
    var id: Int,
    var title: String,
    var content: String,
    var color:Int,
    var liked:Boolean
)
class NoteAdapter(private val notes: ArrayList<Note>, val noteDao: NoteDao) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //itemviewin içeriğinde ki bilgileri buluyoruz
        private val titleTextView: TextView = itemView.findViewById(R.id.itemtitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.itemcontent)
        private val noteCardd:CardView=itemView.findViewById(R.id.cardView)

        fun bind(note: Note,context: Context) {
            if(note.title.length>30){
                titleTextView.text = note.title.substring(0,30)+"..."
            }
            else{
                titleTextView.text = note.title
            }
            if(note.content.length>50){
                descriptionTextView.text = note.content.substring(0,50)+"..."
            }
            else{
                descriptionTextView.text = note.content
            }
            noteCardd.setCardBackgroundColor(note.color)
            val liketrue: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24)
            val likefalse: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_border_24)
            if (note.liked){
                noteCardd.findViewById<ImageView>(R.id.favButoon).setImageDrawable(liketrue)
            }
            else{
                noteCardd.findViewById<ImageView>(R.id.favButoon).setImageDrawable(likefalse)
            }
            //buraya yazının tarihini vs ekleyceğiz
        }
    }

    //filtrelediğimiz verileri geçici bir listede tutuyoruz.
    private var filteredList: List<Note> = notes
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val liketrue: Drawable? = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_baseline_favorite_24)
        val likefalse: Drawable? = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_baseline_favorite_border_24)
        val currentItem = filteredList[position]
        holder.bind(currentItem,holder.itemView.context)
        holder.itemView.setOnClickListener {
            val action= ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem.title,currentItem.content,currentItem.color,currentItem.id,currentItem.liked)
            Navigation.findNavController(it).navigate(action)
        }
        holder.itemView.findViewById<ImageView>(R.id.favButoon).setOnClickListener {
            if (currentItem.liked){
                //veritabanından 0 yap liked değerini
                GlobalScope.launch {
                    val title = currentItem.title
                    val content = currentItem.content
                    val color= currentItem.color
                    noteDao.update(NoteDB(currentItem.id,title,content,color,false))
                }
                //beğeni butonunda ki değişim anlık olarak değişmediği için fragmenti yeniliyoruz bizde
                val action=ListFragmentDirections.actionListFragmentSelf()
                Navigation.findNavController(it).navigate(action)
            }
            else{
                //veritabanından 0 yap liked değerini
                GlobalScope.launch {
                    val title = currentItem.title
                    val content = currentItem.content
                    val color= currentItem.color
                    noteDao.update(NoteDB(currentItem.id,title,content,color,true))
                }
                //beğeni butonunda ki değişim anlık olarak değişmediği için fragmenti yeniliyoruz bizde
                val action=ListFragmentDirections.actionListFragmentSelf()
                Navigation.findNavController(it).navigate(action)

            }
        }

    }

    override fun getItemCount(): Int {
        return filteredList.size
    }
}
