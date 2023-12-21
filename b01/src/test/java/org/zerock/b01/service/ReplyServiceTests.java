package org.zerock.b01.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.ReplyDTO;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ReplyServiceTests {

    @Autowired
    private ReplyService replyService;

    @Test
    public void testRegister(){
        ReplyDTO replyDTO = ReplyDTO.builder()
                .replyText("댓글테스트 ~~~ 104~~ ")
                .replyer("owner")
                .board_bno(104L)
                .build();
        log.info(replyService.register(replyDTO));
        log.info(replyDTO);
        //bno를 지정해줘도 null 값이 들어간 채로 insert 됨..
        //repositorytests에선 잘 들어가고.. 왜일까?
        //일단 repository는 db와 바로 연동되어 있음...
        // db컬럼명은 board_bno여서 replyDTO도 일단 이름을 바꿨지만 테스트 실패
    }

    @Test
    public void testRead(){
        ReplyDTO replyDTO = replyService.read(106L);
        log.info(replyDTO);
    }

    @Test
    public void testModify(){
        ReplyDTO replyDTO = ReplyDTO.builder()
                .rno(1L) // bno아님!!
                .replyText("gfsgdfgdfg")
                .build();
        replyService.modify(replyDTO);

    }

    @Test
    public void testRemove(){
        replyService.remove(11L); //마찬가지로 select 후 delete
    }

    @Test
    public void testGetListOfBoard(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(0)
                .size(10)
                .build();

        replyService.getlistOfBoard(106L,pageRequestDTO);
    }
}