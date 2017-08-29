insert into chirdlutil_location_attribute_value(location_id,value,location_attribute_id)
select c.location_id,'CHICA_PCC',
b.location_attribute_id from chirdlutil_location_attribute b,location c
where b.name='medicationListQueryUser' and c.name='PCPS';

insert into chirdlutil_location_attribute_value(location_id,value,location_attribute_id)
select c.location_id,'CHICA_PECAR',
b.location_attribute_id from chirdlutil_location_attribute b,location c
where b.name='medicationListQueryUser' and c.name='PEPS';

update global_property
set property_value='C:\\chica\\CCD'
where property='rgccd.ccdDirectory';

update global_property
set property_value='C:\\chica\\config\\rgCCD.xsl'
where property='rgccd.convertCCDToHTMLFile';
