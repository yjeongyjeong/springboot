package org.zerock.b01.service;

import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public interface BoardService {
    Long register(BoardDTO boardDTO);

    BoardDTO readOne(Long bno);

    void modify(BoardDTO boardDTO);

    void remove(Long bno);

    PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO);

    //댓글 숫자 처리
    PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(
            PageRequestDTO pageRequestDTO);

    //게시글의 이미지와 댓글의 숫자까지 처리
    PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);


    default Board dtoToEntity(BoardDTO boardDTO){
        Board board = Board.builder()
                .bno(boardDTO.getBno())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .writer(boardDTO.getWriter())

                .build();
        if (boardDTO.getFileNames() != null){
            boardDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]); //0-> uuid, 1-> original fileName
            }); //end for
        }// end if
        return board;
    }

    default BoardDTO entityToDTO(Board board){
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .build();

        List<String> fileNames =board.getImageSet().stream().sorted().map(boardImage ->
                boardImage.getUuid()+"_"+boardImage.getFileName()).collect(Collectors.toList());
        boardDTO.setFileNames(fileNames);

        return boardDTO;
    }




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
