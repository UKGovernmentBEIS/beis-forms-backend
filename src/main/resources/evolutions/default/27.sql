# --- !Ups

delete from application_form_section where section_number > 4;
delete from application_form_question where key = 'item';

insert into application_form_section values (5,1,5,'Contact Details', '[{"isNumeric": false, "maxWords": 20, "type": "contact", "name": "contactDetails"}]','form');
insert into application_form_section values (6,1,6,'Access needs', '[{"maxWords":500,"type":"textArea","name":"accessNeeds"}]','form');
insert into application_form_section values (7,1,7,'Funds received previously', '[{"maxWords":500,"type":"textArea","name":"fundsReceived"}]','form');

insert into application_form_question values (5,5,'contactDetails', 'What are the company contact Details?', 'Contact Details', 'contactDetails help_text');
insert into application_form_question values (6,6,'accessNeeds', 'Do you have any access needs that we should be aware of?', 'Access Needs', 'Access Needs help_text');
insert into application_form_question values (7,7,'fundsReceived', 'Has your organisation received funding through BESN previously?', ' Funds Received', 'Funds Received help_text');

ALTER TABLE "application" ADD COLUMN "user_id" VARCHAR(50) NOT NULL, ADD COLUMN "status" VARCHAR(20) NOT NULL;
update "application" set "user_id" = 'testuser', status= 'In progress' where id = 1;


# -- !Downs

insert into application_form_question values (9,9,'item', 'We will pay up to £2,000 towards equipment', 'Costitem description_text', 'Costitem help_text');
insert into application_form_section values (6,1,6,'Cost of Item', '[{"type":"costItem","name":"item"}]','list');
delete from application_form_question;
delete from application_form_section;

UPDATE "application" set "user_id" = null;
