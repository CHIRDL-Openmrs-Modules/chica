SET FOREIGN_KEY_CHECKS=0;

delete from obs where obs_Id  in (
select openmrs_obs_id from chica1_patient_obsv where openmrs_obs_id is not null);

delete from encounter where encounter_id  in (
select openmrs_encounter_id from chica1_appointments where openmrs_encounter_id is not null);

delete from patient_identifier where patient_id  in (
select openmrs_patient_id from chica1_patient where openmrs_patient_id is not null);

delete from patient where patient_id  in (
select openmrs_patient_id from chica1_patient where openmrs_patient_id is not null);

delete from person_address where person_id  in (
select openmrs_patient_id from chica1_patient where openmrs_patient_id is not null)
and person_id not in (select user_id from users);

delete from person_attribute where person_id  in (
select openmrs_patient_id from chica1_patient where openmrs_patient_id is not null)
and person_id not in (select user_id from users);

delete from person_name where person_id  in (
select openmrs_patient_id from chica1_patient where openmrs_patient_id is not null)
and person_id not in (select user_id from users);

delete from person where person_id  in (
select openmrs_patient_id from chica1_patient where openmrs_patient_id is not null)
and person_id not in (select user_id from users);

 update chica1_patient_obsv 
set openmrs_obs_id=null
where openmrs_obs_id is not null

 update chica1_appointments
set openmrs_encounter_id=null
where openmrs_encounter_id is not null

 update chica1_patient 
set openmrs_patient_id=null
where openmrs_patient_id is not null

/*
There are no statements to remove providers from the user_role and users tables for the chica1 patients
because the providers could be referenced by clinic message encounters.
*/

SET FOREIGN_KEY_CHECKS=1;

