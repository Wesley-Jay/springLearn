package com.example.elasticsearch.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 知识信息表
 * @author wsj
 * @date 2023-11-13
 */
@Data
public class KnowledgeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**   */
	private Long id;
	/**  知识标题 */
	private String title;
	/**  知识摘要 */
	private String description;
	/**  版本 */
	private String version;
	/**  关联设备code */
	private String equipmentCode;
	/**  来源（1：原创，2：引用） */
	private String source;
	/**  内容类型 */
	private String contentType;
	/**  文件类型 */
	private String fileType;
	/**  编辑方式 1.文件上传，2现在编辑，3.知识链接 */
	private String editType;
	/**  状态（草稿，审批中，发布,驳回） */
	private String status;
	/**  知识内容 */
	private String content;
	/**  文章链接 */
	private String articleLink;
	/**  知识文件路径 */
	private String fileUrl;
	/**  知识文件路径 */
	private String previewUrl;
	/**  关键词 */
	private String keywords;
	/**  评分 */
	private BigDecimal score;
	/**  收藏数 */
	private Integer likeNumber;
	/**  评论数 */
	private Integer commentNumber;
	/**  查看数 */
	private Integer viewNumber;
	/**   */
	private String createBy;
	private String createName;

	/**   */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	/**   */
	private String updateBy;
	/**   */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	/**   */
	private String delFlag;
	/**  驳货理由 */
	private String rejectReason;
	/**  审批人 */
	private String approveBy;

	private Integer collect;

	private String dateStr;


}
