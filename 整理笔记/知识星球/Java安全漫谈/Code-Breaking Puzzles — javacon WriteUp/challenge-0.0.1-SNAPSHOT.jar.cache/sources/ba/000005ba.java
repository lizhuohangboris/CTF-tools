package io.tricking.challenge;

import io.tricking.challenge.spel.SmallEvaluationContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

@Controller
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/io/tricking/challenge/MainController.class */
public class MainController {
    ExpressionParser parser = new SpelExpressionParser();
    @Autowired
    private KeyworkProperties keyworkProperties;
    @Autowired
    private UserConfig userConfig;

    @GetMapping
    public String admin(@CookieValue(value = "remember-me", required = false) String rememberMeValue, HttpSession session, Model model) {
        String username;
        if (rememberMeValue != null && !rememberMeValue.equals("") && (username = this.userConfig.decryptRememberMe(rememberMeValue)) != null) {
            session.setAttribute("username", username);
        }
        Object username2 = session.getAttribute("username");
        if (username2 == null || username2.toString().equals("")) {
            return "redirect:/login";
        }
        model.addAttribute("name", getAdvanceValue(username2.toString()));
        return "hello";
    }

    @GetMapping({"/login"})
    public String login() {
        return "login";
    }

    @GetMapping({"/login-error"})
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        model.addAttribute("errorMsg", "登陆失败，用户名或者密码错误！");
        return "login";
    }

    @PostMapping({"/login"})
    public String login(@RequestParam(value = "username", required = true) String username, @RequestParam(value = "password", required = true) String password, @RequestParam(value = "remember-me", required = false) String isRemember, HttpSession session, HttpServletResponse response) {
        if (this.userConfig.getUsername().contentEquals(username) && this.userConfig.getPassword().contentEquals(password)) {
            session.setAttribute("username", username);
            if (isRemember != null && !isRemember.equals("")) {
                Cookie c = new Cookie("remember-me", this.userConfig.encryptRememberMe());
                c.setMaxAge(2592000);
                response.addCookie(c);
                return "redirect:/";
            }
            return "redirect:/";
        }
        return "redirect:/login-error";
    }

    @ExceptionHandler({HttpClientErrorException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbiddenException() {
        return "forbidden";
    }

    private String getAdvanceValue(String val) {
        String[] blacklist;
        for (String keyword : this.keyworkProperties.getBlacklist()) {
            Matcher matcher = Pattern.compile(keyword, 34).matcher(val);
            if (matcher.find()) {
                throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
            }
        }
        ParserContext parserContext = new TemplateParserContext();
        Expression exp = this.parser.parseExpression(val, parserContext);
        SmallEvaluationContext evaluationContext = new SmallEvaluationContext();
        return exp.getValue((EvaluationContext) evaluationContext).toString();
    }
}