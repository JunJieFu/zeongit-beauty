package com.zeongit.web.service

import com.zeongit.data.database.primary.entity.ReplyMessage

interface ReplyMessageService {
    fun save(replyMessage: ReplyMessage): ReplyMessage

    fun list(criticId: Int): List<ReplyMessage>

    fun countUnread(criticId: Int): Long

    fun listUnread(criticId: Int): List<ReplyMessage>

    fun deleteByMonthAgo()
}