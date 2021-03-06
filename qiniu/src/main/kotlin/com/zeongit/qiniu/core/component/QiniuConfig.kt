package com.zeongit.qiniu.core.component

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


/**
 * @author fjj
 * 配置
 */
@Component
@ConfigurationProperties("qiniu")
class QiniuConfig {
    lateinit var qiniuAccessKey: String

    lateinit var qiniuSecretKey: String

    lateinit var qiniuHost: String

    //临时储存空间
    lateinit var qiniuTemporaryBucket: String

    //图片储存空间
    lateinit var qiniuPictureBucket: String

    //临时储存空间
    lateinit var qiniuTemporaryBucketUrl: String

    //图片储存空间
    lateinit var qiniuPictureBucketUrl: String
}
