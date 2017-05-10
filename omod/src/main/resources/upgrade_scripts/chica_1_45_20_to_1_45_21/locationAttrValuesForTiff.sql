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
'C:\\chica\\conceptMaps\\PSFTiffConceptMap.xml'
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