package com.velvet.rest.services.user.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.user.beans.ChauffeurBean;
import com.velvet.rest.services.user.beans.ChauffeurProfileBean;
import com.velvet.rest.services.user.entity.ChauffeurEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.services.ChauffeurService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * The Class ChauffeurController.
 */
@RestController
@Api(tags = "Chauffeur Services", description = "Manage Chauffeur related services.")
public class ChauffeurController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ChauffeurController.class);

	@Autowired
	private ChauffeurService chauffeurService;

	@Autowired
	private BaseMapper<?, ?> chauffeurMapper;

	//ADMIN PORTAL CREATES A CHAUFFEUR. NO SELF SIGNUP FOR CHAUFFEUR.
	@ApiOperation(value = "Create a new Chauffeur", nickname = "createChauffeur")
	@PostMapping("/chauffeurs")
	@ResponseStatus(HttpStatus.CREATED)
	public Long createChauffeur(@Valid @RequestBody ChauffeurBean chauffeurBean) {
		String phone = chauffeurBean.getCountryCode() != null
				? chauffeurBean.getCountryCode() + chauffeurBean.getPhone()
				: chauffeurBean.getPhone();
		ChauffeurEntity chauffeur = chauffeurService.createChauffeur(phone, CommonConstants.UserTypes.CHAUFFEUR);
		chauffeur = chauffeurService.save(getMapper().mapSrcToDest(chauffeurBean, chauffeur));
		return chauffeur.getId();
	}

	@ApiOperation(value = "List all Chauffeurs paginated", nickname = "findChauffeurListPaginated")
	@GetMapping("/chauffeurs")
	public PageAndSortResult<ChauffeurBean> findAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String sortBy,
			@RequestParam(required = false) SortDirection sortDirection, HttpServletRequest request) {
		logger.info("findAllPaginated :: pageNo: {}, pageSize: {}, sortBy: {}", pageNo, pageSize, sortBy);
		java.util.Map<String, SortDirection> sort = null;
		if (sortBy != null) {
			sort = buildSortMap(sortBy, sortDirection);
		}
		final PageAndSortResult<ChauffeurEntity> results = chauffeurService.findAllByPage(pageSize, pageNo, getSearchParams(request, ChauffeurEntity.class), sort);
		PageAndSortResult<ChauffeurBean> response = new PageAndSortResult<>();
		if (results != null && results.getData() != null) {
			response = prepareResponsePageData(results, ChauffeurBean.class);
		}
		return response;
	}

	//CHAUFFEUR APP INTERNAL PROFILE
	@ApiOperation(value = "Get Chauffeur's own profile", nickname = "getChauffeurProfile")
	@GetMapping("/chauffeurs/profile")
	public ResponseEntity<ChauffeurProfileBean> getChauffeurProfile() {
		ChauffeurEntity chauffeur = chauffeurService.getAuthEntity();
		if (chauffeur == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		ChauffeurProfileBean profile = getMapper().mapSrcToDest(chauffeur, ChauffeurProfileBean.newInstance());
		if (chauffeur.getMetadata() != null) {
			profile.setNumberOfRides(chauffeur.getMetadata().getNumberOfRides());
		}
		return ResponseEntity.ok(profile);
	}

	//BLOCK A CHAUFFEUR -> ADMIN PORTAL
	@ApiOperation(value = "Block Chauffeur", nickname = "blockChauffeur")
	@PostMapping("/chauffeurs/{id}/block")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void blockChauffeur(@PathVariable Long id) {
		ChauffeurEntity Chauffeur = chauffeurService.findById(id);
		if (Chauffeur == null) {
			throw new com.velvet.rest.common.exceptions.ValidationException(
					ErrorBean.withError(AppErrors.USER_NOT_FOUND, "Chauffeur not found with the provided Id", "id"));
		}
		Chauffeur.setBlocked(true);
		chauffeurService.save(Chauffeur);
		chauffeurService.logoutById(id);
	}

	//UNBLOCK A CHAUFFEUR -> ADMIN PORTAL
	@ApiOperation(value = "Unblock Chauffeur", nickname = "unblockChauffeur")
	@PostMapping("/chauffeurs/{id}/unblock")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unblockChauffeur(@PathVariable Long id) {
		ChauffeurEntity Chauffeur = chauffeurService.findById(id);
		if (Chauffeur == null) {
			throw new com.velvet.rest.common.exceptions.ValidationException(
					ErrorBean.withError(AppErrors.USER_NOT_FOUND, "Chauffeur not found with the provided Id", "id"));
		}
		Chauffeur.setBlocked(false);
		chauffeurService.save(Chauffeur);
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return chauffeurMapper;
	}
}
