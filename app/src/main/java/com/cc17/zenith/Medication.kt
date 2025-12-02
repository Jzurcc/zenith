package com.cc17.zenith

data class Medication(
    val name: String,
    val instruction: String,
    val category: String, // e.g., "Daily"
    val type: String = "Oral" // Default type
)