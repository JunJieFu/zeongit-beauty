package com.junjie.web.controller

import com.junjie.core.annotations.Auth
import com.junjie.core.annotations.CurrentUserInfoId
import com.junjie.core.annotations.RestfulPack
import com.junjie.core.exception.PermissionException
import com.junjie.core.exception.ProgramException
import com.junjie.data.constant.PrivacyState
import com.junjie.data.database.primary.entity.Picture
import com.junjie.data.database.primary.entity.Tag
import com.junjie.data.index.primary.document.PictureDocument
import com.junjie.qiniu.core.component.QiniuConfig
import com.junjie.qiniu.service.BucketService
import com.junjie.share.service.UserInfoService
import com.junjie.web.core.communal.PictureVoAbstract
import com.junjie.web.service.CollectionService
import com.junjie.web.service.FollowService
import com.junjie.web.service.PictureDocumentService
import com.junjie.web.service.PictureService
import com.junjie.web.vo.PictureVo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author fjj
 * 画像的控制器
 */
@RestController
@RequestMapping("picture")
class PictureController(private val pictureService: PictureService,
                        private val bucketService: BucketService,
                        private val qiniuConfig: QiniuConfig,
                        override val pictureDocumentService: PictureDocumentService,
                        override val collectionService: CollectionService,
                        override val userInfoService: UserInfoService,
                        override val followService: FollowService) : PictureVoAbstract() {

    class SaveDto {
        lateinit var url: String

        lateinit var name: String

        var introduction: String? = null

        lateinit var privacy: PrivacyState

        var tagList: Set<String>? = null
    }

    class UpdateDto {
        var id: Int = 0

        var name: String? = null

        var introduction: String? = null

        var privacy: PrivacyState? = null

        var tagList: Set<String>? = null
    }

    class RemoveDto {
        var id: Int = 0
    }

    class BatchRemoveDto {
        var idList: Set<Int> = setOf()
    }

    class BatchUpdateDto {
        var idList: Set<Int> = setOf()

        var name: String? = null

        var introduction: String? = null

        var privacy: PrivacyState? = null

        var tagList: Set<String>? = null
    }

    /**
     * 根据标签获取
     */
    @GetMapping("paging")
    @RestfulPack
    fun paging(@CurrentUserInfoId userId: Int?, @PageableDefault(value = 20) pageable: Pageable, tagList: String?, precise: Boolean?, name: String?, startDate: Date?, endDate: Date?, targetId: Int?): Page<PictureVo> {
        return getPageVo(pictureDocumentService.paging(pageable, tagList?.split(" "), precise != null && precise, name, startDate, endDate, targetId, userId != null && targetId == userId), userId)
    }

    /**
     * 根据标签获取
     */
    @GetMapping("synchronizationIndexPicture")
    @RestfulPack
    fun synchronizationIndexPicture(): Long {
        return pictureService.synchronizationIndexPicture()
    }

    /**
     * 获取推荐
     */
    @GetMapping("pagingByRecommend")
    @RestfulPack
    fun pagingByRecommend(@CurrentUserInfoId userId: Int?, @PageableDefault(value = 20) pageable: Pageable, startDate: Date?, endDate: Date?): Page<PictureVo> {
        return getPageVo(pictureDocumentService.pagingByRecommend(userId, pageable, startDate, endDate), userId)
    }

    /**
     * 获取图片
     */
    @GetMapping("get")
    @RestfulPack
    fun get(id: Int, @CurrentUserInfoId userId: Int?): PictureVo {
        return getPictureVo(id, userId)
    }

    /**
     * 获取tag第一张
     * 会移到ES搜索
     */
    @GetMapping("getFirstByTag")
    @RestfulPack
    fun getFirstByTag(tag: String): PictureVo {
        return getPictureVo(pictureDocumentService.getFirstByTag(tag))
    }

    /**
     * 按tag统计图片
     */
    @GetMapping("countByTag")
    @RestfulPack
    fun countByTag(tag: String): Long {
        return pictureDocumentService.countByTag(tag)
    }

    /**
     * 保存图片
     */
    @Auth
    @PostMapping("save")
    @RestfulPack
    fun save(@CurrentUserInfoId userId: Int, @RequestBody dto: SaveDto): PictureVo {
        bucketService.move(dto.url, qiniuConfig.qiniuBucket)
        val imageInfo = bucketService.getImageInfo(
                dto.url,
                qiniuConfig.qiniuBucketUrl
        ) ?: throw ProgramException("移除图片出错")
        val picture = Picture(dto.url,
                imageInfo.width,
                imageInfo.height,
                dto.name,
                dto.introduction,
                dto.privacy)
        if (dto.tagList != null) {
            picture.tagList.addAll(dto.tagList!!.map { Tag(it) })
        }
        return getPictureVo(pictureService.save(picture), userId)
    }

    /**
     * 更新
     */
    @Auth
    @PostMapping("update")
    @RestfulPack
    fun update(@CurrentUserInfoId userId: Int, @RequestBody dto: UpdateDto): PictureVo {
        val picture = pictureService.get(dto.id)
        picture.createdBy != userId && throw PermissionException("您无权修改该图片")
        picture.name = dto.name ?: picture.name
        picture.introduction = dto.introduction ?: picture.introduction
        picture.privacy = dto.privacy ?: picture.privacy
        dto.tagList?.let { tagList ->
            val tagNameList = picture.tagList.map { it.name }
            val sourceTagList = picture.tagList.toMutableSet()
            for (addTagName in tagList) {
                if (tagNameList.indexOf(addTagName) == -1) {
                    sourceTagList.add(Tag(addTagName))
                }
            }
            for (tag in picture.tagList.toList()) {
                if (tagList.indexOf(tag.name) == -1) {
                    sourceTagList.remove(tag)
                }
            }
            picture.tagList.clear()
            picture.tagList.addAll(sourceTagList)
        }
        return getPictureVo(pictureService.save(picture), userId)
    }

    /**
     * 逻辑删除图片
     */
    @Auth(true)
    @PostMapping("remove")
    @RestfulPack
    fun remove(@CurrentUserInfoId userId: Int, @RequestBody dto: RemoveDto): Boolean {
        val picture = pictureService.get(dto.id)
        if (userId != picture.createdBy) {
            throw PermissionException("你无权删除该图片")
        }
        return pictureService.remove(picture)
    }

    /**
     * 批量移除图片
     */
    @Auth(true)
    @PostMapping("batchRemove")
    @RestfulPack
    fun batchRemove(@CurrentUserInfoId userId: Int, @RequestBody dto: BatchRemoveDto): Boolean {
        for (id in dto.idList) {
            val picture = pictureService.get(id)
            if (userId != picture.createdBy) {
                throw PermissionException("你无权删除该图片")
            }
            bucketService.move(picture.url, qiniuConfig.qiniuTempBucket, qiniuConfig.qiniuBucket)
            pictureService.remove(picture)
        }
        return true
    }


    @Auth
    @PostMapping("batchUpdate")
    @RestfulPack
    fun batchUpdate(@CurrentUserInfoId userId: Int, @RequestBody dto: BatchUpdateDto): MutableList<PictureDocument> {
        val pictureList = mutableListOf<PictureDocument>()
        for (id in dto.idList) {
            try {
                val picture = pictureService.get(id)
                if (userId != picture.createdBy) {
                    continue
                }
                picture.name = dto.name ?: picture.name
                picture.introduction = dto.introduction ?: picture.introduction
                picture.privacy = dto.privacy ?: picture.privacy
                dto.tagList?.let { tagList ->
                    val sourceTagList = picture.tagList.toMutableSet()
                    sourceTagList.addAll(tagList.map { Tag(it) })
                    picture.tagList.clear()
                    picture.tagList.addAll(sourceTagList.distinctBy { it.name })
                }
                pictureList.add(pictureService.save(picture))
            } catch (e: Exception) {
                println(e.message)
            }
        }
        return pictureList
    }

    private fun getPageVo(page: Page<PictureDocument>, userId: Int? = null): Page<PictureVo> {
        val pictureVoList = page.content.map { getPictureVo(it, userId) }
        return PageImpl(pictureVoList, page.pageable, page.totalElements)
    }
}