/* update atd_state form_id's with new forms */
update atd_state 
set form_id=(select form_id from form where name='PWS_new' )
where form_id in (
select form_id from form where name='PWS' and retired=0);

/* retire old PWS form */
update form 
set retired=1
where name='PWS' and retired=0;

/* rename new PWS form from PWS_new to PWS */
update form 
set name='PWS'
where name='PWS_new';