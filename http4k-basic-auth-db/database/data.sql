CREATE DATABASE IF NOT EXISTS http4k_mysql;

USE http4k_mysql;

CREATE TABLE IF NOT EXISTS `users`
(
    id         int          not null auto_increment,
    username   varchar(50)  not null,
    password   varchar(60)  not null,
    first_name varchar(120) not null,
    last_name  varchar(120) not null,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE
);

INSERT IGNORE INTO `users` (`id`, `username`, `password`, `first_name`, `last_name`) VALUES
    (1, 'user', '$2a$12$AcPJ5D0I1XXvSjDWgZGO4OJ9x33VRxPy/BqtNLe.pOaUmZpMD2EK.', 'Steven', 'Zgaljic');