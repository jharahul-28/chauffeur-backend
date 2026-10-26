package com.velvet.rest.common.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadBean {

	private String title;
    private String description;
    private String imagePath;
    private String imageFileName;
}
