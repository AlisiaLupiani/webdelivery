package WebMarket.util;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletContext;
import model.CartItem;
import model.Client;
import model.Order;
import model.ProductOption;

public class EmailService {

    public static void sendOrderConfirmation(
            ServletContext context,
            Client cliente,
            Order ordine,
            List<CartItem> elementi,
            int tempoStimato) throws MessagingException {

        if (!isEnabled(context) || cliente == null || cliente.getEmail() == null) {
            return;
        }

        StringBuilder body = new StringBuilder();

        body.append("Ciao ").append(cliente.getName()).append(",\n\n");
        body.append("il tuo ordine #").append(ordine.getKey()).append(" e' stato confermato.\n\n");
        body.append("Dettagli ordine:\n");
        body.append("Data: ").append(ordine.getDate()).append("\n");
        body.append("Orario consegna: ").append(ordine.getDeliveryTime()).append("\n");
        body.append("Indirizzo: ").append(ordine.getDeliveryAddress()).append("\n");
        body.append("Metodo pagamento: ").append(ordine.getPaymentMethod()).append("\n");
        body.append("Tempo stimato: ").append(tempoStimato).append(" minuti\n\n");

        body.append("Prodotti ordinati:\n");

        for (CartItem item : elementi) {
            body.append("- ")
                    .append(item.getProdotto() != null ? item.getProdotto().getName() : "Prodotto")
                    .append(" x")
                    .append(item.getQuantita())
                    .append(" - euro ")
                    .append(formatMoney(item.getPrezzoTotaleRiga()))
                    .append("\n");

            if (item.getOpzioniScelte() != null && !item.getOpzioniScelte().isEmpty()) {
                for (ProductOption opzione : item.getOpzioniScelte()) {
                    body.append("  * ")
                            .append(opzione.getName())
                            .append(" (+ euro ")
                            .append(formatMoney(opzione.getAddictionalPrice() != null ? opzione.getAddictionalPrice() : 0.0))
                            .append(")\n");
                }
            }
        }

        body.append("\nTotale: euro ").append(formatMoney(ordine.getPrice())).append("\n\n");
        body.append("Grazie per aver ordinato da WebDelivery!");

        send(context, cliente.getEmail(), "Conferma ordine WebDelivery #" + ordine.getKey(), body.toString());
    }

    public static void sendOrderInDelivery(ServletContext context, Order ordine) throws MessagingException {
        if (!isEnabled(context) || ordine == null || ordine.getClient() == null) {
            return;
        }

        Client cliente = ordine.getClient();

        StringBuilder body = new StringBuilder();

        body.append("Ciao ").append(cliente.getName()).append(",\n\n");
        body.append("il tuo ordine #").append(ordine.getKey()).append(" e' ora in consegna.\n\n");
        body.append("Indirizzo consegna: ").append(ordine.getDeliveryAddress()).append("\n");
        body.append("Orario richiesto: ").append(ordine.getDeliveryTime()).append("\n");
        body.append("Totale: euro ").append(formatMoney(ordine.getPrice())).append("\n\n");
        body.append("A tra poco!\nWebDelivery");

        send(context, cliente.getEmail(), "Ordine WebDelivery in consegna #" + ordine.getKey(), body.toString());
    }

    private static void send(ServletContext context, String to, String subject, String body) throws MessagingException {
        Properties props = new Properties();

        props.put("mail.smtp.host", getParam(context, "mail.smtp.host", "localhost"));
        props.put("mail.smtp.port", getParam(context, "mail.smtp.port", "2525"));
        props.put("mail.smtp.auth", getParam(context, "mail.smtp.auth", "false"));
        props.put("mail.smtp.starttls.enable", getParam(context, "mail.smtp.starttls", "false"));

        boolean auth = Boolean.parseBoolean(getParam(context, "mail.smtp.auth", "false"));

        Session session;

        if (auth) {
            String username = getParam(context, "mail.smtp.username", "");
            String password = getParam(context, "mail.smtp.password", "");

            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            session = Session.getInstance(props);
        }

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(getParam(context, "mail.from", "noreply@webdelivery.local")));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        message.setSubject(subject);
        message.setText(body);
        message.setSentDate(new Date());

        Transport.send(message);
    }

    private static boolean isEnabled(ServletContext context) {
        return Boolean.parseBoolean(getParam(context, "mail.enabled", "true"));
    }

    private static String getParam(ServletContext context, String name, String defaultValue) {
        String value = context.getInitParameter(name);
        return value != null && !value.isBlank() ? value : defaultValue;
    }

    private static String formatMoney(Double value) {
        return String.format(Locale.ITALY, "%.2f", value != null ? value : 0.0);
    }
}