                               FANAMPIANA
    1-Famoronana fifandraisana:
        Mamorona fifandraisana (Anarana):int ...,double ..
    2-Mapiditra singa ao anaty fifandraisana:
        Atsofoy anaty (Anarana) ireto ("singa1","singa2",... "singaN")
    3-Maka singa ao @ fifandraisana:
        Ameza daholo momban'ny (Anarana) (raha toa ka alaina avokoa)
        Ameza (karazana) momban'ny (Anarana) (raha toa haka karazana)
    4-Mitahiry ireo singa napidirina sy fifandraisana noforonina:
        tahirizo
    5-Mamafa ireo singa napidirina sy ireo fifandraisana noforonona:
        fafao avokoa
    6-Fitina:
        create table personne(
        int ...,
        double ...
        );
    7-Firaisana na Union:
        Firaisana Relation1 & Relation2
    8-Fahasamihafana na Different:
        Fahasamihafana Relation1 & Relation2
    9-Fitovizana na intersect: 
        Fitovizana Relation1 & Relation2
    ==> Mamorona fifandraisana personne:int ...,double ..

    select * from personne;
    ==> Ameza daholo momban'ny personne

    insert into personne values ("1","300",doda");
    ==> Atsofoy anaty personne ireto ("1","300","doda")

    commit;
    ==> tahirizo

    drop database;
    ==> fafao avokoa
Misaotra @ fahatokisana midira @ tranokala mpifandraisana mybase.com raha mila fanazavana fanampiny
ameza nom,age,date momban'ny olona raha:age = 18 , raha:nom = gates
vakina avy any arina le requette de select * from ny apesaina @ voloany 


mamorona fifandraisana (taxibe) (double volamiditra,double volamivoka,int ligne,String numero)
atsofoy anaty (taxibe) ireto (1000.0,1000.0,192,0000AAA)
atsofoy anaty (taxibe) ireto (2000.0,1000.0,192,1111AAA)
atsofoy anaty (taxibe) ireto (500.0,1000.0,172,2222AAA)
atsofoy anaty (taxibe) ireto (800.0,800.0,172,3333AAA)

ameza (daholo) momban'ny (taxibe) (volamiditra = volamivoka)