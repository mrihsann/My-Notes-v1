package com.ihsanarslan.mynotes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ihsanarslan.mynotes.adapter.NoteAdapter
import com.ihsanarslan.mynotes.database.NoteDB
import com.ihsanarslan.mynotes.database.NoteDao
import com.ihsanarslan.mynotes.database.TrashDB
import com.ihsanarslan.mynotes.database.TrashDao
import com.ihsanarslan.mynotes.fragment.ListFragmentDirections
import com.ihsanarslan.mynotes.fragment.noteList
import com.ihsanarslan.mynotes.fragment.noteTrashList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SwipeToDeleteCallback(val view: View,private val adapter: NoteAdapter,private var noteDao: NoteDao, private var trashDao: TrashDao,private var context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val currentitem = noteList[viewHolder.adapterPosition]

        GlobalScope.launch {
            val newnote = NoteDB(
                currentitem.id,
                currentitem.title,
                currentitem.content,
                currentitem.color,
                currentitem.liked
            )
            val trashnote = TrashDB(
                currentitem.id,
                currentitem.title,
                currentitem.content,
                currentitem.color,
                currentitem.liked
            )
            noteDao.delete(newnote)
            trashDao.insert(trashnote)
        }
        noteTrashList.add(currentitem)
        noteList.remove(currentitem)
        adapter.notifyDataSetChanged()
        // Geri alma işlemi için kullanıcıya bir mesaj gösterme
        Snackbar.make(
            viewHolder.itemView,
            "Not Silindi",
            Snackbar.LENGTH_LONG
        ).setTextColor(Color.RED)
            .setActionTextColor(Color.WHITE).show()
        val action= ListFragmentDirections.actionListFragmentSelf()
        Navigation.findNavController(view).navigate(action)
    }
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        val icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)
        val background = GradientDrawable()
        background.shape = GradientDrawable.RECTANGLE
        background.cornerRadius = 30f
        background.setColor(Color.parseColor("#FF0000"))

        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        if (dX > 0) { // Swipe right
            val iconLeft = itemView.left + iconMargin
            val iconRight = itemView.left + iconMargin + icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
        } else { // Swipe left
            val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
            val iconRight = itemView.right - iconMargin
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        }

        background.draw(c)
        icon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}