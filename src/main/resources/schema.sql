create table roles (
    id int(11) AUTO_INCREMENT,
    name varchar(24) NOT NULL,
    PRIMARY KEY (id)
);

create table users (
    id int(11) AUTO_INCREMENT,
    username varchar(64) NOT NULL UNIQUE,
    email varchar(64) NOT NULL UNIQUE,
    password varchar(64) NOT NULL,
    PRIMARY KEY(id)
);

create table user_roles (
    user_id int(11) NOT NULL,
    role_id int(11) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);


