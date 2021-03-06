package com.zeongit.data.index.primary.document

import com.zeongit.data.constant.AspectRatio
import com.zeongit.data.constant.PrivacyState
import com.zeongit.data.database.primary.entity.Picture
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.io.Serializable
import java.util.*

@Document(indexName = "beauty_picture_search")
class PictureDocument() : Serializable {
    @Id
    @Field(type = FieldType.Keyword)
    var id: Int = 0

    @Field(type = FieldType.Keyword, index = false)
    lateinit var url: String

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    var name: String = "无题"

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    var introduction: String = "身无彩凤双飞翼，心有灵犀一点通"

    @Field(type = FieldType.Keyword)
    var privacy: PrivacyState = PrivacyState.PUBLIC

    @Field(type = FieldType.Keyword)
    var viewAmount: Long = 0

    @Field(type = FieldType.Keyword)
    var likeAmount: Long = 0

    @Field(type = FieldType.Keyword, index = false)
    var width: Long = 0

    @Field(type = FieldType.Keyword, index = false)
    var height: Long = 0

    @Field(type = FieldType.Keyword)
    lateinit var aspectRatio: AspectRatio

    @Field(type = FieldType.Keyword)
    var tagList: MutableList<String> = mutableListOf()

    @Field(type = FieldType.Keyword)
    var createdBy: Int = 0

    @Field(type = FieldType.Keyword)
    var createDate: Date = Date()

    @Field(type = FieldType.Keyword)
    var lastModifiedDate: Date = Date()

    constructor(picture: Picture) : this() {
        this.id = picture.id!!
        this.introduction = picture.introduction
        this.url = picture.url
        this.createdBy = picture.createdBy!!
        this.name = picture.name
        this.privacy = picture.privacy
        this.width = picture.width
        this.height = picture.height
        this.aspectRatio = picture.aspectRatio
        this.createDate = picture.createDate!!
        this.lastModifiedDate = picture.lastModifiedDate!!
        this.tagList = picture.tagList.asSequence().map { it.name }.toMutableList()
    }
}