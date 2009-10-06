
/*new concepts  + names for set*/
insert into concept (short_name, description, datatype_id, class_id, is_set, creator, date_created)
values ('MEDICAL RECORD FILE OBSERVATIONS', 'concept_set for HL7 OBR Universal Service Identifier',
(select concept_datatype_id from concept_datatype where name like 'N/A'),
(select concept_class_id from concept_class where name like 'CHICA'), 1, 1, now());

insert into concept (short_name, description, datatype_id, class_id, is_set, creator, date_created)
values ('PEDS CL DATA', 'concept_set for HL7 OBR Universal Service Identifier',
(select concept_datatype_id from concept_datatype where name like 'N/A'),
(select concept_class_id from concept_class where name like 'CHICA'), 1, 1, now());


insert into concept_name
(concept_id, name, locale, creator, date_created)
values
(
(select concept_id
from concept where short_name
like 'MEDICAL RECORD FILE OBSERVATIONS'),
'MEDICAL RECORD FILE OBSERVATIONS','en', 1, now());

insert into concept_name
(concept_id, name,locale, creator, date_created)
values
(
(select concept_id
from concept where short_name
like 'PEDS CL DATA'),
'PEDS CL DATA', 'en', 1, now());




/* Sets
Height*/



insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'height'),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'height'),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*weight*/

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'weight'),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'weight'),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*BMI*/
insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'bmi chica'  and class_id in (select concept_class_id from concept_class where name like 'CHICA')),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'bmi chica'  and class_id in (select concept_class_id from concept_class where name like 'CHICA')),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*HEIGHT percentile*/
insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'htcentile'  and class_id in (select concept_class_id from concept_class where name like 'CHICA')),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'htcentile'  and class_id in (select concept_class_id from concept_class where name like 'CHICA')),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*WEIGHT percentile*/

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'WTCENTILE'  and class_id in (select concept_class_id from concept_class where name like 'CHICA')),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'WTCENTILE'  and class_id in (select concept_class_id from concept_class where name like 'CHICA')),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*BMI centile*/


insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'BMICENTILE'  and class_id in (select concept_class_id from concept_class where name like 'CHICA')),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'BMICENTILE'  and class_id in (select concept_class_id from concept_class where name like 'CHICA')),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());



/*TEMPERATURE*/

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'TEMPERATURE CHICA'  and class_id in (select concept_class_id from concept_class where name like 'CHICA')),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'TEMPERATURE CHICA'  and class_id in (select concept_class_id from concept_class where name like 'CHICA')),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());


/*MAPS*/
/* height*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '6493', 'HEIGHT PEDS', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'HEIGHT PEDS' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	
/* weight*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '1280', 'WEIGHT PEDS', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'WEIGHT PEDS' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	
/* htcentile*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '1283', 'HEIGHT %ILE', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'HEIGHT %ILE' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	
/* wtcentile*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '1281', 'WT %ILE', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'WT %ILE' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	
/* bmi*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '26764', 'BMI', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'BMI' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	
/* bmicentile*/
/* **** no RMRS BMI percentile ****/
/*INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '30587', 'BMI %ILE', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'BMI %ILE' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	*/

 /*TEMP*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '65', 'TEMP', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'TEMP' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	
	
	
	/* map for concept sets*/
	/* NOTE: using newly created concept set for CHICA, so subquery for class uses chica
	while source is RMRS */
	
	INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '22391', 'MEDICAL RECORD FILE OBSERVATIONS', 1, now(),
(select distinct c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'MEDICAL RECORD FILE OBSERVATIONS' and c.class_id in
    (select concept_class_id from concept_class where name like 'CHICA')));
	
	INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '8995', 'PEDS CL DATA', 1, now(),
(select distinct c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'PEDS CL DATA' and c.class_id in
    (select concept_class_id from concept_class where name like 'CHICA')));
	

update global_property set property_value = 'C:\\chica\\config\\ConceptMap.xml', 
description = 'RMRS to CHICA concept mapping for outgoing HL7' 
		where property like 'chica.conceptDictionaryMapFile';

INSERT INTO `concept_source`
( name, description, hl7_code, creator, date_created, voided)
 values ('RMRS', 'Regenstrief Medical Record System', ' ', '1', NOW()
 , 0);

 INSERT INTO `concept_source`
 ( name, description, hl7_code, creator, date_created, voided)
 values ('CHICA', 'CHICA', ' ', '1', NOW(), '0' );



/* TEMPERATURE CHICA  -------------------------*/
/*Concept_set TEMPERATURE CHICA*/
insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'TEMPERATURE CHICA'),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'TEMPERATURE CHICA'),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*Concept Map TEMPERATURE CHICA*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '65', 'TEMP', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'TEMP' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
    
/* HC -------------------------*/
    
 /*Concept_set HC*/
insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'HC'),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'HC'),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*Concept Map HC*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '1284', 'HEAD CIRCUM', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'HEAD CIRCUM' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	


/* SYSTOLIC BP -------------------------*/
/*Concept_set SYSTOLIC_BP*/
insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'SYSTOLIC_BP'),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'SYSTOLIC_BP'),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*Concept Map HC*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '68', 'SYS BP SITTING', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'SYS BP SITTING' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	
/* DIASTOLIC_BP -------------------------*/

/*Concept_sets DIASTOLIC_BP*/
insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'DIASTOLIC_BP'),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'DIASTOLIC_BP'),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*Concept Map DIASTOLIC_BP*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '69', 'DIAS BP SITTING', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'DIAS BP SITTING' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	
/* PULSE -------------------------*/
	
	/*Concept_sets PULSE CHICA*/
insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'PULSE CHICA'),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'PULSE CHICA'),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*Concept Map PULSE CHICA*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '66', 'PULSE', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'PULSE' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	

/* HCCENTILE -------------------------*/
	
	
/*Concept_sets HCCENTILE*/
insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'HCCENTILE'),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'HCCENTILE'),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*Concept Map HCCENTILE*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '1285', 'HEAD CIRC %ILE', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'HEAD CIRC %ILE' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));


/*RR CHICA ---------------------*/
	
/*Concept_sets RR CHICA*/
insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'RR CHICA'),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'RR CHICA'),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*Concept Map RR CHICA*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '67', 'RR', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'RR' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	
	
/*VISIONL ---------------------*/
	
/*Concept_sets VISIONL*/
insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'VISIONL'),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'VISIONL'),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*Concept Map VISIONL*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '448', 'ACUITY L FAR 20/', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'ACUITY L FAR 20/' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));
	
	
/*VISIONR ---------------------*/
/*Concept_sets VISIONR*/
insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'VISIONR'),
(select c.concept_id from concept c, concept_name cn where name like 'MEDICAL RECORD FILE OBSERVATIONS'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

insert into concept_set (concept_id, concept_set, creator, date_created)
values ((SELECT c.concept_id FROM concept c, concept_name cn where c.concept_id = cn.concept_id
and name like 'VISIONR'),
(select c.concept_id from concept c, concept_name cn where name like 'PEDS CL DATA'
and is_set=true and c.concept_id = cn.concept_id),
1,
now());

/*Concept Map VISIONL*/
INSERT INTO `concept_map` (source,source_code, comment, creator, date_created, concept_id)
 VALUES (
 (select concept_source_id from concept_source where name = 'RMRS'),
  '446', 'ACUITY R FAR 20/', 1, now(),
(select c.concept_id from concept c, concept_name cn where
     c.concept_id = cn.concept_id and cn.name like 'ACUITY R FAR 20/' and c.class_id in
    (select concept_class_id from concept_class where name like 'RMRS')));