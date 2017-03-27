# --- !Ups

delete from application_form_section where section_number = 6;
delete from application_form_question where key = 'item';

insert into application_form_section values (6,1,6,'Contact Details', '[{"isNumeric": false, "maxWords": 20, "type": "contact", "name": "contactDetails"}]','form');
insert into application_form_section values (7,1,7,'Access needs', '[{"maxWords":500,"type":"textArea","name":"accessNeeds"}]','form');
insert into application_form_section values (8,1,8,'Funds received previously', '[{"maxWords":500,"type":"textArea","name":"fundsReceived"}]','form');

insert into application_form_question values (6,6,'contactDetails', 'What are the company contact Details?', 'Contact Details', 'contactDetails help_text');
insert into application_form_question values (7,7,'accessNeeds', 'Do you have any access needs that we should be aware of?', 'Access Needs', 'Access Needs help_text');
insert into application_form_question values (8,8,'fundsReceived', 'Has your organisation received funding through BESN previously?', ' Funds Received', 'Funds Received help_text');


# -- !Downs

insert into application_form_question values (9,9,'item', 'We will pay up to £2,000 towards equipment', 'Costitem description_text', 'Costitem help_text');
insert into application_form_section values (6,1,6,'Cost of Item', '[{"type":"costItem","name":"item"}]','list');
delete from application_form_question;
delete from application_form_section;