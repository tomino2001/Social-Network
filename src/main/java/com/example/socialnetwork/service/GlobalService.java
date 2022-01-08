package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.Utilizator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

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

    public void exportPdfActivitateUtilizatorDinPerioadaX(Utilizator utilizator, LocalDateTime st, LocalDateTime dr){
        List<Prietenie> prietenieList = prietenieService.listaPrieteniiDinPerioadaX(utilizator, st, dr);
        List<Message> messageList = mesajeService.listaMesajePrimiteDinPerioadaX(utilizator, st, dr);
        String pathPrietenie = "C:\\Users\\Asus\\IdeaProjects\\socialnetwork\\pdfData\\Prietenie.pdf";
        String pathMessage = "C:\\Users\\Asus\\IdeaProjects\\socialnetwork\\pdfData\\Mesaje.pdf";

        writeToPdfPrietenii(prietenieList, pathPrietenie);
        writeToPdfMesaje(messageList, pathMessage);
    }

    public void exportToPdfListaMesajePrimiteDeLaUtilizatorXInPerioadaX(Utilizator utilizatorLogat,Utilizator utilizator
            ,LocalDateTime st, LocalDateTime dr ){
        List<Message> messageList = mesajeService.listaMesajePrimiteDeLaUtilizatorXInPerioadaX(utilizatorLogat ,utilizator, st, dr);
        String pathMessage = "C:\\Users\\Asus\\IdeaProjects\\socialnetwork\\pdfData\\MesajeV2.pdf";
        writeToPdfMesaje(messageList, pathMessage);
    }

    public void writeToPdfPrietenii(List<Prietenie> valuesToExport, String filePath) {
        try (PDDocument doc = new PDDocument()) {
            PDFont font = PDType1Font.HELVETICA;
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.setFont(font, 12);
            int lines = 1;
            float pageHeight = page.getMediaBox().getHeight();

            for (Prietenie row : valuesToExport) {
                int startX = 0;

                content.beginText();
                content.newLineAtOffset(startX, pageHeight - 50 * lines);
                startX += startX + 100;
                content.showText("From: " + utilizatorService.findOne(row.getId().getLeft()).getFirstName() + " " +
                        utilizatorService.findOne(row.getId().getLeft()).getLastName() + ", to: " +
                        utilizatorService.findOne(row.getId().getRight()).getFirstName()  + " " +
                        utilizatorService.findOne(row.getId().getRight()).getLastName() + " " +
                        row.getDate().format((DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"))) + " " + row.getStatus());
                content.endText();
                ++lines;

                if (lines > 10) {
                    page = new PDPage();
                    doc.addPage(page);
                    content.close();
                    content = new PDPageContentStream(doc, page);
                    content.setFont(font, 12);
                }
            }
            content.close();
            doc.save(filePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeToPdfMesaje(List<Message> valuesToExport, String filePath) {
        try (PDDocument doc = new PDDocument()) {
            PDFont font = PDType1Font.TIMES_BOLD_ITALIC;
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.setFont(font, 10);
            int lines = 1;
            float pageHeight = page.getMediaBox().getHeight();

            for (Message row : valuesToExport) {
                int startX = 0;

                content.beginText();
                content.newLineAtOffset(startX, pageHeight - 50 * lines);
                startX += startX + 100;
                String string = "From: " + row.getFrom().getFirstName() + " " +
                        row.getFrom().getLastName() + ", to: " +
                        row.getTo().get(0).getFirstName()  + " " + row.getTo().get(0).getLastName() + ", message: " +
                        row.getMessage() + " " +
                        row.getDate().format((DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss")));
                string = string.replace("\n", "").replace("\r", "");
                content.showText(string);
                content.endText();
                ++lines;

                if (lines > 10) {
                    page = new PDPage();
                    doc.addPage(page);
                    content.close();
                    content = new PDPageContentStream(doc, page);
                    content.setFont(font, 12);
                }
            }
            content.close();
            doc.save(filePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

