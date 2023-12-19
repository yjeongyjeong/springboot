package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;
//table과 관련있는 클래스이므로 setter는 지정하지 않음
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Board extends BaseEntity{ //@MappedSuperClass 를 상속
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동증가
    private Long bno;
    @Column(length = 500, nullable = false)
    private String title;
    @Column(length = 2000, nullable = false)
    private String content;
    @Column(length = 50, nullable = false)
    private String writer;

    //table에 손상을 주지않기위해 따로 메서드 생성..
    public void change(String title, String content){
        this.title=title;
        this.content=content;
    }
}
