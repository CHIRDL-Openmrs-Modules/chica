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


/*concept*/
/*add all new rmrs/chica concepts*/
INSERT INTO `concept` VALUES ('18304', '0', null, null,
'Concept for HL7 OBR Universal Service Identifier for PSF tiff',
 '4', '16', '1', '1', now(), null, '', null, null, null, null, null);
 
INSERT INTO `concept` VALUES ('18305', '0', null, null,
'Concept for HL7 OBR Universal Service Identifier for PWS tiff',
 '4', '16', '1', '1', now(), null, '', null, null, null, null, null);
 
INSERT INTO `concept` VALUES ('18306', '0', null, null,
'CHICA concept for HL7 OBR Universal Service Identifier for PWS tiff',
 '4', '17', '1', '1', now(), null, '', null, null, null, null, null);
 
INSERT INTO `concept` VALUES ('18307', '0', null, null,
'CHICA concept for HL7 OBR Universal Service Identifier for PSF tiff',
 '4', '17', '1', '1', now(), null, '', null, null, null, null, null);
 
 INSERT INTO `concept` VALUES ('18308', '0', null, null,
'Pain qualitative concept for CAREWEB',
 '4', '17', '0', '1', now(), null, '', null, null, null, null, null);
 

 INSERT INTO `concept` VALUES ('18309', '0', null, null,
'PEDS CL DATA for RMRS',
 '4', '16', '0', '1', now(), null, '', null, null, null, null, null);
 
 INSERT INTO `concept` VALUES ('18310', '0', null, null,
'Medication Allergies for Careweb',
 '2', '17', '0', '1', now(), null, '', null, null, null, null, null);
 
 INSERT INTO `concept` VALUES ('18311', '0', null, null,
'Pain Quantitative for CAREWEB',
 '3', '17', '0', '1', now(), null, '', null, null, null, null, null);
 
 
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
 VALUES ('18311', 'PAIN QUANTITATIVE CAREWEB', 'en', '1', now(), '63328', '0', null, null, null);


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
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES CAREWEB'),
 (select concept_id from concept_name where name like 'no known allergies'),
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

INSERT INTO `concept_word` VALUES ('18311', 'PAIN', '', 'en', '63328');
INSERT INTO `concept_word` VALUES ('18311', 'QUANTITATIVE', '', 'en', '63328');
INSERT INTO `concept_word` VALUES ('18311', 'CAREWEB', '', 'en', '63328');

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
INSERT INTO `concept_set` VALUES ('18311', '18302', '1', '1', '2009-12-29 15:23:44');

truncate concept_map;

INSERT INTO `concept_map` VALUES ('1', '1', '6493', 'HEIGHT PEDS', '1', '2009-10-07 10:05:28', '8309');
INSERT INTO `concept_map` VALUES ('2', '1', '1280', 'WEIGHT PEDS', '1', '2009-10-07 10:05:28', '18114');
INSERT INTO `concept_map` VALUES ('3', '1', '1283', 'HEIGHT %ILE', '1', '2009-10-07 10:05:28', '8307');
INSERT INTO `concept_map` VALUES ('4', '1', '1281', 'WT %ILE', '1', '2009-10-07 10:05:28', '18199');
INSERT INTO `concept_map` VALUES ('5', '1', '26764', 'BMI', '1', '2009-10-07 10:05:28', '2426');
INSERT INTO `concept_map` VALUES ('6', '1', '65', 'TEMP', '1', '2009-10-07 10:05:28', '16512');
INSERT INTO `concept_map` VALUES ('9', '1', '65', 'TEMP', '1', '2009-10-07 10:14:05', '16512');
INSERT INTO `concept_map` VALUES ('10', '1', '1284', 'HEAD CIRCUMF', '1', '2009-10-07 10:18:17', '8221');
INSERT INTO `concept_map` VALUES ('11', '1', '68', 'SYS BP SITTING', '1', '2009-10-07 10:18:17', '16334');
INSERT INTO `concept_map` VALUES ('12', '1', '69', 'DIAS BP SITTING', '1', '2009-10-07 10:18:17', '5237');
INSERT INTO `concept_map` VALUES ('13', '1', '66', 'PULSE', '1', '2009-10-07 10:18:17', '14113');
INSERT INTO `concept_map` VALUES ('14', '1', '1285', 'HEAD CIRC %ILE', '1', '2009-10-07 10:18:17', '8216');
INSERT INTO `concept_map` VALUES ('15', '1', '67', 'RR', '1', '2009-10-07 10:18:17', '14918');
INSERT INTO `concept_map` VALUES ('16', '1', '448', 'ACUITY L FAR 20/', '1', '2009-10-07 10:18:17', '513');
INSERT INTO `concept_map` VALUES ('17', '1', '446', 'ACUITY R FAR 20/', '1', '2009-10-07 10:18:17', '515');
INSERT INTO `concept_map` VALUES ('18', '1', '36404', 'OBS SET PEDS PRE-SCREENING', '1', '2009-12-07 00:00:00', '18304');
INSERT INTO `concept_map` VALUES ('19', '1', '27159', 'Pain Scale (0-10)', '1', '2009-12-07 00:00:00', '12586');
INSERT INTO `concept_map` VALUES ('20', '1', '36405', 'OBS SET PEDS PHYSICIAN ENCOUNTER', '1', '2009-12-11 00:00:00', '18305');
INSERT INTO `concept_map` VALUES ('21', '1', '8995', 'PEDS CL DATA', '1', '2009-12-23 14:16:57', '18309');
INSERT INTO `concept_map` VALUES ('22', '1', '22391', 'MEDICAL RECORD FILE OBSERVATIONS', '1', '2009-12-23 14:23:57', '10684');
INSERT INTO `concept_map` VALUES ('23', '1', '7717', 'ALLERGY HX', '1', '2009-12-29 00:00:00', '842');

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
'C:\\chica\\conceptMaps\\Other - PCC\\TIFF\\PSF\\PSFTiffConceptMap.xml',
(select location_tag_attribute_id from chirdlutil_location_tag_attribute
 where name like 'PSFTiffConceptMapLocation'),
 (select location_id from location where name like 'PCPS')
);


/*PECAR PSF TIFF*/

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