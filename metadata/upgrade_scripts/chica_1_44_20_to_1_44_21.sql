update concept_name a,concept b, concept_class c set 
a.name=concat(a.name,' CHICA') where a.name in ('BMI', 'PULSE', 
'TEMPERATURE', 'RR', 'PAST MEDICAL HISTORY'
)
and c.name ='CHICA' and a.concept_id=b.concept_id and 
b.class_id=c.concept_class_id;