package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.Tuple;
import com.example.socialnetwork.domain.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class GlobalService {
    private final UsersService usersService;
    private final FriendshipsService friendshipsService;
    private final MessagesService messagesService;
    private final AccountsService accountsService;
    private final EventService eventService;
    private final NotificationService notificationService;


    public UsersService getUtilizatorService() {
        return usersService;
    }

    public FriendshipsService getPrietenieService() {
        return friendshipsService;
    }

    public MessagesService getMesajeService() {
        return messagesService;
    }

    public AccountsService getAccountService(){return accountsService;}

    public EventService getEventService(){return eventService;}

    public NotificationService getNotificationService(){return notificationService;}

    public GlobalService(UsersService usersService, FriendshipsService friendshipsService, MessagesService messagesService,
                         AccountsService accountsService, EventService eventService, NotificationService notificationService) {
        this.usersService = usersService;
        this.friendshipsService = friendshipsService;
        this.messagesService = messagesService;
        this.accountsService = accountsService;
        this.eventService = eventService;
        this.notificationService = notificationService;
    }

    public void removeUtilizatorAndPrieteniiUtilizator(Long id) {
        this.usersService.removeUtilizator(id);
        this.friendshipsService.removePreteniiIfUserIsDeleted(id);
    }

    public List<Tuple<Long, LocalDateTime>> prieteniiUtilizator(String firstName, String lastName) {
        User user = usersService.findByName(firstName, lastName);
        Iterable<Friendship> prietenii = friendshipsService.getAll();
        return StreamSupport.stream(prietenii.spliterator(), false)
                .filter(prietenie -> ((Objects.equals(prietenie.getId().getLeft(), user.getId())
                        || Objects.equals(prietenie.getId().getRight(), user.getId()))
                        && prietenie.getStatus().equals("approved")))
                .map(prietenie -> mapPrietenie(user, prietenie))
                .collect(toList());
    }

    public List<Friendship> listaPrieteniUtilizator(String firstName, String lastName) {
        User user = usersService.findByName(firstName, lastName);
        return StreamSupport.stream(friendshipsService.getAll().spliterator(), false)
                .filter(prietenie -> ((Objects.equals(prietenie.getId().getLeft(), user.getId()))
                        || Objects.equals(prietenie.getId().getRight(), user.getId()))
                )
                .collect(toList());
    }

    public List<Tuple<Long, LocalDateTime>> prieteniiUtilizatorDinLuna(String firstName, String lastName, String luna) {
        User user = usersService.findByName(firstName, lastName);
        Iterable<Friendship> prietenii = friendshipsService.getAll();

        return StreamSupport.stream(prietenii.spliterator(), false)
                .filter(prietenie -> (prietenie.getId().getLeft() == user.getId()
                        || Objects.equals(prietenie.getId().getRight(), user.getId()))
                        && prietenie.getDate().getMonth() == Month.valueOf(luna.toUpperCase()))
                .map(prietenie -> mapPrietenie(user, prietenie))
                .collect(toList());
    }

    private Tuple<Long, LocalDateTime> mapPrietenie(User user, Friendship friendship) {
        Long left = friendship.getId().getLeft();
        Long right = friendship.getId().getRight();
        Long idUser = user.getId();
        Tuple<Long, LocalDateTime> tuple = null;
        if (!Objects.equals(left, idUser))
            tuple = new Tuple<>(left, friendship.getDate());
        else
            tuple = new Tuple<>(right, friendship.getDate());
        return tuple;
    }

    public void exportPdfActivitateUtilizatorDinPerioadaX(User user, LocalDate st, LocalDate dr){
        List<Friendship> friendshipList = friendshipsService.listaPrieteniiDinPerioadaX(user, st, dr);
        List<Message> messageList = messagesService.listaMesajePrimiteDinPerioadaX(user, st, dr);
        String pathPrietenie = "C:\\Users\\Asus\\IdeaProjects\\socialnetwork\\pdfData\\Friendship.pdf";
        String pathMessage = "C:\\Users\\Asus\\IdeaProjects\\socialnetwork\\pdfData\\Mesaje.pdf";

        writeToPdfPrietenii(friendshipList, pathPrietenie);
        writeToPdfMesaje(messageList, pathMessage);
    }

    public void exportToPdfListaMesajePrimiteDeLaUtilizatorXInPerioadaX(User userLogat, User user
            , LocalDate st, LocalDate dr ){
        List<Message> messageList = messagesService.listaMesajePrimiteDeLaUtilizatorXInPerioadaX(userLogat, user, st, dr);
        String pathMessage = "C:\\Users\\Asus\\IdeaProjects\\socialnetwork\\pdfData\\MesajeV2.pdf";
        writeToPdfMesaje(messageList, pathMessage);
    }

    public void writeToPdfPrietenii(List<Friendship> valuesToExport, String filePath) {
        try (PDDocument doc = new PDDocument()) {
            PDFont font = PDType1Font.HELVETICA;
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.setFont(font, 12);
            int lines = 1;
            float pageHeight = page.getMediaBox().getHeight();

            for (Friendship row : valuesToExport) {
                int startX = 0;

                content.beginText();
                content.newLineAtOffset(startX, pageHeight - 50 * lines);
                startX += startX + 100;
                content.showText("From: " + usersService.findOne(row.getId().getLeft()).getFirstName() + " " +
                        usersService.findOne(row.getId().getLeft()).getLastName() + ", to: " +
                        usersService.findOne(row.getId().getRight()).getFirstName()  + " " +
                        usersService.findOne(row.getId().getRight()).getLastName() + " " +
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

