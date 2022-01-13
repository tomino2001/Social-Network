package com.example.socialnetwork.service;

import com.example.socialnetwork.domain.*;
import com.example.socialnetwork.domain.validators.*;
import com.example.socialnetwork.repository.Repository;
import com.example.socialnetwork.repository.db.*;
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

import static com.example.socialnetwork.domain.Constants.*;
import static java.util.stream.Collectors.toList;

public class GlobalService {
    public static GlobalService globalService = new GlobalService();
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final MessageService messageService;
    private final AccountService accountService;
    private final EventService eventService;
    private final NotificationService notificationService;

    private GlobalService() {
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();
        Validator<Message> messageValidator = new MessageValidator();
        Validator<Account> accountValidator = new AccountValidator();
        Validator<Event> eventValidator = new EventValidator();

        Repository<Long, User> userDbRepository =
                new UserDbRepository(Constants.url, Constants.username, Constants.password, userValidator);
        userService = new UserService(userDbRepository);

        Repository<Tuple<Long, Long>, Friendship> friendshipDbRepository =
                new FriendshipDbRepository(Constants.url, Constants.username, Constants.password, friendshipValidator);
        friendshipService = new FriendshipService(friendshipDbRepository);

        Repository<Long, Message> messageDbRepository =
                new MessageDbRepository(Constants.url, Constants.username, Constants.password, messageValidator);
        messageService = new MessageService(messageDbRepository);

        Repository<Long, Account> accountRepository =
                new AccountDbRepository(Constants.url, Constants.username, Constants.password, accountValidator);
        accountService = new AccountService(accountRepository);

        Repository<Long, Event> eventRepository =
                new EventDbRepository(Constants.url, Constants.username, Constants.password, eventValidator);
        eventService = new EventService(eventRepository);

        Repository<Long, Notification> notificationRepository =
                new NotificationDbRepository(Constants.url, Constants.username, Constants.password);
        notificationService = new NotificationService(notificationRepository);
    }

    public static GlobalService getInstance() {
        return globalService;
    }

    ;

    public UserService getUserService() {
        return userService;
    }

    public FriendshipService getFriendshipService() {
        return friendshipService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void removeUserWithFriends(Long id) {
        this.userService.removeUser(id);
        this.friendshipService.removeFriendshipsIfUserIsDeleted(id);
    }

    public List<Tuple<Long, LocalDateTime>> userFriendships(String firstName, String lastName) {
        User user = userService.findByName(firstName, lastName);
        Iterable<Friendship> friendships = friendshipService.getAll();
        return StreamSupport.stream(friendships.spliterator(), false)
                .filter(friendship -> ((Objects.equals(friendship.getId().getLeft(), user.getId())
                        || Objects.equals(friendship.getId().getRight(), user.getId()))
                        && friendship.getStatus().equals("approved")))
                .map(friendship -> mapFriendship(user, friendship))
                .collect(toList());
    }

    public List<Friendship> userFriendsList(String firstName, String lastName) {
        User user = userService.findByName(firstName, lastName);
        return StreamSupport.stream(friendshipService.getAll().spliterator(), false)
                .filter(friendship -> ((Objects.equals(friendship.getId().getLeft(), user.getId()))
                        || Objects.equals(friendship.getId().getRight(), user.getId()))
                )
                .collect(toList());
    }

    public List<Tuple<Long, LocalDateTime>> userFriendshipsFromMonth(String firstName, String lastName, String month) {
        User user = userService.findByName(firstName, lastName);
        Iterable<Friendship> friendships = friendshipService.getAll();

        return StreamSupport.stream(friendships.spliterator(), false)
                .filter(friendship -> (friendship.getId().getLeft() == user.getId()
                        || Objects.equals(friendship.getId().getRight(), user.getId()))
                        && friendship.getDate().getMonth() == Month.valueOf(month.toUpperCase()))
                .map(friendship -> mapFriendship(user, friendship))
                .collect(toList());
    }

    private Tuple<Long, LocalDateTime> mapFriendship(User user, Friendship friendship) {
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

    public void exportPdfUserActivityDuringPeriod(User user, LocalDate st, LocalDate dr) {
        List<Friendship> friendshipList = friendshipService.friendshipsListDuringPeriod(user, st, dr);
        List<Message> messageList = messageService.messageListReceivedDuringPeriod(user, st, dr);


        writePdfFriendships(friendshipList, pathFriendship);
        writeToPdfMesaje(messageList, pathMessage);
    }

    public void exportPdfMessageListReceivedFromUserDuringPeriod(User userLogat, User user
            , LocalDate st, LocalDate dr) {
        List<Message> messageList = messageService.messageListFromUserDuringPeriod(userLogat, user, st, dr);

        writeToPdfMesaje(messageList, pathMessageV2);
    }

    private void abstractWritePdf(List<? extends Entity> entityList, String filePath, PDFont font, int size) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.setFont(font, 12);
            int lines = 1;
            float pageHeight = page.getMediaBox().getHeight();

            for (Entity entityRow : entityList) {
                int startX = 0;
                content.beginText();
                content.newLineAtOffset(startX, pageHeight - 50 * lines);

                String text = "";
                if (entityRow instanceof Friendship) {
                    Friendship row = (Friendship) entityRow;
                    text = "From: " + userService.findOne(row.getId().getLeft()).getFirstName() + " " +
                            userService.findOne(row.getId().getLeft()).getLastName() + ", to: " +
                            userService.findOne(row.getId().getRight()).getFirstName() + " " +
                            userService.findOne(row.getId().getRight()).getLastName() + " " +
                            row.getDate().format((DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"))) + " " + row.getStatus();
                } else if (entityRow instanceof Message) {
                    Message row = (Message) entityRow;
                    text = "From: " + row.getFrom().getFirstName() + " " +
                            row.getFrom().getLastName() + ", to: " +
                            row.getTo().get(0).getFirstName() + " " + row.getTo().get(0).getLastName() + ", message: " +
                            row.getContent() + " " +
                            row.getDate().format((DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss")));
                    text = text.replace("\n", "").replace("\r", "");

                }
                content.showText(text);
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

    public void writePdfFriendships(List<Friendship> valuesToExport, String filePath) {
        abstractWritePdf(valuesToExport, filePath, PDType1Font.HELVETICA, 12);
    }

    public void writeToPdfMesaje(List<Message> valuesToExport, String filePath) {
        abstractWritePdf(valuesToExport, filePath, PDType1Font.TIMES_BOLD_ITALIC, 10);
    }
}