package com.example.tryserialization

import kotlinx.serialization.Serializable

@Serializable
data class Sample(val str: String = "default")