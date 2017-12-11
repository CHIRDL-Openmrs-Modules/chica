/* retire old PWS form */
update form 
set retired=1
where name='PWS' and retired=0;

/* rename new PWS form from PWS_new to PWS */
update form 
set name='PWS'
where name='PWS_new';

/* update atd_state form_id's with new forms */
update chirdlutil_location_tag_attribute_value 
set value=(select form_id from form where name='PWS' and retired=0)
where location_tag_attribute_id in (select location_tag_attribute_id 
from chirdlutil_location_tag_attribute where name='PWS')

