package com.chicman.api.utils

import com.chicman.api.dto.MembersProfilesResponse
import com.chicman.api.security.jwt.JwtProvider
import com.google.gson.internal.bind.util.ISO8601Utils
import io.ktor.application.Application
import io.ktor.utils.io.charsets.name
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

object MailUtils {

    fun sendAccountActivationMail(host: String, member: MembersProfilesResponse) {
        LogUtils.info("start create account confirmation email process")

        val props = Properties()
        val senderUsername: String
        val senderPassword: String
        val confirmLinkHours: Int
        try {
            val ips = Application::class.java.classLoader.getResourceAsStream("email.properties")
            props.load(ips)
            senderUsername = props["email.username"] as String
            senderPassword = props["email.password"] as String
            confirmLinkHours = (props["email.confirm.link.hours"] as String).toInt()
        } catch (e: Throwable) {
            e.printStackTrace()
            LogUtils.error("cannot load email properties")
            return
        }

        val createDate = try {
            ISO8601Utils.parse(member.createAt, ParsePosition(0))
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        } ?: run {
            LogUtils.error("failed to create confirmation email")
            return
        }

        val expired = createDate.let {
            Calendar.getInstance()
                .apply {
                    time = it
                    add(Calendar.HOUR, confirmLinkHours)
                }
                .time
        }

        val verificationToken = JwtProvider.createVerificationToken(member.uid, createDate, expired)
        val confirmLink = "http://$host/account/accept?m=$verificationToken"

        JavaMailSenderImpl().apply {
            val properties = Properties().apply {
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "465")
                put("mail.smtp.ssl.enable", "true")
            }

            javaMailProperties = properties
            defaultEncoding = Charsets.UTF_8.name
            username = senderUsername
            password = senderPassword

            val subject = "First Mail From API"
            val msg1 = "คุณได้ใช้อีเมลนี้ลงทะเบียนใช้งานเว็บไซต์ www.chicman.com"
            val msg2 = "กรุณาคลิกลิงค์ด้านล่างเพื่อยืนยันการลงทะเบียนภายใน $confirmLinkHours ชม."

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
                            "<br />" +
                            SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(createDate) +
                            "<br />" +
                            SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(expired) +
                            "<br />" +
                            "<br />" +
                            "<a href=" + confirmLink + ">" + confirmLink + "</a>" +
                            "</body>" +
                            "</html>", true
                )
            }.also {
                it.addTo(member.username)
                send(message)
                LogUtils.info("mail sending - done")
            }
        }
    }

}
