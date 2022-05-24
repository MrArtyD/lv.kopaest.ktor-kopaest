package lv.kopaest.data.recipe

import kotlinx.serialization.SerialName
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class Recipe(
    @SerialName("remote_id")
    val remoteId: Int,
    val title: String,
    @SerialName("image_url")
    val imageUrl: String,
    val users: Set<String>,
    @BsonId val id: String = ObjectId().toString()
)
