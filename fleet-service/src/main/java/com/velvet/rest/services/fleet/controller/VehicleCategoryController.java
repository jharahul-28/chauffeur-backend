package com.velvet.rest.services.fleet.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.fleet.beans.VehicleCategoryBean;
import com.velvet.rest.services.fleet.entity.VehicleCategoryEntity;
import com.velvet.rest.services.fleet.services.VehicleCategoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * The Class VehicleCategoryController.
 */
@RestController
@Api(tags = "Vehicle Category Services", description = "Manage vehicle categories.")
public class VehicleCategoryController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(VehicleCategoryController.class);

	@Autowired
	private VehicleCategoryService vehicleCategoryService;

	@Autowired
	private BaseMapper<?, ?> categoryMapper;

	@ApiOperation(value = "Get all active vehicle categories", nickname = "getAllVehicleCategories")
	@GetMapping("/fleet/categories")
	public ResponseEntity<List<VehicleCategoryBean>> getAllCategories() {
		List<VehicleCategoryEntity> categories = vehicleCategoryService.getAllCategories();
		return ResponseEntity.ok(getMapper().mapAsList(categories, VehicleCategoryBean.class));
	}

	@ApiOperation(value = "Get vehicle category by ID", nickname = "getVehicleCategoryById")
	@GetMapping("/fleet/categories/{id}")
	public ResponseEntity<VehicleCategoryBean> getCategoryById(@PathVariable Long id) {
		VehicleCategoryEntity category = vehicleCategoryService.getById(id);
		if (category == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(getMapper().mapSrcToDest(category, VehicleCategoryBean.newInstance()));
	}

	@ApiOperation(value = "Create a new vehicle category", nickname = "createVehicleCategory")
	@PostMapping("/fleet/categories")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<VehicleCategoryBean> createCategory(@Valid @RequestBody VehicleCategoryEntity category) {
		VehicleCategoryEntity created = vehicleCategoryService.createCategory(category);
		return ResponseEntity.status(HttpStatus.CREATED).body(getMapper().mapSrcToDest(created, VehicleCategoryBean.newInstance()));
	}

	@ApiOperation(value = "Update base price of a vehicle category", nickname = "updateVehicleCategoryBasePrice")
	@PatchMapping("/fleet/categories/{id}/base-price")
	public ResponseEntity<VehicleCategoryBean> updateBasePrice(@PathVariable Long id, @RequestParam BigDecimal basePrice) {
		return ResponseEntity.ok(getMapper().mapSrcToDest(vehicleCategoryService.updateBasePrice(id, basePrice), VehicleCategoryBean.newInstance()));
	}

	@ApiOperation(value = "Toggle active status of a vehicle category", nickname = "toggleVehicleCategoryStatus")
	@PatchMapping("/fleet/categories/{id}/toggle-status")
	public ResponseEntity<VehicleCategoryBean> toggleCategoryStatus(@PathVariable Long id) {
		return ResponseEntity.ok(getMapper().mapSrcToDest(vehicleCategoryService.toggleCategoryStatus(id), VehicleCategoryBean.newInstance()));
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return categoryMapper;
	}
}
