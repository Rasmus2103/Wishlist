drop database if exists wishlist;
CREATE DATABASE wishlist DEFAULT CHARACTER SET utf8;
USE wishlist;

CREATE TABLE user (
userid int auto_increment,
name varchar(30),
username varchar(30) unique,
password varchar(30),
primary key (userid)
);

CREATE TABLE wishlist (
wishlistid int auto_increment,
wishlistname varchar(30),
userid int not null,
primary key (wishlistid),
foreign key (userid) references user(userid)
);

CREATE TABLE wish (
wishid int auto_increment,
wishname varchar(30),
description varchar(255),
url varchar(255),
price varchar(30),
wishlistid int not null,
primary key (wishid),
foreign key (wishlistid) references wishlist(wishlistid)
);

insert into user (name,username,password) values
("peter", "peter123", "login123"),
("lars", "lars123", "login123"),
("kurt", "kurt123", "login123");

insert into wishlist (wishlistname, userid) value
("peters ønskeliste", 1),
("lars 70 års", 2),
("kurt ønskeliste", 3),
("lars påske tamtam", 2);

insert into wish (wishname,description,url,price, wishlistid) values
("computer", "computer jeg gerne vil have", "fiktivURL", "2000kr", 1),
("elon musk flammekaster", "jeg hader mysql", "fiktivURL", "3000kr", 1),
("diamant", "bedste karat muligt", "fiktivURL", "10 millioner kr", 2),
("toilet", "godt toilet", "fiktivURL", "4000 kr", 3),
("fodbold", "fodbold trøje", "fiktivURL", "11 kr", 3),
("hejsa", "hej med dig", "fiktivURL", "111111 kr", 3),
("computer", "computer jeg gerne vil have", "fiktivURL", "2000kr", 4),
("elon musk flammekaster", "jeg hader mysql", "fiktivURL", "3000kr", 4);

commit;

