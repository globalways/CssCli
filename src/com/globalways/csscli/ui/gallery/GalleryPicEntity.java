package com.globalways.csscli.ui.gallery;

import java.io.Serializable;

/**
 * 图片实体
 * 
 * @author James
 */
public class GalleryPicEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 相册ID **/
	public String imageBucketId;
	/** 图片ID **/
	public long imageId;
	/** 缩略图路径 **/
	public String thumbnailPath;
	/** 原图路径 **/
	public String imagePath;
	/** 文件大小(byte) **/
	public long imageSize = 0;
}
