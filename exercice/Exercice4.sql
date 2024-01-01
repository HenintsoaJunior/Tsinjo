CREATE TABLE Etudiant(
    NumEtd serial PRIMARY KEY,
    NomEtd VARCHAR(20) NOT NULL,
    PrenomEdt VARCHAR(20) NOT NULL,
    Adresse VARCHAR(20) NOT NULL
);

CREATE TABLE Livre (
    NumLivre serial PRIMARY KEY,
    TitreLivre VARCHAR(20) NOT NULL,
    NumAuteur int REFERENCES Auteur(NumAuteur),
    NumEditeur int REFERENCES Editeur(NumEditeur),
    NumTheme int REFERENCES Theme(NumTheme),
    AnneeEdition date NOT NULL
);

CREATE TABLE Auteur(
    NumAuteur serial PRIMARY KEY,
    NomAuteur VARCHAR(20) NOT NULL,
    AdresseAuteur VARCHAR(20) NOT NULL
);

CREATE TABLE Editeur(
    NumEditeur serial PRIMARY KEY,
    NomEditeur VARCHAR(20) NOT NULL,
    AdresseEditeur VARCHAR(20) NOT NULL
);

CREATE TABLE Theme (
    NumTheme serial PRIMARY KEY,
    IntituleTheme VARCHAR(20) NOT NULL
);

CREATE TABLE Pret (
    NumEtd int REFERENCES Etudiant(NumEtd),
    NumLivre int REFERENCES Livre(NumLivre),
    DatePret date NOT NULL,
    DateRetour date
);


-- 1) Le nom,le prenom et l'adresse de l'etudiant de nom 'Alami'

-- 2) Le numero de l'auteur 'Alami'

-- 3) La liste des livre de l'auteur numero 121

-- 4) Les livres de l'auteur nom 'Alami'

-- 5) Le numero de l'auteur du livre comment avoir 20 en BDD

-- 6) Le nom et l'adresse de l'auteur du livre comment avoir 20 en BDD

-- 7) Les livres de l'auteur Alami <<Edites chez l'editeur Null part

-- 8) Les livres de l'auteur Alami ou Belhadj

-- 9)Les livre qui n'on jamais ete empruntes