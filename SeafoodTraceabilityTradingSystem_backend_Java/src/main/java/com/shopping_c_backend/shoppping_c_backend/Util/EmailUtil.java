package com.shopping_c_backend.shoppping_c_backend.Util;

import com.shopping_c_backend.shoppping_c_backend.Constants.EmailConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Component
public class EmailUtil {
    private final EmailConstants emailConstants;
    @Resource
    JavaMailSenderImpl mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    @Autowired
    public EmailUtil(EmailConstants emailConstants) {
        this.emailConstants = emailConstants;
    }

    public boolean sendEmail(String to, String verCode, String Subject) {
        MimeMessage mimeMessage = null;
        MimeMessageHelper helper = null;
        String time = DateUtil.date2String(new Date(), "yyyy-MM-dd hh:mm:ss");
        try {
            //发送复杂的邮件
            mimeMessage = mailSender.createMimeMessage();
            //组装
            helper = new MimeMessageHelper(mimeMessage, true);
            //邮件标题
            helper.setSubject(Subject);
            //因为设置了邮件格式所以html标签有点多，后面的ture为支持识别html标签
            //想要不一样的邮件格式，百度搜索一个html编译器，自我定制。
            helper.setText("<h3>\n" +
                    "\t<span style=\"font-size:16px;\">亲爱的用户：</span> \n" +
                    "</h3>\n" +
                    "<p>\n" +
                    "\t<span style=\"font-size:14px;\">&nbsp;&nbsp;&nbsp;&nbsp;</span><span style=\"font-size:14px;\">&nbsp; <span style=\"font-size:16px;\">&nbsp;&nbsp;您好！您正在进行邮箱验证，本次请求的验证码为：<span style=\"font-size:24px;color:#FFE500;\"> " + verCode + "</span>,本验证码5分钟内有效，请在5分钟内完成验证。（请勿泄露此验证码）如非本人操作，请忽略该邮件。(这是一封自动发送的邮件，请不要直接回复）</span></span>\n" +
                    "</p>\n" +
                    "<p style=\"text-align:right;\">\n" +
                    "\t<span style=\"background-color:#FFFFFF;font-size:16px;color:#000000;\"><span style=\"color:#000000;font-size:16px;background-color:#FFFFFF;\"><span token string\" style=\"font-family:&quot;font-size:16px;color:#000000;line-height:normal !important;background-color:#FFFFFF;\">海潮-海鲜食品溯源交易平台</span></span></span> \n" +
                    "</p>\n" +
                    "<p style=\"text-align:right;\">\n" +
                    "\t<span style=\"background-color:#FFFFFF;font-size:14px;\"><span style=\"color:#FF9900;font-size:18px;\"><span token string\" style=\"font-family:&quot;font-size:16px;color:#000000;line-height:normal !important;\"><span style=\"font-size:16px;color:#000000;background-color:#FFFFFF;\">" + time + "</span><span style=\"font-size:18px;color:#000000;background-color:#FFFFFF;\"></span></span></span></span> \n" +
                    "</p>", true);
            //收件人
            helper.setTo(to);
            //发送方
            helper.setFrom(emailConstants.getSenderEmail());
            try {
                //发送邮件
                mailSender.send(mimeMessage);
            } catch (MailException e) {
                //邮箱是无效的，或者发送失败
                logger.error("发生异常：", e);
                return false;
            }
        } catch (MessagingException e) {
            //发送失败--服务器繁忙
            logger.error("发生异常：", e);
            return false;
        }
        return true;
    }

    public void sendStaffEmail(String to, String shopName, String Subject, String result) {
        MimeMessage mimeMessage = null;
        MimeMessageHelper helper = null;
        String time = DateUtil.date2String(new Date(), "yyyy-MM-dd hh:mm:ss");
        try {
            //发送复杂的邮件
            mimeMessage = mailSender.createMimeMessage();
            //组装
            helper = new MimeMessageHelper(mimeMessage, true);
            //邮件标题
            helper.setSubject(Subject);
            //因为设置了邮件格式所以html标签有点多，后面的ture为支持识别html标签
            if (result.equals("正常")) {
                helper.setText("<h3>\n" +
                        "\t<span style=\"font-size:16px;\">尊敬的用户：</span> \n" +
                        "</h3>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">&nbsp;&nbsp;&nbsp;&nbsp;</span><span style=\"font-size:14px;\">&nbsp; <span style=\"font-size:16px;\">&nbsp;&nbsp;您好！我们很高兴地通知您，您申请加入的店铺:" + shopName + "\n您的审核结果为：<span style=\"font-size:24px;color:#4CAF50;\">通过</span>。恭喜您！请关注后续的工作安排。</span></span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">如有任何疑问，请随时与对应负责人联系。感谢您的支持！</span>\n" +
                        "</p>\n" +
                        "<p style=\"text-align:right;\">\n" +
                        "\t<span style=\"background-color:#FFFFFF;font-size:16px;color:#000000;\">海潮-海鲜食品溯源交易平台</span> \n" +
                        "</p>\n" +
                        "<p style=\"text-align:right;\">\n" +
                        "\t<span style=\"background-color:#FFFFFF;font-size:14px;\">发件时间：<span style=\"font-size:16px;color:#000000;\">" + time + "</span></span> \n" +
                        "</p>", true);
            } else if (result.equals("拒绝")) {
                helper.setText("<h3>\n" +
                        "\t<span style=\"font-size:16px;\">尊敬的用户：</span> \n" +
                        "</h3>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">&nbsp;&nbsp;&nbsp;&nbsp;</span><span style=\"font-size:14px;\">&nbsp; <span style=\"font-size:16px;\">&nbsp;&nbsp;您好！我们遗憾地通知您，您申请加入的店铺:" + shopName + "\n您的审核结果为：<span style=\"font-size:24px;color:#FF0000;\">未通过</span>。请您针对反馈意见进行改进。</span></span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">如有任何疑问，请随时与对应负责人联系，我们将会协助您！</span>\n" +
                        "</p>\n" +
                        "<p style=\"text-align:right;\">\n" +
                        "\t<span style=\"background-color:#FFFFFF;font-size:16px;color:#000000;\">海潮-海鲜食品溯源交易平台</span> \n" +
                        "</p>\n" +
                        "<p style=\"text-align:right;\">\n" +
                        "\t<span style=\"background-color:#FFFFFF;font-size:14px;\">发件时间：<span style=\"font-size:16px;color:#000000;\">" + time + "</span></span> \n" +
                        "</p>", true);

            } else if (result.equals("离职")) {
                helper.setText("<h3>\n" +
                        "\t<span style=\"font-size:16px;\">尊敬的员工：</span> \n" +
                        "</h3>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">&nbsp;&nbsp;&nbsp;&nbsp;</span><span style=\"font-size:14px;\">&nbsp; <span style=\"font-size:16px;\">您好！我们遗憾地通知您，您正式离职。感谢您在店铺期间的努力与贡献。</span></span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">如有任何疑问，请随时与人力资源部联系，我们将会协助您！</span>\n" +
                        "</p>\n" +
                        "<p style=\"text-align:right;\">\n" +
                        "\t<span style=\"background-color:#FFFFFF;font-size:16px;color:#000000;\">" + shopName + "</span> \n" +
                        "</p>\n" +
                        "<p style=\"text-align:right;\">\n" +
                        "\t<span style=\"background-color:#FFFFFF;font-size:14px;\">发件时间：<span style=\"font-size:16px;color:#000000;\">" + time + "</span></span> \n" +
                        "</p>", true);
            }
            //收件人
            helper.setTo(to);
            //发送方
            helper.setFrom(emailConstants.getSenderEmail());
            try {
                //发送邮件
                mailSender.send(mimeMessage);
            } catch (MailException e) {
                //邮箱是无效的，或者发送失败
                logger.error("发生异常：", e);
            }
        } catch (MessagingException e) {
            //发送失败--服务器繁忙
            logger.error("发生异常：", e);
        }
    }

    public void sendStoreEmail(String to, String shopName, String Subject, String result) {
        MimeMessage mimeMessage = null;
        MimeMessageHelper helper = null;
        String time = DateUtil.date2String(new Date(), "yyyy-MM-dd hh:mm:ss");
        try {
            //发送复杂的邮件
            mimeMessage = mailSender.createMimeMessage();
            //组装
            helper = new MimeMessageHelper(mimeMessage, true);
            //邮件标题
            helper.setSubject(Subject);
            //因为设置了邮件格式所以html标签有点多，后面的ture为支持识别html标签
            if (result.equals("通过")) {
                helper.setText("<h3>\n" +
                        "\t<span style=\"font-size:16px;\">尊敬的店铺主人：</span> \n" +
                        "</h3>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">恭喜您的店铺:" + shopName + "已通过审核！</span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">我们对您店铺的申请材料进行了认真评估，认为符合我们的入驻标准。期待您的店铺在平台上为顾客提供优质的商品和服务！</span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">祝商祺，如有任何问题或需要帮助，请随时联系我们。</span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">此致</span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">海潮-海鲜食品溯源交易平台客服团队</span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">" + time + "</span>\n" +
                        "</p>", true);
            } else if (result.equals("未通过")) {
                helper.setText("<h3>\n" +
                        "\t<span style=\"font-size:16px;\">尊敬的店铺主人：</span> \n" +
                        "</h3>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">很抱歉，您的店铺:" + shopName + "未能通过审核！</span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">请您根据反馈意见进行改进，并重新提交申请。</span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">如有任何疑问，请随时联系我们。</span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">此致</span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">海潮-海鲜食品溯源交易平台客服团队</span>\n" +
                        "</p>\n" +
                        "<p>\n" +
                        "\t<span style=\"font-size:14px;\">" + time + "</span>\n" +
                        "</p>", true);
            }
            //收件人
            helper.setTo(to);
            //发送方
            helper.setFrom(emailConstants.getSenderEmail());
            try {
                //发送邮件
                mailSender.send(mimeMessage);
            } catch (MailException e) {
                //邮箱是无效的，或者发送失败
                logger.error("发生异常：", e);
            }
        } catch (MessagingException e) {
            //发送失败--服务器繁忙
            logger.error("发生异常：", e);
        }
    }
}
