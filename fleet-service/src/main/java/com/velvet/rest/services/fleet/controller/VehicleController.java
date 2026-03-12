package com.velvet.rest.services.fleet.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.services.fleet.entity.VehicleEntity;
import com.velvet.rest.services.fleet.errors.AppErrors;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.fleet.beans.VehicleBean;
import com.velvet.rest.services.fleet.entity.VehicleEntity;
import com.velvet.rest.services.fleet.services.VehicleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * The Class VehicleController.
 */
@RestController
@Api(tags = "Vehicle Services", description = "Manage fleet vehicles.")
public class VehicleController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private BaseMapper<?, ?> vehicleMapper;

	@ApiOperation(value = "Create a new vehicle", nickname = "createVehicle")
	@PostMapping("/fleet/vehicles")
	public ResponseEntity<VehicleBean> createVehicle(@Valid @RequestBody VehicleBean vehicleBean) {
		VehicleEntity vehicle = vehicleService.createVehicle(vehicleBean);
		return ResponseEntity.status(HttpStatus.CREATED).body(getMapper().mapSrcToDest(vehicle, VehicleBean.newInstance()));
	}

	@ApiOperation(value = "Get vehicle by ID", nickname = "getVehicleById")
	@GetMapping("/fleet/vehicles/{id}")
	public VehicleBean getVehicleById(@PathVariable Long id) {
		VehicleEntity vehicle = vehicleService.getById(id);
		if (vehicle == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.VEHICLE_NOT_FOUND, "Vehicle not found", "id"));
		}
		return getMapper().mapSrcToDest(vehicle, VehicleBean.newInstance());
	}

	@ApiOperation(value = "Get all vehicles paginated", nickname = "getAllVehicles")
	@GetMapping("/fleet/vehicles")
	public PageAndSortResult<VehicleBean> getAllVehicles(@RequestParam(defaultValue = "1") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String sortBy,
			@RequestParam(required = false) SortDirection sortDirection, HttpServletRequest request) {
		java.util.Map<String, SortDirection> sort = null;
		if (sortBy != null) {
			sort = buildSortMap(sortBy, sortDirection);
		}
		final PageAndSortResult<VehicleEntity> results = vehicleService.findAllByPage(pageSize, pageNo,
				getSearchParams(request, VehicleEntity.class), sort);
		PageAndSortResult<VehicleBean> response = new PageAndSortResult<>();
		if (results != null && results.getData() != null) {
			response = prepareResponsePageData(results, VehicleBean.class);
		}
		return response;
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return vehicleMapper;
	}
}
