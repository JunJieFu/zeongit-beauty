package com.zeongit.data.database.primary.dao

import com.zeongit.data.database.primary.entity.Collection
import com.zeongit.data.database.primary.entity.Follow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface FollowDao : JpaRepository<Follow, Int>, JpaSpecificationExecutor<Follow> {
    fun existsByCreatedByAndFollowingId(createdBy: Int, followingId: Int): Boolean
    @Transactional
    fun deleteByCreatedByAndFollowingId(createdBy: Int, followingId: Int)

    fun findAllByCreatedBy(createdBy: Int, pageable: Pageable): Page<Follow>

    fun findAllByFollowingId(followingId: Int, pageable: Pageable): Page<Follow>

    fun findAllByCreatedBy(createdBy: Int): List<Follow>
}