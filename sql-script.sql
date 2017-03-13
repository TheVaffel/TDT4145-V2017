CREATE TABLE Ovelser (navn VARCHAR(50),
       	     	    beskrivelse VARCHAR(100),
		    belastning INT,
		    repetisjoner INT,
		    sett INT,
		    kalorierPerKilo FLOAT,
		    PRIMARY KEY(navn));

CREATE TABLE RelaterteOvelser ( ovelse1 VARCHAR (50),
       	     		      	ovelse2 VARCHAR (50),
				PRIMARY KEY(ovelse1, ovelse2),
				FOREIGN KEY(ovelse1) REFERENCES Ovelser(navn),
				FOREIGN KEY(ovelse2) REFERENCES Ovelser(navn));



CREATE TABLE Grupper ( navn VARCHAR(50),
       	     	       PRIMARY KEY(navn));

CREATE TABLE OvelseIGruppe (gruppe VARCHAR (50),
       	     		   ovelse VARCHAR(50),
			   PRIMARY KEY(gruppe, ovelse),
			   FOREIGN KEY(gruppe) REFERENCES Grupper(navn),
			   FOREIGN KEY(ovelse) REFERENCES Ovelser(navn));


CREATE TABLE Maler (navn VARCHAR(50),
       	     	  PRIMARY KEY(navn));

CREATE TABLE OvelseIMal (ovelse VARCHAR(50),
       	     		mal VARCHAR(50),
       	     	       	PRIMARY KEY(ovelse, mal),
			FOREIGN KEY(ovelse) REFERENCES Ovelser(navn),
			FOREIGN KEY(mal) REFERENCES Maler(navn));

CREATE TABLE Treningsokter (oktID INT,
       	     		 tid TIME,
			 dato DATE,
       	     		 form INT,
			 prestasjon INT,
			 notat VARCHAR(1024),
			 brukerID VARCHAR(50),
			 PRIMARY KEY (oktID),
			 FOREIGN KEY(brukerID) REFERENCES Brukere(brukerID));

CREATE TABLE Brukere (brukerID VARCHAR(50),
       	     	    vekt REAL,
		    hoyde REAL,
		    fodselsar INT,
		    kjonn CHAR(1),
		    PRIMARY KEY (brukerID));

CREATE TABLE Mal (malID INT,
       	     	 ovelse VARCHAR(50),
		 brukerID VARCHAR(50),
		 datoSatt DATE,
		 datoNadd DATE,
		 datoFrist DATE,
		 PRIMARY KEY (malID),
		 FOREIGN KEY (ovelse) REFERENCES Ovelser(navn),
		 FOREIGN KEY (brukerID) REFERENCES Brukere(brukerID));
		 

CREATE TABLE Ovelsesresultater ( oktID INT,
       	     		      	ovelse VARCHAR(50),
				tid FLOAT,
				lengde FLOAT,
				PRIMARY KEY (oktID, ovelse),
				FOREIGN KEY(oktID) REFERENCES Treningsokter(oktID),
				FOREIGN KEY(ovelse) REFERENCES Ovelser(navn));

CREATE TABLE GPSKoordinater ( oktID INT,
       	     		      ovelse VARCHAR(50),
			      indeks INT,
			      tidspunkt TIME,
			      puls FLOAT,
			      lengdegrad FLOAT,
			      breddegrad FLOAT,
			      hoyde FLOAT,
			      PRIMARY KEY (oktID, ovelse, indeks),
			      FOREIGN KEY (oktID) REFERENCES Treningsokter(oktID),
			      FOREIGN KEY (ovelse) REFERENCES Ovelser(navn));

INSERT INTO Ovelser VALUES('Langsykling', 'Sykkeltur på over 30 km', 7, 1, 1, 120.0);
INSERT INTO Ovelser VALUES('Maraton', 'Lang loepetur', 8, 1, 1, 176.54);
INSERT INTO Ovelser VALUES('Korte Intervaller', 'Intervalltrening med korte intervaller og mange repetisjoner', 9, 5, 3, 200);

INSERT INTO Brukere VALUES('test', 50, 1.80, 1998, 'M');
INSERT INTO Treningsokter VALUES(0, "10:00:00", "20170101", 5, 5, 'Helt ok', 'test');
INSERT INTO Treningsokter VALUES(1, "10:00:05", "20170102", 6, 8, 'Var bedre', 'test');
INSERT INTO Treningsokter VALUES(2, "10:00:05", "20170302", 6, 6, 'Ok', 'test');
INSERT INTO Treningsokter VALUES(3, "10:00:05", "20170305", 7, 7, 'Hui', 'test');
