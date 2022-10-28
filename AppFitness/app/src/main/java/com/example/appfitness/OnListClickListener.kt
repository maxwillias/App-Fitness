package com.example.appfitness

import com.example.appfitness.model.Calc

interface OnListClickListener {
    fun onClick(id: Int, type: String)
    fun onLongClick(position: Int, calc: Calc)
}