package org.zerock.b01.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.service.ReplyService;

import java.util.HashMap;
import java.util.Map;

@RestController  //반환타입이 JSON
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

//    @Operation(summary = "Replies POST", description = "Post 방식으로 댓글 등록~!")
//    @PostMapping(value="/", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity <Map<String, Long>> register(@Valid @RequestBody ReplyDTO replyDTO,
//                                                       BindingResult bindingResult)
//            throws BindException {
//
//        log.info(replyDTO);
//
//        if(bindingResult.hasErrors()){
//            throw  new BindException(bindingResult);
//        }
//
//        Map<String, Long> resultMap = Map.of("rno", 111L);
//        return ResponseEntity.ok(resultMap);
//    }
//
//
    @Operation(summary = "Replies POST", description = "Post 방식으로 댓글 등록~!")
    @PostMapping(value="/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String , Long> register(
            @Valid @RequestBody ReplyDTO replyDTO,
            BindingResult bindingResult    ) throws  BindException{

        log.info("controller>>>>"+replyDTO);

        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        Map<String, Long> resultMap = new HashMap<>();
        Long rno = replyService.register(replyDTO);
        resultMap.put("rno", rno);
log.info("rno >>>>>>>>>> "+ rno);

        return  resultMap;
    }

    @Operation(summary = "Replies of Board", description = "Get 방식으로 특정 게시물의 댓글 목록 가져오기~~")
    @GetMapping(value = "/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno,
                                             PageRequestDTO pageRequestDTO){
        return replyService.getlistOfBoard(bno, pageRequestDTO);
    }

    @Operation(summary = "Read Reply", description = "get 방식으로 특정 댓글 조회")
    @GetMapping("/{rno}")
    public ReplyDTO getReplyDTO(@PathVariable("rno") Long rno){
        return replyService.read(rno);
    }

    @Operation(summary = "Delete Reply", description = "delete 방식으로 특정 댓글 삭제!")
    @DeleteMapping("/{rno}")
    public Map<String, Long> remove(@PathVariable("rno") Long rno){
        replyService.remove(rno);
        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);
        return resultMap;
    }

    @Operation(summary = "Modify Reply", description = "Put 방식으로 특정 댓글 수정")
    @PutMapping(value = "/{rno}")
    public Map<String, Long> modify(@PathVariable("rno") Long rno, @RequestBody ReplyDTO replyDTO){
        replyDTO.setRno(rno); //번호를 일치시킴
        replyService.modify(replyDTO);

        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);
        return resultMap;
    }
}
