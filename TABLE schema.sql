CREATE DATABASE ebank;

USE ebank;

CREATE TABLE admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(30) NOT NULL
);

INSERT INTO admin(username, password) VALUES('makarand3103', 'mak');
INSERT INTO admin(username, password) VALUES('shreyaa3123', 'mak');

CREATE TABLE user (
    accountNo BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(30) NOT NULL,
    middle_name VARCHAR(30),
    last_name VARCHAR(30) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    card_number BIGINT UNIQUE NOT NULL,
    pan_card_number VARCHAR(10) UNIQUE,
    valid_till DATE NOT NULL,
    register_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pin CHAR(4),
    balance DOUBLE DEFAULT 2000
);

ALTER TABLE user AUTO_INCREMENT = 10000001;

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(20),
    type VARCHAR(10),
    amount DOUBLE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fixed_deposits
(
	fd_id INT PRIMARY KEY AUTO_INCREMENT,
    accountNo BIGINT,
    amount DOUBLE,
    interest_rate DOUBLE,
    duration_months INT,
    start_date DATE,
    maturity_date DATE,
    status VARCHAR(20), -- e.g. Active, Matured
    FOREIGN KEY (accountNo) REFERENCES user(accountNo)
);
