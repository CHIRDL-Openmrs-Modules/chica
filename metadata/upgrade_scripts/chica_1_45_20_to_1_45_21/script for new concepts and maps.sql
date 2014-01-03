INSERT INTO `chica_hl7_export_status` 
VALUES ('5', 'do_not_send', 'Do not send tiff images in hl7', now());          

INSERT INTO `concept` (retired, description, datatype_id, class_id, 
 is_set, creator, date_created)  VALUES ( '0', 'MACROLIDES',
 '3', (select concept_class_id from concept_class where name like 'RMRS'), '0', '1', now());

INSERT INTO `concept_name`
(concept_id, name, locale, creator, date_created, voided)
VALUES ((select concept_id from concept where description like 'MACROLIDES'), 
'MACROLIDES', 'en', '1', now(),'0')
 
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
 
 ---
 
  INSERT INTO `concept_answer`
(concept_id, answer_concept, answer_drug, creator, date_created)
VALUES (
(select concept_id from concept_name where name like 'MEDICATION ALLERGIES CAREWEB'),
 (select concept_id from concept_name where name like 'MACROLIDES'),
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
 
 INSERT INTO `concept_word`
VALUES ((select concept_id from concept
where description like 'MACROLIDES'), 'MACROLIDES', '', 'en',
(select concept_name_id from concept_name where name like 'MACROLIDES'));

 