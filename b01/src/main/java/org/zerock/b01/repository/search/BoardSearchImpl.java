package org.zerock.b01.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.QBoard;
import org.zerock.b01.domain.QReply;
import org.zerock.b01.dto.BoardImageDTO;
import org.zerock.b01.dto.BoardListAllDTO;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch{

    public BoardSearchImpl(){
        super(Board.class);
    }
    @Override
    public Page<Board> search1(Pageable pageable) {
        QBoard board = QBoard.board; //Q도메인 객체
        JPQLQuery<Board> query = from(board); // select .. from board
        query.where((board.title.contains("1"))); //where title like "%1%"

        //페이징
        this.getQuerydsl().applyPagination(pageable,query);

        List<Board> list = query.fetch();
        Long count = query.fetchCount();

        log.info("count >> " + count);
        list.forEach(board1 -> log.info(board1));

        return null;
    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);
        if((types!=null && types.length >0) && keyword!=null ){
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for(String type : types){
                switch (type){
                    case "t":
                    booleanBuilder.or(board.title.contains(keyword));
                    break;
                    case "c":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                }
            } //end for
            query.where(booleanBuilder);
        } //end if
        query.where(board.bno.gt(0L));
        //paging
        this.getQuerydsl().applyPagination(pageable, query);
        List<Board> list = query.fetch();
        Long count = query.fetchCount();

    return new PageImpl<>(list, pageable, count);
    }

    @Override //build에 QReply 존재하는지 확인!
    // 댓글 개수 세기
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> query = from(board);
        query.leftJoin(reply).on(reply.board.eq((board)));

        query.groupBy(board);

        if((types!=null && types.length >0) && keyword!=null ){
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for(String type : types){
                switch (type){
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                }
            } //end for
            query.where(booleanBuilder);
        } //end if
        //bno > 0
        query.where(board.bno.gt(0L));

        JPQLQuery<BoardListReplyCountDTO> dtoQuery =
                query.select(Projections.bean(BoardListReplyCountDTO.class,
                        board.bno,
                        board.title,
                        board.writer,
                        board.regDate,
                        reply.count().as("replyCount") //댓글 개수
                )); //projections로 인해 board+댓글개수 객체가 만들어짐...?(boardlistreplycountdto)

        this.getQuerydsl().applyPagination(pageable , dtoQuery);
//        fetch() :  리스트로 결과를 반환하는 방법입니다. (만약에 데이터가 없으면 빈 리스트를 반환해줍니다.)
        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch();
//        fetchCount() : count 쿼리를 날릴 수 있다.
        long count = dtoQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, count);
    }

    //board 와 reply를 left join하고 쿼리를 실행해서 내용을 확인하는 것
    @Override
    public Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board);
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board)); //left Join

        if((types!=null && types.length >0) && keyword!=null ) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                }
            } //end for
            boardJPQLQuery.where(booleanBuilder);
        }// end if

        boardJPQLQuery.groupBy(board);

        getQuerydsl().applyPagination(pageable, boardJPQLQuery); //paging

        JPQLQuery<Tuple> tupleJPQLQuery = boardJPQLQuery.select(board, reply.countDistinct());
        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<BoardListAllDTO> dtoList = tupleList.stream().map(tuple->{
          Board board1 = (Board) tuple.get(board);
          long replyCount = tuple.get(1,Long.class);

          BoardListAllDTO dto = BoardListAllDTO.builder()
                  .bno(board1.getBno())
                  .title(board1.getTitle())
                  .writer(board1.getWriter())
                  .regDate(board1.getRegDate())
                  .replyCount(replyCount)
                  .build();

          //boardImage를 boardImageDTO 처리할 부분
            List<BoardImageDTO> imageDTOS = board1.getImageSet().stream().sorted()
                    .map(boardImage -> BoardImageDTO.builder()
                            .uuid(boardImage.getUuid())
                            .fileName(boardImage.getFileName())
                            .ord(boardImage.getOrd())
                            .build())
                    .collect(Collectors.toList());
            dto.setBoardImages(imageDTOS);
            return dto;

        }).collect(Collectors.toList());

        long totalCount = boardJPQLQuery.fetchCount();

        return  new PageImpl<>(dtoList,pageable,totalCount);
    }
}
