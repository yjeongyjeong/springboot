package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Log4j2
class BoardRepositoryTests {
    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void testInsert(){
        for(int i = 0; i < 100; i ++){

        Board board = Board.builder()
                .title("Title.." + i)
                .content("content.." + i)
                .writer(("user")+i)
                .build();
        Board result = boardRepository.save(board);
        log.info("BNO : " + result.getBno());
        }
    }

    @Test
    public void testSelect(){
        Long bno = 99L;
        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow(); //null 일 경우 예외처리
        log.info(board);
    }

    @Test
    public void testUpdate(){
        Long bno = 99L;

        Board board = boardRepository.findById(bno).orElseThrow();
        log.info("board >> " + board);

        board.change("update title!!!", "update content!!!");
        log.info("board2 >> " + board);

        boardRepository.save(board);
        log.info("board3 >> " + board);
    }

    @Test
    public void testDelete(){ //select 로 데이터 잇는지 조회 후 delete
        boardRepository.deleteById(99L);
    }

    @Test
    public void testGetList(){
        boardRepository.findAll().forEach(list ->
                log.info(list));
    }
//위와 동일한 구문..!
    @Test
    public void testGetList2(){
        List<Board> list = boardRepository.findAll();
        for(Board b: list)
            log.info(b);
    }
    
    @Test
    public void testPaging(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());
        Page<Board> result = boardRepository.findAll(pageable);
        log.info(result.getTotalElements()); //몇개인지
        log.info(result.getTotalPages()); //전체 몇페이지일지

        result.getContent().forEach(list -> log.info(list));
    }

    @Test
    public void testWriter(){
        boardRepository.findBoardByWriter("user11")
                .forEach(list -> log.info(list));
    }

    @Test
    public void testWriterAndTitle(){
        boardRepository.findByWriterAndTitle("user99", "title..99")
                .forEach(list -> log.info(list));
    }

    @Test
    public void testWriterList(){
        boardRepository.findByWriterLike("user%")
                .forEach(list -> log.info(list));
    }

    @Test
    public void testWriter2(){
        boardRepository.findByWriter2("user11")
                .forEach(list -> log.info(list));
    }

    @Test
    public void testTitle(){
        boardRepository.findByTitle("1")
                .forEach(list -> log.info(list));
    }
    //table로 search가 아니라 Entity를 통해 search(keyword -> title)
    @Test
    public void testPage(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
        Page<Board> result = boardRepository.findByKeyword("1", pageable);
        log.info(result.getTotalElements());
        log.info(result.getTotalPages());
        result.getContent().forEach(board -> log.info(board));
    }

    @Test
    public void testTitle2(){
        boardRepository.findByTitle2("1")
                .forEach(list -> log.info(list)); //testTitle과 다르게 @query를 통해 작성한 쿼리문이 그대로 반영됨
    }

    @Test
    public void testSearch1(){
        //bulid clean 하고 compile하진 말고 돌리기.. Q객체들이 중복돼서 오류발생함
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
        boardRepository.search1(pageable);
    }

    @Test
    public void testSearchAll(){
        String[] types = {"t", "c" ,"w"};
        String keyword = "1";
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        log.info("====================");
        log.info(result.getTotalElements()); //99
        log.info(result.getTotalPages()); //10
        log.info(result.getSize()); //10
        log.info(result.getNumber()); //0
        log.info(result.hasPrevious()); //false : 이전페이지 존재하지않음
        log.info(result.hasNext()); //true : 다음페이지 존재
        log.info("====================");
    }

    @Test
    public void testSearchReplyCount(){
        String[] types = {"t", "c", "w"};
        String keyword = "a";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        //직접확인
        log.info(result.getTotalPages());
        log.info(result.getTotalElements());
        log.info(result.getSize()); //pag size
        log.info(result.getNumber()); //pagenumber
        //has next 등 가능

        result.getContent().forEach(list -> log.info(list));
    }

}