package com.cc17.zenith

import java.io.Serializable
data class Disease(
    val id: String,
    val name: String,
    val category: String,
    val activeCases: Int,
    val date: String,

    // Fields for Info Page
    val newCases: Int,
    val totalCases: Int,
    val fatalityRate: String,
    val description: String,
    val symptoms: String,
    val medication: String,

    // Graph Data
    val trendData: List<Int>,
    val severityData: List<Int>
) : Serializable