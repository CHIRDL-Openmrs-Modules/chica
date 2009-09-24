/* update atd_state form_id's with new forms */
update atd_state 
set form_id=(select form_id from form where name='PSF_new' )
where form_id in (
select form_id from form where name='PSF' and retired=0);

/* retire old PSF form */
update form 
set retired=1
where name='PSF' and retired=0;

/* rename new PSF form from PSF_new to PSF */
update form 
set name='PSF'
where name='PSF_new';