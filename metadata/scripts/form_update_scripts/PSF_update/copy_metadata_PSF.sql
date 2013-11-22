/*copy concept, default_value, and field_type to the new form*/
update field f1 
join (select * from field where field_id in (select field_id from form_field where form_id in (select form_id from form where name='PSF' and retired=0))) f2
on f1.name=f2.name
set f1.concept_id=f2.concept_id, f1.default_value=f2.default_value, f1.field_type=f2.field_type
where f1.field_id in (select field_id from form_field where form_id in (select form_id from form where name='PSF_new'));

/*copy field_number to the new form*/
update form_field f1,
(select b.name,a.* from (select * from form_field where form_id in (select form_id from form where name='PSF' and retired=0)) a
inner join field b
on a.field_id=b.field_id
) f2,
(select b.name,a.* from (select * from form_field where form_id in (select form_id from form where name='PSF_new')) a
inner join field b
on a.field_id=b.field_id
) f3 
set f1.field_number=f2.field_number
where  f1.form_field_id=f3.form_field_id and f2.name=f3.name;

/*copy parent_field mapping to the new form*/
update form_field f1,
(
select b.name as parent_name,child_name from
(select child_name,b.field_id as parent_field_id from
(select b.name as child_name,a.child_field_id,a.parent_form_field from
(select field_id as child_field_id, parent_form_field from form_field where form_id in (select form_id from form where name='PSF' and retired=0)) a
inner join field b on a.child_field_id = b.field_id) a
inner join form_field b
on a.parent_form_field=b.form_field_id) a
inner join field b
on a.parent_field_id=b.field_id
)f2,
(
select a.form_field_id,b.name from(
select form_field_id,field_id from form_field where form_id in (select form_id from form where name='PSF_new')) a
inner join field b on a.field_id =b.field_id
)f3,
(select a.form_field_id,b.name from(
select form_field_id,field_id from form_field where form_id in (select form_id from form where name='PSF_new')) a
inner join field b on a.field_id =b.field_id)
f4
set f1.parent_form_field=f3.form_field_id
where f2.parent_name=f3.name and f4.name=f2.child_name and f1.form_field_id=f4.form_field_id;

/* add rows in atd_form_attribute_value for new form */
insert into atd_form_attribute_value(form_id,value,form_attribute_id,location_tag_id,location_id)
select b.form_id,value,form_attribute_id,location_tag_id,location_id from atd_form_attribute_value a,(select form_id from form where name='PSF_new') b where a.form_id in (
select form_id from form where name='PSF' and retired=0);