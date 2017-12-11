SET FOREIGN_KEY_CHECKS=0;

truncate table atd_form_instance;
truncate table atd_patient_atd;
truncate table atd_patient_state;
truncate table atd_session;
truncate table chica_error;
truncate table chica_family;
truncate table chica_patient_family;
truncate table chica_response;
truncate table chica_statistics;
truncate table chica1_appointments;
truncate table chica1_patient;
truncate table chica1_patient_obsv;
truncate table encounter;
truncate table hl7_in_queue;
truncate table obs;
truncate table patient;
truncate table patient_identifier;
delete from person where person_id not in (select user_id from users where system_id in ('admin') or username in ('chicauser1', '.Other'));
delete from person_address where person_id not in (select user_id from users where system_id in ('admin') or username in ('chicauser1', '.Other'));
delete from person_attribute where person_id not in (select user_id from users where system_id in ('admin') or username in ('chicauser1', '.Other'));
delete from person_name where person_id  not in (select user_id from users where system_id in ('admin') or username in ('chicauser1', '.Other'));
truncate table sockethl7listener_patient_message;
delete from user_role where user_id  not in (select user_id from users where system_id in ('admin') or username in ('chicauser1', '.Other'));
delete from users where user_id not in (select user_id from users where system_id in ('admin') or username in ('chicauser1', '.Other'));


SET FOREIGN_KEY_CHECKS=1;