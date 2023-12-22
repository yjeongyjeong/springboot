package org.zerock.b01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.upload.UploadFileDTO;
import org.zerock.b01.dto.upload.UploadResultDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
@RequiredArgsConstructor
public class UpDownController {

    @Value("${org.zerock.upload.path}") //application.properties에 설정되어있는 upload 경로
    private String uploadPath;

    @Operation(summary = "Upload POST", description = "POST 방식으로 파일등록")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(@Parameter(description = "Files to be uploaded",
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                             UploadFileDTO uploadFileDTO){
        log.info(uploadFileDTO);

        if(uploadFileDTO.getFiles() != null){

            final List<UploadResultDTO> list = new ArrayList<>();

            uploadFileDTO.getFiles().forEach(multipartFile ->{
                String originalName = multipartFile.getOriginalFilename();
                log.info(originalName);

                String uuid = UUID.randomUUID().toString();

                Path savePath = Paths.get(uploadPath, uuid +"_" + originalName);
                log.info("path : " + savePath);

                boolean image = false;

                try {
                    multipartFile.transferTo(savePath); //실제 파일 저장
                    // 파일이 이미지라면..
                    if(Files.probeContentType(savePath).startsWith("image")){ //업로드하는 파일이 이미지라면
                        image = true;
                        File thumbFile = new File(uploadPath, "s_" + uuid+ "_" + originalName);
                        //infile, outfile, width, height
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                list.add(UploadResultDTO.builder()
                                .uuid(uuid)
                                .fileName(originalName)
                                .img(image)
                        .build());

            });// end forEach

            return list;

        } //end if : 파일이 있다면 -> 이미지라면 섬네일, 아니라면 그냥
        return  null; //파일이 없으면 null 반환
    } //end upload
//link가 안생기는데???

    @Operation(summary = "view 파일", description = "get 방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable("fileName") String fileName){
        Resource resource = new FileSystemResource(uploadPath+File.separator + fileName);
        log.info("resource >> " + resource);

        String resourceName = resource.getFilename();
        log.info("resourceName >> " + resourceName);

        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(
                    resource.getFile().toPath() ));
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }// end viewFileGET

    @Operation(summary = "remove File" , description = "딜리트 방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String, Boolean> removeFile(@PathVariable("fileName") String fileName){
        Resource resource = new FileSystemResource(uploadPath+ File.separator+fileName);
        String resourceName = resource.getFilename();
        
        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;
        log.info("삭제 전 removed >> " + removed);

        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete();
            
            //섬네일이 존재한다면(img) 걔도 삭제
            if(contentType.startsWith("image")){ // C:\\upload [\혹은 / : 운영체제따라 다르기 때문에 separator로 줌] s_~~~~.jpg
                File thumbnailFile = new File(uploadPath+File.separator + "s_"+ fileName);
                thumbnailFile.delete();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }//end try catch

        log.info("삭제 후 removed >> " + removed);
        resultMap.put("result", removed); //removed는 boolean타입 => try가 성공적으로 끝난다면 true
        return resultMap;
    } // end remove

}
