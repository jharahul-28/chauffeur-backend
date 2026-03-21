package com.velvet.rest.services.user.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import com.velvet.rest.services.user.beans.ChauffeurAddress;
import com.velvet.rest.services.user.beans.ChauffeurBankDetails;
import com.velvet.rest.services.user.beans.ChauffeurBean;
import com.velvet.rest.services.user.beans.ChauffeurDocuments;
import com.velvet.rest.services.user.beans.ChauffeurMetadata;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * ChauffeurEntity related information
 */
@Entity
@Table(name = ChauffeurConstants.CHAUFFEUR_TABLE_NAME)
@DynamicUpdate
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class ChauffeurEntity extends UserEntity implements Serializable, ChauffeurConstants {

	private static final long serialVersionUID = -4821931095490774310L;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "license_number")
	private String licenseNumber;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	@Column(name = "rating")
	private Double rating;

	@Enumerated(EnumType.STRING)
	@Column(name = "marital_status")
	private MaritalStatus maritalStatus;

	@Column(name = "dl_expiry_date")
	private LocalDate dlExpiryDate;

	@Column(name = "has_criminal_record")
	private Boolean hasCriminalRecord;

	@Column(name = "total_driving_experience")
	private Integer totalDrivingExperience;

	@Column(name = "luxury_driving_experience")
	private Integer luxuryDrivingExperience;

	@Type(type = "jsonb")
	@Column(name = "documents", columnDefinition = "jsonb")
	private ChauffeurDocuments documents;

	@Type(type = "jsonb")
	@Column(name = "metadata", columnDefinition = "jsonb")
	private ChauffeurMetadata metadata = new ChauffeurMetadata();

	@Type(type = "jsonb")
	@Column(name = "address", columnDefinition = "jsonb")
	private ChauffeurAddress address;

	@Type(type = "jsonb")
	@Column(name = "bank_details", columnDefinition = "jsonb")
	private ChauffeurBankDetails bankDetails;

	public static ChauffeurEntity newInstance() {
		return new ChauffeurEntity();
	}

	public ChauffeurEntity buildChauffeur(ChauffeurBean bean) {
		this.setBlocked(false);
		this.setIsDeleted(false);
		this.setEmail(bean.getEmail());
		this.setGender(bean.getGender());
		this.setFirstName(bean.getFirstName());
		this.setLastName(bean.getLastName());
		this.setLicenseNumber(bean.getLicenseNumber());
		this.setDateOfBirth(bean.getDateOfBirth());
		this.setDocuments(bean.getDocuments());
		this.setRating(bean.getRating());
		this.setMaritalStatus(bean.getMaritalStatus());
		this.setDlExpiryDate(bean.getDlExpiryDate());
		this.setHasCriminalRecord(bean.getHasCriminalRecord());
		this.setTotalDrivingExperience(bean.getTotalDrivingExperience());
		this.setLuxuryDrivingExperience(bean.getLuxuryDrivingExperience());
		this.setAddress(bean.getAddress());
		this.setBankDetails(bean.getBankDetails());
		if (bean.getMetadata() != null) {
			if (bean.getMetadata().getWorkingHours() != null) {
				this.getMetadata().setWorkingHours(bean.getMetadata().getWorkingHours());
			}
			if (bean.getMetadata().getLuxuryBrandsDriven() != null) {
				this.getMetadata().setLuxuryBrandsDriven(bean.getMetadata().getLuxuryBrandsDriven());
			}
		}
		return this;
	}
}
