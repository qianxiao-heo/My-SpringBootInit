package com.yanyu.init.model.dto.post;

import lombok.Data;

import java.io.Serializable;

@Data
public class PostDeleteRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
