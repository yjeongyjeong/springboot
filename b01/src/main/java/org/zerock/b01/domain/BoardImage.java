package org.zerock.b01.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "board")
public class BoardImage implements Comparable<BoardImage>{
    //고유한 uuid 값과 이름, 순번을 지정하고 many to one으로 board 객체 지정
    //comparable : 정렬을 위해 사용
    @Id
    private String uuid;
    private String fileName;
    private int ord; //순번(한 게시글에 여러 파일 들어갈 떄)
    @ManyToOne
    private Board board;

    @Override
    public int compareTo(BoardImage other) {
        return this.ord - other.ord;
    }

    //게시글 삭제 시 파일 참조를 끊어서 삭제된 느낌으로..처리
    public void changeBoard(Board board){
        this.board = board;
    }
}
