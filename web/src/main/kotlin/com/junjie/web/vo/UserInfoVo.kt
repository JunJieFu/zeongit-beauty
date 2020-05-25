package com.junjie.web.vo

import com.junjie.data.constant.FollowState
import com.junjie.share.constant.Gender
import com.junjie.share.database.account.entity.UserInfo
import org.springframework.beans.BeanUtils
import java.util.*

class UserInfoVo(info: UserInfo) {
    var id: Int = 0

    var gender: Gender = Gender.MALE

    var birthday: Date = Date()

    lateinit var nickname: String

    lateinit var introduction: String

    var avatarUrl: String? = null

    var background: String? = null

    var focus: FollowState = FollowState.STRANGE

    var country: String = "中国"

    var province: String? = null

    var city: String? = null

    init {
        BeanUtils.copyProperties(info, this)
    }
}