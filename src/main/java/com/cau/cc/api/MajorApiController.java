package com.cau.cc.api;

import com.cau.cc.ifs.CrudInterface;
import com.cau.cc.model.entity.MajorEnum;
import com.cau.cc.model.network.Header;
import com.cau.cc.model.network.response.MajorApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

@RestController
@Api(tags = "major 리스트")
@RequestMapping("/major")
public class MajorApiController  {

    @GetMapping("/list")
    @ApiOperation(value = "major list 반환 ",notes = "major list")
    public Header<MajorApiResponse> getMajors(){
        MajorApiResponse majorApiResponse = new MajorApiResponse();

        for(MajorEnum m : MajorEnum.values()){
            majorApiResponse.getMajorEnums().add(m);
        }
        return Header.OK(majorApiResponse);
    }
}
