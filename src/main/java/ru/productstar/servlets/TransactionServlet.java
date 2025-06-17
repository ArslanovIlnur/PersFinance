package ru.productstar.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.productstar.servlets.model.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/incomes/add")
public class TransactionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var context = req.getServletContext();

        int freeMoney = (int) context.getAttribute("freeMoney");
        var expenses = new ArrayList<Transaction>((List) context.getAttribute("expenses"));

        for (String k : req.getParameterMap().keySet()) {
            int value = Integer.parseInt(req.getParameter(k));
            if (k.equals("bank")) { // при вводе "bank=" в URL выполняется пополнение
                freeMoney += value;
                req.setAttribute("freeMoney", freeMoney);
                expenses.add(new Transaction(k, value));
            } else {
                // Проверка, чтобы freeMoney не стал меньше 0
                if (freeMoney - value < 0) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not enough money to cover expenses");
                    return;
                }
                freeMoney -= value;
                expenses.add(new Transaction(k, value));
            }
        }

        context.setAttribute("expenses", expenses);
        context.setAttribute("freeMoney", freeMoney);
        resp.sendRedirect("/summary");
    }
}

