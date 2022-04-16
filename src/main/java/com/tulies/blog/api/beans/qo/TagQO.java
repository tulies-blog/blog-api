package com.tulies.blog.api.beans.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class TagQO {
    @ApiModelProperty(hidden=true)
    private Integer id;
    private String name;
    private Integer status;

}
