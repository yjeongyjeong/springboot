package org.zerock.b01.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.*;
import org.zerock.b01.repository.BoardRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long register(BoardDTO boardDTO) {
        log.info("========================================");
        log.info("boardDTO >>>>>>>>>" + boardDTO);
        Board board = dtoToEntity(boardDTO);
        log.info("board >>>>>>>>>>" + board);
        Long bno = boardRepository.save(board).getBno();
        log.info("bno >>>>>>>>>>>>" +bno);
        return bno;
    }

    @Override
    public BoardDTO readOne(Long bno) {
////        Optional result = boardRepository.findById(bno);
////        Board board = result.orElseThrow();
//        Board board = boardRepository.findById(bno).orElseThrow();
//        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);

        //board_image까지 조인 처리되는 findByWithImages()를 이용
        Optional<Board> result = boardRepository.findByIdWithImages(bno);
        Board board = result.orElseThrow();

        BoardDTO boardDTO = entityToDTO(board);
        return boardDTO;
    }

    @Override
    public void modify(BoardDTO boardDTO) {
        Board board = boardRepository.findById(boardDTO.getBno()).orElseThrow();
        board.change(boardDTO.getTitle(),boardDTO.getContent());
        
        //첨부파일 처리
        board.clearImages();
        
        if(boardDTO.getFileNames() != null) {
            for (String fileName : boardDTO.getFileNames()) {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            }
        }
        boardRepository.save(board);
    }

    @Override
    public void remove(Long bno) {
        boardRepository.deleteById(bno);
    }

    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {


        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        log.info("=========================types>>" + types);
        log.info("=========================keyword>>" + keyword);

        Pageable pageable = pageRequestDTO.getPageable("bno");
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

//        result.getTotalElements();
//        result.getTotalPages();
//        result.getContent().forEach(board -> log.info(board));

        //형변환 board -> boardDTO
        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<BoardListReplyCountDTO> result =
                boardRepository.searchWithReplyCount(types, keyword, pageable);

        return PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();


    }

    @Override
    public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<BoardListAllDTO> result =
                boardRepository.searchWithAll(types, keyword, pageable);

        return PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }


//    @Override
//    public Long register(BoardDTO boardDTO) {
//        Board board = boardDTOToEntity(boardDTO);
//        Long bno;
//        bno = boardRepository.save(board).getBno();
//        return bno;
//    }
//
//    @Override
//    public BoardDTO readOne(Long bno) {
//        Board board = boardRepository.findById(bno).orElseThrow();
//
//        return entityToBoardDTO(board);
//    }


}
