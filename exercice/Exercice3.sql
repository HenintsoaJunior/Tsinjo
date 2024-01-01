CREATE TABLE Personne (
    CIN VARCHAR(20) PRIMARY KEY,
    NOM VARCHAR(20) NOT NULL,
    Prenom VARCHAR(20) NOT NULL,
    Adresse VARCHAR(20)
);

CREATE TABLE Voiture (
    NCarteGirse VARCHAR(20) PRIMARY KEY,
    CIN VARCHAR(20) REFERENCES Personne(CIN),
    Modele VARCHAR(20) NOT NULL
);

CREATE TABLE Moto (
    NCarteGrise VARCHAR(20) PRIMARY KEY,
    CIN VARCHAR(20) REFERENCES Personne(CIN),
    Modele VARCHAR(20) NOT NULL
);

-- 1) Afficher les personnes qui possedent une voiture mais pas de moto
π Personne.CIN, Personne.NOM, Personne.Prenom, Personne.Adresse (σ Voiture.CIN = Personne.CIN (Personne x Voiture)) - π Personne.CIN, Personne.NOM, Personne.Prenom, Personne.Adresse (σ Moto.CIN = Personne.CIN (Personne x Moto))

SELECT * FROM Personne
WHERE EXISTS (SELECT 1 FROM Voiture WHERE Voiture.CIN = Personne.CIN)
AND NOT EXISTS (SELECT 1 FROM Moto WHERE Moto.CIN = Personne.CIN);


-- 2) Afficher les personnes qui possedent une voiture et une moto
π Personne.CIN, Personne.NOM, Personne.Prenom, Personne.Adresse (σ Voiture.CIN = Personne.CIN (Personne x Voiture)) ∩ π Personne.CIN, Personne.NOM, Personne.Prenom, Personne.Adresse (σ Moto.CIN = Personne.CIN (Personne x Moto))

SELECT * FROM Personne
WHERE EXISTS (SELECT 1 FROM Voiture WHERE Voiture.CIN = Personne.CIN)
AND EXISTS (SELECT 1 FROM Moto WHERE Moto.CIN = Personne.CIN);

-- 3) Afficher les personnes qui ne possedent ni voiture ni moto
Personne - π Personne.CIN, Personne.NOM, Personne.Prenom, Personne.Adresse (σ Voiture.CIN = Personne.CIN (Personne x Voiture)) - π Personne.CIN, Personne.NOM, Personne.Prenom, Personne.Adresse (σ Moto.CIN = Personne.CIN (Personne x Moto))

SELECT * FROM Personne
WHERE NOT EXISTS (SELECT 1 FROM Voiture WHERE Voiture.CIN = Personne.CIN)
AND NOT EXISTS (SELECT 1 FROM Moto WHERE Moto.CIN = Personne.CIN);


