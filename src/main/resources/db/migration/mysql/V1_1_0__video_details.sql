CREATE TABLE video_details (
	id varchar(15) primary key,
	location varchar(500) not null,
	hash varchar(50) not null,
	file_size float(2) not null,
	created_at DATETIME not null
);