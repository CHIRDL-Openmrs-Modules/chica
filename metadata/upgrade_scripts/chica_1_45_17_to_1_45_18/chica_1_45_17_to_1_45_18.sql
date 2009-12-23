insert into chirdlutil_location_tag_attribute_value(location_tag_id,value,location_tag_attribute_id,location_id)
select a.location_tag_id,'C://chica//config//unfilteredMeds.xml',b.location_tag_attribute_id,c.location_id from location_tag a, chirdlutil_location_tag_attribute b, location c
where a.tag='PED1' and b.name='unfilteredMedicationFile' and c.name='PCPS';

insert into chirdlutil_location_tag_attribute_value(location_tag_id,value,location_tag_attribute_id,location_id)
select a.location_tag_id,'C://chica//config//unfilteredMeds.xml',b.location_tag_attribute_id,c.location_id from location_tag a, chirdlutil_location_tag_attribute b, location c
where a.tag='PED2' and b.name='unfilteredMedicationFile' and c.name='PCPS';

insert into chirdlutil_location_tag_attribute_value(location_tag_id,value,location_tag_attribute_id,location_id)
select a.location_tag_id,'C://chica//config//unfilteredMeds.xml',b.location_tag_attribute_id,c.location_id from location_tag a, chirdlutil_location_tag_attribute b, location c
where a.tag='Other - PCC' and b.name='unfilteredMedicationFile' and c.name='PCPS';

insert into chirdlutil_location_tag_attribute_value(location_tag_id,value,location_tag_attribute_id,location_id)
select a.location_tag_id,'C://chica//config//unfilteredMeds.xml',b.location_tag_attribute_id,c.location_id from location_tag a, chirdlutil_location_tag_attribute b, location c
where a.tag='PEREG3' and b.name='unfilteredMedicationFile' and c.name='PEPS';

insert into chirdlutil_location_tag_attribute_value(location_tag_id,value,location_tag_attribute_id,location_id)
select a.location_tag_id,'C://chica//config//unfilteredMeds.xml',b.location_tag_attribute_id,c.location_id from location_tag a, chirdlutil_location_tag_attribute b, location c
where a.tag='Other - PECAR' and b.name='unfilteredMedicationFile' and c.name='PEPS';