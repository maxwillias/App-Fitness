package com.example.appfitness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appfitness.model.Calc
import java.text.SimpleDateFormat
import java.util.*

class ListCalcAcitivity : AppCompatActivity() {

    private lateinit var rvListCalc: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_calc_acitivity)

        val result = mutableListOf<Calc>()
        val adapter = ListCalcAdapter(result)
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

    private inner class ListCalcAdapter(
        private val listCalcItems: List<Calc>
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
            }
        }
    }
}