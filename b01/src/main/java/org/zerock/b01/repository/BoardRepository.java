package org.zerock.b01.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardListReplyCountDTO;
import org.zerock.b01.repository.search.BoardSearch;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch { //MyBatis에서 mapper interface 역할..
//<table(entity) , Id(entity pk 속성)>
//기본키 bno외에 다른 속성으로 select 하기 위해서 등록함
    List<Board> findBoardByWriter(String writer);
    //entity 이름은 생략해도 됨
    List<Board> findByWriterAndTitle(String writer, String title);

    List<Board> findByWriterLike(String writer);

    @Query("select b from Board b where b.writer = :writer")
    List<Board> findByWriter2(@Param("writer") String writer);

    @Query("select b from Board b where b.title like  %:title% order by b.bno desc")
    List<Board> findByTitle(@Param("title") String title);

    @Query("select b from Board b where b.title like concat('%', :keyword, '%') ")
    Page<Board> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    //nativeQuery true : value값을 query 그대로 날림
    @Query(value = "select * from board where title like %:title% order by bno desc", nativeQuery = true)
    List<Board> findByTitle2(@Param("title") String title);





}
