CREATE DATABASE IF NOT EXISTS cybershield;

USE cybershield;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    status BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS incidents (
    id INT PRIMARY KEY AUTO_INCREMENT,

    title VARCHAR(200) NOT NULL,

    description TEXT NOT NULL,

    category VARCHAR(50) NOT NULL,

    severity VARCHAR(20) NOT NULL,

    status VARCHAR(30) DEFAULT 'OPEN',

    reported_by INT NOT NULL,

    assigned_to INT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (reported_by)
        REFERENCES users(id),

    FOREIGN KEY (assigned_to)
        REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS activity_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    incident_id INT NULL,
    user_id INT NOT NULL,
    action VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (incident_id) REFERENCES incidents(id)
        ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);