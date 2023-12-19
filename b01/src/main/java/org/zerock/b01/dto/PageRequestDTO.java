package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    private int page=1;
    private int size = 0;
    private String type;
    private String keyword;

    public String[] getTypes(){
        if(type == null || type.isEmpty()){
            return null;
        }
        return type.split(""); //문자열 하나하나씩 비교 search시 tcw..
    }

    public Pageable getPageable(String ... props) { //가변인자
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
        //page -1은 1페이지가 실제로는 2페이지이므로 -1을 해서 1페이지 입력하면 진짜 1페이지 볼 수 있도록 함
    }

    //검색 조건과 페이징 조건 등을 문자열로 구성
    private String link;
    public String getLink(){
        if(link == null){
            StringBuilder builder = new StringBuilder();
            builder.append("page=" + this.page);
            builder.append("&size=" + this.page);
            
            //forExample) page=1&size=10

            if(type != null && type.length() >0){
                builder.append("&type=" + type);
            }

            if(keyword != null){
                try { //한글입력하면 깨지므로 urlencoder사용
                    builder.append("&keyword=" + URLEncoder.encode(keyword,"UTF-8"));
                }catch (UnsupportedEncodingException e){

                }
                link = builder.toString();
            }
        }
        return link;
    }
}

