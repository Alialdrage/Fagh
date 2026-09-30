package com.example.model

/**
 * Data structures for "كتاب منهاج الصالحين"
 * لسماحة آية الله العظمى السيد علي الحسيني السيستاني (دام ظله)
 */
data class MinhajMasala(
    val id: String,
    val masalaNumber: Int,
    val title: String,
    val text: String,
    val bookVolume: String,
    val chapter: String,
    val subTopic: String,
    val tags: List<String> = emptyList()
)

data class MinhajChapter(
    val id: String,
    val volumeName: String,
    val chapterTitle: String,
    val description: String,
    val masalaRange: String,
    val masalas: List<MinhajMasala> = emptyList()
)
