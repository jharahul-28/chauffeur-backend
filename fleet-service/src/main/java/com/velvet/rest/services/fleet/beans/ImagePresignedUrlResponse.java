package com.velvet.rest.services.fleet.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Presigned URL response for image upload")
public class ImagePresignedUrlResponse {

	@ApiModelProperty(value = "Presigned S3 PUT URL to upload the image directly from the client")
	private String presignedUrl;

	@ApiModelProperty(value = "Final CloudFront URL at which the image will be accessible after upload")
	private String fileUrl;
}
