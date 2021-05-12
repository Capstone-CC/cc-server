package com.cau.cc.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class Pagination {

    private Integer totalPages; // 총 페이지 수
    private Long totalElements; // 총 엘리먼트 수 (유저수)
    private Integer currentPage; // 현재 페이지는 몇번째 인지
    private Integer currentElements; // 현재 페이지에 몇개 데이터 들어있냐
}
