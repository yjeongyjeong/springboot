package org.zerock.b01.service;

import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardDTO;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;

public interface BoardService {
    Long register(BoardDTO boardDTO);

    BoardDTO readOne(Long bno);

    void modify(BoardDTO boardDTO);

    void remove(Long bno);

    PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO);








    //원래 인터페이스는 바디가 없어야 하는데 default를 넣어서 안전을 보장해줌
    //또한 구현해도 괜찮고 구현하지 않아도 괜찮게 해줌
    default public Board boardDTOToEntity(BoardDTO boardDTO){
        Board board = Board.builder()
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .writer(boardDTO.getWriter())
                .build();
        return board;
    };

    //board를 boardDTO로 변경
    default public BoardDTO entityToBoardDTO(Board board){
      return   BoardDTO.builder()
              .bno(board.getBno())
              .title(board.getTitle())
              .content(board.getContent())
              .writer(board.getWriter())
              //baseEntity
              .regDate(board.getRegDate())
              .modDate(board.getModDate())
              .build();
    };




}
