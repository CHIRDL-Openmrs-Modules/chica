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
INSERT INTO `concept` VALUES ('18304', '0', null, 'OBS SET PEDS PRE-SCREENING',
'Concept for HL7 OBR Universal Service Identifier for PSF tiff',
 '4', '16', '1', '1', now(), null, '', null, null, null, null, null);
 
INSERT INTO `concept` VALUES ('18305', '0', null, 'OBS SET PEDS PHYSICIAN ENCOUNTER',
'Concept for HL7 OBR Universal Service Identifier for PWS tiff',
 '4', '16', '1', '1', now(), null, '', null, null, null, null, null);
 
INSERT INTO `concept` VALUES ('18306', '0', null, 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA',
'CHICA concept for HL7 OBR Universal Service Identifier for PWS tiff',
 '4', '17', '1', '1', now(), null, '', null, null, null, null, null);
 
INSERT INTO `concept` VALUES ('18307', '0', null, 'OBS SET PEDS PRE-SCREENING CHICA',
'CHICA concept for HL7 OBR Universal Service Identifier for PSF tiff',
 '4', '17', '1', '1', now(), null, '', null, null, null, null, null);
 
 INSERT INTO `concept` VALUES ('18308', '0', null, 'PAIN QUALITATIVE CAREWEB',
'Pain qualitative concept for CAREWEB',
 '4', '17', '0', '1', now(), null, '', null, null, null, null, null);
 

 INSERT INTO `concept` VALUES ('18309', '0', null, 'PEDS CL DATA',
'PEDS CL DATA for RMRS',
 '4', '16', '0', '1', now(), null, '', null, null, null, null, null);
 
 INSERT INTO `concept` VALUES ('18310', '0', null, 'MEDICATION ALLERGIES CAREWEB',
'Medication Allergies for Careweb',
 '2', '17', '0', '1', now(), null, '', null, null, null, null, null);
 
 INSERT INTO `concept` (retired, description, datatype_id, class_id, 
 is_set, creator, date_created)  VALUES ( '0', 'MACROLIDES',
 '2', (select concept_class_id from concept_class where name like 'RMRS'), '0', '1', now());
 
 INSERT INTO `concept_name`
 VALUES ('18304', 'OBS SET PEDS PRE-SCREENING', 'en', '1', now(), '63321', '0', null, null, null);
 
 INSERT INTO `concept_name`
 VALUES ('18305', 'OBS SET PEDS PHYSICIAN ENCOUNTER', 'en', '1', now(), '63322', '0', null, null, null);

 INSERT INTO `concept_name`
 VALUES ('18306', 'OBS SET PEDS PHYSICIAN ENCOUNTER CHICA', 'en', '1', now(), '63323', '0', null, null, null);

 INSERT INTO `concept_name`
 VALUES ('18307', 'OBS SET PEDS PRE-SCREENING CHICA', 'en', '1', now(), '63324', '0', null, null, null);

 INSERT INTO `concept_name`
 VALUES ('18308', 'PAIN QUALITATIVE CAREWEB', 'en', '1', now(), '63325', '0', null, null, null);
 
 INSERT INTO `concept_name`
 VALUES ('18309', 'PEDS CL DATA', 'en', '1', now(), '63326', '0', null, null, null);
 
  INSERT INTO `concept_name`
 VALUES ('18310', 'MEDICATION ALLERGIES CAREWEB', 'en', '1', now(), '63327', '0', null, null, null);

 
INSERT INTO `concept_name`
(concept_id, name, locale, creator, date_created, voided)
VALUES ((select concept_id from concept where description like 'MACROLIDES'), 
'MACROLIDES', 'en', '1', now(),'0')


/*names*/
update concept set short_name = null where concept_id = 18302;
update concept set short_name = null where concept_id = 18303;
update concept_name set name = 'PEDS CL DATA CHICA' where concept_id = 18303;
update concept_name set name = 'MEDICAL RECORD FILE OBSERVATIONS CHICA' where concept_id = 18302;


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
 
 
INSERT INTO `concept_word` VALUES ('18302', 'FILE', '', 'en', '63319');
INSERT INTO `concept_word` VALUES ('18302', 'MEDICAL', '', 'en', '63319');
INSERT INTO `concept_word` VALUES ('18302', 'RECORD', '', 'en', '63319');
INSERT INTO `concept_word` VALUES ('18302', 'OBSERVATIONS', '', 'en', '63319');
INSERT INTO `concept_word` VALUES ('18302', 'CHICA', '', 'en', '63319');

INSERT INTO `concept_word` VALUES ('18303', 'PEDS', '', 'en', '63320');
INSERT INTO `concept_word` VALUES ('18303', 'CL', '', 'en', '63320');
INSERT INTO `concept_word` VALUES ('18303', 'DATA', '', 'en', '63320');
INSERT INTO `concept_word` VALUES ('18303', 'CHICA', '', 'en', '63320');

INSERT INTO `concept_word` VALUES ('18304', 'OBS', '', 'en', '63321');
INSERT INTO `concept_word` VALUES ('18304', 'SET', '', 'en', '63321');
INSERT INTO `concept_word` VALUES ('18304', 'PEDS', '', 'en', '63321');
INSERT INTO `concept_word` VALUES ('18304', 'PRE-SCREENING', '', 'en', '63321');

INSERT INTO `concept_word` VALUES ('18305', 'OBS', '', 'en', '63322');
INSERT INTO `concept_word` VALUES ('18305', 'SET', '', 'en', '63322');
INSERT INTO `concept_word` VALUES ('18305', 'PEDS', '', 'en', '63322');
INSERT INTO `concept_word` VALUES ('18305', 'PHYSICIAN', '', 'en', '63322');
INSERT INTO `concept_word` VALUES ('18305', 'ENCOUNTER', '', 'en', '63322');

INSERT INTO `concept_word` VALUES ('18306', 'OBS', '', 'en', '63323');
INSERT INTO `concept_word` VALUES ('18306', 'SET', '', 'en', '63323');
INSERT INTO `concept_word` VALUES ('18306', 'PEDS', '', 'en', '63323');
INSERT INTO `concept_word` VALUES ('18306', 'PHYSICIAN', '', 'en', '63323');
INSERT INTO `concept_word` VALUES ('18306', 'ENCOUNTER', '', 'en', '63323');
INSERT INTO `concept_word` VALUES ('18306', 'CHICA', '', 'en', '63323');

INSERT INTO `concept_word` VALUES ('18307', 'OBS', '', 'en', '63324');
INSERT INTO `concept_word` VALUES ('18307', 'SET', '', 'en', '63324');
INSERT INTO `concept_word` VALUES ('18307', 'PEDS', '', 'en', '63324');
INSERT INTO `concept_word` VALUES ('18307', 'PRE-SCREENING', '', 'en', '63324');
INSERT INTO `concept_word` VALUES ('18307', 'CHICA', '', 'en', '63324');

INSERT INTO `concept_word` VALUES ('18308', 'PAIN', '', 'en', '63325');
INSERT INTO `concept_word` VALUES ('18308', 'QUALITATIVE', '', 'en', '63325');
INSERT INTO `concept_word` VALUES ('18308', 'CAREWEB', '', 'en', '63325');

INSERT INTO `concept_word` VALUES ('18309', 'PEDS', '', 'en', '63326');
INSERT INTO `concept_word` VALUES ('18309', 'CL', '', 'en', '63326');
INSERT INTO `concept_word` VALUES ('18309', 'DATA', '', 'en', '63326');

INSERT INTO `concept_word` VALUES ('18310', 'MEDICATION', '', 'en', '63327');
INSERT INTO `concept_word` VALUES ('18310', 'ALLERGIES', '', 'en', '63327');
INSERT INTO `concept_word` VALUES ('18310', 'CAREWEB', '', 'en', '63327');

INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'MACROLIDES'), 'MACROLIDES', '', 'en',
(select concept_name_id from concept_name where name like 'MACROLIDES'));

truncate concept_set;

INSERT INTO `concept_set` VALUES ('2427', '18303', '0', '1', '2009-10-07 10:05:28');
INSERT INTO `concept_set` VALUES ('2428', '18303', '14', '1', '2009-10-07 10:05:28');
INSERT INTO `concept_set` VALUES ('5242', '18303', '1', '1', '2009-10-07 10:18:17');
INSERT INTO `concept_set` VALUES ('8152', '18303', '1', '1', '2009-10-07 10:14:05');
INSERT INTO `concept_set` VALUES ('8155', '18303', '1', '1', '2009-10-07 10:18:17');
INSERT INTO `concept_set` VALUES ('8306', '18303', '1', '1', '2009-10-07 10:05:28');
INSERT INTO `concept_set` VALUES ('8866', '18303', '1', '1', '2009-10-07 10:05:28');
INSERT INTO `concept_set` VALUES ('10686', '18302', '1', '1', '2009-12-23 15:03:05');
INSERT INTO `concept_set` VALUES ('12581', '18303', '1', '1', '2009-12-08 00:00:00');
INSERT INTO `concept_set` VALUES ('14114', '18303', '1', '1', '2009-10-07 10:18:17');
INSERT INTO `concept_set` VALUES ('14917', '18303', '1', '1', '2009-10-07 10:18:17');
INSERT INTO `concept_set` VALUES ('16349', '18303', '1', '1', '2009-10-07 10:18:17');
INSERT INTO `concept_set` VALUES ('16525', '18303', '1', '1', '2009-10-07 10:05:28');
INSERT INTO `concept_set` VALUES ('17860', '18303', '1', '1', '2009-10-07 10:18:17');
INSERT INTO `concept_set` VALUES ('17861', '18303', '1', '1', '2009-10-07 10:18:17');
INSERT INTO `concept_set` VALUES ('18091', '18303', '1', '1', '2009-10-07 10:05:28');
INSERT INTO `concept_set` VALUES ('18200', '18303', '1', '1', '2009-10-07 10:05:28');
INSERT INTO `concept_set` VALUES ('18305', '18305', '1', '1', '2009-12-11 00:00:00');
INSERT INTO `concept_set` VALUES ('18306', '18306', '1', '1', '2009-12-23 14:21:42');
INSERT INTO `concept_set` VALUES ('18307', '18307', '1', '1', '2009-12-23 09:39:20');
INSERT INTO `concept_set` VALUES ('18308', '18303', '1', '1', '2009-12-07 00:00:00');
INSERT INTO `concept_set` VALUES ('18310', '18302', '1', '1', '2009-12-29 00:00:00');

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
