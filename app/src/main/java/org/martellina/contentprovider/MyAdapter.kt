package org.martellina.contentprovider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(list: ArrayList<Contact>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private var list = ArrayList<Contact>(0)


    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private var name: TextView = view.findViewById(R.id.name)
        private var number: TextView = view.findViewById(R.id.number)
        private var id: TextView = view.findViewById(R.id.id)


        fun bind(contact: Contact) {
            name.text = contact.name
            number.text = contact.number
            id.text = contact.id.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: ArrayList<Contact>?) {
        this.list.clear()
        if (list != null) {
            this.list = list
        }
        notifyDataSetChanged()
    }
}