select a.name,b.* from
(select a.name,b.* from
(select * from form where name  in ('PWS_new')) a
inner join form_field b on a.form_id=b.form_id) a
inner join field b
on a.field_id=b.field_id
where b.name not in (
select b.name from
(select a.name,b.* from
(select * from form where name  in ('PWS') and retired=0) a
inner join form_field b on a.form_id=b.form_id) a
inner join field b
on a.field_id=b.field_id
);
