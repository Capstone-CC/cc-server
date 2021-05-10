package com.cau.cc.model.network.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageUploadApiResponse {
    @ApiModelProperty(example = "https://caucampuscontact.s3.amazonaws.com/images/cauconnect_logo.png")
    private String url;
}
