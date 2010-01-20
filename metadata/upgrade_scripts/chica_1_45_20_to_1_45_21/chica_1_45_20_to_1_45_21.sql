update chica_hl7_export_status set name = 'pending_export',
description = 'Inserted in table and pending export',
date_created = now()
where hl7_export_status_id = 1;

update chica_hl7_export_status set name = 'hl7_sent',
description = 'Hl7 constructed and sent',
date_created = now()
where hl7_export_status_id = 2;

update chica_hl7_export_status set name = 'ACK_received',
description = 'ACK received from sent message',
date_created = now()
where hl7_export_status_id = 3;

update chica_hl7_export_status set name = 'ACK_not_received',
description = 'ACK not received from sent message',
date_created = now()
where hl7_export_status_id = 4;

update chica_hl7_export_status set description = 'no_obs',
description = 'No observations to send',
date_created = now()
where hl7_export_status_id = 100;

insert into chica_hl7_export_status
(hl7_export_status_id, name, description, date_created)
values (200, 'concept_map_location_unknown',
'Concept map xml location not found in location_tag_attribute_table', now());

INSERT INTO `chica_hl7_export_status` 
VALUES ('300', 'Image_not_found', 'Tiff image not found', now());
INSERT INTO `chica_hl7_export_status` 
VALUES ('400', 'hl7_config_file_not_found', 'HL7 configuration file not mound', now());
INSERT INTO `chica_hl7_export_status` 
VALUES ('500', 'no_hl7_config_properties', 'Unable to extract properties from hl7 config file', now());
INSERT INTO `chica_hl7_export_status` 
VALUES ('600', 'XML_parsing_error', 'Concept map file not found or unable to parse xml', now());
INSERT INTO `chica_hl7_export_status` 
VALUES ('999', 'Unknown', 'Status unknown', now()); 
INSERT INTO `chica_hl7_export_status` 
VALUES ('5', 'do_not_send', 'Do not send tiff images in hl7', now());            


/*concept*/
/*add all new rmrs/chica concepts*/
INSERT INTO `concept` (retired, description, datatype_id, class_id, 
 is_set, creator, date_created)  VALUES ( '0', 'PEDS CL DATA CHICA',
 '4', (select concept_class_id from concept_class where name like 'CHICA'), '1', '1', now());	
 
 update concept_name cn, concept c, concept_class cl
set cn.name = 'MEDICAL RECORD FILE OBSERVATIONS CHICA'
where cn.concept_id = c.concept_id
and cn.name like 'MEDICAL RECORD FILE OBSERVATIONS'
and cl.concept_class_id = c.class_id
and cl.name = 'CHICA';
 
INSERT INTO `concept_name`
(concept_id, name, locale, creator, date_created, voided)
VALUES ((select concept_id from concept where description like 'PEDS CL DATA CHICA'), 
'PEDS CL DATA CHICA', 'en', '1', now(),'0');

INSERT INTO `concept` (retired, description, datatype_id, class_id, 
 is_set, creator, date_created)  VALUES ( '0', 'OBS SET PEDS PRE-SCREENING',
 '4', (select concept_class_id from concept_class where name like 'RMRS'), '1', '1', now());
 
 INSERT INTO `concept` (retired, description, datatype_id, class_id, 
 is_set, creator, date_created)  VALUES ( '0', 'OBS SET PEDS PRE-SCREENING CHICA',
 '4', (select concept_class_id from concept_class where name like 'CHICA'), '1', '1', now());

INSERT INTO `concept` (retired, description, datatype_id, class_id, 
 is_set, creator, date_created)  VALUES ( '0', 'OBS SET PEDS PHYSICIAN ENCOUNTER',
 '4', (select concept_class_id from concept_class where name like 'RMRS'), '1', '1', now());
 
INSERT INTO `concept` (retired, description, datatype_id, class_id, 
 is_set, creator, date_created)  VALUES ( '0', 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA',
 '4', (select concept_class_id from concept_class where name like 'CHICA'), '1', '1', now());
 
INSERT INTO `concept` (retired, description, datatype_id, class_id, 
 is_set, creator, date_created)  VALUES ( '0', 'PAIN QUALITATIVE CAREWEB',
 '2', (select concept_class_id from concept_class where name like 'CHICA'), '0', '1', now());
 
 INSERT INTO `concept` (retired, description, datatype_id, class_id, 
 is_set, creator, date_created)  VALUES ( '0', 'MEDICATION ALLERGIES CAREWEB',
 '2', (select concept_class_id from concept_class where name like 'CHICA'), '0', '1', now());

 INSERT INTO `concept` (retired, description, datatype_id, class_id, 
 is_set, creator, date_created)  VALUES ( '0', 'MACROLIDES',
 '2', (select concept_class_id from concept_class where name like 'RMRS'), '0', '1', now());
 
 
 INSERT INTO `concept_name`
(concept_id, name, locale, creator, date_created, voided)
VALUES ((select concept_id from concept where description like 'OBS SET PEDS PRE-SCREENING'), 
'OBS SET PEDS PRE-SCREENING', 'en', '1', now(),'0');

INSERT INTO `concept_name`
(concept_id, name, locale, creator, date_created, voided)
VALUES ((select concept_id from concept where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER'), 
'OBS SET PEDS PHYSICIAN ENCOUNTER', 'en', '1', now(),'0');

INSERT INTO `concept_name`
(concept_id, name, locale, creator, date_created, voided)
VALUES ((select concept_id from concept where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'), 
'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA', 'en', '1', now(),'0');

INSERT INTO `concept_name`
(concept_id, name, locale, creator, date_created, voided)
VALUES ((select concept_id from concept where description like 'OBS SET PEDS PRE-SCREENING CHICA'), 
'OBS SET PEDS PRE-SCREENING CHICA', 'en', '1', now(),'0');

INSERT INTO `concept_name`
(concept_id, name, locale, creator, date_created, voided)
VALUES ((select concept_id from concept where description like 'PAIN QUALITATIVE CAREWEB'), 
'PAIN QUALITATIVE CAREWEB', 'en', '1', now(),'0');

INSERT INTO `concept_name`
(concept_id, name, locale, creator, date_created, voided)
VALUES ((select concept_id from concept where description like 'MEDICATION ALLERGIES CAREWEB'), 
'MEDICATION ALLERGIES CAREWEB', 'en', '1', now(),'0');
 
 
INSERT INTO `concept_name`
(concept_id, name, locale, creator, date_created, voided)
VALUES ((select concept_id from concept where description like 'MACROLIDES'), 
'MACROLIDES', 'en', '1', now(),'0');


/*names remove short names from some existing concepts 
 because of openmrs handling of short names*/
update concept set short_name = null where concept_id = (select distinct concept_id from concept_name
  where name like 'MEDICAL RECORD FILE OBSERVATIONS CHICA');
update concept set short_name = null where concept_id = (select distinct concept_id from concept_name
  where name like 'PEDS CL DATA CHICA');
  /* add descriptions for concepts*/
update concept set description = 'PEDS CL DATA CHICA'
   where concept_id = (select distinct concept_id from concept_name
   where name like 'PEDS CL DATA CHICA');
update concept set description = 'MEDICAL RECORD FILE OBSERVATIONS CHICA'
   where concept_id = (select distinct concept_id from concept_name
   where name like 'MEDICAL RECORD FILE OBSERVATIONS CHICA');


/*answers for careweb concepts*/ 
INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
 VALUES ((select concept_id from concept_name where name like 'PAIN QUALITATIVE CAREWEB'),
 (select concept_id from concept_name where name like '0'),
 null, '1', now());
 
 
 INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES'),
 (select concept_id from concept_name where name like 'MACROLIDES'),
 null, '1', now());
 
 INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES'),
 (select concept_id from concept_name where name like 'PENICILLINS'),
 null, '1', now());
 
 INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES'),
 (select concept_id from concept_name where name like 'CEPHALOSPORINS'),
 null, '1', now());
 
 INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES'),
 (select concept_id from concept_name where name like 'SULFONAMIDES'),
 null, '1', now());
 
  INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES'),
 (select concept_id from concept_name where name like 'drug allergy other'),
 null, '1', now());
 
 
INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES CAREWEB'),
 (select concept_id from concept_name where name like 'no known allergies'),
 null, '1', now());
 
 INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES CAREWEB'),
 (select concept_id from concept_name where name like 'PENICILLINS'),
 null, '1', now());
 
  INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES CAREWEB'),
 (select concept_id from concept_name where name like 'CEPHALOSPORINS'),
 null, '1', now());
 
  INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES CAREWEB'),
 (select concept_id from concept_name where name like 'SULFONAMIDES'),
 null, '1', now());
 
 
 INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES CAREWEB'),
 (select concept_id from concept_name where name like 'drug allergy other'),
 null, '1', now());
 
 INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES CAREWEB'),
 (select concept_id from concept_name where name like 'MACROLIDES'),
 null, '1', now());
 
 
 INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'ALLERGY HX'),
 (select concept_id from concept_name where name like 'MACROLIDES'),
 null, '1', now());
 
 
 
 INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'MACROLIDES'), 'MACROLIDES', '', 'en',
(select concept_name_id from concept_name where name like 'MACROLIDES'));

INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'MEDICAL RECORD FILE OBSERVATIONS CHICA'), 'FILE', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'MEDICAL RECORD FILE OBSERVATIONS CHICA'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'MEDICAL RECORD FILE OBSERVATIONS CHICA'), 'MEDICAL', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'MEDICAL RECORD FILE OBSERVATIONS CHICA'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'MEDICAL RECORD FILE OBSERVATIONS CHICA'), 'RECORD', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'MEDICAL RECORD FILE OBSERVATIONS CHICA'));
  
 INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'MEDICAL RECORD FILE OBSERVATIONS CHICA'), 'CHICA', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'MEDICAL RECORD FILE OBSERVATIONS CHICA'));

INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'PEDS CL DATA CHICA'), 'PEDS', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'PEDS CL DATA CHICA'));


INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'PEDS CL DATA CHICA'), 'CL', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'PEDS CL DATA CHICA'));
  
INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'PEDS CL DATA CHICA'), 'DATA', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'PEDS CL DATA CHICA'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'PEDS CL DATA CHICA'), 'CHICA', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'PEDS CL DATA CHICA'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PRE-SCREENING'), 'OBS', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PRE-SCREENING'));

INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PRE-SCREENING'), 'PEDS', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PRE-SCREENING'));

INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PRE-SCREENING'), 'PRE-SCREENING', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PRE-SCREENING'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER'), 'OBS', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PHYSICIAN ENCOUNTER'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER'), 'SET', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PHYSICIAN ENCOUNTER'));
  
   INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER'), 'PEDS', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PHYSICIAN ENCOUNTER'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER'), 'PHYSICIAN', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PHYSICIAN ENCOUNTER'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER'), 'ENCOUNTER', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PHYSICIAN ENCOUNTER'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'), 'OBS', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'), 'SET', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'));
  
   INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'), 'PEDS', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PHYSICIAN ENCOUNTER'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'), 'PHYSICIAN', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'), 'ENCOUNTER', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'));

INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PRE-SCREENING CHICA'), 'OBS', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PRE-SCREENING CHICA'));

INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PRE-SCREENING CHICA'), 'PEDS', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PRE-SCREENING'));

INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'OBS SET PEDS PRE-SCREENING CHICA'), 'PRE-SCREENING', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'OBS SET PEDS PRE-SCREENING CHICA'));

INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'PAIN QUALITATIVE CAREWEB'), 'PAIN', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'PAIN QUALITATIVE CAREWEB'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'PAIN QUALITATIVE CAREWEB'), 'QUALITATIVE', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'PAIN QUALITATIVE CAREWEB'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'PAIN QUALITATIVE CAREWEB'), 'CAREWEB', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'PAIN QUALITATIVE CAREWEB'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'MEDICATION ALLERGIES CAREWEB'), 'MEDICATION', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'MEDICATION ALLERGIES CAREWEB'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'MEDICATION ALLERGIES CAREWEB'), 'ALLERGIES', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'MEDICATION ALLERGIES CAREWEB'));
  
  INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'MEDICATION ALLERGIES CAREWEB'), 'CAREWEB', '', 'en',
 (select concept_name_id from concept_name 
  where name like 'MEDICATION ALLERGIES CAREWEB'));

truncate concept_set;
INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'BMI CHICA'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'BMICENTILE'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'DIASTOLIC_BP'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'HC'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'HCCENTILE'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'HEIGHT'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());


INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'HTCENTILE'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'MEDICATION ALLERGIES'), 
(select distinct concept_id from concept_name where
name like 'MEDICAL RECORD FILE OBSERVATIONS CHICA'), '1', '1', now());


INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'PAIN QUANTITATIVE'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'PULSE CHICA'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'RR CHICA'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());


INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'SYSTOLIC_BP'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'TEMPERATURE CHICA'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'VISIONL'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'VISIONR'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'WEIGHT'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'WTCENTILE'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());


/* OBS SET PEDS PHYSICIAN ENCOUNTER is both a set and an obs*/
INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name 
  like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'), 
(select distinct concept_id from concept_name where
name like 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name 
  like 'OBS SET PEDS PRE-SCREENING CHICA'), 
(select distinct concept_id from concept_name where
name like 'OBS SET PEDS PRE-SCREENING CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'PAIN QUALITATIVE CAREWEB'), 
(select distinct concept_id from concept_name where
name like 'PEDS CL DATA CHICA'), '1', '1', now());

INSERT INTO `concept_set` VALUES ((
select distinct concept_id from concept_name where name like 'MEDICATION ALLERGIES CAREWEB'), 
(select distinct concept_id from concept_name where
name like 'MEDICAL RECORD FILE OBSERVATIONS CHICA'), '1', '1', now());


truncate concept_map;

INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '6493', 'HEIGHT PEDS', '1', now(), 
 (select concept_id from concept_name where name like 'HEIGHT PEDS'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '1280', 'WEIGHT PEDS', '1', now(), 
 (select concept_id from concept_name where name like 'WEIGHT PEDS'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '1283', 'HEIGHT %ILE', '1', now(), 
 (select concept_id from concept_name where name like 'HEIGHT %ILE'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '1281', 'WT %ILE', '1', now(), 
 (select concept_id from concept_name where name like 'WT %ILE'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '26764', 'BMI', '1', now(), 
 (select concept_id from concept_name where name like 'BMI'));
 
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '65', 'TEMP', '1', now(), 
 (select concept_id from concept_name where name like 'TEMP'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '1284', 'HEAD CIRCUMF', '1', now(), 
 (select concept_id from concept_name where name like 'HEAD CIRCUMF'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '68', 'SYS BP SITTING', '1', now(), 
 (select concept_id from concept_name where name like 'SYS BP SITTING'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '69', 'DIAS BP SITTING', '1', now(), 
 (select concept_id from concept_name where name like 'DIAS BP SITTING'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '66', 'PULSE', '1', now(), 
 (select concept_id from concept_name where name like 'PULSE'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '1285', 'HEAD CIRC %ILE', '1', now(), 
 (select concept_id from concept_name where name like 'HEAD CIRC %ILE'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '67', 'RR', '1', now(), 
 (select concept_id from concept_name where name like 'RR'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '448', 'ACUITY L FAR 20/', '1', now(), 
 (select concept_id from concept_name where name like 'ACUITY L FAR 20/'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '446', 'ACUITY R FAR 20/', '1', now(), 
 (select concept_id from concept_name where name like 'ACUITY R FAR 20/'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '36404', 'OBS SET PEDS PRE-SCREENING', '1', now(), 
 (select concept_id from concept_name where name like 'OBS SET PEDS PRE-SCREENING'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '27159', 'Pain Scale (0-10)', '1', now(), 
 (select concept_id from concept_name where name like 'Pain Scale (0-10)'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '36405', 'OBS SET PEDS PHYSICIAN ENCOUNTER', '1', now(), 
 (select concept_id from concept_name where name like 'OBS SET PEDS PHYSICIAN ENCOUNTER'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '8995', 'PEDS CL DATA', '1', now(), 
 (select concept_id from concept_name where name like 'PEDS CL DATA'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '8995', 'PEDS CL DATA', '1', now(), 
 (select concept_id from concept_name where name like 'PEDS CL DATA'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '22391', 'MEDICAL RECORD FILE OBSERVATIONS', '1', now(), 
 (select concept_id from concept_name where name like 'MEDICAL RECORD FILE OBSERVATIONS'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '7717', 'ALLERGY HX', '1', now(), 
 (select concept_id from concept_name where name like 'ALLERGY HX'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '16404', 'no known allergies', '1', now(), 
 (select concept_id from concept_name where name like 'no known allergies'));
 

INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '18049', 'MACROLIDES', '1', now(), 
 (select concept_id from concept_name where name like 'MACROLIDES'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '93', 'PENICILLINS', '1', now(), 
 (select concept_id from concept_name where name like 'PENICILLINS'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '1128', 'CEPHALOSPORINS', '1', now(), 
 (select concept_id from concept_name where name like 'CEPHALOSPORINS'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '568', 'SULFONAMIDES', '1', now(), 
 (select concept_id from concept_name where name like 'SULFONAMIDES'));
 
 INSERT INTO `concept_map` 
(source, source_code, comment, creator, date_created, concept_id)
VALUES ( (select concept_source_id from concept_source where name like 'RMRS') ,
 '4455', 'drug allergy other', '1', now(), 
 (select concept_id from concept_name where name like 'drug allergy other'));

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG4'),
'C:\\chica\\conceptMaps\\PEREG4\\POC\\POCConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'POCConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);


insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG3'),
'C:\\chica\\conceptMaps\\PEREG3\\POC\\POCConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'POCConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG2'),
'C:\\chica\\conceptMaps\\PEREG2\\POC\\POCConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'POCConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG1'),
'C:\\chica\\conceptMaps\\PEREG1\\POC\\POCConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'POCConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'Other - PECAR'),
'C:\\chica\\conceptMaps\\Other - PECAR\\POC\\POCConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'POCConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

/*PCC POC*/

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PED1'),
'C:\\chica\\conceptMaps\\PED1\\POC\\POCConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'POCConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PED2'),
'C:\\chica\\conceptMaps\\PED2\\POC\\POCConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'POCConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'Other - PCC'),
'C:\\chica\\conceptMaps\\Other - PCC\\POC\\POCConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'POCConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);



/* Vitals*/



insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG4'),
'C:\\chica\\conceptMaps\\PEREG4\\Vitals\\VitalsConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'VitalsConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);


insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG3'),
'C:\\chica\\conceptMaps\\PEREG3\\Vitals\\VitalsConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'VitalsConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG2'),
'C:\\chica\\conceptMaps\\PEREG2\\Vitals\\VitalsConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'VitalsConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG1'),
'C:\\chica\\conceptMaps\\PEREG1\\Vitals\\VitalsConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'VitalsConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'Other - PECAR'),
'C:\\chica\\conceptMaps\\Other - PECAR\\Vitals\\VitalsConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'VitalsConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

/*PCC Vitals*/

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PED1'),
'C:\\chica\\conceptMaps\\PED1\\Vitals\\VitalsConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'VitalsConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PED2'),
'C:\\chica\\conceptMaps\\PED2\\Vitals\\VitalsConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'VitalsConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'Other - PCC'),
'C:\\chica\\conceptMaps\\Other - PCC\\Vitals\\VitalsConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'VitalsConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);

/*PCC PSF TIFF*/
insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PED1'),
'C:\\chica\\conceptMaps\\PSFTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PSFTiffConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PED2'),
'C:\\chica\\conceptMaps\\PSFTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PSFTiffConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'Other - PCC'),
'C:\\chica\\conceptMaps\\PSFTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PSFTiffConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);

/*PECAR PSF TIFF*/

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG4'),
'C:\\chica\\conceptMaps\\PSFTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PSFTiffConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG3'),
'C:\\chica\\conceptMaps\\PSFTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PSFTiffConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG2'),
'C:\\chica\\conceptMaps\\PSFTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PSFTiffConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG1'),
'C:\\chica\\conceptMaps\\PSFTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PSFTiffConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'Other - PECAR'),
'C:\\chica\\conceptMaps\\PSFTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
where name like 'PSFTiffConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

/*PCC PWS TIFF*/

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PED1'),
'C:\\chica\\conceptMaps\\PWSTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PWSTiffConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PED2'),
'C:\\chica\\conceptMaps\\PWSTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PWSTiffConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'Other - PCC'),
'C:\\chica\\conceptMaps\\PWSTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PWSTiffConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);


/*PECAR PWS TIFF*/
insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG4'),
'C:\\chica\\conceptMaps\\PWSTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PWSTiffConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG3'),
'C:\\chica\\conceptMaps\\PWSTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PWSTiffConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG2'),
'C:\\chica\\conceptMaps\\PWSTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PWSTiffConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'PEREG1'),
'C:\\chica\\conceptMaps\\PWSTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PWSTiffConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);

insert into chirdlutil_location_tag_attribute_value
(location_tag_id, value, location_tag_attribute_id, location_id)
values (
(select location_tag_id from location_tag where tag like 'Other - PECAR'),
'C:\\chica\\conceptMaps\\PWSTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PWSTiffConceptMapLocation'),
 (select location_id from location where name like 'PEPS')
);
