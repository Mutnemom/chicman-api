package com.chicman.api.utils

import com.google.gson.internal.bind.util.ISO8601Utils
import io.ktor.utils.io.charsets.name
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

object MailUtils {

    fun sendAccountActivationMail(host: String, memberId: String, createAt: String) {
        LogUtils.error("start create email process")

        val receiver = "tt_yordkam@hotmail.com"
        val subject = "First Mail From API"
        val msg1 = "คุณได้ใช้อีเมลนี้ลงทะเบียนใช้งานเว็บไซต์ www.chicman.com"
        val msg2 = "กรุณาคลิกลิงค์ด้านล่างเพื่อยืนยันการลงทะเบียนภายใน"
        val expiryDate = Date()

        val generatedTime = try {
            ISO8601Utils.parse(createAt, ParsePosition(0)).time / 1000
        } catch (e: Throwable) {
            e.printStackTrace()
            expiryDate.time / 1000
        }

        val confirmLink = "$host/account/accept/$generatedTime?id=$memberId"

        LogUtils.error(Charsets.UTF_8.name)

        JavaMailSenderImpl().apply {
            val properties = Properties().apply {
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "465")
                put("mail.smtp.ssl.enable", "true")
            }

            javaMailProperties = properties
            defaultEncoding = Charsets.UTF_8.name
            username = "tt00kensuke@gmail.com"
            password = "lryqnkwyvxurhyzi"

            val message = createMimeMessage()
            MimeMessageHelper(message, false).apply {
                setFrom("tt00kensuke@gmail.com")
                setSubject(subject)
                setText(
                    "<!DOCTYPE html>" +
                            "<html lang=\"en\">" +
                            "<head>" +
                            "<meta charset=\"${Charsets.UTF_8.name}\">" +
                            "<meta name=\"viewport\" content=\"width=100px, initial-scale=1.0\">" +
                            "</head>" +
                            "<body>" +
                            msg1 +
                            "<br />" +
                            msg2 +
                            " " +
                            SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(expiryDate) +
                            "<br />" +
                            "<br />" +
                            "<a href=" + confirmLink + ">" + confirmLink + "</a>" +
                            "</body>" +
                            "</html>", true
                )
            }.also {
                it.addTo(receiver)
                send(message)
            }
        }
    }

}
