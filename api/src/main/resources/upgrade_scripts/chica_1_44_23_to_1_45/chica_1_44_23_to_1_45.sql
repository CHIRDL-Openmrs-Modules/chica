-- config locations
INSERT INTO `location`(name,description,creator,date_created) 
select 'PCPS', 'Generic chica location', '1', NOW()from 
(select count(*) as cnt from location where name='PCPS') a
where a.cnt=0;

INSERT INTO `location`(name,description,creator,date_created) 
		select 'PEPS', 'Pecar chica location', '1', NOW()from 
		(select count(*) as cnt from location where name='PEPS') a
		where a.cnt=0;


-- config location tags
INSERT INTO `location_tag`(tag,creator,date_created) 
select 'PEREG1', '1', NOW() from 
(select count(*) as cnt from location_tag where tag='PEREG1') a
where a.cnt=0;

INSERT INTO `location_tag`(tag,creator,date_created) 
select 'PEREG2', '1', NOW() from 
(select count(*) as cnt from location_tag where tag='PEREG2') a
where a.cnt=0;

INSERT INTO `location_tag`(tag,creator,date_created) 
select 'PEREG3', '1', NOW() from 
(select count(*) as cnt from location_tag where tag='PEREG3') a
where a.cnt=0;

INSERT INTO `location_tag`(tag,creator,date_created) 
select 'PEREG4', '1', NOW() from 
(select count(*) as cnt from location_tag where tag='PEREG4') a
where a.cnt=0;

INSERT INTO `location_tag`(tag,creator,date_created) 
select 'Other - PECAR', '1', NOW() from 
(select count(*) as cnt from location_tag where tag='Other - PECAR') a
where a.cnt=0;

INSERT INTO `location_tag`(tag,creator,date_created) 
select 'PED1', '1', NOW() from 
(select count(*) as cnt from location_tag where tag='PED1') a
where a.cnt=0;

INSERT INTO `location_tag`(tag,creator,date_created) 
select 'PED2', '1', NOW() from 
(select count(*) as cnt from location_tag where tag='PED2') a
where a.cnt=0;

INSERT INTO `location_tag`(tag,creator,date_created) 
select 'Other - PCC', '1', NOW() from 
(select count(*) as cnt from location_tag where tag='Other - PCC') a
where a.cnt=0;

-- configure location tag to location mappings
INSERT INTO `location_tag_map`(location_id,location_tag_id) 
select b.location_id,c.location_tag_id from location b, location_tag c,(select count(*) as cnt from location_tag_map a inner join location b 
on a.location_id=b.location_id inner join location_tag c
on a.location_tag_id=c.location_tag_id where b.name='PEPS' and c.tag='PEREG1') d
where b.name='PEPS' and c.tag='PEREG1' and d.cnt=0;

INSERT INTO `location_tag_map`(location_id,location_tag_id) 
select b.location_id,c.location_tag_id from location b, location_tag c,(select count(*) as cnt from location_tag_map a inner join location b 
on a.location_id=b.location_id inner join location_tag c
on a.location_tag_id=c.location_tag_id where b.name='PEPS' and c.tag='PEREG2') d
where b.name='PEPS' and c.tag='PEREG2' and d.cnt=0;

INSERT INTO `location_tag_map`(location_id,location_tag_id) 
select b.location_id,c.location_tag_id from location b, location_tag c,(select count(*) as cnt from location_tag_map a inner join location b 
on a.location_id=b.location_id inner join location_tag c
on a.location_tag_id=c.location_tag_id where b.name='PEPS' and c.tag='PEREG3') d
where b.name='PEPS' and c.tag='PEREG3' and d.cnt=0;

INSERT INTO `location_tag_map`(location_id,location_tag_id) 
select b.location_id,c.location_tag_id from location b, location_tag c,(select count(*) as cnt from location_tag_map a inner join location b 
on a.location_id=b.location_id inner join location_tag c
on a.location_tag_id=c.location_tag_id where b.name='PEPS' and c.tag='PEREG4') d
where b.name='PEPS' and c.tag='PEREG4' and d.cnt=0;

INSERT INTO `location_tag_map`(location_id,location_tag_id) 
select b.location_id,c.location_tag_id from location b, location_tag c,(select count(*) as cnt from location_tag_map a inner join location b 
on a.location_id=b.location_id inner join location_tag c
on a.location_tag_id=c.location_tag_id where b.name='PEPS' and c.tag='Other - PECAR') d
where b.name='PEPS' and c.tag='Other - PECAR' and d.cnt=0;

INSERT INTO `location_tag_map`(location_id,location_tag_id) 
select b.location_id,c.location_tag_id from location b, location_tag c,(select count(*) as cnt from location_tag_map a inner join location b 
on a.location_id=b.location_id inner join location_tag c
on a.location_tag_id=c.location_tag_id where b.name='PCPS' and c.tag='Other - PCC') d
where b.name='PCPS' and c.tag='Other - PCC' and d.cnt=0;

INSERT INTO `location_tag_map`(location_id,location_tag_id) 
select b.location_id,c.location_tag_id from location b, location_tag c,(select count(*) as cnt from location_tag_map a inner join location b 
on a.location_id=b.location_id inner join location_tag c
on a.location_tag_id=c.location_tag_id where b.name='PCPS' and c.tag='PED1') d
where b.name='PCPS' and c.tag='PED1' and d.cnt=0;

INSERT INTO `location_tag_map`(location_id,location_tag_id) 
select b.location_id,c.location_tag_id from location b, location_tag c,(select count(*) as cnt from location_tag_map a inner join location b 
on a.location_id=b.location_id inner join location_tag c
on a.location_tag_id=c.location_tag_id where b.name='PCPS' and c.tag='PED2') d
where b.name='PCPS' and c.tag='PED2' and d.cnt=0;

-- configure atd_program_tag_map
insert into atd_program_tag_map(program_id,location_id,location_tag_id)
select program_id,location_id,location_tag_id from atd_program a,  location b, location_tag c
where a.name='chica' and a.version='1.0' and b.name='PEPS' and c.tag='PEREG3';

insert into atd_program_tag_map(program_id,location_id,location_tag_id)
select program_id,location_id,location_tag_id from atd_program a,  location b, location_tag c
where a.name='chica' and a.version='1.0' and b.name='PEPS' and c.tag='Other - PECAR';

insert into atd_program_tag_map(program_id,location_id,location_tag_id)
select program_id,location_id,location_tag_id from atd_program a,  location b, location_tag c
where a.name='chica' and a.version='1.0' and b.name='PCPS' and c.tag='PED1';

insert into atd_program_tag_map(program_id,location_id,location_tag_id)
select program_id,location_id,location_tag_id from atd_program a,  location b, location_tag c
where a.name='chica' and a.version='1.0' and b.name='PCPS' and c.tag='PED2';

insert into atd_program_tag_map(program_id,location_id,location_tag_id)
select program_id,location_id,location_tag_id from atd_program a,  location b, location_tag c
where a.name='chica' and a.version='1.0' and b.name='PCPS' and c.tag='Other - PCC';

-- configure chica_location_tag_attribute_value
insert into chica_location_tag_attribute_value(location_tag_id,value,location_tag_attribute_id)
select a.location_tag_id,'true',b.location_tag_attribute_id from location_tag a, chica_location_tag_attribute b,
(
select count(*) as cnt from chica_location_tag_attribute_value c inner join location_tag a on c.location_tag_id=a.location_tag_id
inner join 
chica_location_tag_attribute b on c.location_tag_attribute_id=b.location_tag_attribute_id
where a.tag='PEREG3' and b.name='ActivePrinterLocation')c
where a.tag='PEREG3' and b.name='ActivePrinterLocation' and c.cnt = 0;

insert into chica_location_tag_attribute_value(location_tag_id,value,location_tag_attribute_id)
select a.location_tag_id,'true',b.location_tag_attribute_id from location_tag a, chica_location_tag_attribute b
where a.tag='PED1' and b.name='ActivePrinterLocation';

insert into chica_location_tag_attribute_value(location_tag_id,value,location_tag_attribute_id)
select a.location_tag_id,'true',b.location_tag_attribute_id from location_tag a, chica_location_tag_attribute b
where a.tag='PED2' and b.name='ActivePrinterLocation';

insert into chica_location_tag_attribute_value(location_tag_id,value,location_tag_attribute_id)
select a.location_tag_id,'true',b.location_tag_attribute_id from location_tag a, chica_location_tag_attribute b
where a.tag='Other - PECAR' and b.name='ActivePrinterLocation';

insert into chica_location_tag_attribute_value(location_tag_id,value,location_tag_attribute_id)
select a.location_tag_id,'true',b.location_tag_attribute_id from location_tag a, chica_location_tag_attribute b
where a.tag='Other - PCC' and b.name='ActivePrinterLocation';

-- configure chica_location_attribute_value
insert into chica_location_attribute_value(location_id,value,location_attribute_id)
select c.location_id,'266-2921 PECAR -- Please Call PECAR',
b.location_attribute_id from chica_location_attribute b,location c
where b.name='pagerMessage' and c.name='PEPS';

insert into chica_location_attribute_value(location_id,value,location_attribute_id)
select c.location_id,'692-2306 PCC -- Please Call PCC',
b.location_attribute_id from chica_location_attribute b,location c
where b.name='pagerMessage' and c.name='PCPS';

insert into chica_location_attribute_value(location_id,value,location_attribute_id)
select c.location_id,'(317) 266-2921',
b.location_attribute_id from chica_location_attribute b,location c
where b.name='clinicPhone' and c.name='PEPS';

insert into chica_location_attribute_value(location_id,value,location_attribute_id)
select c.location_id,'(317) 692-2306',
b.location_attribute_id from chica_location_attribute b,location c
where b.name='clinicPhone' and c.name='PCPS';

-- create pcc user
INSERT INTO `person`
(creator,date_created)
values( 1, NOW() );

INSERT INTO `users`
(user_id,system_id,creator,date_created,username,password,salt,voided)
select a.* from 
(
select max(person_id), '141-4','1', NOW(),'pccuser','c790c1a346a7b7a18fd6bb954dc89a383d0a9b64','b324b3d3b56319c268f4ccfce9ffd7d9b22c60c6',0 from person
)a,
(select count(*) as cnt from users where username='pccuser')b
where b.cnt=0;

INSERT INTO `person_name` 
(person_id,family_name,creator,date_created)
select a.* from 
(select max(user_id),'ChicaUser',1, NOW() from users where system_id='140-4')a,
(select count(*) as cnt from person_name where family_name='ChicaUser' and 
person_id=(select max(user_id) from users where system_id='141-4'))b
where b.cnt=0;

INSERT INTO `user_role` 
select a.* from 
(select max(user_id),'Chicauser' from users where system_id='141-4')a,
(select count(*) as cnt from user_role where user_id=(select max(user_id) from users where system_id='141-4'))b
where b.cnt=0;

-- configure user properties
		INSERT INTO `user_property`(user_id,property,property_value) 
		select b.user_id,'locationTags','PEREG3, Other - PECAR' from 
		(select count(*) as cnt from user_property where property='locationTags' and user_id in  (select user_id from users where username='pecaruser')) a,
		users b
		where a.cnt = 0 and b.username='pecaruser';

		INSERT INTO `user_property`(user_id,property,property_value) 
		select b.user_id,'location','PEPS' from 
		(select count(*) as cnt from user_property where property='location' and user_id in  (select user_id from users where username='pecaruser')) a,
		users b
		where a.cnt = 0 and b.username='pecaruser';

		INSERT INTO `user_property`(user_id,property,property_value) 
		select b.user_id,'locationTags','PED1, PED2, Other - PCC' from 
		(select count(*) as cnt from user_property where property='locationTags' and user_id in  (select user_id from users where username='pccuser')) a,
		users b
		where a.cnt = 0 and b.username='pccuser';

		INSERT INTO `user_property`(user_id,property,property_value) 
		select b.user_id,'location','PCPS' from 
		(select count(*) as cnt from user_property where property='location' and user_id in  (select user_id from users where username='pccuser')) a,
		users b
		where a.cnt = 0 and b.username='pccuser';


-- change Other printer locations to Other - PECAR
update encounter 
set printer_location='Other - PECAR'
where printer_location='Other';

-- configure atd_form_attribute_value table
update atd_form_attribute_value a, location_tag b, location c
set a.location_tag_id=b.location_tag_id, a.location_id=c.location_id
where b.tag='PEREG3' and c.name='PEPS' and (b.location_tag_id=1 or c.location_id=1);

update atd_patient_atd_element a, encounter b
set a.location_id=b.location_id
where a.location_id=1 and a.encounter_id=b.encounter_id;

update atd_patient_state a,atd_session b,encounter c,location_tag d
set a.location_id=c.location_id, a.location_tag_id=d.location_tag_id
where (a.location_id=1 or a.location_tag_id=1) and a.session_id=b.session_id and
 b.encounter_id=c.encounter_id and c.printer_location=d.tag;

-- if any location still unset, assume PECAR
update atd_patient_state a, location b
set a.location_id=b.location_id
where a.location_id=1 and b.name='PEPS';

-- try to map the location based on form_instance_id
 update atd_form_instance a, atd_patient_state b
set a.location_id=b.location_id
where a.location_id=1 and 
a.form_id=b.form_id and 
a.form_instance_id=b.form_instance_id;

-- if any location still unset, assume PECAR
update atd_form_instance a, location b
set a.location_id=b.location_id
where a.location_id=1 and b.name='PEPS';

update chica_statistics a, encounter b, location_tag c
set a.location_id=b.location_id,a.location_tag_id=c.location_tag_id
where (a.location_id=1 or b.location_id=1) and a.encounter_id=b.encounter_id and b.printer_location=c.tag;

update chica_location_tag_attribute_value a,location_tag_map b 
set a.location_id=b.location_id
where a.location_id=1 and a.location_tag_id=b.location_tag_id;

insert into chica_location_tag_attribute_value (location_tag_attribute_id,value,location_id,location_tag_id)
select location_tag_attribute_id, form_id as value,location_id,location_tag_id from chica_location_tag_attribute a, form b, location c, location_tag d
 where a.name in ('PSF') and b.name='PSF' and b.retired = 0 and d.tag  in ('Other - PECAR') and c.name in ('PEPS')
union 
select location_tag_attribute_id, form_id as value,location_id,location_tag_id from chica_location_tag_attribute a, form b, location c, location_tag d
 where a.name in ('PSF') and b.name='PSF' and b.retired = 0 and d.tag in ('PED1','PED2','Other - PCC') and c.name in ('PCPS')
union
select location_tag_attribute_id, form_id as value,location_id,location_tag_id from chica_location_tag_attribute a, form b, location c, location_tag d
 where a.name in ('PWS') and b.name='PWS' and b.retired = 0 and d.tag  in ('Other - PECAR') and c.name in ('PEPS')
union 
select location_tag_attribute_id, form_id as value,location_id,location_tag_id from chica_location_tag_attribute a, form b, location c, location_tag d
 where a.name in ('PWS') and b.name='PWS' and b.retired = 0 and d.tag in ('PED1','PED2','Other - PCC') and c.name in ('PCPS');


update field
set default_value='clinicPhone'
where default_value='(317) 266-2921';

update field 
set field_type=(select field_type_id from field_type where name='Merge Field')
where default_value='clinicPhone';

update field
set default_value='birthdate>weightPF'
where name='WeightPUnits';

update field
set default_value='birthdate>weightSF'
where name='WeightSUnits';

update field a, form_field b, form c
set default_value='consumeWeight'
where a.name='WeightP'
and c.name='PSF' and a.field_id=b.field_id and b.form_id=c.form_id
;

update field a, form_field b, form c
set default_value='conceptRule>percentile'
where a.name='WeightP'
and c.name='PWS' and a.field_id=b.field_id and b.form_id=c.form_id
;

update field 
set concept_id=(select concept_id from concept_name where name='PULSE CHICA')
where concept_id in (
select concept_id from concept_name where name='PULSE');

update atd_form_attribute_value
set value=replace(value,'F:\\chica\\merge\\','F:\\chica\\merge\\PEPS\\');

update atd_form_attribute_value
set value = replace(value,'F:\\chica\\scan\\','F:\\chica\\scan\\PEPS\\');

insert into atd_form_attribute_value(form_id,value,form_attribute_id,location_id,location_tag_id)
select distinct a.form_id,a.value,b.form_attribute_id,c.location_id,d.location_tag_id from 
(
select form_id,value from atd_form_attribute_value where form_attribute_id in (
select form_attribute_id from atd_form_attribute where name like 'useAlternatePrinter%'
and name like '%PEREG3'))a, atd_form_attribute b,location c, location_tag d
where b.name='useAlternatePrinter' and c.name='PEPS' and d.tag='PEREG3';

insert into atd_form_attribute_value(form_id,value,form_attribute_id,location_id,location_tag_id)
select distinct a.form_id,a.value,b.form_attribute_id,c.location_id,d.location_tag_id from 
(
select form_id,value from atd_form_attribute_value where form_attribute_id in (
select form_attribute_id from atd_form_attribute where name like 'useAlternatePrinter%'
and name like '%Other'))a, atd_form_attribute b,location c, location_tag d
where b.name='useAlternatePrinter' and c.name='PEPS' and d.tag='Other - PECAR';

insert into atd_form_attribute_value(form_id,value,form_attribute_id,location_id,location_tag_id)
select distinct a.form_id,a.value,b.form_attribute_id,c.location_id,d.location_tag_id from 
(
select form_id,value from atd_form_attribute_value where form_attribute_id in (
select form_attribute_id from atd_form_attribute where name like 'defaultPrinter%'
and name like '%PEREG3'))a, atd_form_attribute b,location c, location_tag d
where b.name='defaultPrinter' and c.name='PEPS' and d.tag='PEREG3';

insert into atd_form_attribute_value(form_id,value,form_attribute_id,location_id,location_tag_id)
select distinct a.form_id,a.value,b.form_attribute_id,c.location_id,d.location_tag_id from 
(
select form_id,value from atd_form_attribute_value where form_attribute_id in (
select form_attribute_id from atd_form_attribute where name like 'defaultPrinter%'
and name like '%Other'))a, atd_form_attribute b,location c, location_tag d
where b.name='defaultPrinter' and c.name='PEPS' and d.tag='Other - PECAR';

insert into atd_form_attribute_value(form_id,value,form_attribute_id,location_id,location_tag_id)
select distinct a.form_id,a.value,b.form_attribute_id,c.location_id,d.location_tag_id from 
(
select form_id,value from atd_form_attribute_value where form_attribute_id in (
select form_attribute_id from atd_form_attribute where name like 'alternatePrinter%'
and name like '%PEREG3'))a, atd_form_attribute b,location c, location_tag d
where b.name='alternatePrinter' and c.name='PEPS' and d.tag='PEREG3';

insert into atd_form_attribute_value(form_id,value,form_attribute_id,location_id,location_tag_id)
select distinct a.form_id,a.value,b.form_attribute_id,c.location_id,d.location_tag_id from 
(
select form_id,value from atd_form_attribute_value where form_attribute_id in (
select form_attribute_id from atd_form_attribute where name like 'alternatePrinter%'
and name like '%Other'))a, atd_form_attribute b,location c, location_tag d
where b.name='alternatePrinter' and c.name='PEPS' and d.tag='Other - PECAR';

-- delete all old printer config values
delete a.* from atd_form_attribute_value a inner join atd_form_attribute b on a.form_attribute_id=b.form_attribute_id 
where b.name in
(
'defaultPrinter PEREG1',
'alternatePrinter PEREG1',
'useAlternatePrinter PEREG1',
'defaultPrinter PEREG2',
'alternatePrinter PEREG2',
'useAlternatePrinter PEREG2',
'defaultPrinter PEREG3',
'alternatePrinter PEREG3',
'useAlternatePrinter PEREG3',
'defaultPrinter PEREG4',
'alternatePrinter PEREG4',
'useAlternatePrinter PEREG4',
'defaultPrinter Other',
'alternatePrinter Other',
'useAlternatePrinter Other'
);

delete b.* from atd_form_attribute b
where b.name in
(
'defaultPrinter PEREG1',
'alternatePrinter PEREG1',
'useAlternatePrinter PEREG1',
'defaultPrinter PEREG2',
'alternatePrinter PEREG2',
'useAlternatePrinter PEREG2',
'defaultPrinter PEREG3',
'alternatePrinter PEREG3',
'useAlternatePrinter PEREG3',
'defaultPrinter PEREG4',
'alternatePrinter PEREG4',
'useAlternatePrinter PEREG4',
'defaultPrinter Other',
'alternatePrinter Other',
'useAlternatePrinter Other'
);

insert into atd_form_attribute_value(form_id,value,form_attribute_id,location_id,location_tag_id)
select a.form_id,a.value,a.form_attribute_id,b.location_id,c.location_tag_id from atd_form_attribute_value  a,
location b, location_tag c where b.name='PEPS' and c.tag='Other - PECAR' and a.location_id=1 and a.location_tag_id=1;

update atd_form_attribute_value a,location b, location_tag c
set a.location_id=b.location_id,a.location_tag_id=c.location_tag_id
where a.location_id=1 and a.location_tag_id=1
and  b.name='PEPS' and c.tag='PEREG3';

insert into atd_form_attribute_value(form_id,value,form_attribute_id,location_tag_id,location_id)
select distinct b.form_id,a.value,a.form_attribute_id,a.location_tag_id,a.location_Id from atd_form_attribute_value a, form b 
where a.form_id in (select max(form_id) from form where name in ('PSF') and retired=1)
and value not like 'PSF_ID_Barcode%' and b.name='PSF' and b.retired=0;

insert into atd_form_attribute_value(form_id,value,form_attribute_id,location_tag_id,location_id)
select distinct b.form_id,a.value,a.form_attribute_id,a.location_tag_id,a.location_Id from atd_form_attribute_value a, form b 
where a.form_id in (select max(form_id) from form where name in ('PWS') and retired=1)
and value not like 'PWS_ID_Barcode%' and b.name='PWS' and b.retired=0;

-- scripts for PCC form_attribute_values
insert into atd_form_attribute_value(Form_id,value,form_attribute_id,location_id,location_tag_id)
select form_id,replace(value,'F:\\chica\\merge\\PEPS','F:\\chica\\merge\\PCPS'),form_attribute_id,b.location_id,c.location_tag_id  from atd_form_attribute_value a,
location b, location_tag c
 where a.location_tag_id in (select location_tag_id from location_tag where tag='PEREG3')
and form_id in (select form_id from form where retired=0)
and form_attribute_id in (select form_attribute_id from atd_form_attribute where name in ('defaultMergeDirectory','pendingMergeDirectory'))
and b.name='PCPS' and c.tag='PED1';

insert into atd_form_attribute_value(Form_id,value,form_attribute_id,location_id,location_tag_id)
select form_id,replace(value,'F:\\chica\\scan\\PEPS','F:\\chica\\scan\\PCPS'),form_attribute_id,b.location_id,c.location_tag_id  from atd_form_attribute_value a,
location b, location_tag c
 where a.location_tag_id in (select location_tag_id from location_tag where tag='PEREG3')
and form_id in (select form_id from form where retired=0)
and form_attribute_id  in (select form_attribute_id from atd_form_attribute where name in ('defaultExportDirectory'))
and b.name='PCPS' and c.tag='PED1';

insert into atd_form_attribute_value(Form_id,value,form_attribute_id,location_id,location_tag_id)
select form_id,replace(value,'\\SVCHICA02\\images\\PEPS','\\SVCHICA02\\images\\PCPS'),form_attribute_id,b.location_id,c.location_tag_id  from atd_form_attribute_value a,
location b, location_tag c
 where a.location_tag_id in (select location_tag_id from location_tag where tag='PEREG3')
and form_id in (select form_id from form where retired=0)
and form_attribute_id  in (select form_attribute_id from atd_form_attribute where name in ('imageDirectory'))
and b.name='PCPS' and c.tag='PED1';

insert into atd_form_attribute_value(Form_id,value,form_attribute_id,location_id,location_tag_id)
select form_id,replace(value,'\\SVCHICA02\\images\\PEPS','\\SVCHICA02\\images\\PCPS'),form_attribute_id,b.location_id,c.location_tag_id  from atd_form_attribute_value a,
location b, location_tag c
 where a.location_tag_id in (select location_tag_id from location_tag where tag='PEREG3')
and form_id in (select form_id from form where retired=0)
and form_attribute_id not in (select form_attribute_id from atd_form_attribute where name in ('imageDirectory','defaultExportDirectory','defaultMergeDirectory','pendingMergeDirectory'))
and b.name='PCPS' and c.tag='PED1';

insert into atd_form_attribute_value(Form_id,value,form_attribute_id,location_id,location_tag_id)
select form_id,value,form_attribute_id,location_id,b.location_tag_id  from atd_form_attribute_value a, location_tag b
where a.location_tag_id in
(select location_tag_id from location_tag where tag='PED1') and b.tag in ('PED2','Other - PCC');
