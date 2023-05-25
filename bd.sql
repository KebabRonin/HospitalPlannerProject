--
-- PostgreSQL database dump
--

-- Dumped from database version 15.2
-- Dumped by pg_dump version 15.2

-- Started on 2023-05-26 00:02:56
--
-- TOC entry 237 (class 1255 OID 25228)
-- Name: add_constraints(); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.add_constraints()
    LANGUAGE plpgsql
    AS $$
DECLARE
	i RECORD;
BEGIN
	FOR i IN (select * from information_schema.tables where table_schema = 'public') loop
		IF (select COUNT(*) from information_schema.columns where table_name = i.table_name AND column_name = 'id') > 0 THEN
			EXECUTE FORMAT('CREATE OR REPLACE TRIGGER %s_maxId before insert on %s for each row execute procedure maxId()', i.table_name, i.table_name);
		END IF;
	end loop;
END;
$$;


ALTER PROCEDURE public.add_constraints() OWNER TO postgres;

--
-- TOC entry 223 (class 1255 OID 25200)
-- Name: choice(character varying[]); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.choice(p_list character varying[]) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
BEGIN
	RETURN p_list[(random() * (array_length(p_list, 1) - 1) + 1)::INT];
END;
$$;


ALTER FUNCTION public.choice(p_list character varying[]) OWNER TO postgres;

--
-- TOC entry 235 (class 1255 OID 25213)
-- Name: delete_all_data(); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_all_data()
    LANGUAGE plpgsql
    AS $$
BEGIN
	delete from programari;
	delete from pacienti;
	delete from doctori_specializari;
	delete from program_doctori;
	delete from doctori;
	delete from specializari;
	delete from cabinete;
END;
$$;


ALTER PROCEDURE public.delete_all_data() OWNER TO postgres;

--
-- TOC entry 221 (class 1255 OID 16512)
-- Name: get_specializations_by_doctor_id(bigint); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_specializations_by_doctor_id(doc_id bigint) RETURNS bigint[]
    LANGUAGE plpgsql
    AS $$
DECLARE
  spec_list BIGINT[];
BEGIN
  SELECT ARRAY_AGG(id_specializare) INTO spec_list
  FROM doctori_specializari
  WHERE id_doctor = doc_id;

  RETURN spec_list;
END;
$$;


ALTER FUNCTION public.get_specializations_by_doctor_id(doc_id bigint) OWNER TO postgres;

--
-- TOC entry 222 (class 1255 OID 25184)
-- Name: maxid(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.maxid() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
	v_temp BIGINT;
BEGIN
	EXECUTE FORMAT('SELECT MAX(id) FROM %s', TG_TABLE_NAME) INTO v_temp;
	IF v_temp IS NULL THEN
		v_temp := 0;
	END IF;
	NEW.id := v_temp + 1;
	RETURN NEW;
END;
$$;


ALTER FUNCTION public.maxid() OWNER TO postgres;

--
-- TOC entry 239 (class 1255 OID 25385)
-- Name: normalize_hours(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.normalize_hours() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
	v_rec program_doctori%ROWTYPE;
BEGIN
	-- Lucrez 20 min
	IF (NEW.timp_final - NEW.timp_inceput)::TIME BETWEEN '00:00:01'::TIME AND '00:59:00'::TIME THEN
		RAISE EXCEPTION 'Cannot have less than 1 worked hour';
	END IF;
	
	-- Aliniez la ora
	IF NEW.timp_inceput <> '24:00:00'::TIME THEN
		NEW.timp_inceput := DATE_BIN(INTERVAL '1 hour', ('2023-01-01 ' ||NEW.timp_inceput)::TIMESTAMP, TIMESTAMP '2023-01-01 00:00:00');
	END IF;
	
	IF NEW.timp_final <> '24:00:00'::TIME THEN
		NEW.timp_final := DATE_BIN(INTERVAL '1 hour', ('2023-01-01 ' ||NEW.timp_final)::TIMESTAMP, TIMESTAMP '2023-01-01 00:00:00');
	END IF;

	-- Impart intervalul pe zile (daca am 9:00 - 9:00 zic ca lucreaza 24h)
	IF NEW.timp_inceput >= NEW.timp_final THEN
		v_rec := NEW;
		v_rec.timp_final := '24:00'::TIME;
		INSERT INTO program_doctori VALUES(v_rec.*);
		
		NEW.timp_inceput := '00:00'::TIME;
		NEW.zi := NEW.zi + '1 day'::INTERVAL;
	END IF;
	
	-- Vad daca nu se intersecteaza cu alte programe ale aceluiasi doctor
	DECLARE
			v_start1 TIMESTAMP;
			v_end1 TIMESTAMP;
			v_start2 TIMESTAMP;
			v_end2 TIMESTAMP;
			v_prog RECORD;
	BEGIN
		FOR v_prog IN (SELECT zi, timp_inceput, timp_final FROM program_doctori WHERE id_doctor = NEW.id_doctor) LOOP
				v_start1 := v_prog.zi || ' ' || v_prog.timp_inceput;
				v_end1 := v_prog.zi || ' ' || v_prog.timp_final;
				v_start2 := NEW.zi || ' ' || NEW.timp_inceput;
				v_end2 := NEW.zi || ' ' || NEW.timp_final;
				IF v_start1 < v_end2 AND v_end1 > v_start2 THEN
					RAISE EXCEPTION 'Intersection: % - % and % - %', v_start1, v_end1, v_start2, v_end2;
				END IF;

		END LOOP;
	END;
	RETURN NEW;
END;
$$;


ALTER FUNCTION public.normalize_hours() OWNER TO postgres;

--
-- TOC entry 238 (class 1255 OID 25352)
-- Name: populare_tabele(integer, integer, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.populare_tabele(IN p_nr_pacienti integer DEFAULT 10, IN p_nr_doctori integer DEFAULT 10, IN p_nr_programari integer DEFAULT 100)
    LANGUAGE plpgsql
    AS $$
DECLARE
	v_nume CHARACTER VARYING;
	v_prenume CHARACTER VARYING;
	v_nume_l CHARACTER VARYING[] := array['Adina','Alexandra','Alina','Ana','Anca','Anda','Andra','Andreea','Andreia','Antonia','Bianca','Camelia','Claudia','Codrina','Cristina','Daniela','Daria','Delia','Denisa','Diana','Ecaterina','Elena','Eleonora','Elisa','Ema','Emanuela','Emma','Gabriela','Georgiana','Ileana','Ilona','Ioana','Iolanda','Irina','Iulia','Iuliana','Larisa','Laura','Loredana','Madalina','Malina','Manuela','Maria','Mihaela','Mirela','Monica','Oana','Paula','Petruta','Raluca','Sabina','Sanziana','Simina','Simona','Stefana','Stefania','Tamara','Teodora','Theodora','Vasilica','Xena','Adrian','Alex','Alexandru','Alin','Andreas','Andrei','Aurelian','Beniamin','Bogdan','Camil','Catalin','Cezar','Ciprian','Claudiu','Codrin','Constantin','Corneliu','Cosmin','Costel','Cristian','Damian','Dan','Daniel','Danut','Darius','Denise','Dimitrie','Dorian','Dorin','Dragos','Dumitru','Eduard','Elvis','Emil','Ervin','Eugen','Eusebiu','Fabian','Filip','Florian','Florin','Gabriel','George','Gheorghe','Giani','Giulio','Iaroslav','Ilie','Ioan','Ion','Ionel','Ionut','Iosif','Irinel','Iulian','Iustin','Laurentiu','Liviu','Lucian','Marian','Marius','Matei','Mihai','Mihail','Nicolae','Nicu','Nicusor','Octavian','Ovidiu','Paul','Petru','Petrut','Radu','Rares','Razvan','Richard','Robert','Roland','Rolland','Romanescu','Sabin','Samuel','Sebastian','Sergiu','Silviu','Stefan','Teodor','Teofil','Theodor','Tudor','Vadim','Valentin','Valeriu','Vasile','Victor','Vlad','Vladimir','Vladut'];
	v_prenume_l CHARACTER VARYING[] := array['Ababei','Acasandrei','Adascalitei','Afanasie','Agafitei','Agape','Aioanei','Alexandrescu','Alexandru','Alexe','Alexii','Amarghioalei','Ambroci','Andonesei','Andrei','Andrian','Andrici','Andronic','Andros','Anghelina','Anita','Antochi','Antonie','Apetrei','Apostol','Arhip','Arhire','Arteni','Arvinte','Asaftei','Asofiei','Aungurenci','Avadanei','Avram','Babei','Baciu','Baetu','Balan','Balica','Banu','Barbieru','Barzu','Bazgan','Bejan','Bejenaru','Belcescu','Belciuganu','Benchea','Bilan','Birsanu','Bivol','Bizu','Boca','Bodnar','Boistean','Borcan','Bordeianu','Botezatu','Bradea','Braescu','Budaca','Bulai','Bulbuc-aioanei','Burlacu','Burloiu','Bursuc','Butacu','Bute','Buza','Calancea','Calinescu','Capusneanu','Caraiman','Carbune','Carp','Catana','Catiru','Catonoiu','Cazacu','Cazamir','Cebere','Cehan','Cernescu','Chelaru','Chelmu','Chelmus','Chibici','Chicos','Chilaboc','Chile','Chiriac','Chirila','Chistol','Chitic','Chmilevski','Cimpoesu','Ciobanu','Ciobotaru','Ciocoiu','Ciofu','Ciornei','Citea','Ciucanu','Clatinici','Clim','Cobuz','Coca','Cojocariu','Cojocaru','Condurache','Corciu','Corduneanu','Corfu','Corneanu','Corodescu','Coseru','Cosnita','Costan','Covatariu','Cozma','Cozmiuc','Craciunas','Crainiceanu','Creanga','Cretu','Cristea','Crucerescu','Cumpata','Curca','Cusmuliuc','Damian','Damoc','Daneliuc','Daniel','Danila','Darie','Dascalescu','Dascalu','Diaconu','Dima','Dimache','Dinu','Dobos','Dochitei','Dochitoiu','Dodan','Dogaru','Domnaru','Dorneanu','Dragan','Dragoman','Dragomir','Dragomirescu','Duceac','Dudau','Durnea','Edu','Eduard','Eusebiu','Fedeles','Ferestraoaru','Filibiu','Filimon','Filip','Florescu','Folvaiter','Frumosu','Frunza','Galatanu','Gavrilita','Gavriliuc','Gavrilovici','Gherase','Gherca','Ghergu','Gherman','Ghibirdic','Giosanu','Gitlan','Giurgila','Glodeanu','Goldan','Gorgan','Grama','Grigore','Grigoriu','Grosu','Grozavu','Gurau','Haba','Harabula','Hardon','Harpa','Herdes','Herscovici','Hociung','Hodoreanu','Hostiuc','Huma','Hutanu','Huzum','Iacob','Iacobuta','Iancu','Ichim','Iftimesei','Ilie','Insuratelu','Ionesei','Ionesi','Ionita','Iordache','Iordache-tiroiu','Iordan','Iosub','Iovu','Irimia','Ivascu','Jecu','Jitariuc','Jitca','Joldescu','Juravle','Larion','Lates','Latu','Lazar','Leleu','Leon','Leonte','Leuciuc','Leustean','Luca','Lucaci','Lucasi','Luncasu','Lungeanu','Lungu','Lupascu','Lupu','Macariu','Macoveschi','Maftei','Maganu','Mangalagiu','Manolache','Manole','Marcu','Marinov','Martinas','Marton','Mataca','Matcovici','Matei','Maties','Matrana','Maxim','Mazareanu','Mazilu','Mazur','Melniciuc-puica','Micu','Mihaela','Mihai','Mihaila','Mihailescu','Mihalachi','Mihalcea','Mihociu','Milut','Minea','Minghel','Minuti','Miron','Mitan','Moisa','Moniry-abyaneh','Morarescu','Morosanu','Moscu','Motrescu','Motroi','Munteanu','Murarasu','Musca','Mutescu','Nastaca','Nechita','Neghina','Negrus','Negruser','Negrutu','Nemtoc','Netedu','Nica','Nicu','Oana','Olanuta','Olarasu','Olariu','Olaru','Onu','Opariuc','Oprea','Ostafe','Otrocol','Palihovici','Pantiru','Pantiruc','Paparuz','Pascaru','Patachi','Patras','Patriche','Perciun','Perju','Petcu','Pila','Pintilie','Piriu','Platon','Plugariu','Podaru','Poenariu','Pojar','Popa','Popescu','Popovici','Poputoaia','Postolache','Predoaia','Prisecaru','Procop','Prodan','Puiu','Purice','Rachieru','Razvan','Reut','Riscanu','Riza','Robu','Roman','Romanescu','Romaniuc','Rosca','Rusu','Samson','Sandu','Sandulache','Sava','Savescu','Schifirnet','Scortanu','Scurtu','Sfarghiu','Silitra','Simiganoschi','Simion','Simionescu','Simionesei','Simon','Sitaru','Sleghel','Sofian','Soficu','Sparhat','Spiridon','Stan','Stavarache','Stefan','Stefanita','Stingaciu','Stiufliuc','Stoian','Stoica','Stoleru','Stolniceanu','Stolnicu','Strainu','Strimtu','Suhani','Tabusca','Talif','Tanasa','Teclici','Teodorescu','Tesu','Tifrea','Timofte','Tincu','Tirpescu','Toader','Tofan','Toma','Toncu','Trifan','Tudosa','Tudose','Tuduri','Tuiu','Turcu','Ulinici','Unghianu','Ungureanu','Ursache','Ursachi','Urse','Ursu','Varlan','Varteniuc','Varvaroi','Vasilache','Vasiliu','Ventaniuc','Vicol','Vidru','Vinatoru','Vlad','Voaides','Vrabie','Vulpescu','Zamosteanu','Zazuleac'];
	v_specializari_l CHARACTER VARYING[] := array['Alergologie şi imunologie clinică','Anestezie şi terapie intensivă','Boli infecţioase','Cardiologie','Cardiologie pediatrică','Dermatovenerologie','Diabet zaharat, nutriţie şi boli metabolice','Endocrinologie','Expertiza medicală a capacităţii de muncă','Farmacologie clinică','Gastroenterologie','Gastroenterologie pediatrică','Genetică medicală','Geriatrie şi gerontologie','Hematologie','Medicină de familie','Medicină de urgenţă','Medicină internă','Medicină fizică şi de reabilitare','Medicina muncii','Medicină sportivă','Nefrologie','Nefrologie pediatrică','Neonatologie','Neurologie','Neurologie pediatrică','Oncologie medicală','Oncologie şi hematologie pediatrică','Pediatrie','Pneumologie','Pneumologie pediatrică','Psihiatrie','Psihiatrie pediatrică','Radioterapie','Reumatologie'];
	v_temp1 CHARACTER VARYING;
	v_temp2 CHARACTER VARYING;
	v_temp_i INTEGER;
	v_temp_t TIMESTAMP;
BEGIN
	BEGIN
		FOR v_i IN 1..ARRAY_LENGTH(v_specializari_l,1) LOOP
			INSERT INTO specializari(denumire) VALUES(v_specializari_l[v_i]);
		END LOOP;
	EXCEPTION WHEN OTHERS THEN NULL;
	END;
  	FOR v_i IN 1..p_nr_pacienti LOOP
		v_nume := choice(v_nume_l);
		v_prenume := choice(v_prenume_l);	
		IF random() > 0.5 THEN
			v_temp2 := NULL;
		ELSE 
			v_temp2 := array_to_string(array_prepend('0'::character,array_prepend('7'::character,random_str('0123456789', 8))),'');
		END IF;
		INSERT INTO pacienti(nume, prenume, nr_telefon, email, parola, data_nastere) 
			VALUES(v_nume, v_prenume, v_temp2, lower(v_nume) || '.' || lower(v_prenume) || '@yahoo.com', lower(v_nume) || '.' || lower(v_prenume), now()::date);
	END LOOP;
	
	FOR v_i IN 1..p_nr_doctori LOOP
		v_nume := choice(v_nume_l);
		v_prenume := choice(v_prenume_l);
		IF random() > 0.5 THEN
			v_temp1 := NULL;
		ELSE 
			v_temp1 := lower(v_nume) || '.' || lower(v_prenume) || '@yahoo.com';
		END IF;
		
		IF random() > 0.5 THEN
			v_temp2 := NULL;
		ELSE 
			v_temp2 := array_to_string(array_prepend('0'::character,array_prepend('7'::character,random_str('0123456789', 8))),'');
		END IF;
		
		INSERT INTO cabinete(denumire, etaj) 
				VALUES(ARRAY_TO_STRING(random_str('ABCDEFGH',1) || random_str('0123456789',3),''), (random() * 9 + 1)::INT) RETURNING id INTO v_temp_i;
		
		
		INSERT INTO doctori(nume, prenume, nr_telefon, email, id_cabinet) 
				VALUES(v_nume, v_prenume, v_temp2, v_temp1, v_temp_i) RETURNING id INTO v_temp_i;
				
		
		BEGIN
			INSERT INTO doctori_specializari(id_doctor, id_specializare) 
				VALUES(v_temp_i, ((random() * (SELECT MAX(id) - 1 FROM specializari) + 1)::INT));
		EXCEPTION WHEN OTHERS THEN NULL;
		END;
		
		FOR v_j IN 0..14 LOOP
			v_temp_t := DATE_BIN('1 hour', NOW()::TIMESTAMP + (((random() * 23)) || ' hours')::INTERVAL, TIMESTAMP '2023-01-01 00:00:00');
			BEGIN
				INSERT INTO program_doctori(id_doctor, zi, timp_inceput, timp_final)
					VALUES(v_temp_i, NOW()::TIMESTAMP + (v_j || ' days')::INTERVAL, v_temp_t::TIME,
						   v_temp_t::TIMESTAMP + (((random() * 7) + 1)::INTEGER || ' hours')::INTERVAL 
					);
			EXCEPTION WHEN OTHERS THEN NULL;
			END;
		END LOOP;
		
	END LOOP;
	
	FOR v_i IN 1..p_nr_programari LOOP
		INSERT INTO programari(id_pacient, id_doctor, data_programare, ora_programare)
			VALUES( ((random() * (SELECT MAX(id) - 1 FROM pacienti) + 1)::INT), 
					((random() * (SELECT MAX(id) - 1 FROM doctori ) + 1)::INT),
					NOW()::TIMESTAMP + ((random() * 7) || ' days')::INTERVAL,
					DATE_BIN('30 minutes'::INTERVAL, NOW()::TIMESTAMP + (((random() * 47) * 30) || ' minutes')::INTERVAL, TIMESTAMP '2023-01-01 00:00:00')
				  );
	END LOOP;
	
	
END;
$$;


ALTER PROCEDURE public.populare_tabele(IN p_nr_pacienti integer, IN p_nr_doctori integer, IN p_nr_programari integer) OWNER TO postgres;

--
-- TOC entry 236 (class 1255 OID 25208)
-- Name: random_str(character varying, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.random_str(p_alphabet character varying, p_length integer) RETURNS character[]
    LANGUAGE plpgsql
    AS $$
DECLARE
	v_cuv CHARACTER[];
BEGIN
	FOR i IN 1..p_length LOOP
		v_cuv[i] := SUBSTRING(p_alphabet,(random() * (LENGTH(p_alphabet) - 1))::INT + 1,1);
	END LOOP;
	RETURN v_cuv;
END;
$$;


ALTER FUNCTION public.random_str(p_alphabet character varying, p_length integer) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 218 (class 1259 OID 16487)
-- Name: cabinete; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cabinete (
    id integer NOT NULL,
    denumire character varying NOT NULL,
    etaj integer NOT NULL,
    imagine character varying(255)
);


ALTER TABLE public.cabinete OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 16468)
-- Name: doctori; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.doctori (
    id integer NOT NULL,
    nume character varying NOT NULL,
    prenume character varying NOT NULL,
    email character varying,
    nr_telefon character varying(10),
    imagine character varying,
    id_cabinet integer NOT NULL
);


ALTER TABLE public.doctori OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16482)
-- Name: doctori_specializari; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.doctori_specializari (
    id_doctor integer NOT NULL,
    id_specializare integer NOT NULL
);


ALTER TABLE public.doctori_specializari OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 16455)
-- Name: pacienti; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pacienti (
    id integer NOT NULL,
    nume character varying NOT NULL,
    prenume character varying NOT NULL,
    adresa character varying,
    email character varying NOT NULL,
    parola character varying NOT NULL,
    nr_telefon character varying(10),
    data_nastere character varying(15) NOT NULL
);


ALTER TABLE public.pacienti OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16494)
-- Name: program_doctori; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.program_doctori (
    id_doctor integer NOT NULL,
    id integer NOT NULL,
    zi date NOT NULL,
    timp_inceput time without time zone NOT NULL,
    timp_final time without time zone NOT NULL
);


ALTER TABLE public.program_doctori OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 25163)
-- Name: programari; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.programari (
    id integer NOT NULL,
    id_doctor integer NOT NULL,
    id_pacient integer NOT NULL,
    data_programare date NOT NULL,
    ora_programare time without time zone NOT NULL
);


ALTER TABLE public.programari OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16475)
-- Name: specializari; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.specializari (
    id integer NOT NULL,
    denumire character varying NOT NULL
);


ALTER TABLE public.specializari OWNER TO postgres;

--
-- TOC entry 3375 (class 0 OID 16487)
-- Dependencies: 218
-- Data for Name: cabinete; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cabinete (id, denumire, etaj, imagine) FROM stdin;
\.


--
-- TOC entry 3372 (class 0 OID 16468)
-- Dependencies: 215
-- Data for Name: doctori; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.doctori (id, nume, prenume, email, nr_telefon, imagine, id_cabinet) FROM stdin;
\.


--
-- TOC entry 3374 (class 0 OID 16482)
-- Dependencies: 217
-- Data for Name: doctori_specializari; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.doctori_specializari (id_doctor, id_specializare) FROM stdin;
\.


--
-- TOC entry 3371 (class 0 OID 16455)
-- Dependencies: 214
-- Data for Name: pacienti; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pacienti (id, nume, prenume, adresa, email, parola, nr_telefon, data_nastere) FROM stdin;
\.


--
-- TOC entry 3376 (class 0 OID 16494)
-- Dependencies: 219
-- Data for Name: program_doctori; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.program_doctori (id_doctor, id, zi, timp_inceput, timp_final) FROM stdin;
\.


--
-- TOC entry 3377 (class 0 OID 25163)
-- Dependencies: 220
-- Data for Name: programari; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.programari (id, id_doctor, id_pacient, data_programare, ora_programare) FROM stdin;
\.


--
-- TOC entry 3373 (class 0 OID 16475)
-- Dependencies: 216
-- Data for Name: specializari; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.specializari (id, denumire) FROM stdin;
\.


--
-- TOC entry 3215 (class 2606 OID 25319)
-- Name: cabinete cabinete_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cabinete
    ADD CONSTRAINT cabinete_pkey PRIMARY KEY (id);


--
-- TOC entry 3207 (class 2606 OID 25246)
-- Name: doctori doctori_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doctori
    ADD CONSTRAINT doctori_pkey PRIMARY KEY (id);


--
-- TOC entry 3213 (class 2606 OID 25265)
-- Name: doctori_specializari doctori_specializari_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doctori_specializari
    ADD CONSTRAINT doctori_specializari_pkey PRIMARY KEY (id_doctor, id_specializare);


--
-- TOC entry 3205 (class 2606 OID 25233)
-- Name: pacienti pacients_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pacienti
    ADD CONSTRAINT pacients_pkey PRIMARY KEY (id);


--
-- TOC entry 3217 (class 2606 OID 25371)
-- Name: program_doctori program_doctori_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.program_doctori
    ADD CONSTRAINT program_doctori_pkey PRIMARY KEY (id);


--
-- TOC entry 3219 (class 2606 OID 25287)
-- Name: programari programari_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.programari
    ADD CONSTRAINT programari_pkey PRIMARY KEY (id);


--
-- TOC entry 3209 (class 2606 OID 25340)
-- Name: specializari specializari_denumire_denumire1_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.specializari
    ADD CONSTRAINT specializari_denumire_denumire1_key UNIQUE (denumire) INCLUDE (denumire);


--
-- TOC entry 3211 (class 2606 OID 25311)
-- Name: specializari specializari_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.specializari
    ADD CONSTRAINT specializari_pkey PRIMARY KEY (id);


--
-- TOC entry 3226 (class 2620 OID 25391)
-- Name: program_doctori a_trg_program_doctori_normalize_hours; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER a_trg_program_doctori_normalize_hours BEFORE INSERT ON public.program_doctori FOR EACH ROW EXECUTE FUNCTION public.normalize_hours();


--
-- TOC entry 3225 (class 2620 OID 25190)
-- Name: cabinete cabinete_maxid; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER cabinete_maxid BEFORE INSERT ON public.cabinete FOR EACH ROW EXECUTE FUNCTION public.maxid();


--
-- TOC entry 3223 (class 2620 OID 25188)
-- Name: doctori doctori_maxid; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER doctori_maxid BEFORE INSERT ON public.doctori FOR EACH ROW EXECUTE FUNCTION public.maxid();


--
-- TOC entry 3222 (class 2620 OID 25189)
-- Name: pacienti pacienti_maxid; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER pacienti_maxid BEFORE INSERT ON public.pacienti FOR EACH ROW EXECUTE FUNCTION public.maxid();


--
-- TOC entry 3227 (class 2620 OID 25387)
-- Name: program_doctori program_doctori_maxid; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER program_doctori_maxid BEFORE INSERT ON public.program_doctori FOR EACH ROW EXECUTE FUNCTION public.maxid();


--
-- TOC entry 3228 (class 2620 OID 25187)
-- Name: programari programari_maxid; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER programari_maxid BEFORE INSERT ON public.programari FOR EACH ROW EXECUTE FUNCTION public.maxid();


--
-- TOC entry 3224 (class 2620 OID 25229)
-- Name: specializari specializari_maxid; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER specializari_maxid BEFORE INSERT ON public.specializari FOR EACH ROW EXECUTE FUNCTION public.maxid();


--
-- TOC entry 3220 (class 2606 OID 25292)
-- Name: programari appointments_id_doctor_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.programari
    ADD CONSTRAINT appointments_id_doctor_fkey FOREIGN KEY (id_doctor) REFERENCES public.doctori(id);


--
-- TOC entry 3221 (class 2606 OID 25301)
-- Name: programari appointments_id_pacient_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.programari
    ADD CONSTRAINT appointments_id_pacient_fkey FOREIGN KEY (id_pacient) REFERENCES public.pacienti(id);


-- Completed on 2023-05-26 00:02:56

--
-- PostgreSQL database dump complete
--

