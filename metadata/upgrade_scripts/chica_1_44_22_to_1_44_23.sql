insert into chica_location_tag_attribute_value (location_tag_id, value,
location_tag_attribute_id, location_id)
values(
(select distinct location_tag_id from location_tag where tag = 'PEREG3'),
'f:\\chica\\config\\PEREG3\\hl7_config_pecar.xml',
(select location_tag_attribute_id from chica_location_tag_attribute
where name = 'HL7ConfigFile'),
(select location_id from location where name = 'PEPS'));

insert into chica_location_tag_attribute_value (location_tag_id, value,
location_tag_attribute_id, location_id)
values(
(select distinct location_tag_id from location_tag where tag = 'PEREG2'),
'f:\\chica\\config\\PEREG2\\hl7_config_pecar.xml',
(select location_tag_attribute_id from chica_location_tag_attribute
where name = 'HL7ConfigFile'),
(select location_id from location where name = 'PEPS'));

insert into chica_location_tag_attribute_value (location_tag_id, value,
location_tag_attribute_id, location_id)
values(
(select distinct location_tag_id from location_tag where tag = 'PEREG1'),
'f:\\chica\\config\\PEREG1\\hl7_config_pecar.xml',
(select location_tag_attribute_id from chica_location_tag_attribute
where name = 'HL7ConfigFile'),
(select location_id from location where name = 'PEPS'));

insert into chica_location_tag_attribute_value (location_tag_id, value,
location_tag_attribute_id, location_id)
values(
(select distinct location_tag_id from location_tag where tag = 'PEREG4'),
'f:\\chica\\config\\PEREG4\\hl7_config_pecar.xml',
(select location_tag_attribute_id from chica_location_tag_attribute
where name = 'HL7ConfigFile'),
(select location_id from location where name = 'PEPS'));

insert into chica_location_tag_attribute_value (location_tag_id, value,
location_tag_attribute_id, location_id)
values(
(select distinct location_tag_id from location_tag where tag = 'Other - PECAR'),
'f:\\chica\\config\\other_pecar\\hl7_config_pecar.xml',
(select location_tag_attribute_id from chica_location_tag_attribute
where name = 'HL7ConfigFile'),
(select location_id from location where name = 'PEPS'));


insert into chica_location_tag_attribute_value (location_tag_id, value,
location_tag_attribute_id, location_id)
values(
(select distinct location_tag_id from location_tag where tag = 'PED1'),
'f:\\chica\\config\\PED1\\hl7_config_pcc.xml',
(select location_tag_attribute_id from chica_location_tag_attribute
where name = 'HL7ConfigFile'),
(select location_id from location where name = 'PCPS'));

insert into chica_location_tag_attribute_value (location_tag_id, value,
location_tag_attribute_id, location_id)
values(
(select distinct location_tag_id from location_tag where tag = 'PED2'),
'f:\\chica\\config\\PED2\\hl7_config_pcc.xml',
(select location_tag_attribute_id from chica_location_tag_attribute
where name = 'HL7ConfigFile'),
(select location_id from location where name = 'PCPS'));

insert into chica_location_tag_attribute_value (location_tag_id, value,
location_tag_attribute_id, location_id)
values(
(select distinct location_tag_id from location_tag where tag = 'Other - PCC'),
'f:\\chica\\config\\other_pcc\\hl7_config_pcc.xml',
(select location_tag_attribute_id from chica_location_tag_attribute
where name = 'HL7ConfigFile'),
(select location_id from location where name = 'PCPS'));

