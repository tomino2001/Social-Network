package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Tuple;
import org.apache.pdfbox.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDTableAttributeObject;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

import java.time.Month;

public class GlobalService {
    private final UtilizatorService utilizatorService;
    private final PrietenieService prietenieService;
    private final MesajeService mesajeService;
    private final AccountService accountService;


    public UtilizatorService getUtilizatorService() {
        return utilizatorService;
    }

    public PrietenieService getPrietenieService() {
        return prietenieService;
    }

    public MesajeService getMesajeService() {
        return mesajeService;
    }

    public   AccountService getAccountService(){return accountService;}

    public GlobalService(UtilizatorService utilizatorService, PrietenieService prietenieService, MesajeService mesajeService, AccountService accountService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.mesajeService = mesajeService;
        this.accountService = accountService;
    }

    public void removeUtilizatorAndPrieteniiUtilizator(Long id) {
        this.utilizatorService.removeUtilizator(id);
        this.prietenieService.removePreteniiIfUserIsDeleted(id);
    }

    public List<Tuple<Long, LocalDateTime>> prieteniiUtilizator(String firstName, String lastName) {
        Utilizator utilizator = utilizatorService.findByName(firstName, lastName);
        Iterable<Prietenie> prietenii = prietenieService.getAll();
        List<Tuple<Long, LocalDateTime>> rezultat =
                StreamSupport.stream(prietenii.spliterator(), false)
                        .filter(prietenie -> ((Objects.equals(prietenie.getId().getLeft(), utilizator.getId())
                                || Objects.equals(prietenie.getId().getRight(), utilizator.getId()))
                                && prietenie.getStatus().equals("approved")))
                        .map(prietenie -> mapPrietenie(utilizator, prietenie))
                        .collect(toList());
        return rezultat;
    }

    public List<Prietenie> listaPrieteniUtilizator(String firstName, String lastName) {
        Utilizator utilizator = utilizatorService.findByName(firstName, lastName);
        return StreamSupport.stream(prietenieService.getAll().spliterator(), false)
                .filter(prietenie -> ((Objects.equals(prietenie.getId().getLeft(), utilizator.getId()))
                        || Objects.equals(prietenie.getId().getRight(), utilizator.getId()))
                )
                .collect(toList());
    }

    public List<Tuple<Long, LocalDateTime>> prieteniiUtilizatorDinLuna(String firstName, String lastName, String luna) {
        Utilizator utilizator = utilizatorService.findByName(firstName, lastName);
        Iterable<Prietenie> prietenii = prietenieService.getAll();

        List<Tuple<Long, LocalDateTime>> rezultat =
                StreamSupport.stream(prietenii.spliterator(), false)
                        .filter(prietenie -> (prietenie.getId().getLeft() == utilizator.getId()
                                || Objects.equals(prietenie.getId().getRight(), utilizator.getId()))
                                && prietenie.getDate().getMonth() == Month.valueOf(luna.toUpperCase()))
                        .map(prietenie -> mapPrietenie(utilizator, prietenie))
                        .collect(toList());
        return rezultat;
    }

    private Tuple<Long, LocalDateTime> mapPrietenie(Utilizator utilizator, Prietenie prietenie) {
        Long left = prietenie.getId().getLeft();
        Long right = prietenie.getId().getRight();
        Long idUser = utilizator.getId();
        Tuple<Long, LocalDateTime> tuple = null;
        if (!Objects.equals(left, idUser))
            tuple = new Tuple<>(left, prietenie.getDate());
        else
            tuple = new Tuple<>(right, prietenie.getDate());
        return tuple;
    }

    public void exportPdfPrieteniiSiMesajeDinPerioadaX(Utilizator utilizator, LocalDateTime st, LocalDateTime dr){
        List<Prietenie> prietenieList = prietenieService.listaPrieteniiDinPerioadaX(utilizator, st, dr);
        List<Message> messageList = mesajeService.listaMesajePrimiteDinPerioadaX(utilizator, st, dr);

        PDDocument document = new PDDocument();
        try {

            document.save("PrieteniiSiMesaje.pdf");
            //PDfWriter.getInstance(document, new FileOutputStream("Prietenii si mesaje din perioad data",false));
            //document.open();
            PDPage page = new PDPage(PDRectangle.A4);
            // PDRectangle.LETTER and others are also possible
            PDRectangle rect = page.getMediaBox();
            // rect can be used to get the page width and height
            document.addPage(page);
            // Start a new content stream which will "hold" the to be created content
            PDPageContentStream cos = new PDPageContentStream(document, page);

            //Dummy Table
            float margin = 50;
            // starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
            // we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

            //PdfPTable tablePrietenii = new PdfPTable(2);
            //PdfPTable tableMesaje = new PdfPTable(3);

            addTableHeader(tablePrietenii,"Name","Date");
            addTablePrieteniiRows(tablePrietenii, utilizator, prietenieList);

            addTableHeader(tableMesaje, "From", "Message", "Date");
            addTableMesajeRows(tableMesaje, messageList);

            document.add(tablePrietenii);
            document.add(tableMesaje);
            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTableHeader(PdfPTable table,String... firstCol) {
        Stream.of(firstCol)
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addTablePrieteniiRows(PdfPTable tablePrietenii, Utilizator utilizator, List<Prietenie> prietenieList) {
        prietenieList.forEach(x -> {
            String name;
            if (x.getId().getLeft().equals(utilizator.getId())){
                Utilizator utilizator1 = utilizatorService.findOne(x.getId().getRight());
                name = utilizator1.getFirstName() + ' ' + utilizator1.getLastName();
            }else{
                Utilizator utilizator1 = utilizatorService.findOne(x.getId().getLeft());
                name = utilizator1.getFirstName() + ' ' + utilizator1.getLastName();
            }

            PdfPCell cell1 = new PdfPCell();
            cell1.setPhrase(new Phrase(name));
            PdfPCell cell2 = new PdfPCell();
            cell2.setPhrase(new Phrase(x.getDate().toString()));
            tablePrietenii.addCell(cell1);
            tablePrietenii.addCell(cell2);
        });
        ifNoDataToExport(prietenieList,tablePrietenii);
    }

    private void addTableMesajeRows(PdfPTable tableMesaje, List<Message> messageList) {
        messageList.forEach(x -> {
            PdfPCell cell1 = new PdfPCell();
            cell1.setPhrase(new Phrase(x.getFrom().getFirstName() + " " + x.getFrom().getLastName()));
            PdfPCell cell2 = new PdfPCell();
            cell2.setPhrase(new Phrase(x.getMessage()));
            PdfPCell cell3 = new PdfPCell();
            cell3.setPhrase(new Phrase(x.getDate().toString()));
            tableMesaje.addCell(cell1);
            tableMesaje.addCell(cell2);
            tableMesaje.addCell(cell3);
        });
        ifNoDataToExport(messageList,tableMesaje);
    }

    private void ifNoDataToExport(List<?> aList, PdfPTable table){
        if(aList.size()==0){
            PdfPCell cell1 = new PdfPCell();
            cell1.setPhrase(new Phrase("No data in application"));
            PdfPCell cell2 = new PdfPCell();
            cell1.setPhrase(new Phrase("to export"));
            table.addCell(cell1);
            table.addCell(cell2);
        }
    }
}