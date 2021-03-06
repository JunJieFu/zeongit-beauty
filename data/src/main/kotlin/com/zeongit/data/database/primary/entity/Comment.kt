package com.zeongit.data.database.primary.entity

import com.zeongit.share.entity.AskEntity
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Table

/**
 * 评论
 * @author fjj
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "comment")
class Comment() : AskEntity(), Serializable {
    //图片作者id
    @Column(name = "author_id", length = 10)
    var authorId: Int = 0
    //图片id
    @Column(name = "picture_id", length = 10)
    var pictureId: Int = 0
    //内容
    @Column(name = "content", length = 10)
    lateinit var content: String

    constructor(authorId: Int, pictureId: Int, content: String) : this() {
        this.authorId = authorId
        this.pictureId = pictureId
        this.content = content
    }
}