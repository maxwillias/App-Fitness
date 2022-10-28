package com.example.appfitness

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appfitness.model.Calc
import java.text.SimpleDateFormat
import java.util.*

class ListCalcAcitivity : AppCompatActivity(), OnListClickListener {

    private lateinit var rvListCalc: RecyclerView
    private lateinit var adapter: ListCalcAdapter
    private lateinit var result: MutableList<Calc>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_calc_acitivity)

        result = mutableListOf<Calc>()

        adapter = ListCalcAdapter(result,this)

        rvListCalc = findViewById(R.id.rvListCalc)
        rvListCalc.layoutManager = LinearLayoutManager(this)
        rvListCalc.adapter = adapter

        val type = intent?.extras?.getString("type") ?: throw IllegalStateException("type not found")

        Thread{
            val app = application as App
            val dao = app.db.calcDao()
            val response = dao.getRegisterByType(type)

            runOnUiThread{
                result.addAll(response)
                adapter.notifyDataSetChanged()
            }
        }.start()
    }

    override fun onClick(id: Int, type: String) {
        when(type) {
            "imc" -> {
                val intent = Intent(this, ImcActivity::class.java)
                intent.putExtra("updateId", id)
                startActivity(intent)
            }
            "tmb" -> {
                val intent = Intent(this, TmbActivity::class.java)
                intent.putExtra("updateId", id)
                startActivity(intent)
            }
        }
        finish()
    }

    override fun onLongClick(position: Int, calc: Calc) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_message))
            .setNegativeButton(android.R.string.cancel){ p1, p2 ->
            }
            .setPositiveButton(android.R.string.ok){ p1, p2 ->
                Thread{
                    val app = application as App
                    val dao = app.db.calcDao()

                    val response = dao.delete(calc)

                    if(response > 0){
                        runOnUiThread{
                            result.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                    }
                }.start()
            }
            .create()
            .show()
    }

    private inner class ListCalcAdapter(
        private val listCalcItems: List<Calc>,
        private val listener: OnListClickListener
        ) : RecyclerView.Adapter<ListCalcAdapter.ListCalcViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCalcViewHolder {
            val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            return ListCalcViewHolder(view)
        }

        override fun onBindViewHolder(holder: ListCalcViewHolder, position: Int) {
            val itemCurrent = listCalcItems[position]
            holder.bind(itemCurrent)
        }

        override fun getItemCount(): Int {
            return listCalcItems.size
        }

        private inner class ListCalcViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            fun bind(item : Calc){
                val name = itemView as TextView

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
                val data = sdf.format(item.createdDate)
                val res = item.res

                name.text = getString(R.string.list_response, res, data)

                name.setOnLongClickListener {
                    listener.onLongClick(adapterPosition, item)
                    true
                }

                name.setOnClickListener {
                    listener.onClick(item.id, item.type)
                }
            }
        }
    }
}