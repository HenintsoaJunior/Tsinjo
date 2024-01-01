CREATE TABLE CJH (
    IdCours VARCHAR(20),
    Jour VARCHAR(20),
    Heure VARCHAR(20)
);

INSERT INTO CJH (IdCours,Jour,Heure) VALUES
('Archi','Lu','9h'),
('Algo','Ma','9h'),
('Algo','Ve','9h'),
('Syst','Ma','14h');

CREATE TABLE ENA (
    IdEtudiant int(20) PRIMARY KEY,
    Nom VARCHAR(20),
    Adresse VARCHAR(20)
);
INSERT INTO CJH (IdEtudiant,Nom,Adresse) VALUES
(100,'Toto','Nice'),
(200,'Tata','Paris'),
(300,'Titi','Rome');


CREATE TABLE CS (
    IdCours VARCHAR(20) PRIMARY KEY,
    IdSalle VARCHAR(20)
);

INSERT INTO CJH (IdCours,IdSalle) VALUES
('Archi','S1'),
('Algo','S2'),
('Syst','S3');


CREATE TABLE CEN (
    IdCours VARCHAR(20),
    IdEtudiant int(20),
    note VARCHAR(20)
);

INSERT INTO CJH (IdCours,IdEtudiant,note) VALUES
('Archi',100,'A'),
('Archi',300,'A'),
('Syst',100,'B'),
('Syst',200,'A'),
('Syst',300,'B'),
('Algo',100,'C'),
('Algo',200,'A');