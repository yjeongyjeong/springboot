package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

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
    @OneToMany(mappedBy = "board",
        cascade = {CascadeType.ALL}, fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    //boardImage에서도 참조할 수 있도록(양방향참조) 연관관계 부여
    //board엔티티 객체의 모든 상태변화에 같이 변경되도록 casaadeType all
    //상위(Board)를 삭제하면 하위도 삭제되도록 함(orphanRemoval) 만약 저 코드가 없다면 board_bno=null상태로 됨
    @Builder.Default
    @BatchSize(size = 20)
    private Set<BoardImage> imageSet = new HashSet<>();
    
    public void addImage(String uuid, String fileName){
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size()) //전체 개수
                .build();
        imageSet.add(boardImage);
    } // end addImage
    
    //이미지가 삭제되지않았으나 null로 되어서 마치 삭제된 것처럼
    public void clearImages(){
        imageSet.forEach(boardImage -> boardImage.changeBoard(null));
        this.imageSet.clear();
    }
}
