package com.zeongit.web.service

import com.zeongit.data.database.primary.entity.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


/**
 * 评论的服务
 *
 * @author fjj
 */
interface CommentService {
    fun save(comment: Comment): Comment

    fun count(pictureId: Int): Long

    fun list(pictureId: Int): List<Comment>

    fun listTop4(pictureId: Int): List<Comment>

    fun paging(pageable: Pageable, pictureId: Int): Page<Comment>
}