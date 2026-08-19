package com.clj.ai.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档上传结果 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadResultVo {

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 文档名称
     */
    private String documentName;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 处理状态：PENDING-待处理, PROCESSING-处理中, SUCCESS-成功, FAILED-失败
     */
    private String status;
}
