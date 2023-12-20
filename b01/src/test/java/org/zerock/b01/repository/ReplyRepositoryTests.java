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
import org.zerock.b01.domain.Reply;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ReplyRepositoryTests {

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    public void testInsert(){
//reply, replyDTO에서 board로 했기 때문에 join이 들어간 채로 테이블이 생성되고,
//bno만 가져왔기 때문에 bno로 생성된..걸가..?
        Long bno = 106L;
        Board board = Board.builder().bno(bno).build();

        Reply reply = Reply.builder()
                .replyer("replyer")
                .replyText("replyTest")
                .board(board)
                .build();
        replyRepository.save(reply);
    }

    @Test
    public void testSelectOne(){
        Reply reply = replyRepository.findById(1L).orElseThrow(); //id=>rno
        log.info(reply);
    }

    @Test
    public void testBoardReplies(){
        Long bno = 106L;

        Pageable pageable = PageRequest.of(0, 10, Sort.by("rno").descending());
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);
        result.getContent().forEach(reply -> log.info(reply));
    }
}