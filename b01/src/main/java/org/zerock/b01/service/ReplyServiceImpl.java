package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Reply;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.repository.ReplyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper; //형변환

    @Override
    public Long register(ReplyDTO replyDTO) {
        Reply reply = modelMapper.map(replyDTO, Reply.class);
        Long rno = replyRepository.save(reply).getRno();
        return rno;
    }

    @Override
    public ReplyDTO read(Long rno) {
        Optional<Reply> replyOptional = replyRepository.findById(rno);
        Reply reply = replyOptional.orElseThrow();

        return modelMapper.map(reply, ReplyDTO.class);
    }

    @Override
    public void modify(ReplyDTO replyDTO) {
        //findById로 찾은 후(존재여부확인) changeText로 내용만! 변경
        Reply reply = replyRepository.findById(replyDTO.getRno()).orElseThrow();
        reply.changeText(replyDTO.getReplyText());
        replyRepository.save(reply);
    }

    @Override
    public void remove(Long rno) {
        replyRepository.deleteById(rno);
    }

    @Override
    public PageResponseDTO<ReplyDTO> getlistOfBoard(Long bno, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <= 0 ? 0 : pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(), Sort.by("rno").descending());

        Page<Reply> result = replyRepository.listOfBoard(bno,pageable);

//        result.getContent().forEach(list -> log.info(list));

        List<ReplyDTO> dtoList =
                result.getContent().stream().
                        map(reply -> modelMapper.map(reply, ReplyDTO.class))
                        .collect(Collectors.toList());

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }
}
