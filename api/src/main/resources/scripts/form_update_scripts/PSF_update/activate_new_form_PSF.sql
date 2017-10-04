/* retire old PSF form */
update form 
set retired=1
where name='PSF' and retired=0;

/* rename new PSF form from PSF_new to PSF */
update form 
set name='PSF'
where name='PSF_new';

/* update atd_state form_id's with new forms */
update chirdlutil_location_tag_attribute_value 
set value=(select form_id from form where name='PSF' and retired=0)
where location_tag_attribute_id in (select location_tag_attribute_id 
from chirdlutil_location_tag_attribute where name='PSF')