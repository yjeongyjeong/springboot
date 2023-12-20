package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Reply", indexes = {
        @Index(name = "idx_reply_board_bno", columnList = "board_bno")
})
@Getter @Builder  @ToString(exclude = "board") //참조하는 객체를 사용하지 않도록 함!!
@AllArgsConstructor @NoArgsConstructor
public class Reply extends BaseEntity { //baseEntity에는 시간설정 담겨잇음

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동증가
    private Long rno;
    
    @ManyToOne(fetch = FetchType.LAZY) //연관관계 나타낼 때 fetch속성은 반드시 LAZY로 작성
    //lazy : 원래는(eager?) reply만 가져오지 않고 join해서 결과를 가져옴
    // 그러나 lazy를 하면 reply table만 가져옴 필요할 때만 데이터 전부 읽어옴
    // 연관테이블 그냥 다가져옴
    private Board board;
    
    private String replyText;
    
    private String replyer;

}
