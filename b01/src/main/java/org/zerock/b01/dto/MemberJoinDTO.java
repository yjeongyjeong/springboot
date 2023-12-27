package org.zerock.b01.dto;

import lombok.Data;

@Data
public class MemberJoinDTO {
    private String mid, mpw, email;
    private boolean del, social;
}
