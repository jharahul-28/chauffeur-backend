package com.velvet.rest.services.fleet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.services.common.upload.UploadService;
import com.velvet.rest.services.fleet.beans.ImagePresignedUrlResponse;
import com.velvet.rest.services.fleet.constants.FleetConstants;
import com.velvet.rest.services.fleet.errors.AppErrors;
import com.velvet.rest.services.fleet.repository.VehicleCategoryRepository;
import com.velvet.rest.services.fleet.repository.VehicleRepository;

@Service
public class FleetImageUploadService {

	@Autowired
	private UploadService uploadService;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private VehicleCategoryRepository vehicleCategoryRepository;

	public ImagePresignedUrlResponse getPresignedUrlForVehicle(Long id, String fileName) {
		vehicleRepository.findById(id)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.VEHICLE_NOT_FOUND, "Vehicle not found", "id")));
		String subdirectory = ParamsUtils.getParam(FleetConstants.S3_FLEET_VEHICLES_SUBDIRECTORY);
		return buildPresignedUrlResponse(fileName, subdirectory, "vehicle");
	}

	public ImagePresignedUrlResponse getPresignedUrlForCategory(Long id, String fileName) {
		vehicleCategoryRepository.findById(id)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.CATEGORY_NOT_FOUND, "Category not found", "id")));
		String subdirectory = ParamsUtils.getParam(FleetConstants.S3_FLEET_CATEGORIES_SUBDIRECTORY);
		return buildPresignedUrlResponse(fileName, subdirectory, "category");
	}

	private ImagePresignedUrlResponse buildPresignedUrlResponse(String fileName, String subdirectory, String prefix) {
		String bucketName = ParamsUtils.getParam(FleetConstants.S3_FILES_BUCKET);
		String fileExt = uploadService.findFileExt(fileName);
		String objectKey = String.format("%s/%s-%s.%s", subdirectory, prefix, System.currentTimeMillis(), fileExt);
		String presignedUrl = uploadService.presignedPutUrl(bucketName, objectKey, 15);
		String cloudfrontUrl = ParamsUtils.getParam(FleetConstants.CLOUDFRONT_URL);
		String fileUrl = String.format("%s/%s", cloudfrontUrl, objectKey);
		return new ImagePresignedUrlResponse(presignedUrl, fileUrl);
	}
}
