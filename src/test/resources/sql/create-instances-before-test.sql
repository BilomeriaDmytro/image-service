delete from images;
delete from image_tags;
delete from accounts;
delete from tags;

insert into accounts values('1',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '$2a$10$wdMbP8cjXAspQaKVPR4OC.acVOpCVZX0xuNKT3DHmodxNj9vwRccC', '0', 'testuser1');
insert into accounts values('2',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '$2a$10$wdMbP8cjXAspQaKVPR4OC.acVOpCVZX0xuNKT3DHmodxNj9vwRccC', '0', 'testuser2');
insert into accounts values('3',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '$2a$10$wdMbP8cjXAspQaKVPR4OC.acVOpCVZX0xuNKT3DHmodxNj9vwRccC', '0', 'testuser3');

insert into images values('1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'image/jpeg', 'testimage1', 'storage/account-1/11_testimage1.jpg', '43212', '1');
insert into images values('2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'image/jpeg', 'testimage2', 'storage/account-1/12_testimage2.jpg', '12421', '1');
insert into images values('3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'image/jpeg', 'testimage3', 'storage/account-1/13_testimage3.jpg', '65212', '1');
insert into images values('4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'image/jpeg', 'testimage4', 'storage/account-2/24_testimage4.jpg', '32543', '2');
insert into images values('5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'image/jpeg', 'testimage5', 'storage/account-2/25_testimage5.jpg', '73190', '2');
insert into images values('6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'image/jpeg', 'testimage6', 'storage/account-2/26_testimage6.jpg', '91242', '2');
insert into tags values('1', 'Tag1');
insert into tags values('2', 'Tag2');
insert into tags values('3', 'Tag3');
insert into tags values('4', 'Tag4');
insert into tags values('5', 'Tag5');

insert into image_tags values('1', '1');
insert into image_tags values('1', '2');
insert into image_tags values('2', '2');
insert into image_tags values('2', '3');
insert into image_tags values('3', '1');
insert into image_tags values('4', '3');
insert into image_tags values('5', '2');
insert into image_tags values('6', '1');
insert into image_tags values('6', '2');
insert into image_tags values('6', '3');
insert into image_tags values('1', '5');
insert into image_tags values('2', '5');
insert into image_tags values('3', '5');
insert into image_tags values('4', '5');
insert into image_tags values('5', '5');