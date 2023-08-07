package in.globalit.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
@Table(name = "Citizen_Dtls_Table")
public class CitizenApplicationEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long caseNum;
	private String city;
	private LocalDate dob;
	private String fullname;
	private String gender;
	private String email;
	private Long mobNo;
	private String houseNum;
	private String ssn;
	private String state;
	private String planName;
	
	@CreationTimestamp
	private LocalDate creationDate;
	
	//@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "createdByUserId",referencedColumnName = "user_id")
	private UserEntity user;
	
	
	//creation data
	//created by foreign key from user table

}
