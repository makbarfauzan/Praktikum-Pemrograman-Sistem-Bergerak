package com.example.uasakbar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uasakbar.adapter.MahasiswaAdapter
import com.example.uasakbar.model.Mahasiswa
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var inputNim: EditText
    private lateinit var inputNama: EditText
    private lateinit var btnSimpan: Button
    private lateinit var rvMahasiswa: RecyclerView

    private lateinit var dbRef: DatabaseReference
    private var mhsList = mutableListOf<Mahasiswa>()
    private lateinit var adapter: MahasiswaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inisialisasi View
        inputNim = findViewById(R.id.inputNim)
        inputNama = findViewById(R.id.inputNama)
        btnSimpan = findViewById(R.id.btnSimpan)
        rvMahasiswa = findViewById(R.id.recyclerView)

        // 2. Inisialisasi Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("Mahasiswa")

        // 3. Setup RecyclerView
        adapter = MahasiswaAdapter(mhsList)
        rvMahasiswa.layoutManager = LinearLayoutManager(this)
        rvMahasiswa.adapter = adapter

        // 4. Tombol Simpan
        btnSimpan.setOnClickListener {
            val nim = inputNim.text.toString().trim()
            val nama = inputNama.text.toString().trim()

            if (nim.isNotEmpty() && nama.isNotEmpty()) {
                val mhs = Mahasiswa(nim, nama)
                dbRef.child(nim).setValue(mhs).addOnSuccessListener {
                    Toast.makeText(this, "Berhasil simpan!", Toast.LENGTH_SHORT).show()
                    inputNim.text.clear()
                    inputNama.text.clear()
                }
            } else {
                Toast.makeText(this, "Isi semua data!", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Ambil Data
        ambilData()
    }

    private fun ambilData() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mhsList.clear()
                for (data in snapshot.children) {
                    val mhs = data.getValue(Mahasiswa::class.java)
                    mhs?.let { mhsList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}