package com.example.uasakbar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uasakbar.R
import com.example.uasakbar.model.Mahasiswa

class MahasiswaAdapter(private val listMhs: List<Mahasiswa>) :
    RecyclerView.Adapter<MahasiswaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNim: TextView = view.findViewById(R.id.txtNim)
        val tvNama: TextView = view.findViewById(R.id.txtNama)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mahasiswa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mhs = listMhs[position]
        holder.tvNim.text = mhs.nim
        holder.tvNama.text = mhs.nama
    }

    override fun getItemCount(): Int = listMhs.size
}