package com.globalways.csscli.ui.gallery;

import java.io.Serializable;

/**
 * 相册实体
 * 
 * @author James
 */
public class GalleryAlbumEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 相册ID **/
	public String imageBucketId;
	/** 当前相册图片数量 **/
	public int imageCount = 0;
	/** 相册名称 **/
	public String bucketName;
	/** 封面原图 **/
	public String coverOriginalPath;
	/** 封面缩略图路径 **/
	public String coverThumbnailPath;

}
