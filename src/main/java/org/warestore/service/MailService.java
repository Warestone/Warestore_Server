package org.warestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.warestore.model.Item;
import org.warestore.model.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Service
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendMessage(String to, String subject, String messageText){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(messageText);
        emailSender.send(message);
    }

    public String compileNewPasswordMessage(User user){
        StringBuffer message = new StringBuffer();
        message.append("В Вашем профиле под логином '"+user.getUsername()+"' зарегистрировано изменение пароля.")
                .append("\nДата изменения: ").append(new SimpleDateFormat("yyyy.MM.dd ', время ' hh:mm:ss a").format(new Date()))
                .append("\nЕсли это были не Вы, пожалуйста, свяжитесь с администратором магазина WARESTORE!")
                .append("\n\nС Уважением, оружейный магазин 'WARESTORE'.");
        return message.toString();
    }

    public String compileOrderMessage(HashMap<Integer, Item> cart, User user){
        StringBuffer message = new StringBuffer();
        double orderSum=0.0;
        if (cart.size()!=0)
            for(Item founded:cart.values())
                orderSum+=founded.getPrice()*founded.getQuantity();

        message.append("Спасибо за покупку в магазине Warestore! Ребята с Grove Street выражают respect+");
        message.append("\n\nИнформация о заказе:");
        message.append("\n\tДата заказа: ").append(new SimpleDateFormat("yyyy.MM.dd ', время ' hh:mm:ss a").format(new Date()));
        message.append("\n\tПолучатель: "+user.getFIO()); // add FIO to database, models & UI in registration
        message.append("\n\tАдрес доставки: ").append(user.getAddress());
        message.append("\n\tОбщая сумма заказа: ").append(orderSum).append("₽");
        message.append("\n\tКупленные товары:\n");
        int counter = 0;
        for(Item founded:cart.values()){
            message.append("\n\t\t").append(counter+=1).append(") ");
            message.append("Наименование: ").append(founded.getName());
            message.append("\n\t\t     Количество: ").append(founded.getQuantity());
            message.append("\n\t\t     Цена за шт.: ").append(founded.getPrice()).append("₽");
            message.append("\n\t\t     Общая цена: ").append((Double)(founded.getPrice() * founded.getQuantity())).append("₽");
            message.append("\n\n");
        }
        message.append("\n\nПожалуйста, не отвечайте на это письмо, оно было сгенерировано автоматически!");
        message.append("\nС Уважением, оружейный магазин 'WARESTORE'.");
        return message.toString();
    }
}
