package com.clj.ai.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioService {

    public String upload(MultipartFile file);

    /**
     * 上传 RAG 文档到指定路径
     *
     * @param file 文件
     * @param objectName 对象路径（如 rag/1/123/document.pdf）
     * @return 文件访问 URL
     */
    String uploadRagDocument(MultipartFile file, String objectName);

    /**
     * 从 MinIO 读取 RAG 文档流
     *
     * @param objectName 对象路径
     * @return 输入流
     */
    InputStream readRagDocument(String objectName);

    /**
     * 删除 MinIO 中的 RAG 文档
     *
     * @param objectName 对象路径
     */
    void deleteRagDocument(String objectName);

    /**
     * 根据图片 URL 从 MinIO 读取图片，转为 Base64 data URL
     * <p>
     * 前端传的是 upload 返回的完整 URL（如 http://192.168.127.128:9000/agriculture/xxx.png），
     * 后端解析出对象名后从 MinIO 读取字节流，转换为 data:image/png;base64,xxx 格式。
     * Base64 仅在 Java → Qwen 这一段使用，不返回给前端。
     *
     * @param imageUrl MinIO 图片完整 URL
     * @return data URL（data:image/png;base64,xxxxxx）
     */
    public String readImageAsDataUrl(String imageUrl);
}
