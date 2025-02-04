package com.example.Rider.dto.request.common;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomPageRequest {
    private int pageNo;
    private int size;
    private String keyword;
}
