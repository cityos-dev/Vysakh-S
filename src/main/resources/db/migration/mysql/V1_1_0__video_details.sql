CREATE TABLE video_details (
	id int primary key AUTO_INCREMENT,
	location varchar(500) not null,
	hash varchar(50) not null,
	file_size float(2) not null,
	created_at DATETIME not null
);