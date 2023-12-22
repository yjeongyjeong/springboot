package org.zerock.b01.service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardDTO;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister(){
        BoardDTO boardDTO = BoardDTO.builder()
                .title("service mapper test")
                .content("testMapper")
                .writer("sTestM")
                .build();
        Long bno = boardService.register(boardDTO);
        log.info("bno >> " + bno);
    }

    @Test
    public void testReadOne(){
        BoardDTO boardDTO = boardService.readOne(100L);
        log.info("boardDTO >> " + boardDTO);


    }

    @Transactional //없으면 에러 발생
    /*영속 상태인 Proxy 객체에 실제 데이터를 불러오려고 초기화를 시도하지만 Session이 close되어서 준영속 상태가 되어 값을 가져올 수가 없어 발생한 오류...?
    * 지연로딩(lazy)하려면 해당 객체는 무조건 영속성 컨텍스트에서 관리가 필요함..?
    * @transactional */
    @Test
    public void testModify(){
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(101L)
                .title("modify test 101")
                .content("modify content 101")
                .build();

        //첨부파일 추가
        boardDTO.setFileNames(Arrays.asList(UUID.randomUUID().toString()+"_zzz.jpg"));

        boardService.modify(boardDTO);
    }

    @Test
    public void testDelete(){
        boardService.remove(2L);
    }

    @Test
    public void testList(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();
        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
        log.info(responseDTO);
    }

    @Test
    public void testRegisterWithImages(){
        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("file sample title~~")
                .content("sample content~~~~")
                .writer("user00")
                .build();

        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aaa.jpg",
                        UUID.randomUUID()+"_bbb.jpg",
                        UUID.randomUUID()+"_ccc.jpg"
                ));

        Long bno = boardService.register(boardDTO);
        log.info("bno >> " + bno);
    }

    @Test
    public void testReadAll(){
        Long bno = 101L;
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info(boardDTO);

        for(String fileName : boardDTO.getFileNames()){
            log.info(fileName);
        } // end for
    }

}