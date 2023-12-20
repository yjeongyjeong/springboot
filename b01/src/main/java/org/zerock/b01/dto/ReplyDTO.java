package org.zerock.b01.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jdk.jshell.execution.LoaderDelegate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.b01.domain.Board;

import java.time.LocalDateTime;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ReplyDTO {

    private Long rno;
    @NotNull
    private Board board;
    @NotEmpty
    private  String replyText;
    @NotEmpty
    private String replyer;
    private LocalDateTime regDate, modDate;
}
