package org.zerock.b01.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.*;
import org.zerock.b01.service.BoardService;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "list")
    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){
//        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
        PageResponseDTO<BoardListAllDTO> responseDTO =
                boardService.listWithAll(pageRequestDTO);
        log.info("---------------------------------");
        log.info(responseDTO);
        model.addAttribute("responseDTO", responseDTO);
    }

    @Operation(summary = "register", method = "GetMapping")
    @GetMapping("/register")
    public void register(){

    }

    @PostMapping("/register")
    public String register(@Valid BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){

        log.info("board POST register");

        if(bindingResult.hasErrors()){
            log.info("ERROR");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }
        log.info("boardDTO >> " + boardDTO);

        Long bno = boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result", bno);
        return "redirect:/board/list";
    }

    @GetMapping({"/read", "/modify"})
    public void read(@RequestParam("bno") Long bno, PageRequestDTO pageRequestDTO, Model model){
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("boardDTO >> " + boardDTO);

        model.addAttribute("dto", boardDTO);
    }

    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO,
                       @Valid BoardDTO boardDTO,
                       RedirectAttributes redirectAttributes,
                       BindingResult bindingResult){
        log.info("This is Board modify post~!~!~!");

        if(bindingResult.hasErrors()){
            log.info("ERROR");

            String link = pageRequestDTO.getLink();

            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("bno", boardDTO.getBno());
            log.info("bno======================> " + boardDTO.getBno());
            /*bno값 에러남 없다고..*/
            return "redirect:/board/modify?"+link;
        }
        boardService.modify(boardDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addFlashAttribute("bno", boardDTO.getBno());

        return "redirect:/board/read?bno="+ boardDTO.getBno();
        /*책에는 /board/read로만 되어잇는데 에러나서 임의로 링크 걸어둠..*/

    }

    @PostMapping("/remove")
    public String remove(@RequestParam("bno") Long bno, RedirectAttributes redirectAttributes){
        log.info("remove post~~~~~~~~~~~~~~ >> " + bno);
        boardService.remove(bno);
        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/board/list";
    }
}
