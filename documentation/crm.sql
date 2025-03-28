CREATE TABLE roles(
   id INT AUTO_INCREMENT,
   name VARCHAR(255)  NOT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE users(
   id INT AUTO_INCREMENT,
   email VARCHAR(100)  NOT NULL,
   password VARCHAR(255)  DEFAULT NUL,
   hire_date DATETIME DEFAULT NULL,
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   username VARCHAR(50)  NOT NULL,
   status VARCHAR(50)  DEFAULT NULL,
   token VARCHAR(200)  DEFAULT NULL,
   is_password_set BOOLEAN DEFAULT '0',
   PRIMARY KEY(id),
   UNIQUE(email),
   UNIQUE(username)
);

CREATE TABLE oauth_users(
   id INT AUTO_INCREMENT,
   access_token VARCHAR(255)  NOT NULL,
   access_token_issued_at DATETIME NOT NULL,
   access_token_expiration DATETIME NOT NULL,
   refresh_token VARCHAR(255)  NOT NULL,
   refresh_token_issued_at DATETIME NOT NULL,
   refresh_token_expiration DATETIME DEFAULT NULL,
   granted_scopes VARCHAR(255)  DEFAULT NULL,
   email VARCHAR(255)  DEFAULT NULL,
   id_user INT,
   PRIMARY KEY(id),
   UNIQUE(email),
   FOREIGN KEY(id_user) REFERENCES users(id)
);

CREATE TABLE user_profile(
   id INT AUTO_INCREMENT,
   first_name VARCHAR(50)  DEFAULT NULL,
   last_name VARCHAR(50)  DEFAULT NULL,
   phone VARCHAR(50)  DEFAULT NULL,
   department VARCHAR(255)  DEFAULT NULL,
   salary DECIMAL(10,2)   DEFAULT NULL,
   status VARCHAR(50)  DEFAULT NULL,
   oauth_user_image_link VARCHAR(255)  DEFAULT NULL,
   user_image BLOB,
   bio TEXT,
   youtube VARCHAR(255)  DEFAULT NULL,
   twitter VARCHAR(255)  DEFAULT NULL,
   facebook VARCHAR(255)  DEFAULT NULL,
   country VARCHAR(100)  DEFAULT NULL,
   _position_ VARCHAR(100)  DEFAULT NULL,
   address VARCHAR(255)  DEFAULT NULL,
   id_user INT,
   PRIMARY KEY(id),
   FOREIGN KEY(id_user) REFERENCES users(id)
);

CREATE TABLE employee(
   id INT AUTO_INCREMENT,
   username VARCHAR(45)  NOT NULL,
   first_name VARCHAR(45)  NOT NULL,
   last_name VARCHAR(45)  NOT NULL,
   email VARCHAR(45)  NOT NULL,
   password VARCHAR(80)  NOT NULL,
   provider VARCHAR(45)  DEFAULT NULL,
   PRIMARY KEY(id)
);

CREATE TABLE email_template(
   id INT AUTO_INCREMENT,
   name VARCHAR(255)  DEFAULT NULL,
   content TEXT,
   json_design TEXT,
   created_at DATETIME DEFAULT NULL,
   id_user INT,
   PRIMARY KEY(id),
   UNIQUE(name),
   FOREIGN KEY(id_user) REFERENCES users(id)
);

CREATE TABLE customer_login_info(
   id INT AUTO_INCREMENT,
   password VARCHAR(255)  DEFAULT NULL,
   username VARCHAR(255)  DEFAULT NULL,
   token VARCHAR(500)  DEFAULT NULL,
   password_set TINYINT (1) DEFAULT '0',
   PRIMARY KEY(id),
   UNIQUE(token)
);

CREATE TABLE customer(
   id INT AUTO_INCREMENT,
   name VARCHAR(255)  DEFAULT NULL,
   phone VARCHAR(20)  DEFAULT NULL,
   address VARCHAR(255)  DEFAULT NULL,
   city VARCHAR(255)  DEFAULT NULL,
   state VARCHAR(255)  DEFAULT NULL,
   country VARCHAR(255)  DEFAULT NULL,
   description TEXT,
   _position_ VARCHAR(255)  DEFAULT NULL,
   twitter VARCHAR(255)  DEFAULT NULL,
   facebook VARCHAR(255)  DEFAULT NULL,
   youtoube VARCHAR(255)  DEFAULT NULL,
   created_at DATETIME DEFAULT NULL,
   email VARCHAR(255)  DEFAULT NULL,
   id_user INT,
   id_profile INT,
   PRIMARY KEY(id),
   FOREIGN KEY(id_user) REFERENCES users(id),
   FOREIGN KEY(id_profile) REFERENCES customer_login_info(id)
);

CREATE TABLE trigger_lead(
   id INT AUTO_INCREMENT,
   name VARCHAR(255)  DEFAULT NULL,
   phone VARCHAR(20)  DEFAULT NULL,
   status VARCHAR(50)  DEFAULT NULL,
   meeting_id VARCHAR(255)  DEFAULT NULL,
   google_drive TINYINT (1) DEFAULT NULL,
   google_drive_folder_id VARCHAR(255)  DEFAULT NULL,
   created_at DATETIME DEFAULT NULL,
   id_employee INT,
   id_customer INT NOT NULL,
   id_user INT,
   PRIMARY KEY(id),
   UNIQUE(meeting_id),
   FOREIGN KEY(id_employee) REFERENCES employee(id),
   FOREIGN KEY(id_customer) REFERENCES customer(id),
   FOREIGN KEY(id_user) REFERENCES users(id)
);

CREATE TABLE trigger_ticket(
   id INT AUTO_INCREMENT,
   subject VARCHAR(255)  DEFAULT NULL,
   description TEXT,
   status VARCHAR(50)  DEFAULT NULL,
   priority VARCHAR(50)  DEFAULT NULL,
   created_at DATETIME DEFAULT NULL,
   id_manager INT,
   id_employee INT,
   PRIMARY KEY(id),
   FOREIGN KEY(id_manager) REFERENCES users(id),
   FOREIGN KEY(id_employee) REFERENCES users(id)
);

CREATE TABLE trigger_contract(
   id INT AUTO_INCREMENT,
   subject VARCHAR(255)  DEFAULT NULL,
   status VARCHAR(100)  DEFAULT NULL,
   description TEXT,
   start_date DATE DEFAULT NULL,
   end_date DATE DEFAULT NULL,
   amount DECIMAL(10,0)   DEFAULT NULL,
   google_drive TINYINT (1) DEFAULT NULL,
   google_drive_folder_id VARCHAR(255)  DEFAULT NULL,
   created_at DATETIME DEFAULT NULL,
   id_lead INT,
   id_customer INT,
   id_user INT,
   PRIMARY KEY(id),
   FOREIGN KEY(id_lead) REFERENCES trigger_lead(id),
   FOREIGN KEY(id_customer) REFERENCES customer(id),
   FOREIGN KEY(id_user) REFERENCES users(id)
);

CREATE TABLE contract_settings(
   id INT AUTO_INCREMENT,
   amount TINYINT (1) DEFAULT NULL,
   subject TINYINT (1) DEFAULT NULL,
   description TINYINT (1) DEFAULT NULL,
   end_date TINYINT (1) DEFAULT NULL,
   start_date TINYINT (1) DEFAULT NULL,
   status TINYINT (1) DEFAULT NULL,
   end_email_template INT,
   start_email_template INT,
   description_email_template INT,
   subject_email_template INT,
   amount_email_template INT,
   status_email_template INT,
   id_user INT,
   PRIMARY KEY(id),
   FOREIGN KEY(end_email_template) REFERENCES email_template(id),
   FOREIGN KEY(start_email_template) REFERENCES email_template(id),
   FOREIGN KEY(description_email_template) REFERENCES email_template(id),
   FOREIGN KEY(subject_email_template) REFERENCES email_template(id),
   FOREIGN KEY(amount_email_template) REFERENCES email_template(id),
   FOREIGN KEY(status_email_template) REFERENCES email_template(id),
   FOREIGN KEY(id_user) REFERENCES users(id)
);

CREATE TABLE lead_action(
   id INT AUTO_INCREMENT,
   action VARCHAR(255)  DEFAULT NULL,
   date_time DATETIME DEFAULT NULL,
   id_lead INT,
   PRIMARY KEY(id),
   FOREIGN KEY(id_lead) REFERENCES trigger_lead(id)
);

CREATE TABLE lead_settings(
   id INT AUTO_INCREMENT,
   status TINYINT (1) DEFAULT NULL,
   meeting TINYINT (1) DEFAULT NULL,
   phone TINYINT (1) DEFAULT NULL,
   name TINYINT (1) DEFAULT NULL,
   id_customer INT,
   id_user INT,
   status_email_template INT,
   name_email_template INT,
   meeting_email_template INT,
   phone_email_template INT,
   PRIMARY KEY(id),
   FOREIGN KEY(id_customer) REFERENCES customer_login_info(id),
   FOREIGN KEY(id_user) REFERENCES users(id),
   FOREIGN KEY(status_email_template) REFERENCES email_template(id),
   FOREIGN KEY(name_email_template) REFERENCES email_template(id),
   FOREIGN KEY(meeting_email_template) REFERENCES email_template(id),
   FOREIGN KEY(phone_email_template) REFERENCES email_template(id)
);

CREATE TABLE ticket_settings(
   id INT AUTO_INCREMENT,
   priority TINYINT (1) DEFAULT NULL,
   subject TINYINT (1) DEFAULT NULL,
   description TINYINT (1) DEFAULT NULL,
   status TINYINT (1) DEFAULT NULL,
   id_customer INT,
   id_1 INT,
   description_email_template INT,
   priority_email_template INT,
   subject_email_template INT,
   status_email_template INT,
   PRIMARY KEY(id),
   FOREIGN KEY(id_customer) REFERENCES customer_login_info(id),
   FOREIGN KEY(id_1) REFERENCES users(id),
   FOREIGN KEY(description_email_template) REFERENCES email_template(id),
   FOREIGN KEY(priority_email_template) REFERENCES email_template(id),
   FOREIGN KEY(subject_email_template) REFERENCES email_template(id),
   FOREIGN KEY(status_email_template) REFERENCES email_template(id)
);

CREATE TABLE file(
   id INT AUTO_INCREMENT,
   file_name VARCHAR(100)  DEFAULT NULL,
   file_data BLOB,
   file_type VARCHAR(255)  DEFAULT NULL,
   id_lead INT,
   id_contract INT,
   PRIMARY KEY(id),
   FOREIGN KEY(id_lead) REFERENCES trigger_lead(id),
   FOREIGN KEY(id_contract) REFERENCES trigger_contract(id)
);

CREATE TABLE google_drive_file(
   id INT AUTO_INCREMENT,
   drive_file_id VARCHAR(255)  DEFAULT NULL,
   drive_folder_id VARCHAR(255)  DEFAULT NULL,
   id_lead INT,
   id_contract INT,
   PRIMARY KEY(id),
   FOREIGN KEY(id_lead) REFERENCES trigger_lead(id),
   FOREIGN KEY(id_contract) REFERENCES trigger_contract(id)
);

CREATE TABLE user_roles(
   id_rolse INT,
   id_user INT,
   PRIMARY KEY(id_rolse, id_user),
   FOREIGN KEY(id_rolse) REFERENCES roles(id),
   FOREIGN KEY(id_user) REFERENCES users(id)
);
