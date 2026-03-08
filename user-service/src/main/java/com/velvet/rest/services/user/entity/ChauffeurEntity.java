package com.velvet.rest.services.user.entity;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

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

	@Column(name = "documents", columnDefinition = "jsonb")
	private String documents;

	@Column(name = "rating")
	private Double rating;

	@Column(name = "working_hours")
	private Double workingHours;

	@Type(type = "jsonb")
	@Column(name = "metadata", columnDefinition = "jsonb")
	private ChauffeurMetadata metadata = new ChauffeurMetadata();

	public static ChauffeurEntity newInstance() {
		return new ChauffeurEntity();
	}
}
