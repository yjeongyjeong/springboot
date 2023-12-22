package org.zerock.b01.dto.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFileDTO {
    // 파일 이름
    private List<MultipartFile> files; 

}
