package org.zerock.b01.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.*;
import org.zerock.b01.repository.MemberRepository;
import org.zerock.b01.service.BoardService;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {


    @Value("${org.zerock.upload.path") //import 시에 springFramework로 시작하는 value
    private String uploadPath;
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
    @PreAuthorize("hasRole('USER')")
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

    @PreAuthorize("isAuthenticated()") //인증된 사용자만
    @GetMapping({"/read", "/modify"})
    public void read(@RequestParam("bno") Long bno, PageRequestDTO pageRequestDTO, Model model){
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("boardDTO >> " + boardDTO);
        log.info("boardDTO.writer >> " + boardDTO.getWriter());
//        log.info("principal.username >> " );

        model.addAttribute("dto", boardDTO);
    }

    @PostMapping("/modify")
    //@PreAuthorize("principal.username == #boardDTO.writer")
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
    @PreAuthorize("principal.username == #boardDTO.writer")
    public String remove(BoardDTO boardDTO, RedirectAttributes redirectAttributes){
        Long bno = boardDTO.getBno();
        log.info("remove post~~~~~~~~~~~~~~ >> " + bno);
        boardService.remove(bno);
        
        //게시물이 데이터베이스상에서 삭제되었다면 첨부파일 삭제
        log.info(boardDTO.getFileNames());
        List<String> fileNames = boardDTO.getFileNames();
        if(fileNames != null && fileNames.size() > 0){
            removeFiles(fileNames);
        }
        
        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/board/list";
    }

    public void removeFiles(List<String> files){
        for(String fileName:files){
            Resource resource = new FileSystemResource((uploadPath + File.separator + fileName));
            String resourceName = resource.getFilename();
            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();

                //섬네일이 존재한다면
                if(contentType.startsWith("image")){
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }
            } catch (Exception e){
                log.error(e.getMessage());
            }
        }
    }// end for
}
