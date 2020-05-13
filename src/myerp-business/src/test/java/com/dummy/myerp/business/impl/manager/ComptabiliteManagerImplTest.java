package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.dummy.myerp.business.impl.BusinessProxyImpl;
import com.dummy.myerp.model.bean.comptabilite.*;
import static org.assertj.core.api.Assertions.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.myerp.testbusiness.BusinessTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteManagerImplTest  extends BusinessTestCase {

    private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();


    String expectedReference;

    EcritureComptable ecritureComptable = new EcritureComptable();
    @Before
    public void BeforeAllTest() throws ParseException {
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setReference("AC-2020/00001");
    }

    @Test
    public void checkEcritureComptableUnit() throws Exception {
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
        manager.checkEcritureComptableUnit(ecritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        manager.checkEcritureComptableUnit(ecritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(1234)));
        manager.checkEcritureComptableUnit(ecritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3() throws Exception {
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        manager.checkEcritureComptableUnit(ecritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5() throws Exception {

        manager.checkEcritureComptableUnit(ecritureComptable);
    }
    @Test
    public void addReferenceTest() throws FunctionalException, ParseException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC","Banque"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dateJournal = sdf.format(new Date());
        Date date=new SimpleDateFormat("yyyy/MM/dd").parse(dateJournal);
        ecritureComptable.setDate(date);


        System.out.println(dateJournal);
        expectedReference = "AC-2020/00001";

        manager.addReference(ecritureComptable);

        assertThat(ecritureComptable.getReference()).isEqualTo(expectedReference);

        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable();
        sequenceEcritureComptable.setCodeJournal("AC");
        sequenceEcritureComptable.setAnnee(2020);
        sequenceEcritureComptable.setDerniereValeur(00001);

        manager.deleteSequenceEcritureComptable(sequenceEcritureComptable);
    }




    @Test
    public void insertEcritureComptableTest() throws FunctionalException {
        EcritureComptable ecritureComptable  = new EcritureComptable();
        Date currentDate = new Date();
        Integer annee = 2020;
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference(ecritureComptable.getJournal().getCode()+"-" + annee + "/00007");
        ecritureComptable.setDate(currentDate);
        ecritureComptable.setLibelle("Courses");

        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),"Achat", new BigDecimal(25),null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),"Vente", null ,new BigDecimal(50)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),"Achat", new BigDecimal(25),null));

        manager.insertEcritureComptable(ecritureComptable);

        List<EcritureComptable> listEcritureComptable = manager.getListEcritureComptable();
        EcritureComptable ecritureComptable1 = new EcritureComptable();

        for(EcritureComptable tempEcriture : listEcritureComptable){
            if(tempEcriture.getReference().equals(ecritureComptable.getReference()) && tempEcriture.getLibelle().equals("Courses")){
                ecritureComptable1 = tempEcriture;
            }
        }
        Assert.assertTrue(ecritureComptable.getReference().equals(ecritureComptable1.getReference()));
        Assert.assertTrue(ecritureComptable.getLibelle().equals(ecritureComptable1.getLibelle()));
        manager.deleteEcritureComptable(ecritureComptable1.getId());

    }

//    @Test
//    public void updateEcritureComptable() throws FunctionalException {
//        EcritureComptable ecritureComptable  = new EcritureComptable();
//        Date currentDate = new Date();
//        Integer annee = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault()).toLocalDate().getYear();
//        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
//        ecritureComptable.setReference("AC-" + annee + "/40404");
//        ecritureComptable.setDate(currentDate);
//        ecritureComptable.setLibelle("Depense");
//
//        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(606),"Facture EDF", new BigDecimal(45),null));
//        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),"Fournitures", null,new BigDecimal(45)));
//
//        manager.insertEcritureComptable(ecritureComptable);
//        List<EcritureComptable> listAfterInsert = manager.getListEcritureComptable();
//        EcritureComptable ecritureComptableBis = listAfterInsert.get(listAfterInsert.size()-1);
//
//        ecritureComptableBis.setLibelle("Achat");
//
//        manager.updateEcritureComptable(ecritureComptableBis);
//        List<EcritureComptable> listAfterUpdate = manager.getListEcritureComptable();
//        EcritureComptable ecritureComptableAfterUpdate = listAfterUpdate.get(listAfterUpdate.size()-1);
//        System.out.println(" Libelle "+ecritureComptableAfterUpdate.getLibelle() + " ID " + ecritureComptableAfterUpdate.getId());
//        Assert.assertTrue(ecritureComptableAfterUpdate.getLibelle().equals("Achat"));
//        manager.deleteEcritureComptable(ecritureComptableBis.getId());
//    }



}
