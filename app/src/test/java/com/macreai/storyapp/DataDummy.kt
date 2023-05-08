package com.macreai.storyapp

import com.macreai.storyapp.model.remote.response.*

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem>{
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100){
            val story = ListStoryItem(
                i.toString(),
                "created $i",
                "name $i",
                "description $i",
                i.toDouble(),
                "id $i",
                i.toDouble()
            )
            items.add(story)
        }

        return items
    }
}